/* MACHINE GENERATED FILE, DO NOT EDIT */

package org.lwjgl.opengl;

import org.lwjgl.*;
import java.nio.*;

public final class ARBShadingLanguageInclude {

	/**
	 * Accepted by the &lt;type&gt; parameter of NamedStringARB: 
	 */
	public static final int GL_SHADER_INCLUDE_ARB = 0x8DAE;

	/**
	 * Accepted by the &lt;pname&gt; parameter of GetNamedStringivARB: 
	 */
	public static final int GL_NAMED_STRING_LENGTH_ARB = 0x8DE9,
		GL_NAMED_STRING_TYPE_ARB = 0x8DEA;

	private ARBShadingLanguageInclude() {}

	public static void glNamedStringARB(int type, ByteBuffer name, ByteBuffer string) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glNamedStringARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(name);
		BufferChecks.checkDirect(string);
		nglNamedStringARB(type, name.remaining(), name, name.position(), string.remaining(), string, string.position(), function_pointer);
	}
	static native void nglNamedStringARB(int type, int name_namelen, ByteBuffer name, int name_position, int string_stringlen, ByteBuffer string, int string_position, long function_pointer);

	/** Overloads glNamedStringARB. */
	public static void glNamedStringARB(int type, CharSequence name, CharSequence string) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glNamedStringARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglNamedStringARB(type, name.length(), APIUtil.getBuffer(name), 0, string.length(), APIUtil.getBuffer(string, name.length()), name.length(), function_pointer);
	}

	public static void glDeleteNamedStringARB(ByteBuffer name) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glDeleteNamedStringARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(name);
		nglDeleteNamedStringARB(name.remaining(), name, name.position(), function_pointer);
	}
	static native void nglDeleteNamedStringARB(int name_namelen, ByteBuffer name, int name_position, long function_pointer);

	/** Overloads glDeleteNamedStringARB. */
	public static void glDeleteNamedStringARB(CharSequence name) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glDeleteNamedStringARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglDeleteNamedStringARB(name.length(), APIUtil.getBuffer(name), 0, function_pointer);
	}

	public static void glCompileShaderIncludeARB(int shader, int count, ByteBuffer path) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glCompileShaderIncludeARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(path);
		BufferChecks.checkNullTerminated(path, count);
		nglCompileShaderIncludeARB(shader, count, path, path.position(), null, 0, function_pointer);
	}
	static native void nglCompileShaderIncludeARB(int shader, int count, ByteBuffer path, int path_position, IntBuffer length, int length_position, long function_pointer);

	/** Overloads glCompileShaderIncludeARB. */
	public static void glCompileShaderIncludeARB(int shader, CharSequence[] path) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glCompileShaderIncludeARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkArray(path);
		nglCompileShaderIncludeARB2(shader, path.length, APIUtil.getBuffer(path), 0, APIUtil.getLengths(path), 0, function_pointer);
	}
	static native void nglCompileShaderIncludeARB2(int shader, int count, ByteBuffer path, int path_position, IntBuffer length, int length_position, long function_pointer);

	public static boolean glIsNamedStringARB(ByteBuffer name) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glIsNamedStringARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(name);
		boolean __result = nglIsNamedStringARB(name.remaining(), name, name.position(), function_pointer);
		return __result;
	}
	static native boolean nglIsNamedStringARB(int name_namelen, ByteBuffer name, int name_position, long function_pointer);

	/** Overloads glIsNamedStringARB. */
	public static boolean glIsNamedStringARB(CharSequence name) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glIsNamedStringARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		boolean __result = nglIsNamedStringARB(name.length(), APIUtil.getBuffer(name), 0, function_pointer);
		return __result;
	}

	public static void glGetNamedStringARB(ByteBuffer name, IntBuffer stringlen, ByteBuffer string) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetNamedStringARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(name);
		if (stringlen != null)
			BufferChecks.checkBuffer(stringlen, 1);
		BufferChecks.checkDirect(string);
		nglGetNamedStringARB(name.remaining(), name, name.position(), string.remaining(), stringlen, stringlen != null ? stringlen.position() : 0, string, string.position(), function_pointer);
	}
	static native void nglGetNamedStringARB(int name_namelen, ByteBuffer name, int name_position, int string_bufSize, IntBuffer stringlen, int stringlen_position, ByteBuffer string, int string_position, long function_pointer);

	/** Overloads glGetNamedStringARB. */
	public static void glGetNamedStringARB(CharSequence name, IntBuffer stringlen, ByteBuffer string) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetNamedStringARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		if (stringlen != null)
			BufferChecks.checkBuffer(stringlen, 1);
		BufferChecks.checkDirect(string);
		nglGetNamedStringARB(name.length(), APIUtil.getBuffer(name), 0, string.remaining(), stringlen, stringlen != null ? stringlen.position() : 0, string, string.position(), function_pointer);
	}

	/** Overloads glGetNamedStringARB. */
	public static String glGetNamedStringARB(CharSequence name, int bufSize) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetNamedStringARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		IntBuffer string_length = APIUtil.getLengths();
		ByteBuffer string = APIUtil.getBufferByte(bufSize + name.length());
		nglGetNamedStringARB(name.length(), APIUtil.getBuffer(name), 0, bufSize, string_length, 0, string, string.position(), function_pointer);
		string.limit(name.length() + string_length.get(0));
		return APIUtil.getString(string);
	}

	public static void glGetNamedStringARB(ByteBuffer name, int pname, IntBuffer params) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetNamedStringivARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(name);
		BufferChecks.checkBuffer(params, 1);
		nglGetNamedStringivARB(name.remaining(), name, name.position(), pname, params, params.position(), function_pointer);
	}
	static native void nglGetNamedStringivARB(int name_namelen, ByteBuffer name, int name_position, int pname, IntBuffer params, int params_position, long function_pointer);

	/** Overloads glGetNamedStringivARB. */
	public static void glGetNamedStringiARB(CharSequence name, int pname, IntBuffer params) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetNamedStringivARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkBuffer(params, 1);
		nglGetNamedStringivARB(name.length(), APIUtil.getBuffer(name), 0, pname, params, params.position(), function_pointer);
	}

	/** Overloads glGetNamedStringivARB. */
	public static int glGetNamedStringiARB(CharSequence name, int pname) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetNamedStringivARB;
		BufferChecks.checkFunctionAddress(function_pointer);
		IntBuffer params = APIUtil.getBufferInt();
		nglGetNamedStringivARB(name.length(), APIUtil.getBuffer(name), 0, pname, params, params.position(), function_pointer);
		return params.get(0);
	}
}
