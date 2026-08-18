#include "signal_processing.h"

#include <cmath>
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

}  // namespace dsp
}  // namespace nehear
