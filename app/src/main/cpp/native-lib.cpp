#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_harputoglu_orhun_util_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Orhun Native Core Initialized";
    return env->NewStringUTF(hello.c_str());
}
