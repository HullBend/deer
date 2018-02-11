
#include "Reflect.h"


#ifndef __CONTEXT_H_INCLUDED_
#include "Context.h"
#endif /* __CONTEXT_H_INCLUDED_ */

#ifndef _JAVASOFT_JNI_H_
#include "jni.h"
#endif /* _JAVASOFT_JNI_H_ */

//#ifndef JUTILS_INCLUDED_
//#include "JUtils.h"
//#endif /* JUTILS_INCLUDED_ */

#ifndef JHANDLECACHE_INCLUDED_
#include "JHandleCache.h"
#endif /* JHANDLECACHE_INCLUDED_ */

//#ifndef JEXCEPTION_INCLUDED_
//#include "JException.h"
//#endif /* JEXCEPTION_INCLUDED_ */

//#ifndef LOGGER_INCLUDED_
//#include "Logger.h"
//#endif /* LOGGER_INCLUDED_ */

//#ifndef NAMECONSTANTS_INCLUDED_
//#include "NameConstants.h"
//#endif /* NAMECONSTANTS_INCLUDED_ */



int __GCC_DONT_EXPORT getDeviceId(Context* ctx, jobject obj)
{
    JHandleCache* cache = JHandleCache::instance();
    jfieldID deviceId = (jfieldID) cache->get("Address/deviceId");

    return ctx->GetIntField(obj, deviceId);
}


void* __GCC_DONT_EXPORT getAddress(Context* ctx, jobject obj)
{
    JHandleCache* cache = JHandleCache::instance();
    jfieldID address = (jfieldID) cache->get("Address/address");

    jlong addr = ctx->GetLongField(obj, address);
    return (void*) (uintptr_t) addr;
}

