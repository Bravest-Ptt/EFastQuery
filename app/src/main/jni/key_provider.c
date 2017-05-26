//
// Created by pengtian on 2017/1/21.
//

#include <jni.h>
#include <string.h>
#include <time.h>
#include <stdlib.h>
#include <android/log.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#ifndef LOG_TAG
#define LOG_TAG "bravest-ptt"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#endif

#ifndef _Included_bravest_ptt_efastquery_utils_JNIUtils
#define _Included_bravest_ptt_efastquery_utils_JNIUtils

#ifndef KEY_FILENAME
#define KEY_FILENAME "key.pro"
#endif

#ifndef KEY_CLASS
#define KEY_CLASS "bravest/ptt/efastquery/entity/word/Key"
#endif

jobject asset_key_decrypter(JNIEnv* env, jobject asset_manager);

#ifdef __cplusplus

extern "C" {
#endif
/*
 * Class:     bravest_ptt_efastquery_utils_JNIUtils
 * Method:    getKey
 * Signature: (Landroid/content/res/AssetManager;)Lbravest/ptt/efastquery/entity/word/Key;
 */
JNIEXPORT jobject JNICALL Java_bravest_ptt_efastquery_utils_JNIUtils_getKey
        (JNIEnv *env, jclass thiz, jobject asset_manager)
{
    LOGD("get key id from native");
    return asset_key_decrypter(env, asset_manager);
}

jobject asset_key_decrypter(JNIEnv* env, jobject asset_manager)
{
    LOGD("read assets");
    //get AssentManager
    AAssetManager* mgr = AAssetManager_fromJava(env, asset_manager);
    if (mgr == NULL)
    {
        LOGD("AAssetManager is NULL");
        return NULL;
    }

    //get asset file
    /**
     * Obtain a C-copy of the Java string, if you set a not java string, unknown error occurred.
    jboolean is_copy;
    LOGD("is_copy");
    const char* mfile = (*env)->GetStringUTFChars(env, KEY_FILENAME, &is_copy);
    LOGD("mfile");
     **/

    AAsset* aAsset = AAssetManager_open(mgr, KEY_FILENAME, AASSET_MODE_UNKNOWN);
    LOGD("aAsset");
    /*Now we are done with str
    (*env)->ReleaseStringUTFChars(env, KEY_FILENAME, mfile);
    LOGD("ReleaseStringUTFChars");
     */
    if (aAsset == NULL)
    {
        LOGD("Asset is NULL");
        return NULL;
    }

    //get file length
    off_t buffer_size = AAsset_getLength(aAsset);
    LOGD("buffer_size");
    LOGD("File size is %d\n", buffer_size);

    //get file content
    char* buffer = (char*)malloc(buffer_size + 1);
    buffer[buffer_size] = 0;

    int num_bytes_read = AAsset_read(aAsset, buffer, buffer_size);
    int num_rows = 0;

    //decryption
    for (int i = 0; i < num_bytes_read; ++i) {
        buffer[i] = ~buffer[i];
        if (buffer[i] == ';')
        {
            num_rows++;
        }
    }

    //get random number
    LOGD("num_rows = %d\n", num_rows);
    int rand_num = num_rander(num_rows);

    const char file_delims[] = "|;\n";
    char* result = NULL;
    int result_counter = 0;

    //create Java Key Object
    jclass key_class = (*env)->FindClass(env, KEY_CLASS);
    jmethodID key_mid = (*env)->GetMethodID(env, key_class, "<init>", "()V");
    jobject key_obj = (*env)->NewObject(env, key_class, key_mid);

    jfieldID key_id = (*env)->GetFieldID(env, key_class, "keyId", "Ljava/lang/String;");
    jfieldID key_secret = (*env)->GetFieldID(env, key_class, "keySecret", "Ljava/lang/String;");

    result = strtok(buffer, file_delims);

    while (result != NULL)
    {
        if ((result_counter / 2) >= rand_num) {
            break;
        }
        if (result_counter % 2 == 0)
        {
            (*env)->SetObjectField(env, key_obj, key_id, (*env)->NewStringUTF(env, result));
        }
        else
        {
            (*env)->SetObjectField(env, key_obj, key_secret, (*env)->NewStringUTF(env, result));
        }
        LOGD("result is  %s \n",result);
        result = strtok(NULL, file_delims);
        result_counter++;
    }
    return key_obj;
}

int num_rander(int rows)
{
    int num = 0;
    srand((unsigned)time(NULL));
    num = rand() % rows + 1;
    LOGD("Rand number is %d\n", num);
    return num;
}

#ifdef __cplusplus
}
#endif
#endif


