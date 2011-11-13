/* MACHINE GENERATED FILE, DO NOT EDIT */

#include <jni.h>
#include "extgl.h"

typedef void (APIENTRY *glDebugMessageEnableAMDPROC) (GLenum category, GLenum severity, GLsizei count, const GLuint * ids, GLboolean enabled);
typedef void (APIENTRY *glDebugMessageInsertAMDPROC) (GLenum category, GLenum severity, GLuint id, GLsizei length, const GLchar * buf);
typedef void (APIENTRY *glDebugMessageCallbackAMDPROC) (GLDEBUGPROCAMD callback, GLvoid * userParam);
typedef GLuint (APIENTRY *glGetDebugMessageLogAMDPROC) (GLuint count, GLsizei logSize, GLenum * categories, GLuint * severities, GLuint * ids, GLsizei * lengths, GLchar * messageLog);

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_AMDDebugOutput_nglDebugMessageEnableAMD(JNIEnv *env, jclass clazz, jint category, jint severity, jint count, jobject ids, jint ids_position, jboolean enabled, jlong function_pointer) {
	const GLuint *ids_address = ((const GLuint *)safeGetBufferAddress(env, ids)) + ids_position;
	glDebugMessageEnableAMDPROC glDebugMessageEnableAMD = (glDebugMessageEnableAMDPROC)((intptr_t)function_pointer);
	glDebugMessageEnableAMD(category, severity, count, ids_address, enabled);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_AMDDebugOutput_nglDebugMessageInsertAMD(JNIEnv *env, jclass clazz, jint category, jint severity, jint id, jint length, jobject buf, jint buf_position, jlong function_pointer) {
	const GLchar *buf_address = ((const GLchar *)(*env)->GetDirectBufferAddress(env, buf)) + buf_position;
	glDebugMessageInsertAMDPROC glDebugMessageInsertAMD = (glDebugMessageInsertAMDPROC)((intptr_t)function_pointer);
	glDebugMessageInsertAMD(category, severity, id, length, buf_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_AMDDebugOutput_nglDebugMessageCallbackAMD(JNIEnv *env, jclass clazz, jlong callback, jlong userParam, jlong function_pointer) {
	glDebugMessageCallbackAMDPROC glDebugMessageCallbackAMD = (glDebugMessageCallbackAMDPROC)((intptr_t)function_pointer);
	glDebugMessageCallbackAMD((GLDEBUGPROCAMD)(intptr_t)callback, (GLvoid *)(intptr_t)userParam);
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opengl_AMDDebugOutput_nglGetDebugMessageLogAMD(JNIEnv *env, jclass clazz, jint count, jint logSize, jobject categories, jint categories_position, jobject severities, jint severities_position, jobject ids, jint ids_position, jobject lengths, jint lengths_position, jobject messageLog, jint messageLog_position, jlong function_pointer) {
	GLenum *categories_address = ((GLenum *)safeGetBufferAddress(env, categories)) + categories_position;
	GLuint *severities_address = ((GLuint *)safeGetBufferAddress(env, severities)) + severities_position;
	GLuint *ids_address = ((GLuint *)safeGetBufferAddress(env, ids)) + ids_position;
	GLsizei *lengths_address = ((GLsizei *)safeGetBufferAddress(env, lengths)) + lengths_position;
	GLchar *messageLog_address = ((GLchar *)safeGetBufferAddress(env, messageLog)) + messageLog_position;
	glGetDebugMessageLogAMDPROC glGetDebugMessageLogAMD = (glGetDebugMessageLogAMDPROC)((intptr_t)function_pointer);
	GLuint __result = glGetDebugMessageLogAMD(count, logSize, categories_address, severities_address, ids_address, lengths_address, messageLog_address);
	return __result;
}

