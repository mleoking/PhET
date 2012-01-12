/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * JNI implementation of the AMD_debug_output function callback.
 *
 * @author Spasi
 */

#include <jni.h>
#include "common_tools.h"
#include "extcl.h"
#include "org_lwjgl_opencl_CallbackUtil.h"

static jmethodID contextCallbackJ;
static jmethodID memObjectDestructorCallbackJ;
static jmethodID buildProgramCallbackJ;
static jmethodID nativeKernelCallbackJ;
static jmethodID eventCallbackJ;

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CallbackUtil_ncreateGlobalRef(JNIEnv *env, jclass clazz, jobject obj) {
	return (intptr_t)(*env)->NewGlobalRef(env, obj);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opencl_CallbackUtil_deleteGlobalRef(JNIEnv *env, jclass clazz, jlong globalRef) {
    (*env)->DeleteGlobalRef(env, (jobject)(intptr_t)globalRef);
}

// ----------------- [ CONTEXT CALLBACK ] -----------------

static void CL_CALLBACK contextCallback(const char *errinfo, const void *private_info, size_t cb, void *user_data) {
    JNIEnv *env = attachCurrentThread();
    jobject private_info_buffer = NULL;

	if ( env != NULL && !(*env)->ExceptionOccurred(env) && contextCallbackJ != NULL ) {
        if ( private_info != NULL )
            private_info_buffer = NewReadOnlyDirectByteBuffer(env, private_info, cb);

        (*env)->CallVoidMethod(env, (jobject)user_data, contextCallbackJ,
            NewStringNativeWithLength(env, errinfo, strlen(errinfo)),
            private_info_buffer
        );
    }

    detachCurrentThread();
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CallbackUtil_getContextCallback(JNIEnv *env, jclass clazz) {
    // Cache the callback methodID
    jclass callbackClass;
    if ( contextCallbackJ == NULL ) {
        callbackClass = (*env)->FindClass(env, "org/lwjgl/opencl/CLContextCallback");
        if ( callbackClass != NULL )
            contextCallbackJ = (*env)->GetMethodID(env, callbackClass, "handleMessage", "(Ljava/lang/String;Ljava/nio/ByteBuffer;)V");
    }

    return (jlong)(intptr_t)&contextCallback;
}

// ----------------- [ MEM OBJECT DESTRUCTOR CALLBACK ] -----------------

static void CL_CALLBACK memObjectDestructorCallback(cl_mem memobj, void *user_data) {
    JNIEnv *env = attachCurrentThread();

	if ( env != NULL && !(*env)->ExceptionOccurred(env) && memObjectDestructorCallbackJ != NULL ) {
        (*env)->CallVoidMethod(env, (jobject)user_data, memObjectDestructorCallbackJ,
            (jlong)(intptr_t)memobj
        );
        (*env)->DeleteGlobalRef(env, (jobject)user_data);
    }

    detachCurrentThread();
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CallbackUtil_getMemObjectDestructorCallback(JNIEnv *env, jclass clazz) {
    // Cache the callback methodID
    jclass callbackClass;
    if ( memObjectDestructorCallbackJ == NULL ) {
        callbackClass = (*env)->FindClass(env, "org/lwjgl/opencl/CLMemObjectDestructorCallback");
        if ( callbackClass != NULL )
            memObjectDestructorCallbackJ = (*env)->GetMethodID(env, callbackClass, "handleMessage", "(J)V");
    }

    return (jlong)(intptr_t)&memObjectDestructorCallback;
}

// ----------------- [ MEM OBJECT DESTRUCTOR CALLBACK ] -----------------

static void CL_CALLBACK buildProgramCallback(cl_program program, void *user_data) {
    JNIEnv *env = attachCurrentThread();

	if ( env != NULL && !(*env)->ExceptionOccurred(env) && buildProgramCallbackJ != NULL ) {
        (*env)->CallVoidMethod(env, (jobject)user_data, buildProgramCallbackJ,
            (jlong)(intptr_t)program
        );
        (*env)->DeleteGlobalRef(env, (jobject)user_data);
    }

    detachCurrentThread();
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CallbackUtil_getBuildProgramCallback(JNIEnv *env, jclass clazz) {
    // Cache the callback methodID
    jclass callbackClass;
    if ( buildProgramCallbackJ == NULL ) {
        callbackClass = (*env)->FindClass(env, "org/lwjgl/opencl/CLBuildProgramCallback");
        if ( callbackClass != NULL )
            buildProgramCallbackJ = (*env)->GetMethodID(env, callbackClass, "handleMessage", "(J)V");
    }

    return (jlong)(intptr_t)&buildProgramCallback;
}

// ----------------- [ NATIVE KERNEL CALLBACK ] -----------------

static void CL_USER_FUNC_CALLBACK nativeKernelCallback(void *args) {
    JNIEnv *env = attachCurrentThread();
    jobject user_func = (jobject)(intptr_t)*(jlong *)args;
    jint num_mem_objects = *(jint *)((char *)args + 8);
    jobjectArray memobjs = NULL;
    jobject buffer;
    jint i;

	if ( env != NULL && !(*env)->ExceptionOccurred(env) && nativeKernelCallbackJ != NULL ) {
        if ( num_mem_objects > 0 ) {
            memobjs = (*env)->NewObjectArray(env, num_mem_objects, (*env)->FindClass(env, "java/nio/ByteBuffer"), NULL);
            for ( i = 0; i < num_mem_objects; i++ ) {
                buffer = (*env)->NewDirectByteBuffer(env,
                    // Pointer to cl_mem buffer
                    (void *)((char *)args + (12 + 4 + (i * (4 + sizeof(void *))))),
                    // cl_mem buffer size
                    *((jint *)((char *)args + (12 + (i * (4 + sizeof(void *))))))
                );
                (*env)->SetObjectArrayElement(env, memobjs, i, buffer);
            }
        }

        (*env)->CallVoidMethod(env, user_func, nativeKernelCallbackJ, memobjs);

        if ( num_mem_objects > 0 )
            (*env)->DeleteLocalRef(env, memobjs);
        (*env)->DeleteGlobalRef(env, user_func);
    }

    detachCurrentThread();
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CallbackUtil_getNativeKernelCallback(JNIEnv *env, jclass clazz) {
    // Cache the callback methodID
    jclass callbackClass;
    if ( nativeKernelCallbackJ == NULL ) {
        callbackClass = (*env)->FindClass(env, "org/lwjgl/opencl/CLNativeKernel");
        if ( callbackClass != NULL )
            nativeKernelCallbackJ = (*env)->GetMethodID(env, callbackClass, "execute", "([Ljava/nio/ByteBuffer;)V");
    }

    return (jlong)(intptr_t)&nativeKernelCallback;
}

// ----------------- [ EVENT CALLBACK ] -----------------

static void CL_CALLBACK eventCallback(cl_event event, cl_int event_command_exec_status, void *user_data) {
    JNIEnv *env = attachCurrentThread();

	if ( env != NULL && !(*env)->ExceptionOccurred(env) && eventCallbackJ != NULL ) {
        (*env)->CallVoidMethod(env, (jobject)user_data, eventCallbackJ,
            (jlong)(intptr_t)event,
            event_command_exec_status
        );
        (*env)->DeleteGlobalRef(env, (jobject)user_data);
    }

    detachCurrentThread();
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CallbackUtil_getEventCallback(JNIEnv *env, jclass clazz) {
    // Cache the callback methodID
    jclass callbackClass;
    if ( eventCallbackJ == NULL ) {
        callbackClass = (*env)->FindClass(env, "org/lwjgl/opencl/CLEventCallback");
        if ( callbackClass != NULL )
            eventCallbackJ = (*env)->GetMethodID(env, callbackClass, "handleMessage", "(JI)V");
    }

    return (jlong)(intptr_t)&eventCallback;
}

// ----------------- [ APPLE_ContextLoggingFunctions CALLBACKS ] -----------------

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CallbackUtil_getLogMessageToSystemLogAPPLE(JNIEnv *env, jclass clazz) {
    return (jlong)(intptr_t)extcl_GetProcAddress("clLogMessagesToSystemLogAPPLE");
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CallbackUtil_getLogMessageToStdoutAPPLE(JNIEnv *env, jclass clazz) {
    return (jlong)(intptr_t)extcl_GetProcAddress("getLogMessageToStdoutAPPLE");
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CallbackUtil_getLogMessageToStderrAPPLE(JNIEnv *env, jclass clazz) {
    return (jlong)(intptr_t)extcl_GetProcAddress("getLogMessageToStderrAPPLE");
}
