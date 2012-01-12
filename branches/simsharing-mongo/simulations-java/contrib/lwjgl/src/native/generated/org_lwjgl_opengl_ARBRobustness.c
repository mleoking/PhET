/* MACHINE GENERATED FILE, DO NOT EDIT */

#include <jni.h>
#include "extgl.h"

typedef GLenum (APIENTRY *glGetGraphicsResetStatusARBPROC) ();
typedef void (APIENTRY *glGetnMapdvARBPROC) (GLenum target, GLenum query, GLsizei bufSize, GLdouble * v);
typedef void (APIENTRY *glGetnMapfvARBPROC) (GLenum target, GLenum query, GLsizei bufSize, GLfloat * v);
typedef void (APIENTRY *glGetnMapivARBPROC) (GLenum target, GLenum query, GLsizei bufSize, GLint * v);
typedef void (APIENTRY *glGetnPixelMapfvARBPROC) (GLenum map, GLsizei bufSize, GLfloat * values);
typedef void (APIENTRY *glGetnPixelMapuivARBPROC) (GLenum map, GLsizei bufSize, GLuint * values);
typedef void (APIENTRY *glGetnPixelMapusvARBPROC) (GLenum map, GLsizei bufSize, GLushort * values);
typedef void (APIENTRY *glGetnPolygonStippleARBPROC) (GLsizei bufSize, GLubyte * pattern);
typedef void (APIENTRY *glGetnTexImageARBPROC) (GLenum target, GLint level, GLenum format, GLenum type, GLsizei bufSize, GLvoid * img);
typedef void (APIENTRY *glReadnPixelsARBPROC) (GLint x, GLint y, GLsizei width, GLsizei height, GLenum format, GLenum type, GLsizei bufSize, GLvoid * data);
typedef void (APIENTRY *glGetnColorTableARBPROC) (GLenum target, GLenum format, GLenum type, GLsizei bufSize, GLvoid * table);
typedef void (APIENTRY *glGetnConvolutionFilterARBPROC) (GLenum target, GLenum format, GLenum type, GLsizei bufSize, GLvoid * image);
typedef void (APIENTRY *glGetnSeparableFilterARBPROC) (GLenum target, GLenum format, GLenum type, GLsizei rowBufSize, GLvoid * row, GLsizei columnBufSize, GLvoid * column, GLvoid * span);
typedef void (APIENTRY *glGetnHistogramARBPROC) (GLenum target, GLboolean reset, GLenum format, GLenum type, GLsizei bufSize, GLvoid * values);
typedef void (APIENTRY *glGetnMinmaxARBPROC) (GLenum target, GLboolean reset, GLenum format, GLenum type, GLsizei bufSize, GLvoid * values);
typedef void (APIENTRY *glGetnCompressedTexImageARBPROC) (GLenum target, GLint lod, GLsizei bufSize, GLvoid * img);
typedef void (APIENTRY *glGetnUniformfvARBPROC) (GLuint program, GLint location, GLsizei bufSize, GLfloat * params);
typedef void (APIENTRY *glGetnUniformivARBPROC) (GLuint program, GLint location, GLsizei bufSize, GLint * params);
typedef void (APIENTRY *glGetnUniformuivARBPROC) (GLuint program, GLint location, GLsizei bufSize, GLuint * params);
typedef void (APIENTRY *glGetnUniformdvARBPROC) (GLuint program, GLint location, GLsizei bufSize, GLdouble * params);

