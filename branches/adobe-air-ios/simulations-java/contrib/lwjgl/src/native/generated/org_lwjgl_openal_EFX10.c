/* MACHINE GENERATED FILE, DO NOT EDIT */

#include <jni.h>
#include "extal.h"

typedef ALvoid (ALAPIENTRY *alGenAuxiliaryEffectSlotsPROC) (ALsizei n, ALuint * auxiliaryeffectslots);
typedef ALvoid (ALAPIENTRY *alDeleteAuxiliaryEffectSlotsPROC) (ALsizei n, ALuint * auxiliaryeffectslots);
typedef ALboolean (ALAPIENTRY *alIsAuxiliaryEffectSlotPROC) (ALuint auxiliaryeffectslot);
typedef ALvoid (ALAPIENTRY *alAuxiliaryEffectSlotiPROC) (ALuint auxiliaryeffectslot, ALenum param, ALint value);
typedef ALvoid (ALAPIENTRY *alAuxiliaryEffectSlotivPROC) (ALuint auxiliaryeffectslot, ALenum param, const ALint * values);
typedef ALvoid (ALAPIENTRY *alAuxiliaryEffectSlotfPROC) (ALuint auxiliaryeffectslot, ALenum param, ALfloat value);
typedef ALvoid (ALAPIENTRY *alAuxiliaryEffectSlotfvPROC) (ALuint auxiliaryeffectslot, ALenum param, const ALfloat * values);
typedef ALvoid (ALAPIENTRY *alGetAuxiliaryEffectSlotiPROC) (ALuint auxiliaryeffectslot, ALenum param, ALint* value);
typedef ALvoid (ALAPIENTRY *alGetAuxiliaryEffectSlotivPROC) (ALuint auxiliaryeffectslot, ALenum param, ALint * intdata);
typedef ALvoid (ALAPIENTRY *alGetAuxiliaryEffectSlotfPROC) (ALuint auxiliaryeffectslot, ALenum param, ALfloat* value);
typedef ALvoid (ALAPIENTRY *alGetAuxiliaryEffectSlotfvPROC) (ALuint auxiliaryeffectslot, ALenum param, ALfloat * floatdata);
typedef ALvoid (ALAPIENTRY *alGenEffectsPROC) (ALsizei n, ALuint * effects);
typedef ALvoid (ALAPIENTRY *alDeleteEffectsPROC) (ALsizei n, ALuint * effects);
typedef ALboolean (ALAPIENTRY *alIsEffectPROC) (ALuint effect);
typedef ALvoid (ALAPIENTRY *alEffectiPROC) (ALuint effect, ALenum param, ALint value);
typedef ALvoid (ALAPIENTRY *alEffectivPROC) (ALuint effect, ALenum param, const ALint * values);
typedef ALvoid (ALAPIENTRY *alEffectfPROC) (ALuint effect, ALenum param, ALfloat value);
typedef ALvoid (ALAPIENTRY *alEffectfvPROC) (ALuint effect, ALenum param, const ALfloat * values);
typedef ALvoid (ALAPIENTRY *alGetEffectiPROC) (ALuint effect, ALenum param, ALint* value);
typedef ALvoid (ALAPIENTRY *alGetEffectivPROC) (ALuint effect, ALenum param, ALint * intdata);
typedef ALvoid (ALAPIENTRY *alGetEffectfPROC) (ALuint effect, ALenum param, ALfloat* value);
typedef ALvoid (ALAPIENTRY *alGetEffectfvPROC) (ALuint effect, ALenum param, ALfloat * floatdata);
typedef ALvoid (ALAPIENTRY *alGenFiltersPROC) (ALsizei n, ALuint * filters);
typedef ALvoid (ALAPIENTRY *alDeleteFiltersPROC) (ALsizei n, ALuint * filters);
typedef ALboolean (ALAPIENTRY *alIsFilterPROC) (ALuint filter);
typedef ALvoid (ALAPIENTRY *alFilteriPROC) (ALuint filter, ALenum param, ALint value);
typedef ALvoid (ALAPIENTRY *alFilterivPROC) (ALuint filter, ALenum param, const ALint * values);
typedef ALvoid (ALAPIENTRY *alFilterfPROC) (ALuint filter, ALenum param, ALfloat value);
typedef ALvoid (ALAPIENTRY *alFilterfvPROC) (ALuint filter, ALenum param, const ALfloat * values);
typedef ALvoid (ALAPIENTRY *alGetFilteriPROC) (ALuint filter, ALenum param, ALint* value);
typedef ALvoid (ALAPIENTRY *alGetFilterivPROC) (ALuint filter, ALenum param, ALint * intdata);
typedef ALvoid (ALAPIENTRY *alGetFilterfPROC) (ALuint filter, ALenum param, ALfloat* value);
typedef ALvoid (ALAPIENTRY *alGetFilterfvPROC) (ALuint filter, ALenum param, ALfloat * floatdata);

