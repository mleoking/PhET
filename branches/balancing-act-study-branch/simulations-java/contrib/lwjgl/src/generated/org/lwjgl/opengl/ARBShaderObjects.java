/* MACHINE GENERATED FILE, DO NOT EDIT */

package org.lwjgl.opengl;

import org.lwjgl.*;
import java.nio.*;

public final class ARBShaderObjects {

	/**
	 * Accepted by the &lt;pname&gt; argument of GetHandleARB: 
	 */
	public static final int GL_PROGRAM_OBJECT_ARB = 0x8B40;

	/**
	 * Accepted by the &lt;pname&gt; parameter of GetObjectParameter{fi}vARB: 
	 */
	public static final int GL_OBJECT_TYPE_ARB = 0x8B4E,
		GL_OBJECT_SUBTYPE_ARB = 0x8B4F,
		GL_OBJECT_DELETE_STATUS_ARB = 0x8B80,
		GL_OBJECT_COMPILE_STATUS_ARB = 0x8B81,
		GL_OBJECT_LINK_STATUS_ARB = 0x8B82,
		GL_OBJECT_VALIDATE_STATUS_ARB = 0x8B83,
		GL_OBJECT_INFO_LOG_LENGTH_ARB = 0x8B84,
		GL_OBJECT_ATTACHED_OBJECTS_ARB = 0x8B85,
		GL_OBJECT_ACTIVE_UNIFORMS_ARB = 0x8B86,
		GL_OBJECT_ACTIVE_UNIFORM_MAX_LENGTH_ARB = 0x8B87,
		GL_OBJECT_SHADER_SOURCE_LENGTH_ARB = 0x8B88;

	/**
	 * Returned by the &lt;params&gt; parameter of GetObjectParameter{fi}vARB: 
	 */
	public static final int GL_SHADER_OBJECT_ARB = 0x8B48;

	/**
	 * Returned by the &lt;type&gt; parameter of GetActiveUniformARB: 
	 */
	public static final int GL_FLOAT = 0x1406,
		GL_FLOAT_VEC2_ARB = 0x8B50,
		GL_FLOAT_VEC3_ARB = 0x8B51,
		GL_FLOAT_VEC4_ARB = 0x8B52,
		GL_INT = 0x1404,
		GL_INT_VEC2_ARB = 0x8B53,
		GL_INT_VEC3_ARB = 0x8B54,
		GL_INT_VEC4_ARB = 0x8B55,
		GL_BOOL_ARB = 0x8B56,
		GL_BOOL_VEC2_ARB = 0x8B57,
		GL_BOOL_VEC3_ARB = 0x8B58,
		GL_BOOL_VEC4_ARB = 0x8B59,
		GL_FLOAT_MAT2_ARB = 0x8B5A,
		GL_FLOAT_MAT3_ARB = 0x8B5B,
		GL_FLOAT_MAT4_ARB = 0x8B5C,
		GL_SAMPLER_1D_ARB = 0x8B5D,
		GL_SAMPLER_2D_ARB = 0x8B5E,
		GL_SAMPLER_3D_ARB = 0x8B5F,
		GL_SAMPLER_CUBE_ARB = 0x8B60,
		GL_SAMPLER_1D_SHADOW_ARB = 0x8B61,
		GL_SAMPLER_2D_SHADOW_ARB = 0x8B62,
		GL_SAMPLER_2D_RECT_ARB = 0x8B63,
		GL_SAMPLER_2D_RECT_SHADOW_ARB = 0x8B64;

	private ARBShaderObjects() {}

