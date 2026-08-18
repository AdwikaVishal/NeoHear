#pragma once

#include <jni.h>
#include <vector>

/**
 * Core DSP routines for OAE signal processing.
 *
 * These functions are exposed to Kotlin via JNI through NativeBridge.
 * All functions operate on raw float arrays for maximum performance.
 */
namespace nehear {
namespace dsp {

/**
 * Generate a click stimulus (single-sample impulse).
 *
 * @param sampleRateHz  Sampling rate in Hz.
 * @param numSamples    Total number of output samples.
 * @param out           Output buffer (caller-allocated, length >= numSamples).
 */
void generateClickStimulus(int sampleRateHz, int numSamples, float* out);

/**
 * Generate a distortion-product stimulus (two simultaneous sinusoidal tones).
 */
void generateDpStimulus(
    int sampleRateHz, int numSamples,
    double f1Hz, double f2Hz,
    float amplitude1, float amplitude2,
    float* out
);

/**
 * Average N buffers element-wise.
 *
 * All buffers must have the same length. The result is the element-wise mean.
 *
 * @param buffers   Array of pointers to input buffers.
 * @param numBufs   Number of input buffers.
 * @param bufLen    Length of each buffer.
 * @param out       Output buffer (caller-allocated, length >= bufLen).
 */
void averageBuffers(const float* const* buffers, int numBufs, int bufLen, float* out);

/**
 * Compute the root-mean-square (RMS) of a signal.
 */
double computeRms(const float* data, int len);

/**
 * Estimate the noise floor RMS from the tail (last 25%) of the waveform.
 *
 * Assumes the OAE signal has mostly decayed by the last quarter of the buffer.
 */
double computeNoiseFloorRms(const float* data, int len);

}  // namespace dsp
}  // namespace nehear
