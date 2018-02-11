
#include "JUtils.h"

#include <string.h>
#include "Portability.h" /* strnlen_portable() */


#ifndef JEXCEPTION_INCLUDED_
#include "JException.h"
#endif /* JEXCEPTION_INCLUDED_ */

#ifndef JVMPROVIDER_INCLUDED_
#include "JvmProvider.h"
#endif /* JVMPROVIDER_INCLUDED_ */

#ifndef JHANDLECACHE_INCLUDED_
#include "JHandleCache.h"
#endif /* JHANDLECACHE_INCLUDED_ */

//#ifndef LOGGER_INCLUDED_
//#include "Logger.h"
//#endif /* LOGGER_INCLUDED_ */

//#ifndef NAMECONSTANTS_INCLUDED_
//#include "NameConstants.h"
//#endif /* NAMECONSTANTS_INCLUDED_ */



#if defined (_WIN64) || defined (_WIN32)
// disable "This function may be unsafe" warnings for older C API functions
#pragma warning( disable: 4996 ) // instead of _CRT_SECURE_NO_WARNINGS, which doesn't work
#endif /* (_WIN64) || (_WIN32) */



Context* getContext(JNIEnv* pEnv) {
    return static_cast<Context*>(pEnv);
}


jlong toJavaLong(void* pointer) {
    return (jlong) (uintptr_t) pointer;
}


bool throwJavaRuntimeException(Context* pCtx, const char* format, ...) {
    try {
        jclass exceptClass = pCtx->FindClass("java/lang/RuntimeException");
        if (exceptClass == NULL) {
            return false;
        }
        const int MAX_MSG_SIZE = 4096;
        char message[MAX_MSG_SIZE] = {0};
        va_list args;
        va_start(args, format);
        int rc = vsprintf(message, format, args);
        va_end(args);
        if (rc < 0) {
            return false;
        }
        rc = pCtx->ThrowNew(exceptClass, message);
        pCtx->DeleteLocalRef(exceptClass);
        if (rc < 0) {
            return false;
        }
    } catch (const JException& /*ignore*/) {
//        __LOG_WARN __LARG("JUtils::throwJavaRuntimeException : ") __LARG(ignore.what());
        return false;
    } catch (...) {
//        __LOG_WARN __LARG("JUtils::throwJavaRuntimeException") __LARG(UNEXPECTED_ERR);
        return false;
    }
    return true;
}


void convJByteArr2Chars(Context* pCtx, jbyteArray jarray, unsigned char* psz, long maxlen) {
    
    long len = pCtx->GetArrayLength(jarray);
    if (len > maxlen) {
        len = maxlen;
    }

    memset(psz, '\0', maxlen);
    jbyte * tmpBuf = new jbyte[len];

    pCtx->GetByteArrayRegion(jarray, 0, len, tmpBuf);
    for (long i = 0; i < len; ++i) {
         psz[i] = (unsigned char) tmpBuf[i];
    }

    delete [] tmpBuf;
}

jbyteArray convChars2JByteArr(Context* pCtx, unsigned char* psz, size_t maxlen) {

    size_t len = 0;
    if (psz) {
        len = strnlen_portable(reinterpret_cast<const char*>(psz), maxlen);
    }
    if (len == 0) {
        return NULL;
    }

    jbyteArray bytes = pCtx->NewByteArray(static_cast<jsize>(len));
    if (bytes == NULL) {
        throw JException("JUtils::convChars2JByteArr(maxlen) - failed to allocate byte array");
    }

    pCtx->SetByteArrayRegion(bytes, 0, static_cast<jsize>(len), reinterpret_cast<jbyte*>(psz));
    return bytes;
}


jbyteArray convChars2JByteArr(Context* pCtx, unsigned char* psz) {

    size_t len = 0;
    if (psz) {
        len = strlen(reinterpret_cast<const char*>(psz));
    }
    if (len == 0) {
        return NULL;
    }

    jbyteArray bytes = pCtx->NewByteArray(static_cast<jsize>(len));
    if (bytes == NULL) {
        throw JException("JUtils::convChars2JByteArr - failed to allocate byte array");
    }

    pCtx->SetByteArrayRegion(bytes, 0, static_cast<jsize>(len), reinterpret_cast<jbyte*>(psz));
    return bytes;
}


jstring convChars2JString_NoThrow(Context* pCtx, const char* psz) {
    if (!psz) {
        return NULL;
    }
    jstring str = NULL;
    try {
        str = pCtx->NewStringUTF(psz);
    } catch (const JException& /*ignore*/) {
//        __LOG_ERROR __LARG("JUtils::convChars2JString_NoThrow : ") __LARG(ignore.what());
    } catch (...) { // ignore
//        __LOG_ERROR __LARG("JUtils::convChars2JString_NoThrow") __LARG(UNEXPECTED_ERR);
    }
    return str;
}


jstring convSlimString2JString(Context* pCtx, const SlimString& str) {
    return pCtx->NewStringUTF(str.c_str());
}


const SlimString convJString2UTF8SlimString(Context* pCtx, jstring jstr) {
    SlimString ret;
    if (jstr) {
        jsize numBytes = pCtx->GetStringUTFLength(jstr);
        if (numBytes > 0) {
            char* tmpBuf = new char[numBytes + 1];
            if (tmpBuf) {
                const char* utfChars = pCtx->GetStringUTFChars(jstr, NULL);
                if (utfChars) {
                    memcpy(tmpBuf, utfChars, numBytes * sizeof(char));
                    pCtx->ReleaseStringUTFChars(jstr, utfChars);
                    tmpBuf[numBytes] = '\0';
                    ret.append(tmpBuf);
                }
                delete [] tmpBuf;
            }
        }
    }
    return ret;
}
