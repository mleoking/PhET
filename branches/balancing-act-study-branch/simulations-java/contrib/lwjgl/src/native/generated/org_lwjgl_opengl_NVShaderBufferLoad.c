/* MACHINE GENERATED FILE, DO NOT EDIT */

#include <jni.h>
#include "extgl.h"

typedef void (APIENTRY *glMakeBufferResidentNVPROC) (GLenum target, GLenum access);
typedef void (APIENTRY *glMakeBufferNonResidentNVPROC) (GLenum target);
typedef GLboolean (APIENTRY *glIsBufferResidentNVPROC) (GLenum target);
typedef void (APIENTRY *glMakeNamedBufferResidentNVPROC) (GLuint buffer, GLenum access);
typedef void (APIENTRY *glMakeNamedBufferNonResidentNVPROC) (GLuint buffer);
typedef GLboolean (APIENTRY *glIsNamedBufferResidentNVPROC) (GLuint buffer);
typedef void (APIENTRY *glGetBufferParameterui64vNVPROC) (GLenum target, GLenum pname, GLuint64EXT * params);
typedef void (APIENTRY *glGetNamedBufferParameterui64vNVPROC) (GLuint buffer, GLenum pname, GLuint64EXT * params);
typedef void (APIENTRY *glGetIntegerui64vNVPROC) (GLenum value, GLuint64EXT * result);
typedef void (APIENTRY *glUniformui64NVPROC) (GLint location, GLuint64EXT value);
typedef void (APIENTRY *glUniformui64vNVPROC) (GLint location, GLsizei count, const GLuint64EXT * value);
typedef void (APIENTRY *glProgramUniformui64NVPROC) (GLuint program, GLint location, GLuint64EXT value);
typedef void (APIENTRY *glProgramUniformui64vNVPROC) (GLuint program, GLint location, GLsizei count, const GLuint64EXT * value);

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_NVShaderBufferLoad_nglMakeBufferResidentNV(JNIEnv *env, jclass clazz, jint target, jint access, jlong function_pointer) {
	glMakeBufferResidentNVPROC glMakeBufferResidentNV = (glMakeBufferResidentNVPROC)((intptr_t)function_pointer);
	glMakeBufferResidentNV(target, access);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_NVShaderBufferLoad_nglMakeBufferNonResidentNV(JNIEnv *env, jclass clazz, jint target, jlong function_pointer) {
	glMakeBufferNonResidentNVPROC glMakeBufferNonResidentNV = (glMakeBufferNonResidentNVPROC)((intptr_t)function_pointer);
	glMakeBufferNonResidentNV(target);
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_opengl_NVShaderBufferLoad_nglIsBufferResidentNV(JNIEnv *env, jclass clazz, jint target, jlong function_pointer) {
	glIsBufferResidentNVPROC glIsBufferResidentNV = (glIsBufferResidentNVPROC)((intptr_t)function_pointer);
	GLboolean __result = glIsBufferResidentNV(target);
	return __result;
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_NVShaderBufferLoad_nglMakeNamedBufferResidentNV(JNIEnv *env, jclass clazz, jint buffer, jint access, jlong function_pointer) {
	glMakeNamedBufferResidentNVPROC glMakeNamedBufferResidentNV = (glMakeNamedBufferResidentNVPROC)((intptr_t)function_pointer);
	glMakeNamedBufferResidentNV(buffer, access);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_NVShaderBufferLoad_nglMakeNamedBufferNonResidentNV(JNIEnv *env, jclass clazz, jint buffer, jlong function_pointer) {
	glMakeNamedBufferNonResidentNVPROC glMakeNamedBufferNonResidentNV = (glMakeNamedBufferNonResidentNVPROC)((intptr_t)function_pointer);
	glMakeNamedBufferNonResidentNV(buffer);
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_opengl_NVShaderBufferLoad_nglIsNamedBufferResidentNV(JNIEnv *env, jclass clazz, jint buffer, jlong function_pointer) {
	glIsNamedBufferResidentNVPROC glIsNamedBufferResidentNV = (glIsNamedBufferResidentNVPROC)((intptr_t)function_pointer);
	GLboolean __result = glIsNamedBufferResidentNV(buffer);
	return __result;
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_NVShaderBufferLoad_nglGetBufferParameterui64vNV(JNIEnv *env, jclass clazz, jint target, jint pname, jobject params, jint params_position, jlong function_pointer) {
	GLuint64EXT *params_address = ((GLuint64EXT *)(*env)->GetDirectBufferAddress(env, params)) + params_position;
	glGetBufferParameterui64vNVPROC glGetBufferParameterui64vNV = (glGetBufferParameterui64vNVPROC)((intptr_t)function_pointer);
	glGetBufferParameterui64vNV(target, pname, params_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_NVShaderBufferLoad_nglGetNamedBufferParameterui64vNV(JNIEnv *env, jclass clazz, jint buffer, jint pname, jobject params, jint params_position, jlong function_pointer) {
	GLuint64EXT *params_address = ((GLuint64EXT *)(*env)->GetDirectBufferAddress(env, params)) + params_position;
	glGetNamedBufferParameterui64vNVPROC glGetNamedBufferParameterui64vNV = (glGetNamedBufferParameterui64vNVPROC)((intptr_t)function_pointer);
	glGetNamedBufferParameterui64vNV(buffer, pname, params_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_NVShaderBufferLoad_nglGetIntegerui64vNV(JNIEnv *env, jclass clazz, jint value, jobject result, jint result_position, jlong function_pointer) {
	GLuint64EXT *result_address = ((GLuint64EXT *)(*env)->GetDirectBufferAddress(env, result)) + result_position;
	glGetIntegerui64vNVPROC glGetIntegerui64vNV = (glGetIntegerui64vNVPROC)((intptr_t)function_pointer);
	glGetIntegerui64vNV(value, result_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_NVShaderBufferLoad_nglUniformui64NV(JNIEnv *env, jclass clazz, jint location, jlong value, jlong function_pointer) {
	glUniformui64NVPROC glUniformui64NV = (glUniformui64NVPROC)((intptr_t)function_pointer);
	glUniformui64NV(location, value);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_NVShaderBufferLoad_nglUniformui64vNV(JNIEnv *env, jclass clazz, jint location, jint count, jobject value, jint value_position, jlong function_pointer) {
	const GLuint64EXT *value_address = ((const GLuint64EXT *)(*env)->GetDirectBufferAddress(env, value)) + value_position;
	glUniformui64vNVPROC glUniformui64vNV = (glUniformui64vNVPROC)((intptr_t)function_pointer);
	glUniformui64vNV(location, count, value_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_NVShaderBufferLoad_nglProgramUniformui64NV(JNIEnv *env, jclass clazz, jint program, jint location, jlong value, jlong function_pointer) {
	glProgramUniformui64NVPROC glProgramUniformui64NV = (glProgramUniformui64NVPROC)((intptr_t)function_pointer);
	glProgramUniformui64NV(program, location, value);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_NVShaderBufferLoad_nglProgramUniformui64vNV(JNIEnv *env, jclass clazz, jint program, jint location, jint count, jobject value, jint value_position, jlong function_pointer) {
	const GLuint64EXT *value_address = ((const GLuint64EXT *)(*env)->GetDirectBufferAddress(env, value)) + value_position;
	glProgramUniformui64vNVPROC glProgramUniformui64vNV = (glProgramUniformui64vNVPROC)((intptr_t)function_pointer);
	glProgramUniformui64vNV(program, location, count, value_address);
}

