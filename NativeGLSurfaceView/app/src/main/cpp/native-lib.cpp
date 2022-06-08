#include <jni.h>
#include <string>

#include <android/log.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_nide_nativeandroid_NativeRenderer_getNameJNI(JNIEnv *env, jobject) {
    std::string hello = "This is NativeRenderer";
    return env->NewStringUTF(hello.c_str());
}
