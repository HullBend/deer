
#ifndef JUTILS_INCLUDED_
#define JUTILS_INCLUDED_


#ifndef _JAVASOFT_JNI_H_
#include "jni.h"
#endif /* _JAVASOFT_JNI_H_ */

#ifndef __CONTEXT_H_INCLUDED_
#include "Context.h"
#endif /* __CONTEXT_H_INCLUDED_ */

#ifndef SLIMSTRING_INCLUDED_
#include "SlimString.h"
#endif /* SLIMSTRING_INCLUDED_ */



Context* getContext(JNIEnv* env);

jlong toJavaLong(void* pointer);

jbyteArray convChars2JByteArr(Context* pCtx, unsigned char* psz, size_t maxlen);

jbyteArray convChars2JByteArr(Context* pCtx, unsigned char* psz);

jstring convChars2JString_NoThrow(Context* pCtx, const char* psz);

jstring convSlimString2JString(Context* pCtx, const SlimString& str);

const SlimString convJString2UTF8SlimString(Context* pCtx, jstring jstr);

bool throwJavaRuntimeException(Context* pCtx, const char* format, ...);

void convJByteArr2Chars(Context* pCtx, jbyteArray jarray, unsigned char* psz, long maxlen);


#endif /* JUTILS_INCLUDED_ */