	public static void glDeleteObjectARB(int obj) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glDeleteObjectARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglDeleteObjectARB(obj, function_pointer);
	}
	static native void nglDeleteObjectARB(int obj, long function_pointer);

	public static int glGetHandleARB(int pname) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetHandleARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		int __result = nglGetHandleARB(pname, function_pointer);
		return __result;
	}
	static native int nglGetHandleARB(int pname, long function_pointer);

	public static void glDetachObjectARB(int containerObj, int attachedObj) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glDetachObjectARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglDetachObjectARB(containerObj, attachedObj, function_pointer);
	}
	static native void nglDetachObjectARB(int containerObj, int attachedObj, long function_pointer);

	public static int glCreateShaderObjectARB(int shaderType) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glCreateShaderObjectARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		int __result = nglCreateShaderObjectARB(shaderType, function_pointer);
		return __result;
	}
	static native int nglCreateShaderObjectARB(int shaderType, long function_pointer);

	/**
	 *  The ARB_shader_objects extension allows multiple, optionally null-terminated, source strings to define a shader program.
	 *  <p/>
	 *  This method uses just a single string, that should NOT be null-terminated.
	 */
	public static void glShaderSourceARB(int shader, ByteBuffer string) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glShaderSourceARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(string);
		nglShaderSourceARB(shader, 1, string, string.position(), string.remaining(), function_pointer);
	}
	static native void nglShaderSourceARB(int shader, int count, ByteBuffer string, int string_position, int string_length, long function_pointer);

	/** Overloads glShaderSourceARB. */
	public static void glShaderSourceARB(int shader, CharSequence string) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glShaderSourceARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglShaderSourceARB(shader, 1, APIUtil.getBuffer(string), 0, string.length(), function_pointer);
	}

	/** Overloads glShaderSourceARB. */
	public static void glShaderSourceARB(int shader, CharSequence[] strings) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glShaderSourceARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkArray(strings);
		nglShaderSourceARB3(shader, strings.length, APIUtil.getBuffer(strings), 0, APIUtil.getLengths(strings), 0, function_pointer);
	}
	static native void nglShaderSourceARB3(int shader, int count, ByteBuffer strings, int strings_position, IntBuffer length, int length_position, long function_pointer);

	public static void glCompileShaderARB(int shaderObj) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glCompileShaderARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglCompileShaderARB(shaderObj, function_pointer);
	}
	static native void nglCompileShaderARB(int shaderObj, long function_pointer);

	public static int glCreateProgramObjectARB() {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glCreateProgramObjectARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		int __result = nglCreateProgramObjectARB(function_pointer);
		return __result;
	}
	static native int nglCreateProgramObjectARB(long function_pointer);

	public static void glAttachObjectARB(int containerObj, int obj) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glAttachObjectARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglAttachObjectARB(containerObj, obj, function_pointer);
	}
	static native void nglAttachObjectARB(int containerObj, int obj, long function_pointer);

	public static void glLinkProgramARB(int programObj) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glLinkProgramARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglLinkProgramARB(programObj, function_pointer);
	}
	static native void nglLinkProgramARB(int programObj, long function_pointer);

	public static void glUseProgramObjectARB(int programObj) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUseProgramObjectARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglUseProgramObjectARB(programObj, function_pointer);
	}
	static native void nglUseProgramObjectARB(int programObj, long function_pointer);

	public static void glValidateProgramARB(int programObj) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glValidateProgramARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglValidateProgramARB(programObj, function_pointer);
	}
	static native void nglValidateProgramARB(int programObj, long function_pointer);

	public static void glUniform1fARB(int location, float v0) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniform1fARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglUniform1fARB(location, v0, function_pointer);
	}
	static native void nglUniform1fARB(int location, float v0, long function_pointer);

	public static void glUniform2fARB(int location, float v0, float v1) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniform2fARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglUniform2fARB(location, v0, v1, function_pointer);
	}
	static native void nglUniform2fARB(int location, float v0, float v1, long function_pointer);

	public static void glUniform3fARB(int location, float v0, float v1, float v2) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniform3fARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglUniform3fARB(location, v0, v1, v2, function_pointer);
	}
	static native void nglUniform3fARB(int location, float v0, float v1, float v2, long function_pointer);

	public static void glUniform4fARB(int location, float v0, float v1, float v2, float v3) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniform4fARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglUniform4fARB(location, v0, v1, v2, v3, function_pointer);
	}
	static native void nglUniform4fARB(int location, float v0, float v1, float v2, float v3, long function_pointer);

	public static void glUniform1iARB(int location, int v0) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniform1iARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglUniform1iARB(location, v0, function_pointer);
	}
	static native void nglUniform1iARB(int location, int v0, long function_pointer);

	public static void glUniform2iARB(int location, int v0, int v1) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniform2iARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglUniform2iARB(location, v0, v1, function_pointer);
	}
	static native void nglUniform2iARB(int location, int v0, int v1, long function_pointer);

	public static void glUniform3iARB(int location, int v0, int v1, int v2) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniform3iARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglUniform3iARB(location, v0, v1, v2, function_pointer);
	}
	static native void nglUniform3iARB(int location, int v0, int v1, int v2, long function_pointer);

	public static void glUniform4iARB(int location, int v0, int v1, int v2, int v3) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniform4iARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglUniform4iARB(location, v0, v1, v2, v3, function_pointer);
	}
	static native void nglUniform4iARB(int location, int v0, int v1, int v2, int v3, long function_pointer);

	public static void glUniform1ARB(int location, FloatBuffer values) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniform1fvARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(values);
		nglUniform1fvARB(location, values.remaining(), values, values.position(), function_pointer);
	}
	static native void nglUniform1fvARB(int location, int values_count, FloatBuffer values, int values_position, long function_pointer);

	public static void glUniform2ARB(int location, FloatBuffer values) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniform2fvARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(values);
		nglUniform2fvARB(location, values.remaining() >> 1, values, values.position(), function_pointer);
	}
	static native void nglUniform2fvARB(int location, int values_count, FloatBuffer values, int values_position, long function_pointer);

	public static void glUniform3ARB(int location, FloatBuffer values) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniform3fvARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(values);
		nglUniform3fvARB(location, values.remaining() / 3, values, values.position(), function_pointer);
	}
	static native void nglUniform3fvARB(int location, int values_count, FloatBuffer values, int values_position, long function_pointer);

	public static void glUniform4ARB(int location, FloatBuffer values) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniform4fvARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(values);
		nglUniform4fvARB(location, values.remaining() >> 2, values, values.position(), function_pointer);
	}
	static native void nglUniform4fvARB(int location, int values_count, FloatBuffer values, int values_position, long function_pointer);

	public static void glUniform1ARB(int location, IntBuffer values) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniform1ivARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(values);
		nglUniform1ivARB(location, values.remaining(), values, values.position(), function_pointer);
	}
	static native void nglUniform1ivARB(int location, int values_count, IntBuffer values, int values_position, long function_pointer);

	public static void glUniform2ARB(int location, IntBuffer values) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniform2ivARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(values);
		nglUniform2ivARB(location, values.remaining() >> 1, values, values.position(), function_pointer);
	}
	static native void nglUniform2ivARB(int location, int values_count, IntBuffer values, int values_position, long function_pointer);

	public static void glUniform3ARB(int location, IntBuffer values) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniform3ivARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(values);
		nglUniform3ivARB(location, values.remaining() / 3, values, values.position(), function_pointer);
	}
	static native void nglUniform3ivARB(int location, int values_count, IntBuffer values, int values_position, long function_pointer);

	public static void glUniform4ARB(int location, IntBuffer values) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniform4ivARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(values);
		nglUniform4ivARB(location, values.remaining() >> 2, values, values.position(), function_pointer);
	}
	static native void nglUniform4ivARB(int location, int values_count, IntBuffer values, int values_position, long function_pointer);

	public static void glUniformMatrix2ARB(int location, boolean transpose, FloatBuffer matrices) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniformMatrix2fvARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(matrices);
		nglUniformMatrix2fvARB(location, matrices.remaining() >> 2, transpose, matrices, matrices.position(), function_pointer);
	}
	static native void nglUniformMatrix2fvARB(int location, int matrices_count, boolean transpose, FloatBuffer matrices, int matrices_position, long function_pointer);

	public static void glUniformMatrix3ARB(int location, boolean transpose, FloatBuffer matrices) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniformMatrix3fvARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(matrices);
		nglUniformMatrix3fvARB(location, matrices.remaining() / (3 * 3), transpose, matrices, matrices.position(), function_pointer);
	}
	static native void nglUniformMatrix3fvARB(int location, int matrices_count, boolean transpose, FloatBuffer matrices, int matrices_position, long function_pointer);

	public static void glUniformMatrix4ARB(int location, boolean transpose, FloatBuffer matrices) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glUniformMatrix4fvARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(matrices);
		nglUniformMatrix4fvARB(location, matrices.remaining() >> 4, transpose, matrices, matrices.position(), function_pointer);
	}
	static native void nglUniformMatrix4fvARB(int location, int matrices_count, boolean transpose, FloatBuffer matrices, int matrices_position, long function_pointer);

	public static void glGetObjectParameterARB(int obj, int pname, FloatBuffer params) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetObjectParameterfvARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(params);
		nglGetObjectParameterfvARB(obj, pname, params, params.position(), function_pointer);
	}
	static native void nglGetObjectParameterfvARB(int obj, int pname, FloatBuffer params, int params_position, long function_pointer);

	/** Overloads glGetObjectParameterfvARB. */
	public static float glGetObjectParameterfARB(int obj, int pname) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetObjectParameterfvARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		FloatBuffer params = APIUtil.getBufferFloat();
		nglGetObjectParameterfvARB(obj, pname, params, params.position(), function_pointer);
		return params.get(0);
	}

	public static void glGetObjectParameterARB(int obj, int pname, IntBuffer params) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetObjectParameterivARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(params);
		nglGetObjectParameterivARB(obj, pname, params, params.position(), function_pointer);
	}
	static native void nglGetObjectParameterivARB(int obj, int pname, IntBuffer params, int params_position, long function_pointer);

	/** Overloads glGetObjectParameterivARB. */
	public static int glGetObjectParameteriARB(int obj, int pname) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetObjectParameterivARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		IntBuffer params = APIUtil.getBufferInt();
		nglGetObjectParameterivARB(obj, pname, params, params.position(), function_pointer);
		return params.get(0);
	}

	public static void glGetInfoLogARB(int obj, IntBuffer length, ByteBuffer infoLog) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetInfoLogARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		if (length != null)
			BufferChecks.checkBuffer(length, 1);
		BufferChecks.checkDirect(infoLog);
		nglGetInfoLogARB(obj, infoLog.remaining(), length, length != null ? length.position() : 0, infoLog, infoLog.position(), function_pointer);
	}
	static native void nglGetInfoLogARB(int obj, int infoLog_maxLength, IntBuffer length, int length_position, ByteBuffer infoLog, int infoLog_position, long function_pointer);

	/** Overloads glGetInfoLogARB. */
	public static String glGetInfoLogARB(int obj, int maxLength) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetInfoLogARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		IntBuffer infoLog_length = APIUtil.getLengths();
		ByteBuffer infoLog = APIUtil.getBufferByte(maxLength);
		nglGetInfoLogARB(obj, maxLength, infoLog_length, 0, infoLog, infoLog.position(), function_pointer);
		infoLog.limit(infoLog_length.get(0));
		return APIUtil.getString(infoLog);
	}

	public static void glGetAttachedObjectsARB(int containerObj, IntBuffer count, IntBuffer obj) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetAttachedObjectsARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		if (count != null)
			BufferChecks.checkBuffer(count, 1);
		BufferChecks.checkDirect(obj);
		nglGetAttachedObjectsARB(containerObj, obj.remaining(), count, count != null ? count.position() : 0, obj, obj.position(), function_pointer);
	}
	static native void nglGetAttachedObjectsARB(int containerObj, int obj_maxCount, IntBuffer count, int count_position, IntBuffer obj, int obj_position, long function_pointer);

	/**
	 *  Returns the location of the uniform with the specified name. The ByteBuffer should contain the uniform name as a <b>null-terminated</b> string.
	 * <p>
	 *  @param programObj
	 *  @param name
	 */
	public static int glGetUniformLocationARB(int programObj, ByteBuffer name) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetUniformLocationARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(name);
		BufferChecks.checkNullTerminated(name);
		int __result = nglGetUniformLocationARB(programObj, name, name.position(), function_pointer);
		return __result;
	}
	static native int nglGetUniformLocationARB(int programObj, ByteBuffer name, int name_position, long function_pointer);

	/** Overloads glGetUniformLocationARB. */
	public static int glGetUniformLocationARB(int programObj, CharSequence name) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetUniformLocationARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		int __result = nglGetUniformLocationARB(programObj, APIUtil.getBufferNT(name), 0, function_pointer);
		return __result;
	}

	public static void glGetActiveUniformARB(int programObj, int index, IntBuffer length, IntBuffer size, IntBuffer type, ByteBuffer name) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetActiveUniformARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		if (length != null)
			BufferChecks.checkBuffer(length, 1);
		BufferChecks.checkBuffer(size, 1);
		BufferChecks.checkBuffer(type, 1);
		BufferChecks.checkDirect(name);
		nglGetActiveUniformARB(programObj, index, name.remaining(), length, length != null ? length.position() : 0, size, size.position(), type, type.position(), name, name.position(), function_pointer);
	}
	static native void nglGetActiveUniformARB(int programObj, int index, int name_maxLength, IntBuffer length, int length_position, IntBuffer size, int size_position, IntBuffer type, int type_position, ByteBuffer name, int name_position, long function_pointer);

	/**
	 * Overloads glGetActiveUniformARB.
	 * <p>
	 * Overloads glGetActiveUniformARB. This version returns both size and type in the sizeType buffer (at .position() and .position() + 1). 
	 */
	public static String glGetActiveUniformARB(int programObj, int index, int maxLength, IntBuffer sizeType) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetActiveUniformARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkBuffer(sizeType, 2);
		IntBuffer name_length = APIUtil.getLengths();
		ByteBuffer name = APIUtil.getBufferByte(maxLength);
		nglGetActiveUniformARB(programObj, index, maxLength, name_length, 0, sizeType, sizeType.position(), sizeType, sizeType.position() + 1, name, name.position(), function_pointer);
		name.limit(name_length.get(0));
		return APIUtil.getString(name);
	}

	/**
	 * Overloads glGetActiveUniformARB.
	 * <p>
	 * Overloads glGetActiveUniformARB. This version returns only the uniform name. 
	 */
	public static String glGetActiveUniformARB(int programObj, int index, int maxLength) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetActiveUniformARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		IntBuffer name_length = APIUtil.getLengths();
		ByteBuffer name = APIUtil.getBufferByte(maxLength);
		nglGetActiveUniformARB(programObj, index, maxLength, name_length, 0, APIUtil.getBufferInt(), 0, APIUtil.getBufferInt(), 1, name, name.position(), function_pointer);
		name.limit(name_length.get(0));
		return APIUtil.getString(name);
	}

	/**
	 * Overloads glGetActiveUniformARB.
	 * <p>
	 * Overloads glGetActiveUniformARB. This version returns only the uniform size. 
	 */
	public static int glGetActiveUniformSizeARB(int programObj, int index) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetActiveUniformARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		IntBuffer size = APIUtil.getBufferInt();
		nglGetActiveUniformARB(programObj, index, 0, null, 0, size, size.position(), size, 1, APIUtil.getBufferByte(0), 0, function_pointer);
		return size.get(0);
	}

	/**
	 * Overloads glGetActiveUniformARB.
	 * <p>
	 * Overloads glGetActiveUniformARB. This version returns only the uniform type. 
	 */
	public static int glGetActiveUniformTypeARB(int programObj, int index) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetActiveUniformARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		IntBuffer type = APIUtil.getBufferInt();
		nglGetActiveUniformARB(programObj, index, 0, null, 0, type, 1, type, type.position(), APIUtil.getBufferByte(0), 0, function_pointer);
		return type.get(0);
	}

	public static void glGetUniformARB(int programObj, int location, FloatBuffer params) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetUniformfvARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(params);
		nglGetUniformfvARB(programObj, location, params, params.position(), function_pointer);
	}
	static native void nglGetUniformfvARB(int programObj, int location, FloatBuffer params, int params_position, long function_pointer);

	public static void glGetUniformARB(int programObj, int location, IntBuffer params) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetUniformivARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(params);
		nglGetUniformivARB(programObj, location, params, params.position(), function_pointer);
	}
	static native void nglGetUniformivARB(int programObj, int location, IntBuffer params, int params_position, long function_pointer);

	public static void glGetShaderSourceARB(int obj, IntBuffer length, ByteBuffer source) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetShaderSourceARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		if (length != null)
			BufferChecks.checkBuffer(length, 1);
		BufferChecks.checkDirect(source);
		nglGetShaderSourceARB(obj, source.remaining(), length, length != null ? length.position() : 0, source, source.position(), function_pointer);
	}
	static native void nglGetShaderSourceARB(int obj, int source_maxLength, IntBuffer length, int length_position, ByteBuffer source, int source_position, long function_pointer);

	/** Overloads glGetShaderSourceARB. */
	public static String glGetShaderSourceARB(int obj, int maxLength) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetShaderSourceARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		IntBuffer source_length = APIUtil.getLengths();
		ByteBuffer source = APIUtil.getBufferByte(maxLength);
		nglGetShaderSourceARB(obj, maxLength, source_length, 0, source, source.position(), function_pointer);
		source.limit(source_length.get(0));
		return APIUtil.getString(source);
	}
}