JNIEXPORT jint JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetGraphicsResetStatusARB(JNIEnv *env, jclass clazz, jlong function_pointer) {
	glGetGraphicsResetStatusARBPROC glGetGraphicsResetStatusARB = (glGetGraphicsResetStatusARBPROC)((intptr_t)function_pointer);
	GLenum __result = glGetGraphicsResetStatusARB();
	return __result;
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnMapdvARB(JNIEnv *env, jclass clazz, jint target, jint query, jint bufSize, jobject v, jint v_position, jlong function_pointer) {
	GLdouble *v_address = ((GLdouble *)(*env)->GetDirectBufferAddress(env, v)) + v_position;
	glGetnMapdvARBPROC glGetnMapdvARB = (glGetnMapdvARBPROC)((intptr_t)function_pointer);
	glGetnMapdvARB(target, query, bufSize, v_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnMapfvARB(JNIEnv *env, jclass clazz, jint target, jint query, jint bufSize, jobject v, jint v_position, jlong function_pointer) {
	GLfloat *v_address = ((GLfloat *)(*env)->GetDirectBufferAddress(env, v)) + v_position;
	glGetnMapfvARBPROC glGetnMapfvARB = (glGetnMapfvARBPROC)((intptr_t)function_pointer);
	glGetnMapfvARB(target, query, bufSize, v_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnMapivARB(JNIEnv *env, jclass clazz, jint target, jint query, jint bufSize, jobject v, jint v_position, jlong function_pointer) {
	GLint *v_address = ((GLint *)(*env)->GetDirectBufferAddress(env, v)) + v_position;
	glGetnMapivARBPROC glGetnMapivARB = (glGetnMapivARBPROC)((intptr_t)function_pointer);
	glGetnMapivARB(target, query, bufSize, v_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnPixelMapfvARB(JNIEnv *env, jclass clazz, jint map, jint bufSize, jobject values, jint values_position, jlong function_pointer) {
	GLfloat *values_address = ((GLfloat *)(*env)->GetDirectBufferAddress(env, values)) + values_position;
	glGetnPixelMapfvARBPROC glGetnPixelMapfvARB = (glGetnPixelMapfvARBPROC)((intptr_t)function_pointer);
	glGetnPixelMapfvARB(map, bufSize, values_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnPixelMapuivARB(JNIEnv *env, jclass clazz, jint map, jint bufSize, jobject values, jint values_position, jlong function_pointer) {
	GLuint *values_address = ((GLuint *)(*env)->GetDirectBufferAddress(env, values)) + values_position;
	glGetnPixelMapuivARBPROC glGetnPixelMapuivARB = (glGetnPixelMapuivARBPROC)((intptr_t)function_pointer);
	glGetnPixelMapuivARB(map, bufSize, values_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnPixelMapusvARB(JNIEnv *env, jclass clazz, jint map, jint bufSize, jobject values, jint values_position, jlong function_pointer) {
	GLushort *values_address = ((GLushort *)(*env)->GetDirectBufferAddress(env, values)) + values_position;
	glGetnPixelMapusvARBPROC glGetnPixelMapusvARB = (glGetnPixelMapusvARBPROC)((intptr_t)function_pointer);
	glGetnPixelMapusvARB(map, bufSize, values_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnPolygonStippleARB(JNIEnv *env, jclass clazz, jint bufSize, jobject pattern, jint pattern_position, jlong function_pointer) {
	GLubyte *pattern_address = ((GLubyte *)(*env)->GetDirectBufferAddress(env, pattern)) + pattern_position;
	glGetnPolygonStippleARBPROC glGetnPolygonStippleARB = (glGetnPolygonStippleARBPROC)((intptr_t)function_pointer);
	glGetnPolygonStippleARB(bufSize, pattern_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnTexImageARB(JNIEnv *env, jclass clazz, jint target, jint level, jint format, jint type, jint bufSize, jobject img, jint img_position, jlong function_pointer) {
	GLvoid *img_address = ((GLvoid *)(((char *)(*env)->GetDirectBufferAddress(env, img)) + img_position));
	glGetnTexImageARBPROC glGetnTexImageARB = (glGetnTexImageARBPROC)((intptr_t)function_pointer);
	glGetnTexImageARB(target, level, format, type, bufSize, img_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnTexImageARBBO(JNIEnv *env, jclass clazz, jint target, jint level, jint format, jint type, jint bufSize, jlong img_buffer_offset, jlong function_pointer) {
	GLvoid *img_address = ((GLvoid *)offsetToPointer(img_buffer_offset));
	glGetnTexImageARBPROC glGetnTexImageARB = (glGetnTexImageARBPROC)((intptr_t)function_pointer);
	glGetnTexImageARB(target, level, format, type, bufSize, img_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglReadnPixelsARB(JNIEnv *env, jclass clazz, jint x, jint y, jint width, jint height, jint format, jint type, jint bufSize, jobject data, jint data_position, jlong function_pointer) {
	GLvoid *data_address = ((GLvoid *)(((char *)(*env)->GetDirectBufferAddress(env, data)) + data_position));
	glReadnPixelsARBPROC glReadnPixelsARB = (glReadnPixelsARBPROC)((intptr_t)function_pointer);
	glReadnPixelsARB(x, y, width, height, format, type, bufSize, data_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglReadnPixelsARBBO(JNIEnv *env, jclass clazz, jint x, jint y, jint width, jint height, jint format, jint type, jint bufSize, jlong data_buffer_offset, jlong function_pointer) {
	GLvoid *data_address = ((GLvoid *)offsetToPointer(data_buffer_offset));
	glReadnPixelsARBPROC glReadnPixelsARB = (glReadnPixelsARBPROC)((intptr_t)function_pointer);
	glReadnPixelsARB(x, y, width, height, format, type, bufSize, data_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnColorTableARB(JNIEnv *env, jclass clazz, jint target, jint format, jint type, jint bufSize, jobject table, jint table_position, jlong function_pointer) {
	GLvoid *table_address = ((GLvoid *)(((char *)(*env)->GetDirectBufferAddress(env, table)) + table_position));
	glGetnColorTableARBPROC glGetnColorTableARB = (glGetnColorTableARBPROC)((intptr_t)function_pointer);
	glGetnColorTableARB(target, format, type, bufSize, table_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnConvolutionFilterARB(JNIEnv *env, jclass clazz, jint target, jint format, jint type, jint bufSize, jobject image, jint image_position, jlong function_pointer) {
	GLvoid *image_address = ((GLvoid *)(((char *)(*env)->GetDirectBufferAddress(env, image)) + image_position));
	glGetnConvolutionFilterARBPROC glGetnConvolutionFilterARB = (glGetnConvolutionFilterARBPROC)((intptr_t)function_pointer);
	glGetnConvolutionFilterARB(target, format, type, bufSize, image_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnConvolutionFilterARBBO(JNIEnv *env, jclass clazz, jint target, jint format, jint type, jint bufSize, jlong image_buffer_offset, jlong function_pointer) {
	GLvoid *image_address = ((GLvoid *)offsetToPointer(image_buffer_offset));
	glGetnConvolutionFilterARBPROC glGetnConvolutionFilterARB = (glGetnConvolutionFilterARBPROC)((intptr_t)function_pointer);
	glGetnConvolutionFilterARB(target, format, type, bufSize, image_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnSeparableFilterARB(JNIEnv *env, jclass clazz, jint target, jint format, jint type, jint rowBufSize, jobject row, jint row_position, jint columnBufSize, jobject column, jint column_position, jobject span, jint span_position, jlong function_pointer) {
	GLvoid *row_address = ((GLvoid *)(((char *)(*env)->GetDirectBufferAddress(env, row)) + row_position));
	GLvoid *column_address = ((GLvoid *)(((char *)(*env)->GetDirectBufferAddress(env, column)) + column_position));
	GLvoid *span_address = ((GLvoid *)(((char *)(*env)->GetDirectBufferAddress(env, span)) + span_position));
	glGetnSeparableFilterARBPROC glGetnSeparableFilterARB = (glGetnSeparableFilterARBPROC)((intptr_t)function_pointer);
	glGetnSeparableFilterARB(target, format, type, rowBufSize, row_address, columnBufSize, column_address, span_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnSeparableFilterARBBO(JNIEnv *env, jclass clazz, jint target, jint format, jint type, jint rowBufSize, jlong row_buffer_offset, jint columnBufSize, jlong column_buffer_offset, jlong span_buffer_offset, jlong function_pointer) {
	GLvoid *row_address = ((GLvoid *)offsetToPointer(row_buffer_offset));
	GLvoid *column_address = ((GLvoid *)offsetToPointer(column_buffer_offset));
	GLvoid *span_address = ((GLvoid *)offsetToPointer(span_buffer_offset));
	glGetnSeparableFilterARBPROC glGetnSeparableFilterARB = (glGetnSeparableFilterARBPROC)((intptr_t)function_pointer);
	glGetnSeparableFilterARB(target, format, type, rowBufSize, row_address, columnBufSize, column_address, span_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnHistogramARB(JNIEnv *env, jclass clazz, jint target, jboolean reset, jint format, jint type, jint bufSize, jobject values, jint values_position, jlong function_pointer) {
	GLvoid *values_address = ((GLvoid *)(((char *)(*env)->GetDirectBufferAddress(env, values)) + values_position));
	glGetnHistogramARBPROC glGetnHistogramARB = (glGetnHistogramARBPROC)((intptr_t)function_pointer);
	glGetnHistogramARB(target, reset, format, type, bufSize, values_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnHistogramARBBO(JNIEnv *env, jclass clazz, jint target, jboolean reset, jint format, jint type, jint bufSize, jlong values_buffer_offset, jlong function_pointer) {
	GLvoid *values_address = ((GLvoid *)offsetToPointer(values_buffer_offset));
	glGetnHistogramARBPROC glGetnHistogramARB = (glGetnHistogramARBPROC)((intptr_t)function_pointer);
	glGetnHistogramARB(target, reset, format, type, bufSize, values_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnMinmaxARB(JNIEnv *env, jclass clazz, jint target, jboolean reset, jint format, jint type, jint bufSize, jobject values, jint values_position, jlong function_pointer) {
	GLvoid *values_address = ((GLvoid *)(((char *)(*env)->GetDirectBufferAddress(env, values)) + values_position));
	glGetnMinmaxARBPROC glGetnMinmaxARB = (glGetnMinmaxARBPROC)((intptr_t)function_pointer);
	glGetnMinmaxARB(target, reset, format, type, bufSize, values_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnMinmaxARBBO(JNIEnv *env, jclass clazz, jint target, jboolean reset, jint format, jint type, jint bufSize, jlong values_buffer_offset, jlong function_pointer) {
	GLvoid *values_address = ((GLvoid *)offsetToPointer(values_buffer_offset));
	glGetnMinmaxARBPROC glGetnMinmaxARB = (glGetnMinmaxARBPROC)((intptr_t)function_pointer);
	glGetnMinmaxARB(target, reset, format, type, bufSize, values_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnCompressedTexImageARB(JNIEnv *env, jclass clazz, jint target, jint lod, jint bufSize, jobject img, jint img_position, jlong function_pointer) {
	GLvoid *img_address = ((GLvoid *)(((char *)(*env)->GetDirectBufferAddress(env, img)) + img_position));
	glGetnCompressedTexImageARBPROC glGetnCompressedTexImageARB = (glGetnCompressedTexImageARBPROC)((intptr_t)function_pointer);
	glGetnCompressedTexImageARB(target, lod, bufSize, img_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnCompressedTexImageARBBO(JNIEnv *env, jclass clazz, jint target, jint lod, jint bufSize, jlong img_buffer_offset, jlong function_pointer) {
	GLvoid *img_address = ((GLvoid *)offsetToPointer(img_buffer_offset));
	glGetnCompressedTexImageARBPROC glGetnCompressedTexImageARB = (glGetnCompressedTexImageARBPROC)((intptr_t)function_pointer);
	glGetnCompressedTexImageARB(target, lod, bufSize, img_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnUniformfvARB(JNIEnv *env, jclass clazz, jint program, jint location, jint bufSize, jobject params, jint params_position, jlong function_pointer) {
	GLfloat *params_address = ((GLfloat *)(*env)->GetDirectBufferAddress(env, params)) + params_position;
	glGetnUniformfvARBPROC glGetnUniformfvARB = (glGetnUniformfvARBPROC)((intptr_t)function_pointer);
	glGetnUniformfvARB(program, location, bufSize, params_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnUniformivARB(JNIEnv *env, jclass clazz, jint program, jint location, jint bufSize, jobject params, jint params_position, jlong function_pointer) {
	GLint *params_address = ((GLint *)(*env)->GetDirectBufferAddress(env, params)) + params_position;
	glGetnUniformivARBPROC glGetnUniformivARB = (glGetnUniformivARBPROC)((intptr_t)function_pointer);
	glGetnUniformivARB(program, location, bufSize, params_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnUniformuivARB(JNIEnv *env, jclass clazz, jint program, jint location, jint bufSize, jobject params, jint params_position, jlong function_pointer) {
	GLuint *params_address = ((GLuint *)(*env)->GetDirectBufferAddress(env, params)) + params_position;
	glGetnUniformuivARBPROC glGetnUniformuivARB = (glGetnUniformuivARBPROC)((intptr_t)function_pointer);
	glGetnUniformuivARB(program, location, bufSize, params_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_ARBRobustness_nglGetnUniformdvARB(JNIEnv *env, jclass clazz, jint program, jint location, jint bufSize, jobject params, jint params_position, jlong function_pointer) {
	GLdouble *params_address = ((GLdouble *)(*env)->GetDirectBufferAddress(env, params)) + params_position;
	glGetnUniformdvARBPROC glGetnUniformdvARB = (glGetnUniformdvARBPROC)((intptr_t)function_pointer);
	glGetnUniformdvARB(program, location, bufSize, params_address);
}

