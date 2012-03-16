/* MACHINE GENERATED FILE, DO NOT EDIT */

package org.lwjgl.opengl;

import org.lwjgl.*;
import java.nio.*;

public final class NVPointSprite {

	public static final int GL_POINT_SPRITE_NV = 0x8861,
		GL_COORD_REPLACE_NV = 0x8862,
		GL_POINT_SPRITE_R_MODE_NV = 0x8863;

	private NVPointSprite() {}

	public static void glPointParameteriNV(int pname, int param) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glPointParameteriNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglPointParameteriNV(pname, param, function_pointer);
	}
	static native void nglPointParameteriNV(int pname, int param, long function_pointer);

	public static void glPointParameterNV(int pname, IntBuffer params) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glPointParameterivNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkBuffer(params, 4);
		nglPointParameterivNV(pname, params, params.position(), function_pointer);
	}
	static native void nglPointParameterivNV(int pname, IntBuffer params, int params_position, long function_pointer);
}
