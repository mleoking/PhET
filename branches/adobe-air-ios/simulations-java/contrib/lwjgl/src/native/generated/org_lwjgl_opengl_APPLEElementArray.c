/* MACHINE GENERATED FILE, DO NOT EDIT */

#include <jni.h>
#include "extgl.h"

typedef void (APIENTRY *glElementPointerAPPLEPROC) (GLenum type, const GLvoid * pointer);
typedef void (APIENTRY *glDrawElementArrayAPPLEPROC) (GLenum mode, GLint first, GLsizei count);
typedef void (APIENTRY *glDrawRangeElementArrayAPPLEPROC) (GLenum mode, GLuint start, GLuint end, GLint first, GLsizei count);
typedef void (APIENTRY *glMultiDrawElementArrayAPPLEPROC) (GLenum mode, const GLint * first, const GLsizei * count, GLsizei primcount);
typedef void (APIENTRY *glMultiDrawRangeElementArrayAPPLEPROC) (GLenum mode, GLuint start, GLuint end, const GLint * first, const GLsizei * count, GLsizei primcount);

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_APPLEElementArray_nglElementPointerAPPLE(JNIEnv *env, jclass clazz, jint type, jobject pointer, jint pointer_position, jlong function_pointer) {
	const GLvoid *pointer_address = ((const GLvoid *)(((char *)(*env)->GetDirectBufferAddress(env, pointer)) + pointer_position));
	glElementPointerAPPLEPROC glElementPointerAPPLE = (glElementPointerAPPLEPROC)((intptr_t)function_pointer);
	glElementPointerAPPLE(type, pointer_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_APPLEElementArray_nglDrawElementArrayAPPLE(JNIEnv *env, jclass clazz, jint mode, jint first, jint count, jlong function_pointer) {
	glDrawElementArrayAPPLEPROC glDrawElementArrayAPPLE = (glDrawElementArrayAPPLEPROC)((intptr_t)function_pointer);
	glDrawElementArrayAPPLE(mode, first, count);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_APPLEElementArray_nglDrawRangeElementArrayAPPLE(JNIEnv *env, jclass clazz, jint mode, jint start, jint end, jint first, jint count, jlong function_pointer) {
	glDrawRangeElementArrayAPPLEPROC glDrawRangeElementArrayAPPLE = (glDrawRangeElementArrayAPPLEPROC)((intptr_t)function_pointer);
	glDrawRangeElementArrayAPPLE(mode, start, end, first, count);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_APPLEElementArray_nglMultiDrawElementArrayAPPLE(JNIEnv *env, jclass clazz, jint mode, jobject first, jint first_position, jobject count, jint count_position, jint primcount, jlong function_pointer) {
	const GLint *first_address = ((const GLint *)(*env)->GetDirectBufferAddress(env, first)) + first_position;
	const GLsizei *count_address = ((const GLsizei *)(*env)->GetDirectBufferAddress(env, count)) + count_position;
	glMultiDrawElementArrayAPPLEPROC glMultiDrawElementArrayAPPLE = (glMultiDrawElementArrayAPPLEPROC)((intptr_t)function_pointer);
	glMultiDrawElementArrayAPPLE(mode, first_address, count_address, primcount);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_APPLEElementArray_nglMultiDrawRangeElementArrayAPPLE(JNIEnv *env, jclass clazz, jint mode, jint start, jint end, jobject first, jint first_position, jobject count, jint count_position, jint primcount, jlong function_pointer) {
	const GLint *first_address = ((const GLint *)(*env)->GetDirectBufferAddress(env, first)) + first_position;
	const GLsizei *count_address = ((const GLsizei *)(*env)->GetDirectBufferAddress(env, count)) + count_position;
	glMultiDrawRangeElementArrayAPPLEPROC glMultiDrawRangeElementArrayAPPLE = (glMultiDrawRangeElementArrayAPPLEPROC)((intptr_t)function_pointer);
	glMultiDrawRangeElementArrayAPPLE(mode, start, end, first_address, count_address, primcount);
}

