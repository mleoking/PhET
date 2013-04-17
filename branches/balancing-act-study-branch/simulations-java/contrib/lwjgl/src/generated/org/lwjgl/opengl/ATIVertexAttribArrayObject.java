/* MACHINE GENERATED FILE, DO NOT EDIT */

package org.lwjgl.opengl;

import org.lwjgl.*;
import java.nio.*;

public final class ATIVertexAttribArrayObject {

	private ATIVertexAttribArrayObject() {}

	public static void glVertexAttribArrayObjectATI(int index, int size, int type, boolean normalized, int stride, int buffer, int offset) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glVertexAttribArrayObjectATI;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglVertexAttribArrayObjectATI(index, size, type, normalized, stride, buffer, offset, function_pointer);
	}
	static native void nglVertexAttribArrayObjectATI(int index, int size, int type, boolean normalized, int stride, int buffer, int offset, long function_pointer);

	public static void glGetVertexAttribArrayObjectATI(int index, int pname, FloatBuffer params) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetVertexAttribArrayObjectfvATI;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkBuffer(params, 4);
		nglGetVertexAttribArrayObjectfvATI(index, pname, params, params.position(), function_pointer);
	}
	static native void nglGetVertexAttribArrayObjectfvATI(int index, int pname, FloatBuffer params, int params_position, long function_pointer);

	public static void glGetVertexAttribArrayObjectATI(int index, int pname, IntBuffer params) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetVertexAttribArrayObjectivATI;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkBuffer(params, 4);
		nglGetVertexAttribArrayObjectivATI(index, pname, params, params.position(), function_pointer);
	}
	static native void nglGetVertexAttribArrayObjectivATI(int index, int pname, IntBuffer params, int params_position, long function_pointer);
}