static alGenAuxiliaryEffectSlotsPROC alGenAuxiliaryEffectSlots;
static alDeleteAuxiliaryEffectSlotsPROC alDeleteAuxiliaryEffectSlots;
static alIsAuxiliaryEffectSlotPROC alIsAuxiliaryEffectSlot;
static alAuxiliaryEffectSlotiPROC alAuxiliaryEffectSloti;
static alAuxiliaryEffectSlotivPROC alAuxiliaryEffectSlotiv;
static alAuxiliaryEffectSlotfPROC alAuxiliaryEffectSlotf;
static alAuxiliaryEffectSlotfvPROC alAuxiliaryEffectSlotfv;
static alGetAuxiliaryEffectSlotiPROC alGetAuxiliaryEffectSloti;
static alGetAuxiliaryEffectSlotivPROC alGetAuxiliaryEffectSlotiv;
static alGetAuxiliaryEffectSlotfPROC alGetAuxiliaryEffectSlotf;
static alGetAuxiliaryEffectSlotfvPROC alGetAuxiliaryEffectSlotfv;
static alGenEffectsPROC alGenEffects;
static alDeleteEffectsPROC alDeleteEffects;
static alIsEffectPROC alIsEffect;
static alEffectiPROC alEffecti;
static alEffectivPROC alEffectiv;
static alEffectfPROC alEffectf;
static alEffectfvPROC alEffectfv;
static alGetEffectiPROC alGetEffecti;
static alGetEffectivPROC alGetEffectiv;
static alGetEffectfPROC alGetEffectf;
static alGetEffectfvPROC alGetEffectfv;
static alGenFiltersPROC alGenFilters;
static alDeleteFiltersPROC alDeleteFilters;
static alIsFilterPROC alIsFilter;
static alFilteriPROC alFilteri;
static alFilterivPROC alFilteriv;
static alFilterfPROC alFilterf;
static alFilterfvPROC alFilterfv;
static alGetFilteriPROC alGetFilteri;
static alGetFilterivPROC alGetFilteriv;
static alGetFilterfPROC alGetFilterf;
static alGetFilterfvPROC alGetFilterfv;

static void JNICALL Java_org_lwjgl_openal_EFX10_nalGenAuxiliaryEffectSlots(JNIEnv *env, jclass clazz, jint n, jobject auxiliaryeffectslots, jint auxiliaryeffectslots_position) {
	ALuint *auxiliaryeffectslots_address = ((ALuint *)(*env)->GetDirectBufferAddress(env, auxiliaryeffectslots)) + auxiliaryeffectslots_position;
	alGenAuxiliaryEffectSlots(n, auxiliaryeffectslots_address);
}

