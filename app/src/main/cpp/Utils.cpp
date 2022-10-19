//
// Created by Samuel Pascua on 8/22/2022.
//

#include <iostream>
#include <fstream>
#include <jni.h>

using std::string;
using std::ifstream;

extern "C"
JNIEXPORT jstring JNICALL
Java_com_sammy_sbatterytweaks_Utils_readFile(JNIEnv *env, jobject thiz, jstring file_path) {
    string ret;
    string filePath = (*env).GetStringUTFChars(file_path, JNI_FALSE);
    ifstream file(filePath);
    file >> ret;
    file.close();

    return env->NewStringUTF(ret.c_str());
}