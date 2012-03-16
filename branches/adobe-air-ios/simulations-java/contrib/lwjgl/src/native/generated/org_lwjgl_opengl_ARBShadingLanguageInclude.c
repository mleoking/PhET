/* MACHINE GENERATED FILE, DO NOT EDIT */

#include <jni.h>
#include "extgl.h"

typedef void (APIENTRY *glNamedStringARBPROC) (GLenum type, GLint namelen, const GLchar * name, GLint stringlen, const GLchar * string);
typedef void (APIENTRY *glDeleteNamedStringARBPROC) (GLint namelen, const GLchar * name);
typedef void (APIENTRY *glCompileShaderIncludeARBPROC) (GLuint shader, GLsizei count, const GLchar ** path, const GLint * length);
typedef GLboolean (APIENTRY *glIsNamedStringARBPROC) (GLint namelen, const GLchar * name);
typedef void (APIENTRY *glGetNamedStringARBPROC) (GLint namelen, const GLchar * name, GLsizei bufSize, GLint * stringlen, GLchar * string);
typedef void (APIENTRY *glGetNamedStringivARBPROC) (GLint namelen, const GLchar * name, GLenum pname, GLint * params);

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBShadingLanguageInclude_nglNamedStringARB(JNIEnv *env, jclass clazz, jint type, jint namelen, jobject name, jint name_position, jint stringlen, jobject string, jint string_position, jlong function_pointer) {
	const GLchar *name_address = ((const GLchar *)(*env)->GetDirectBufferAddress(env, name)) + name_position;
	const GLchar *string_address = ((const GLchar *)(*env)->GetDirectBufferAddress(env, string)) + string_position;
	glNamedStringARBPROC glNamedStringARB = (glNamedStringARBPROC)((intptr_t)function_pointer);
	glNamedStringARB(type, namelen, name_address, stringlen, string_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBShadingLanguageInclude_nglDeleteNamedStringARB(JNIEnv *env, jclass clazz, jint namelen, jobject name, jint name_position, jlong function_pointer) {
	const GLchar *name_address = ((const GLchar *)(*env)->GetDirectBufferAddress(env, name)) + name_position;
	glDeleteNamedStringARBPROC glDeleteNamedStringARB = (glDeleteNamedStringARBPROC)((intptr_t)function_pointer);
	glDeleteNamedStringARB(namelen, name_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBShadingLanguageInclude_nglCompileShaderIncludeARB(JNIEnv *env, jclass clazz, jint shader, jint count, jobject path, jint path_position, jobject length, jint length_position, jlong function_pointer) {
	const GLchar *path_address = ((const GLchar *)(*env)->GetDirectBufferAddress(env, path)) + path_position;
	unsigned int _str_i;
	GLchar *_str_address;
	GLchar **path_str = (GLchar **) malloc(count * sizeof(GLchar *));
	const GLint *length_address = ((const GLint *)(*env)->GetDirectBufferAddress(env, length)) + length_position;
	glCompileShaderIncludeARBPROC glCompileShaderIncludeARB = (glCompileShaderIncludeARBPROC)((intptr_t)function_pointer);
	_str_i = 0;
	_str_address = (GLchar *)path_address;
	while ( _str_i < count ) {
		path_str[_str_i++] = _str_address;
		_str_address += strlen(_str_address) + 1;
	}
	glCompileShaderIncludeARB(shader, count, (const GLchar **)path_str, length_address);
	free(path_str);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBShadingLanguageInclude_nglCompileShaderIncludeARB2(JNIEnv *env, jclass clazz, jint shader, jint count, jobject path, jint path_position, jobject length, jint length_position, jlong function_pointer) {
	const GLchar *path_address = ((const GLchar *)(*env)->GetDirectBufferAddress(env, path)) + path_position;
	unsigned int _str_i;
	GLchar *_str_address;
	GLchar **path_str = (GLchar **) malloc(count * sizeof(GLchar *));
	const GLint *length_address = ((const GLint *)(*env)->GetDirectBufferAddress(env, length)) + length_position;
	glCompileShaderIncludeARBPROC glCompileShaderIncludeARB = (glCompileShaderIncludeARBPROC)((intptr_t)function_pointer);
	_str_i = 0;
	_str_address = (GLchar *)path_address;
	while ( _str_i < count ) {
		path_str[_str_i] = _str_address;
		_str_address += length_address[_str_i++];
	}
	glCompileShaderIncludeARB(shader, count, (const GLchar **)path_str, length_address);
	free(path_str);
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_opengl_ARBShadingLanguageInclude_nglIsNamedStringARB(JNIEnv *env, jclass clazz, jint namelen, jobject name, jint name_position, jlong function_pointer) {
	const GLchar *name_address = ((const GLchar *)(*env)->GetDirectBufferAddress(env, name)) + name_position;
	glIsNamedStringARBPROC glIsNamedStringARB = (glIsNamedStringARBPROC)((intptr_t)function_pointer);
	GLboolean __result = glIsNamedStringARB(namelen, name_address);
	return __result;
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBShadingLanguageInclude_nglGetNamedStringARB(JNIEnv *env, jclass clazz, jint namelen, jobject name, jint name_position, jint bufSize, jobject stringlen, jint stringlen_position, jobject string, jint string_position, jlong function_pointer) {
	const GLchar *name_address = ((const GLchar *)(*env)->GetDirectBufferAddress(env, name)) + name_position;
	GLint *stringlen_address = ((GLint *)safeGetBufferAddress(env, stringlen)) + stringlen_position;
	GLchar *string_address = ((GLchar *)(*env)->GetDirectBufferAddress(env, string)) + string_position;
	glGetNamedStringARBPROC glGetNamedStringARB = (glGetNamedStringARBPROC)((intptr_t)function_pointer);
	glGetNamedStringARB(namelen, name_address, bufSize, stringlen_address, string_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBShadingLanguageInclude_nglGetNamedStringivARB(JNIEnv *env, jclass clazz, jint namelen, jobject name, jint name_position, jint pname, jobject params, jint params_position, jlong function_pointer) {
	const GLchar *name_address = ((const GLchar *)(*env)->GetDirectBufferAddress(env, name)) + name_position;
	GLint *params_address = ((GLint *)(*env)->GetDirectBufferAddress(env, params)) + params_position;
	glGetNamedStringivARBPROC glGetNamedStringivARB = (glGetNamedStringivARBPROC)((intptr_t)function_pointer);
	glGetNamedStringivARB(namelen, name_address, pname, params_address);
}

