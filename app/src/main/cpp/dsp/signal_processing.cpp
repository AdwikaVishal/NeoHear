#include "signal_processing.h"

#include <cmath>
#include <vector>
#include <numeric>
#include <algorithm>

namespace nehear {
namespace dsp {

void generateClickStimulus(int sampleRateHz, int numSamples, float* out) {
    for (int i = 0; i < numSamples; i++) {
        out[i] = (i == 0) ? 1.0f : 0.0f;
    }
}

void generateDpStimulus(
    int sampleRateHz, int numSamples,
    double f1Hz, double f2Hz,
    float amplitude1, float amplitude2,
    float* out)
{
    const double twoPi = 2.0 * M_PI;
    for (int i = 0; i < numSamples; i++) {
        double t = static_cast<double>(i) / sampleRateHz;
        out[i] = amplitude1 * static_cast<float>(sin(twoPi * f1Hz * t))
               + amplitude2 * static_cast<float>(sin(twoPi * f2Hz * t));
    }
}

void averageBuffers(const float* const* buffers, int numBufs, int bufLen, float* out) {
    if (numBufs <= 0 || bufLen <= 0) return;

    if (numBufs == 1) {
        for (int i = 0; i < bufLen; i++) {
            out[i] = buffers[0][i];
        }
        return;
    }

    for (int i = 0; i < bufLen; i++) {
        double sum = 0.0;
        for (int b = 0; b < numBufs; b++) {
            sum += buffers[b][i];
        }
        out[i] = static_cast<float>(sum / numBufs);
    }
}

double computeRms(const float* data, int len) {
    if (len <= 0) return 0.0;
    double sumSq = 0.0;
    for (int i = 0; i < len; i++) {
        sumSq += static_cast<double>(data[i]) * data[i];
    }
    return std::sqrt(sumSq / len);
}

double computeNoiseFloorRms(const float* data, int len) {
    if (len <= 0) return 0.0;
    int tailStart = static_cast<int>(len * 0.75);
    int tailLen = len - tailStart;
    if (tailLen <= 0) return computeRms(data, len);
    return computeRms(data + tailStart, tailLen);
}

// ── EXPERIMENTAL: Cry acoustic analysis ──────────────────────────────────

static float rmsToDb(float rms) {
    if (rms < 1e-6f) return -80.0f;
    return 20.0f * log10f(rms);
}

static float computePitch(const float* audio, int start, int len, int sampleRate) {
    int maxLag = sampleRate / 50;
    int minLag = sampleRate / 600;
    if (len < maxLag) return 0.0f;

    std::vector<float> corr(maxLag - minLag, 0.0f);
    for (int lag = minLag; lag < maxLag; ++lag) {
        float sum = 0.0f;
        for (int i = 0; i < len - lag; ++i) {
            sum += audio[start + i] * audio[start + i + lag];
        }
        corr[lag - minLag] = sum / (len - lag);
    }

    auto maxIt = std::max_element(corr.begin(), corr.end());
    if (*maxIt < 0.1f) return 0.0f;
    int bestLag = minLag + static_cast<int>(std::distance(corr.begin(), maxIt));
    return static_cast<float>(sampleRate) / bestLag;
}

void analyzeCry(const float* audio, int numSamples, int sampleRate, float* out) {
    for (int i = 0; i < 7; i++) out[i] = 0.0f;

    if (numSamples < static_cast<int>(sampleRate * 0.5f)) return;

    // 1. Energy (RMS in dB)
    float sumSq = 0.0f;
    for (int i = 0; i < numSamples; ++i) sumSq += audio[i] * audio[i];
    float rms = sqrtf(sumSq / numSamples);
    float avgEnergyDb = rmsToDb(rms);

    // 2. Split into 50ms windows for pitch extraction
    int windowSize = sampleRate / 20;
    int numWindows = numSamples / windowSize;
    if (numWindows < 2) numWindows = 2;

    std::vector<float> pitches;
    int voicedCount = 0;
    for (int w = 0; w < numWindows; ++w) {
        int start = w * windowSize;
        int len = windowSize;
        if (start + len > numSamples) len = numSamples - start;
        if (len < 100) continue;

        float p = computePitch(audio, start, len, sampleRate);
        if (p > 50.0f && p < 600.0f) {
            pitches.push_back(p);
            voicedCount++;
        }
    }

    float voicingRatio = (numWindows > 0) ? static_cast<float>(voicedCount) / numWindows : 0.0f;

    if (pitches.empty()) {
        out[2] = avgEnergyDb;
        out[5] = voicingRatio;
        return;
    }

    // 3. Pitch statistics
    float sumPitch = 0.0f;
    for (float p : pitches) sumPitch += p;
    float avgPitchHz = sumPitch / static_cast<float>(pitches.size());

    float sqSum = 0.0f;
    for (float p : pitches) sqSum += (p - avgPitchHz) * (p - avgPitchHz);
    float pitchStdDev = sqrtf(sqSum / static_cast<float>(pitches.size()));

    // 4. Jitter (mean absolute deviation of consecutive pitch periods / mean)
    float jitterSum = 0.0f;
    for (size_t i = 1; i < pitches.size(); ++i) {
        jitterSum += fabsf(pitches[i] - pitches[i - 1]);
    }
    float jitter = (pitches.size() > 1)
        ? (jitterSum / static_cast<float>(pitches.size() - 1)) / avgPitchHz
        : 0.0f;

    // 5. Shimmer placeholder (amplitude perturbation — simplified)
    float shimmer = 0.0f;
    // Compute per-window RMS to measure amplitude variation
    std::vector<float> windowRms;
    for (int w = 0; w < numWindows; ++w) {
        int start = w * windowSize;
        int len = windowSize;
        if (start + len > numSamples) len = numSamples - start;
        if (len < 100) continue;
        float wsq = 0.0f;
        for (int i = 0; i < len; ++i) wsq += audio[start + i] * audio[start + i];
        windowRms.push_back(sqrtf(wsq / len));
    }
    if (windowRms.size() > 1) {
        float meanAmp = 0.0f;
        for (float r : windowRms) meanAmp += r;
        meanAmp /= static_cast<float>(windowRms.size());
        float shimmerSum = 0.0f;
        for (size_t i = 1; i < windowRms.size(); ++i) {
            shimmerSum += fabsf(windowRms[i] - windowRms[i - 1]);
        }
        shimmer = (meanAmp > 1e-6f)
            ? (shimmerSum / static_cast<float>(windowRms.size() - 1)) / meanAmp
            : 0.0f;
    }

    // 6. Risk flags (experimental thresholds)
    int riskFlags = 0;
    if (avgPitchHz > 450.0f) riskFlags |= 1;
    if (avgEnergyDb < -30.0f) riskFlags |= 2;
    if (jitter > 0.15f) riskFlags |= 4;
    if (voicingRatio < 0.3f) riskFlags |= 8;

    out[0] = avgPitchHz;
    out[1] = pitchStdDev;
    out[2] = avgEnergyDb;
    out[3] = jitter;
    out[4] = shimmer;
    out[5] = voicingRatio;
    out[6] = static_cast<float>(riskFlags);
}

}  // namespace dsp
}  // namespace nehear
