
#ifndef JUTILS_INCLUDED_
#include "JUtils.h"
#endif /* JUTILS_INCLUDED_ */

#ifndef __CONTEXT_H_INCLUDED_
#include "Context.h"
#endif /* __CONTEXT_H_INCLUDED_ */

#ifndef _JAVASOFT_JNI_H_
#include "jni.h"
#endif /* _JAVASOFT_JNI_H_ */

#ifndef REFLECT_INCLUDED_
#include "Reflect.h"
#endif /* REFLECT_INCLUDED_ */



#ifdef __cplusplus
extern "C" {
#endif


/*
 * Class:     net_deer_cuda_DeviceMemory
 * Method:    cudaMallocN
 * Signature: (IJ)J
 */
JNIEXPORT jlong JNICALL Java_net_deer_cuda_DeviceMemory_cudaMallocN
  (JNIEnv* env, jclass /*clazz*/, jint deviceId, jlong byteCount)
{
    Context* pCtx = getContext(env);
    void* d_address = NULL;
//  jlong test = toJavaLong(d_address);
    return 0L;
}


/*
 * Class:     net_deer_cuda_DeviceMemory
 * Method:    cudaFreeN
 * Signature: (Lnet/deer/cuda/Address;)J
 */
JNIEXPORT jlong JNICALL Java_net_deer_cuda_DeviceMemory_cudaFreeN
  (JNIEnv* env, jclass /*clazz*/, jobject addr)
{
    Context* pCtx = getContext(env);
    int deviceId = getDeviceId(pCtx, addr);
    void* d_address = getAddress(pCtx, addr);
    return 0L;
}


/*
 * Class:     net_deer_cuda_DeviceMemory
 * Method:    cudaMemcpyHostToDeviceN
 * Signature: (Lnet/deer/cuda/Address;[FII)V
 */
JNIEXPORT void JNICALL Java_net_deer_cuda_DeviceMemory_cudaMemcpyHostToDeviceN
  (JNIEnv* env, jclass /*clazz*/, jobject addr, jfloatArray array, jint fromIndex, jint toIndex)
{
    Context* pCtx = getContext(env);
    int deviceId = getDeviceId(pCtx, addr);
    void* d_address = getAddress(pCtx, addr);
}


/*
 * Class:     net_deer_cuda_DeviceMemory
 * Method:    cudaMemcpyDeviceToHostN
 * Signature: (Lnet/deer/cuda/Address;[FII)V
 */
JNIEXPORT void JNICALL Java_net_deer_cuda_DeviceMemory_cudaMemcpyDeviceToHostN
  (JNIEnv* env, jclass /*clazz*/, jobject addr, jfloatArray array, jint fromIndex, jint toIndex)
{
    Context* pCtx = getContext(env);
    int deviceId = getDeviceId(pCtx, addr);
    void* d_address = getAddress(pCtx, addr);
}


#ifdef __cplusplus
}
#endif
