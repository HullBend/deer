
#ifndef REFLECT_INCLUDED_
#define REFLECT_INCLUDED_


#ifndef STDAFX_INCLUDED_
#include "stdafx.h"
#endif /* STDAFX_INCLUDED_ */

#ifndef __CONTEXT_H_INCLUDED_
#include "Context.h"
#endif /* __CONTEXT_H_INCLUDED_ */



int __GCC_DONT_EXPORT getDeviceId(Context* ctx, jobject obj);

void* __GCC_DONT_EXPORT getAddress(Context* ctx, jobject obj);



#endif /* REFLECT_INCLUDED_ */
