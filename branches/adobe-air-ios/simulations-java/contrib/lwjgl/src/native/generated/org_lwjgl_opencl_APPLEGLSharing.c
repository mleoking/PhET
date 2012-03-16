/* MACHINE GENERATED FILE, DO NOT EDIT */

#include <jni.h>
#include "extcl.h"

typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetGLContextInfoAPPLEPROC) (cl_context context, cl_void * platform_gl_ctx, cl_gl_platform_info param_name, size_t param_value_size, cl_void * param_value, size_t * param_value_size_ret);

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_APPLEGLSharing_nclGetGLContextInfoAPPLE(JNIEnv *env, jclass clazz, jlong context, jobject platform_gl_ctx, jint platform_gl_ctx_position, jint param_name, jlong param_value_size, jobject param_value, jint param_value_position, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	cl_void *platform_gl_ctx_address = ((cl_void *)(((char *)(*env)->GetDirectBufferAddress(env, platform_gl_ctx)) + platform_gl_ctx_position));
	cl_void *param_value_address = ((cl_void *)(((char *)safeGetBufferAddress(env, param_value)) + param_value_position));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetGLContextInfoAPPLEPROC clGetGLContextInfoAPPLE = (clGetGLContextInfoAPPLEPROC)((intptr_t)function_pointer);
	cl_int __result = clGetGLContextInfoAPPLE((cl_context)(intptr_t)context, platform_gl_ctx_address, param_name, param_value_size, param_value_address, param_value_size_ret_address);
	return __result;
}

