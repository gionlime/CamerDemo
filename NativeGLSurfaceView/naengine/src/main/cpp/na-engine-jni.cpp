#include <jni.h>

#include <string>
#include <memory>

#include <android/log.h>

#include "na-engine.h"


// some details: http://stylishtoupee.blogspot.com/2011/07/jni-example-with-class-instantiation-c.html

extern "C" JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    // Get jclass with env->FindClass.
    // Register methods with env->RegisterNatives.

    return JNI_VERSION_1_6;
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_nide_naengine_NARender_createNAEngine(JNIEnv *, jobject, jlong value) {
    // Some init values that can be passed
    jlong tmp = value;
    return reinterpret_cast<jlong>(new NAEngine());
}

extern "C" JNIEXPORT void JNICALL
Java_com_nide_naengine_NARender_destroyNAEngine(JNIEnv *, jobject, jlong ptr) {
    NAEngine *engine_ptr = reinterpret_cast<NAEngine*>(ptr);
    if(engine_ptr) {
        delete engine_ptr;
    }
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_nide_naengine_NARender_getNameJNI(JNIEnv *env, jobject) {
    std::string hello = "This is NativeRenderer";
    return env->NewStringUTF(hello.c_str());
}


extern "C" JNIEXPORT void JNICALL
Java_com_nide_naengine_NARender_draw(JNIEnv *env, jobject, jlong ptr) {
    NAEngine *engine_ptr = reinterpret_cast<NAEngine*>(ptr);
    if(engine_ptr) {
        engine_ptr->draw();
    } else  {
        // Shit
    }
    return;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_nide_naengine_NARender_compute(JNIEnv *env, jobject, jlong ptr, jint value) {
    NAEngine *engine_ptr = reinterpret_cast<NAEngine*>(ptr);
    jint return_value = 0;
    if(engine_ptr) {
        return_value = engine_ptr->compute(value);
    } else  {
        // Shit
    }
    return return_value;
}