JNIEXPORT jint JNICALL Java_org_lwjgl_openal_EFX10_nalGenAuxiliaryEffectSlots2(JNIEnv *env, jclass clazz, jint n) {
	ALuint __result;
	alGenAuxiliaryEffectSlots(n, &__result);
	return __result;
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalDeleteAuxiliaryEffectSlots(JNIEnv *env, jclass clazz, jint n, jobject auxiliaryeffectslots, jint auxiliaryeffectslots_position) {
	ALuint *auxiliaryeffectslots_address = ((ALuint *)(*env)->GetDirectBufferAddress(env, auxiliaryeffectslots)) + auxiliaryeffectslots_position;
	alDeleteAuxiliaryEffectSlots(n, auxiliaryeffectslots_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_openal_EFX10_nalDeleteAuxiliaryEffectSlots2(JNIEnv *env, jclass clazz, jint n, jint auxiliaryeffectslot) {
	alDeleteAuxiliaryEffectSlots(n, (ALuint*)&auxiliaryeffectslot);
}

static jboolean JNICALL Java_org_lwjgl_openal_EFX10_nalIsAuxiliaryEffectSlot(JNIEnv *env, jclass clazz, jint auxiliaryeffectslot) {
	ALboolean __result = alIsAuxiliaryEffectSlot(auxiliaryeffectslot);
	return __result;
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalAuxiliaryEffectSloti(JNIEnv *env, jclass clazz, jint auxiliaryeffectslot, jint param, jint value) {
	alAuxiliaryEffectSloti(auxiliaryeffectslot, param, value);
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalAuxiliaryEffectSlotiv(JNIEnv *env, jclass clazz, jint auxiliaryeffectslot, jint param, jobject values, jint values_position) {
	const ALint *values_address = ((const ALint *)(*env)->GetDirectBufferAddress(env, values)) + values_position;
	alAuxiliaryEffectSlotiv(auxiliaryeffectslot, param, values_address);
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalAuxiliaryEffectSlotf(JNIEnv *env, jclass clazz, jint auxiliaryeffectslot, jint param, jfloat value) {
	alAuxiliaryEffectSlotf(auxiliaryeffectslot, param, value);
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalAuxiliaryEffectSlotfv(JNIEnv *env, jclass clazz, jint auxiliaryeffectslot, jint param, jobject values, jint values_position) {
	const ALfloat *values_address = ((const ALfloat *)(*env)->GetDirectBufferAddress(env, values)) + values_position;
	alAuxiliaryEffectSlotfv(auxiliaryeffectslot, param, values_address);
}

static jint JNICALL Java_org_lwjgl_openal_EFX10_nalGetAuxiliaryEffectSloti(JNIEnv *env, jclass clazz, jint auxiliaryeffectslot, jint param) {
	ALint __result;
	alGetAuxiliaryEffectSloti(auxiliaryeffectslot, param, &__result);
	return __result;
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalGetAuxiliaryEffectSlotiv(JNIEnv *env, jclass clazz, jint auxiliaryeffectslot, jint param, jobject intdata, jint intdata_position) {
	ALint *intdata_address = ((ALint *)(*env)->GetDirectBufferAddress(env, intdata)) + intdata_position;
	alGetAuxiliaryEffectSlotiv(auxiliaryeffectslot, param, intdata_address);
}

static jfloat JNICALL Java_org_lwjgl_openal_EFX10_nalGetAuxiliaryEffectSlotf(JNIEnv *env, jclass clazz, jint auxiliaryeffectslot, jint param) {
	ALfloat __result;
	alGetAuxiliaryEffectSlotf(auxiliaryeffectslot, param, &__result);
	return __result;
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalGetAuxiliaryEffectSlotfv(JNIEnv *env, jclass clazz, jint auxiliaryeffectslot, jint param, jobject floatdata, jint floatdata_position) {
	ALfloat *floatdata_address = ((ALfloat *)(*env)->GetDirectBufferAddress(env, floatdata)) + floatdata_position;
	alGetAuxiliaryEffectSlotfv(auxiliaryeffectslot, param, floatdata_address);
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalGenEffects(JNIEnv *env, jclass clazz, jint n, jobject effects, jint effects_position) {
	ALuint *effects_address = ((ALuint *)(*env)->GetDirectBufferAddress(env, effects)) + effects_position;
	alGenEffects(n, effects_address);
}

JNIEXPORT jint JNICALL Java_org_lwjgl_openal_EFX10_nalGenEffects2(JNIEnv *env, jclass clazz, jint n) {
	ALuint __result;
	alGenEffects(n, &__result);
	return __result;
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalDeleteEffects(JNIEnv *env, jclass clazz, jint n, jobject effects, jint effects_position) {
	ALuint *effects_address = ((ALuint *)(*env)->GetDirectBufferAddress(env, effects)) + effects_position;
	alDeleteEffects(n, effects_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_openal_EFX10_nalDeleteEffects2(JNIEnv *env, jclass clazz, jint n, jint effect) {
	alDeleteEffects(n, (ALuint*)&effect);
}

static jboolean JNICALL Java_org_lwjgl_openal_EFX10_nalIsEffect(JNIEnv *env, jclass clazz, jint effect) {
	ALboolean __result = alIsEffect(effect);
	return __result;
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalEffecti(JNIEnv *env, jclass clazz, jint effect, jint param, jint value) {
	alEffecti(effect, param, value);
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalEffectiv(JNIEnv *env, jclass clazz, jint effect, jint param, jobject values, jint values_position) {
	const ALint *values_address = ((const ALint *)(*env)->GetDirectBufferAddress(env, values)) + values_position;
	alEffectiv(effect, param, values_address);
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalEffectf(JNIEnv *env, jclass clazz, jint effect, jint param, jfloat value) {
	alEffectf(effect, param, value);
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalEffectfv(JNIEnv *env, jclass clazz, jint effect, jint param, jobject values, jint values_position) {
	const ALfloat *values_address = ((const ALfloat *)(*env)->GetDirectBufferAddress(env, values)) + values_position;
	alEffectfv(effect, param, values_address);
}

static jint JNICALL Java_org_lwjgl_openal_EFX10_nalGetEffecti(JNIEnv *env, jclass clazz, jint effect, jint param) {
	ALint __result;
	alGetEffecti(effect, param, &__result);
	return __result;
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalGetEffectiv(JNIEnv *env, jclass clazz, jint effect, jint param, jobject intdata, jint intdata_position) {
	ALint *intdata_address = ((ALint *)(*env)->GetDirectBufferAddress(env, intdata)) + intdata_position;
	alGetEffectiv(effect, param, intdata_address);
}

static jfloat JNICALL Java_org_lwjgl_openal_EFX10_nalGetEffectf(JNIEnv *env, jclass clazz, jint effect, jint param) {
	ALfloat __result;
	alGetEffectf(effect, param, &__result);
	return __result;
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalGetEffectfv(JNIEnv *env, jclass clazz, jint effect, jint param, jobject floatdata, jint floatdata_position) {
	ALfloat *floatdata_address = ((ALfloat *)(*env)->GetDirectBufferAddress(env, floatdata)) + floatdata_position;
	alGetEffectfv(effect, param, floatdata_address);
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalGenFilters(JNIEnv *env, jclass clazz, jint n, jobject filters, jint filters_position) {
	ALuint *filters_address = ((ALuint *)(*env)->GetDirectBufferAddress(env, filters)) + filters_position;
	alGenFilters(n, filters_address);
}

JNIEXPORT jint JNICALL Java_org_lwjgl_openal_EFX10_nalGenFilters2(JNIEnv *env, jclass clazz, jint n) {
	ALuint __result;
	alGenFilters(n, &__result);
	return __result;
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalDeleteFilters(JNIEnv *env, jclass clazz, jint n, jobject filters, jint filters_position) {
	ALuint *filters_address = ((ALuint *)(*env)->GetDirectBufferAddress(env, filters)) + filters_position;
	alDeleteFilters(n, filters_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_openal_EFX10_nalDeleteFilters2(JNIEnv *env, jclass clazz, jint n, jint filter) {
	alDeleteFilters(n, (ALuint*)&filter);
}

static jboolean JNICALL Java_org_lwjgl_openal_EFX10_nalIsFilter(JNIEnv *env, jclass clazz, jint filter) {
	ALboolean __result = alIsFilter(filter);
	return __result;
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalFilteri(JNIEnv *env, jclass clazz, jint filter, jint param, jint value) {
	alFilteri(filter, param, value);
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalFilteriv(JNIEnv *env, jclass clazz, jint filter, jint param, jobject values, jint values_position) {
	const ALint *values_address = ((const ALint *)(*env)->GetDirectBufferAddress(env, values)) + values_position;
	alFilteriv(filter, param, values_address);
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalFilterf(JNIEnv *env, jclass clazz, jint filter, jint param, jfloat value) {
	alFilterf(filter, param, value);
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalFilterfv(JNIEnv *env, jclass clazz, jint filter, jint param, jobject values, jint values_position) {
	const ALfloat *values_address = ((const ALfloat *)(*env)->GetDirectBufferAddress(env, values)) + values_position;
	alFilterfv(filter, param, values_address);
}

static jint JNICALL Java_org_lwjgl_openal_EFX10_nalGetFilteri(JNIEnv *env, jclass clazz, jint filter, jint param) {
	ALint __result;
	alGetFilteri(filter, param, &__result);
	return __result;
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalGetFilteriv(JNIEnv *env, jclass clazz, jint filter, jint param, jobject intdata, jint intdata_position) {
	ALint *intdata_address = ((ALint *)(*env)->GetDirectBufferAddress(env, intdata)) + intdata_position;
	alGetFilteriv(filter, param, intdata_address);
}

static jfloat JNICALL Java_org_lwjgl_openal_EFX10_nalGetFilterf(JNIEnv *env, jclass clazz, jint filter, jint param) {
	ALfloat __result;
	alGetFilterf(filter, param, &__result);
	return __result;
}

static void JNICALL Java_org_lwjgl_openal_EFX10_nalGetFilterfv(JNIEnv *env, jclass clazz, jint filter, jint param, jobject floatdata, jint floatdata_position) {
	ALfloat *floatdata_address = ((ALfloat *)(*env)->GetDirectBufferAddress(env, floatdata)) + floatdata_position;
	alGetFilterfv(filter, param, floatdata_address);
}

JNIEXPORT void JNICALL Java_org_lwjgl_openal_EFX10_initNativeStubs(JNIEnv *env, jclass clazz) {
	JavaMethodAndExtFunction functions[] = {
		{"nalGenAuxiliaryEffectSlots", "(ILjava/nio/IntBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalGenAuxiliaryEffectSlots, "alGenAuxiliaryEffectSlots", (void *)&alGenAuxiliaryEffectSlots},
		{"nalGenAuxiliaryEffectSlots2", "(I)I", (void *)&Java_org_lwjgl_openal_EFX10_nalGenAuxiliaryEffectSlots2, "alGenAuxiliaryEffectSlots", (void *)&alGenAuxiliaryEffectSlots},
		{"nalDeleteAuxiliaryEffectSlots", "(ILjava/nio/IntBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalDeleteAuxiliaryEffectSlots, "alDeleteAuxiliaryEffectSlots", (void *)&alDeleteAuxiliaryEffectSlots},
		{"nalDeleteAuxiliaryEffectSlots2", "(II)V", (void *)&Java_org_lwjgl_openal_EFX10_nalDeleteAuxiliaryEffectSlots2, "alDeleteAuxiliaryEffectSlots", (void *)&alDeleteAuxiliaryEffectSlots},
		{"nalIsAuxiliaryEffectSlot", "(I)Z", (void *)&Java_org_lwjgl_openal_EFX10_nalIsAuxiliaryEffectSlot, "alIsAuxiliaryEffectSlot", (void *)&alIsAuxiliaryEffectSlot},
		{"nalAuxiliaryEffectSloti", "(III)V", (void *)&Java_org_lwjgl_openal_EFX10_nalAuxiliaryEffectSloti, "alAuxiliaryEffectSloti", (void *)&alAuxiliaryEffectSloti},
		{"nalAuxiliaryEffectSlotiv", "(IILjava/nio/IntBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalAuxiliaryEffectSlotiv, "alAuxiliaryEffectSlotiv", (void *)&alAuxiliaryEffectSlotiv},
		{"nalAuxiliaryEffectSlotf", "(IIF)V", (void *)&Java_org_lwjgl_openal_EFX10_nalAuxiliaryEffectSlotf, "alAuxiliaryEffectSlotf", (void *)&alAuxiliaryEffectSlotf},
		{"nalAuxiliaryEffectSlotfv", "(IILjava/nio/FloatBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalAuxiliaryEffectSlotfv, "alAuxiliaryEffectSlotfv", (void *)&alAuxiliaryEffectSlotfv},
		{"nalGetAuxiliaryEffectSloti", "(II)I", (void *)&Java_org_lwjgl_openal_EFX10_nalGetAuxiliaryEffectSloti, "alGetAuxiliaryEffectSloti", (void *)&alGetAuxiliaryEffectSloti},
		{"nalGetAuxiliaryEffectSlotiv", "(IILjava/nio/IntBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalGetAuxiliaryEffectSlotiv, "alGetAuxiliaryEffectSlotiv", (void *)&alGetAuxiliaryEffectSlotiv},
		{"nalGetAuxiliaryEffectSlotf", "(II)F", (void *)&Java_org_lwjgl_openal_EFX10_nalGetAuxiliaryEffectSlotf, "alGetAuxiliaryEffectSlotf", (void *)&alGetAuxiliaryEffectSlotf},
		{"nalGetAuxiliaryEffectSlotfv", "(IILjava/nio/FloatBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalGetAuxiliaryEffectSlotfv, "alGetAuxiliaryEffectSlotfv", (void *)&alGetAuxiliaryEffectSlotfv},
		{"nalGenEffects", "(ILjava/nio/IntBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalGenEffects, "alGenEffects", (void *)&alGenEffects},
		{"nalGenEffects2", "(I)I", (void *)&Java_org_lwjgl_openal_EFX10_nalGenEffects2, "alGenEffects", (void *)&alGenEffects},
		{"nalDeleteEffects", "(ILjava/nio/IntBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalDeleteEffects, "alDeleteEffects", (void *)&alDeleteEffects},
		{"nalDeleteEffects2", "(II)V", (void *)&Java_org_lwjgl_openal_EFX10_nalDeleteEffects2, "alDeleteEffects", (void *)&alDeleteEffects},
		{"nalIsEffect", "(I)Z", (void *)&Java_org_lwjgl_openal_EFX10_nalIsEffect, "alIsEffect", (void *)&alIsEffect},
		{"nalEffecti", "(III)V", (void *)&Java_org_lwjgl_openal_EFX10_nalEffecti, "alEffecti", (void *)&alEffecti},
		{"nalEffectiv", "(IILjava/nio/IntBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalEffectiv, "alEffectiv", (void *)&alEffectiv},
		{"nalEffectf", "(IIF)V", (void *)&Java_org_lwjgl_openal_EFX10_nalEffectf, "alEffectf", (void *)&alEffectf},
		{"nalEffectfv", "(IILjava/nio/FloatBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalEffectfv, "alEffectfv", (void *)&alEffectfv},
		{"nalGetEffecti", "(II)I", (void *)&Java_org_lwjgl_openal_EFX10_nalGetEffecti, "alGetEffecti", (void *)&alGetEffecti},
		{"nalGetEffectiv", "(IILjava/nio/IntBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalGetEffectiv, "alGetEffectiv", (void *)&alGetEffectiv},
		{"nalGetEffectf", "(II)F", (void *)&Java_org_lwjgl_openal_EFX10_nalGetEffectf, "alGetEffectf", (void *)&alGetEffectf},
		{"nalGetEffectfv", "(IILjava/nio/FloatBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalGetEffectfv, "alGetEffectfv", (void *)&alGetEffectfv},
		{"nalGenFilters", "(ILjava/nio/IntBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalGenFilters, "alGenFilters", (void *)&alGenFilters},
		{"nalGenFilters2", "(I)I", (void *)&Java_org_lwjgl_openal_EFX10_nalGenFilters2, "alGenFilters", (void *)&alGenFilters},
		{"nalDeleteFilters", "(ILjava/nio/IntBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalDeleteFilters, "alDeleteFilters", (void *)&alDeleteFilters},
		{"nalDeleteFilters2", "(II)V", (void *)&Java_org_lwjgl_openal_EFX10_nalDeleteFilters2, "alDeleteFilters", (void *)&alDeleteFilters},
		{"nalIsFilter", "(I)Z", (void *)&Java_org_lwjgl_openal_EFX10_nalIsFilter, "alIsFilter", (void *)&alIsFilter},
		{"nalFilteri", "(III)V", (void *)&Java_org_lwjgl_openal_EFX10_nalFilteri, "alFilteri", (void *)&alFilteri},
		{"nalFilteriv", "(IILjava/nio/IntBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalFilteriv, "alFilteriv", (void *)&alFilteriv},
		{"nalFilterf", "(IIF)V", (void *)&Java_org_lwjgl_openal_EFX10_nalFilterf, "alFilterf", (void *)&alFilterf},
		{"nalFilterfv", "(IILjava/nio/FloatBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalFilterfv, "alFilterfv", (void *)&alFilterfv},
		{"nalGetFilteri", "(II)I", (void *)&Java_org_lwjgl_openal_EFX10_nalGetFilteri, "alGetFilteri", (void *)&alGetFilteri},
		{"nalGetFilteriv", "(IILjava/nio/IntBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalGetFilteriv, "alGetFilteriv", (void *)&alGetFilteriv},
		{"nalGetFilterf", "(II)F", (void *)&Java_org_lwjgl_openal_EFX10_nalGetFilterf, "alGetFilterf", (void *)&alGetFilterf},
		{"nalGetFilterfv", "(IILjava/nio/FloatBuffer;I)V", (void *)&Java_org_lwjgl_openal_EFX10_nalGetFilterfv, "alGetFilterfv", (void *)&alGetFilterfv}
	};
	int num_functions = NUMFUNCTIONS(functions);
	extal_InitializeClass(env, clazz, num_functions, functions);
}
