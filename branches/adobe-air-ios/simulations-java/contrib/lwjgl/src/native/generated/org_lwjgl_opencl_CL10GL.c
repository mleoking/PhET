/* MACHINE GENERATED FILE, DO NOT EDIT */

#include <jni.h>
#include "extcl.h"

typedef CL_API_ENTRY cl_mem (CL_API_CALL *clCreateFromGLBufferPROC) (cl_context context, cl_mem_flags flags, GLuint bufobj, cl_int * errcode_ret);
typedef CL_API_ENTRY cl_mem (CL_API_CALL *clCreateFromGLTexture2DPROC) (cl_context context, cl_mem_flags flags, GLenum target, GLint miplevel, GLuint texture, cl_int * errcode_ret);
typedef CL_API_ENTRY cl_mem (CL_API_CALL *clCreateFromGLTexture3DPROC) (cl_context context, cl_mem_flags flags, GLenum target, GLint miplevel, GLuint texture, cl_int * errcode_ret);
typedef CL_API_ENTRY cl_mem (CL_API_CALL *clCreateFromGLRenderbufferPROC) (cl_context context, cl_mem_flags flags, GLuint renderbuffer, cl_int * errcode_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetGLObjectInfoPROC) (cl_mem memobj, cl_gl_object_type * gl_object_type, GLuint * gl_object_name);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetGLTextureInfoPROC) (cl_mem memobj, cl_gl_texture_info param_name, size_t param_value_size, cl_void * param_value, size_t * param_value_size_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueAcquireGLObjectsPROC) (cl_command_queue command_queue, cl_uint num_objects, const cl_mem * mem_objects, cl_uint num_events_in_wait_list, const cl_event * event_wait_list, cl_event * event);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueReleaseGLObjectsPROC) (cl_command_queue command_queue, cl_uint num_objects, const cl_mem * mem_objects, cl_uint num_events_in_wait_list, const cl_event * event_wait_list, cl_event * event);

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10GL_nclCreateFromGLBuffer(JNIEnv *env, jclass clazz, jlong context, jlong flags, jint bufobj, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateFromGLBufferPROC clCreateFromGLBuffer = (clCreateFromGLBufferPROC)((intptr_t)function_pointer);
	cl_mem __result = clCreateFromGLBuffer((cl_context)(intptr_t)context, flags, bufobj, errcode_ret_address);
	return (intptr_t)__result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10GL_nclCreateFromGLTexture2D(JNIEnv *env, jclass clazz, jlong context, jlong flags, jint target, jint miplevel, jint texture, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateFromGLTexture2DPROC clCreateFromGLTexture2D = (clCreateFromGLTexture2DPROC)((intptr_t)function_pointer);
	cl_mem __result = clCreateFromGLTexture2D((cl_context)(intptr_t)context, flags, target, miplevel, texture, errcode_ret_address);
	return (intptr_t)__result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10GL_nclCreateFromGLTexture3D(JNIEnv *env, jclass clazz, jlong context, jlong flags, jint target, jint miplevel, jint texture, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateFromGLTexture3DPROC clCreateFromGLTexture3D = (clCreateFromGLTexture3DPROC)((intptr_t)function_pointer);
	cl_mem __result = clCreateFromGLTexture3D((cl_context)(intptr_t)context, flags, target, miplevel, texture, errcode_ret_address);
	return (intptr_t)__result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10GL_nclCreateFromGLRenderbuffer(JNIEnv *env, jclass clazz, jlong context, jlong flags, jint renderbuffer, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateFromGLRenderbufferPROC clCreateFromGLRenderbuffer = (clCreateFromGLRenderbufferPROC)((intptr_t)function_pointer);
	cl_mem __result = clCreateFromGLRenderbuffer((cl_context)(intptr_t)context, flags, renderbuffer, errcode_ret_address);
	return (intptr_t)__result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10GL_nclGetGLObjectInfo(JNIEnv *env, jclass clazz, jlong memobj, jobject gl_object_type, jint gl_object_type_position, jobject gl_object_name, jint gl_object_name_position, jlong function_pointer) {
	cl_gl_object_type *gl_object_type_address = ((cl_gl_object_type *)(((char *)safeGetBufferAddress(env, gl_object_type)) + gl_object_type_position));
	GLuint *gl_object_name_address = ((GLuint *)(((char *)safeGetBufferAddress(env, gl_object_name)) + gl_object_name_position));
	clGetGLObjectInfoPROC clGetGLObjectInfo = (clGetGLObjectInfoPROC)((intptr_t)function_pointer);
	cl_int __result = clGetGLObjectInfo((cl_mem)(intptr_t)memobj, gl_object_type_address, gl_object_name_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10GL_nclGetGLTextureInfo(JNIEnv *env, jclass clazz, jlong memobj, jint param_name, jlong param_value_size, jobject param_value, jint param_value_position, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	cl_void *param_value_address = ((cl_void *)(((char *)safeGetBufferAddress(env, param_value)) + param_value_position));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetGLTextureInfoPROC clGetGLTextureInfo = (clGetGLTextureInfoPROC)((intptr_t)function_pointer);
	cl_int __result = clGetGLTextureInfo((cl_mem)(intptr_t)memobj, param_name, param_value_size, param_value_address, param_value_size_ret_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10GL_nclEnqueueAcquireGLObjects(JNIEnv *env, jclass clazz, jlong command_queue, jint num_objects, jobject mem_objects, jint mem_objects_position, jint num_events_in_wait_list, jobject event_wait_list, jint event_wait_list_position, jobject event, jint event_position, jlong function_pointer) {
	const cl_mem *mem_objects_address = ((const cl_mem *)(((char *)(*env)->GetDirectBufferAddress(env, mem_objects)) + mem_objects_position));
	const cl_event *event_wait_list_address = ((const cl_event *)(((char *)safeGetBufferAddress(env, event_wait_list)) + event_wait_list_position));
	cl_event *event_address = ((cl_event *)(((char *)safeGetBufferAddress(env, event)) + event_position));
	clEnqueueAcquireGLObjectsPROC clEnqueueAcquireGLObjects = (clEnqueueAcquireGLObjectsPROC)((intptr_t)function_pointer);
	cl_int __result = clEnqueueAcquireGLObjects((cl_command_queue)(intptr_t)command_queue, num_objects, mem_objects_address, num_events_in_wait_list, event_wait_list_address, event_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10GL_nclEnqueueReleaseGLObjects(JNIEnv *env, jclass clazz, jlong command_queue, jint num_objects, jobject mem_objects, jint mem_objects_position, jint num_events_in_wait_list, jobject event_wait_list, jint event_wait_list_position, jobject event, jint event_position, jlong function_pointer) {
	const cl_mem *mem_objects_address = ((const cl_mem *)(((char *)(*env)->GetDirectBufferAddress(env, mem_objects)) + mem_objects_position));
	const cl_event *event_wait_list_address = ((const cl_event *)(((char *)safeGetBufferAddress(env, event_wait_list)) + event_wait_list_position));
	cl_event *event_address = ((cl_event *)(((char *)safeGetBufferAddress(env, event)) + event_position));
	clEnqueueReleaseGLObjectsPROC clEnqueueReleaseGLObjects = (clEnqueueReleaseGLObjectsPROC)((intptr_t)function_pointer);
	cl_int __result = clEnqueueReleaseGLObjects((cl_command_queue)(intptr_t)command_queue, num_objects, mem_objects_address, num_events_in_wait_list, event_wait_list_address, event_address);
	return __result;
}

