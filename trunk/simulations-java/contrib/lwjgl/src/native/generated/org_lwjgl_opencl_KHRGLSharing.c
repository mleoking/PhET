/* MACHINE GENERATED FILE, DO NOT EDIT */

#include <jni.h>
#include "extcl.h"

typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetGLContextInfoKHRPROC) (const cl_context_properties * properties, cl_gl_context_info param_name, size_t param_value_size, cl_void * param_value, size_t * param_value_size_ret);

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_KHRGLSharing_nclGetGLContextInfoKHR(JNIEnv *env, jclass clazz, jobject properties, jint properties_position, jint param_name, jlong param_value_size, jobject param_value, jint param_value_position, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	const cl_context_properties *properties_address = ((const cl_context_properties *)(((char *)(*env)->GetDirectBufferAddress(env, properties)) + properties_position));
	cl_void *param_value_address = ((cl_void *)(((char *)safeGetBufferAddress(env, param_value)) + param_value_position));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetGLContextInfoKHRPROC clGetGLContextInfoKHR = (clGetGLContextInfoKHRPROC)((intptr_t)function_pointer);
	cl_int __result = clGetGLContextInfoKHR(properties_address, param_name, param_value_size, param_value_address, param_value_size_ret_address);
	return __result;
}

