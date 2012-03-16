/* MACHINE GENERATED FILE, DO NOT EDIT */

#include <jni.h>
#include "extgl.h"

typedef void (APIENTRY *glDebugMessageControlARBPROC) (GLenum source, GLenum type, GLenum severity, GLsizei count, const GLuint * ids, GLboolean enabled);
typedef void (APIENTRY *glDebugMessageInsertARBPROC) (GLenum source, GLenum type, GLuint id, GLenum severity, GLsizei length, const GLchar * buf);
typedef void (APIENTRY *glDebugMessageCallbackARBPROC) (GLDEBUGPROCARB callback, GLvoid * userParam);
typedef GLuint (APIENTRY *glGetDebugMessageLogARBPROC) (GLuint count, GLsizei logSize, GLenum * sources, GLenum * types, GLuint * ids, GLenum * severities, GLsizei * lengths, GLchar * messageLog);

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBDebugOutput_nglDebugMessageControlARB(JNIEnv *env, jclass clazz, jint source, jint type, jint severity, jint count, jobject ids, jint ids_position, jboolean enabled, jlong function_pointer) {
	const GLuint *ids_address = ((const GLuint *)safeGetBufferAddress(env, ids)) + ids_position;
	glDebugMessageControlARBPROC glDebugMessageControlARB = (glDebugMessageControlARBPROC)((intptr_t)function_pointer);
	glDebugMessageControlARB(source, type, severity, count, ids_address, enabled);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBDebugOutput_nglDebugMessageInsertARB(JNIEnv *env, jclass clazz, jint source, jint type, jint id, jint severity, jint length, jobject buf, jint buf_position, jlong function_pointer) {
	const GLchar *buf_address = ((const GLchar *)(*env)->GetDirectBufferAddress(env, buf)) + buf_position;
	glDebugMessageInsertARBPROC glDebugMessageInsertARB = (glDebugMessageInsertARBPROC)((intptr_t)function_pointer);
	glDebugMessageInsertARB(source, type, id, severity, length, buf_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBDebugOutput_nglDebugMessageCallbackARB(JNIEnv *env, jclass clazz, jlong callback, jlong userParam, jlong function_pointer) {
	glDebugMessageCallbackARBPROC glDebugMessageCallbackARB = (glDebugMessageCallbackARBPROC)((intptr_t)function_pointer);
	glDebugMessageCallbackARB((GLDEBUGPROCARB)(intptr_t)callback, (GLvoid *)(intptr_t)userParam);
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opengl_ARBDebugOutput_nglGetDebugMessageLogARB(JNIEnv *env, jclass clazz, jint count, jint logSize, jobject sources, jint sources_position, jobject types, jint types_position, jobject ids, jint ids_position, jobject severities, jint severities_position, jobject lengths, jint lengths_position, jobject messageLog, jint messageLog_position, jlong function_pointer) {
	GLenum *sources_address = ((GLenum *)safeGetBufferAddress(env, sources)) + sources_position;
	GLenum *types_address = ((GLenum *)safeGetBufferAddress(env, types)) + types_position;
	GLuint *ids_address = ((GLuint *)safeGetBufferAddress(env, ids)) + ids_position;
	GLenum *severities_address = ((GLenum *)safeGetBufferAddress(env, severities)) + severities_position;
	GLsizei *lengths_address = ((GLsizei *)safeGetBufferAddress(env, lengths)) + lengths_position;
	GLchar *messageLog_address = ((GLchar *)safeGetBufferAddress(env, messageLog)) + messageLog_position;
	glGetDebugMessageLogARBPROC glGetDebugMessageLogARB = (glGetDebugMessageLogARBPROC)((intptr_t)function_pointer);
	GLuint __result = glGetDebugMessageLogARB(count, logSize, sources_address, types_address, ids_address, severities_address, lengths_address, messageLog_address);
	return __result;
}

