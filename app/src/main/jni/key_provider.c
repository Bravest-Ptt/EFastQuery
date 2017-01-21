//
// Created by pengtian on 2017/1/21.
//

#include <jni.h>
#include <android/log.h>

#ifndef LOG_TAG
#define LOG_TAG "bravest-ptt"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#endif

#ifndef _Included_bravest_ptt_efastquery_data_TranslateEngine
#define _Included_bravest_ptt_efastquery_data_TranslateEngine
#ifdef __cplusplus

extern "C" {
#endif
JNIEXPORT jstring JNICALL Java_bravest_ptt_efastquery_data_TranslateEngine_getKeyIdFromNative
        (JNIEnv* env, jobject jobject)
{
    LOGD("get key id from native");
    return (*env)->NewStringUTF(env, "keyid");
}

JNIEXPORT jstring JNICALL Java_bravest_ptt_efastquery_data_TranslateEngine_getKeySecretFromNative
        (JNIEnv *env, jobject jObj)
{
    LOGD("get key secret from native");
    return (*env)->NewStringUTF(env, "keysecret");
}

#ifdef __cplusplus
}
#endif
#endif


