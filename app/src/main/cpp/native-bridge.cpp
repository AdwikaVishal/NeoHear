#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>

#include "dsp/signal_processing.h"

#define LOG_TAG "NeoHearNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// ── Helper: extract float* from a Java float[] ───────────────────────────

static float* getFloatArrayElements(JNIEnv* env, jfloatArray arr) {
    return env->GetFloatArrayElements(arr, nullptr);
}

static void releaseFloatArrayElements(JNIEnv* env, jfloatArray arr, float* elems) {
    env->ReleaseFloatArrayElements(arr, elems, JNI_ABORT);
}

// ── Original ping stub ───────────────────────────────────────────────────

extern "C" JNIEXPORT jstring JNICALL
Java_com_neohear_audio_NativeBridge_nativePing(
        JNIEnv *env,
        jobject /* this */) {
    LOGI("nativePing called");
    std::string message = "NeoHear native bridge is alive!";
    return env->NewStringUTF(message.c_str());
}

// Alias to fix potential naming mismatch reported in logs
extern "C" JNIEXPORT jstring JNICALL
Java_com_neohear_audio_NativeBridge_ping(
        JNIEnv *env,
        jobject thiz) {
    LOGI("ping (alias) called");
    return Java_com_neohear_audio_NativeBridge_nativePing(env, thiz);
}

// ── Stimulus generation ──────────────────────────────────────────────────

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_neohear_audio_NativeBridge_nativeGenerateClickStimulus(
        JNIEnv *env,
        jobject /* this */,
        jint sampleRateHz,
        jint numSamples) {
    jfloatArray result = env->NewFloatArray(numSamples);
    std::vector<float> buf(numSamples);
    nehear::dsp::generateClickStimulus(sampleRateHz, numSamples, buf.data());
    env->SetFloatArrayRegion(result, 0, numSamples, buf.data());
    return result;
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_neohear_audio_NativeBridge_nativeGenerateDpStimulus(
        JNIEnv *env,
        jobject /* this */,
        jint sampleRateHz,
        jint numSamples,
        jdouble f1Hz,
        jdouble f2Hz,
        jfloat amplitude1,
        jfloat amplitude2) {
    jfloatArray result = env->NewFloatArray(numSamples);
    std::vector<float> buf(numSamples);
    nehear::dsp::generateDpStimulus(
        sampleRateHz, numSamples, f1Hz, f2Hz, amplitude1, amplitude2, buf.data());
    env->SetFloatArrayRegion(result, 0, numSamples, buf.data());
    return result;
}

// ── Buffer averaging ─────────────────────────────────────────────────────

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_neohear_audio_NativeBridge_nativeAverageBuffers(
        JNIEnv *env,
        jobject /* this */,
        jobjectArray buffers) {
    jint numBufs = env->GetArrayLength(buffers);
    if (numBufs == 0) {
        return env->NewFloatArray(0);
    }

    jfloatArray first = (jfloatArray)env->GetObjectArrayElement(buffers, 0);
    jint bufLen = env->GetArrayLength(first);

    // Collect raw pointers
    std::vector<float*> ptrs(numBufs);
    std::vector<jfloatArray> javaArrays(numBufs);
    javaArrays[0] = first;
    ptrs[0] = getFloatArrayElements(env, first);

    for (int b = 1; b < numBufs; b++) {
        javaArrays[b] = (jfloatArray)env->GetObjectArrayElement(buffers, b);
        ptrs[b] = getFloatArrayElements(env, javaArrays[b]);
    }

    // Average
    std::vector<float> result(bufLen);
    nehear::dsp::averageBuffers(
        const_cast<const float* const*>(ptrs.data()),
        numBufs, bufLen, result.data());

    // Release Java arrays
    for (int b = 0; b < numBufs; b++) {
        releaseFloatArrayElements(env, javaArrays[b], ptrs[b]);
    }

    jfloatArray out = env->NewFloatArray(bufLen);
    env->SetFloatArrayRegion(out, 0, bufLen, result.data());
    return out;
}

// ── RMS computation ──────────────────────────────────────────────────────

extern "C" JNIEXPORT jdouble JNICALL
Java_com_neohear_audio_NativeBridge_nativeComputeRms(
        JNIEnv *env,
        jobject /* this */,
        jfloatArray data) {
    jint len = env->GetArrayLength(data);
    float* elems = getFloatArrayElements(env, data);
    double rms = nehear::dsp::computeRms(elems, len);
    releaseFloatArrayElements(env, data, elems);
    return rms;
}

extern "C" JNIEXPORT jdouble JNICALL
Java_com_neohear_audio_NativeBridge_nativeComputeNoiseFloorRms(
        JNIEnv *env,
        jobject /* this */,
        jfloatArray data) {
    jint len = env->GetArrayLength(data);
    float* elems = getFloatArrayElements(env, data);
    double rms = nehear::dsp::computeNoiseFloorRms(elems, len);
    releaseFloatArrayElements(env, data, elems);
    return rms;
}
