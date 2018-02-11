
#ifndef _JAVASOFT_JNI_H_
#include "jni.h"
#endif /* _JAVASOFT_JNI_H_ */

#ifndef JVMPROVIDER_INCLUDED_
#include "JvmProvider.h"
#endif /* JVMPROVIDER_INCLUDED_ */


#ifdef __cplusplus
extern "C" {
#endif


/**
 * JNI_OnLoad
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* /*reserved*/) {
    JvmProvider::instance()->initializeJavaVM(vm);
    return JNI_VERSION_1_6;
}

/**
 * JNI_OnUnload
 */
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* /*vm*/, void* /*reserved*/) {
    JvmProvider::instance()->clearOnJavaVMUnload();
}


#ifdef __cplusplus
}
#endif
