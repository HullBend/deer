
#ifndef JUTILS_INCLUDED_
#include "JUtils.h"
#endif /* JUTILS_INCLUDED_ */

#ifndef JEXCEPTION_INCLUDED_
#include "JException.h"
#endif /* JEXCEPTION_INCLUDED_ */

#ifndef JHANDLECACHE_INCLUDED_
#include "JHandleCache.h"
#endif

#ifndef __CONTEXT_H_INCLUDED_
#include "Context.h"
#endif /* __CONTEXT_H_INCLUDED_ */

#ifndef NAMECONSTANTS_INCLUDED_
#include "NameConstants.h"
#endif /* NAMECONSTANTS_INCLUDED_ */

#ifndef SLIMSTRING_INCLUDED_
#include "SlimString.h"
#endif /* SLIMSTRING_INCLUDED_ */




//////////////////////////////////////////////////////////////////////
// Forward declarations
//////////////////////////////////////////////////////////////////////

// Module-private helper functions for inferring Java handles
static jclass findClass(Context* pCtx, const char* className);
static jmethodID findDefaultConstructor(Context* pCtx, jclass clazz);
static jmethodID findStaticMethod(Context* pCtx, jclass clazz, const char* name, const char* signature);
static jfieldID findCharFieldId(Context* pCtx, jclass clazz, const char* fieldName);
static jfieldID findShortFieldId(Context* pCtx, jclass clazz, const char* fieldName);
static jfieldID findIntFieldId(Context* pCtx, jclass clazz, const char* fieldName);
static jfieldID findByteArrayFieldId(Context* pCtx, jclass clazz, const char* fieldName);
static jfieldID findLongFieldId(Context* pCtx, jclass clazz, const char* fieldName);
static jfieldID findNonPrimitiveFieldId(Context* pCtx, jclass clazz, const char* fieldName, const char* typeSignature);


// Module-private helper functions for caching the Java descriptors
static void cache_Address_Handles(Context* pCtx, JHandleCache* cache);




#ifdef __cplusplus
extern "C" {
#endif


/**
 * Class:     net_deer_cuda_Native
 * Method:    initializeHandleCacheN
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_net_deer_cuda_Native_initializeHandleCacheN(JNIEnv* env, jclass /*clazz*/) {

    Context* pCtx = getContext(env);
    try {
        JHandleCache::clear();
        JHandleCache* cache = JHandleCache::instance();

        cache_Address_Handles(pCtx, cache);

    } catch (const JException& ex) {
        if (!throwJavaRuntimeException(pCtx, "%s %s", "initializeHandleCacheN", ex.what())) {
            return JNI_FALSE;
        }
    } catch (...) {
        if (!throwJavaRuntimeException(pCtx, "%s", "initializeHandleCacheN: caught unknown structured exception")) {
            return JNI_FALSE;
        }
    }
    return JNI_TRUE;
}


#ifdef __cplusplus
} /* __cplusplus */
#endif





//////////////////////////////////////////////////////////////////////
// Module-private helper functions for caching the Java descriptors
// Each Java object has its own dedicated caching method.
//////////////////////////////////////////////////////////////////////
void cache_Address_Handles(Context* pCtx, JHandleCache* cache) {
    jclass clazz = findClass(pCtx, PACKAGE "Addresses$AddressImpl");

    jfieldID deviceId = findIntFieldId(pCtx, clazz, "deviceId");
    jfieldID address = findLongFieldId(pCtx, clazz, "address");

    cache->add("Address", clazz);

    cache->add("Address/deviceId", deviceId);
    cache->add("Address/address", address);
}




//////////////////////////////////////////////////////////////////////
// Module-private helper functions for finding the Java handles
//
//////////////////////////////////////////////////////////////////////

jclass findClass(Context* pCtx, const char* className) {
    jclass localClass = pCtx->FindClass(className);
    if (localClass == NULL) {
        SlimString msg("CacheInitialization::findClass - ");
        msg.append(className).append(" Class not found");
        throw JException(msg);
    }
    jclass globalClass = reinterpret_cast<jclass>(pCtx->NewGlobalRef(localClass));
    if (globalClass == NULL) {
        pCtx->DeleteLocalRef(localClass);
        SlimString msg("CacheInitialization::findClass - ");
        msg.append(className).append(" unable to create global reference (out of memory?)");
        throw JException(msg);
    }
    return globalClass;
}

jmethodID findDefaultConstructor(Context* pCtx, jclass clazz) {
    jmethodID constructor = pCtx->GetMethodID(clazz, "<init>", "()V");
    if (constructor == NULL) {
        throw JException("CacheInitialization::findDefaultConstructor - couldn't find default constructor");
    }
    return constructor;
}

jmethodID findStaticMethod(Context* pCtx, jclass clazz, const char* name, const char* signature) {
    jmethodID method = pCtx->GetStaticMethodID(clazz, name, signature);
    if (method == NULL) {
        SlimString msg("CacheInitialization::findStaticMethod - couldn't find static method ");
        msg.append(name).append(" ").append(signature);
        throw JException(msg);
    }
    return method;
}

jfieldID findCharFieldId(Context* pCtx, jclass clazz, const char* fieldName) {
    jfieldID fieldId = pCtx->GetFieldID(clazz, fieldName, "C");
    if (fieldId == NULL) {
        SlimString msg("CacheInitialization::findCharFieldId - ");
        msg.append(fieldName).append(" Char member field not found");
        throw JException(msg);
    }
    return fieldId;
}

jfieldID findShortFieldId(Context* pCtx, jclass clazz, const char* fieldName) {
    jfieldID fieldId = pCtx->GetFieldID(clazz, fieldName, "S");
    if (fieldId == NULL) {
        SlimString msg("CacheInitialization::findShortFieldId - ");
        msg.append(fieldName).append(" short member field not found");
        throw JException(msg);
    }
    return fieldId;
}

jfieldID findIntFieldId(Context* pCtx, jclass clazz, const char* fieldName) {
    jfieldID fieldId = pCtx->GetFieldID(clazz, fieldName, "I");
    if (fieldId == NULL) {
        SlimString msg("CacheInitialization::findIntFieldId - ");
        msg.append(fieldName).append(" int member field not found");
        throw JException(msg);
    }
    return fieldId;
}

jfieldID findByteArrayFieldId(Context* pCtx, jclass clazz, const char* fieldName) {
    jfieldID fieldId = pCtx->GetFieldID(clazz, fieldName, "[B");
    if (fieldId == NULL) {
        SlimString msg("CacheInitialization::findByteArrayFieldId - ");
        msg.append(fieldName).append(" byte[] member field not found");
        throw JException(msg);
    }
    return fieldId;
}

jfieldID findLongFieldId(Context* pCtx, jclass clazz, const char* fieldName) {
    jfieldID fieldId = pCtx->GetFieldID(clazz, fieldName, "J");
    if (fieldId == NULL) {
        SlimString msg("CacheInitialization::findLongFieldId - ");
        msg.append(fieldName).append(" long member field not found");
        throw JException(msg);
    }
    return fieldId;
}

jfieldID findNonPrimitiveFieldId(Context* pCtx, jclass clazz, const char* fieldName, const char* typeSignature) {
    jfieldID fieldId = pCtx->GetFieldID(clazz, fieldName, typeSignature);
    if (fieldId == NULL) {
        SlimString msg("CacheInitialization::findNonPrimitiveFieldId - Object or non-primitive array[] ");
        msg.append(fieldName).append(" ").append(typeSignature).append(" member field not found");
        throw JException(msg);
    }
    return fieldId;
}
