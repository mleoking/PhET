/* MACHINE GENERATED FILE, DO NOT EDIT */

package org.lwjgl.opengl;

import org.lwjgl.*;
import java.nio.*;

public final class EXTTimerQuery {

	/**
	 *  Accepted by the &lt;target&gt; parameter of BeginQuery, EndQuery, and
	 *  GetQueryiv:
	 */
	public static final int GL_TIME_ELAPSED_EXT = 0x88BF;

	private EXTTimerQuery() {}

	public static void glGetQueryObjectEXT(int id, int pname, LongBuffer params) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetQueryObjecti64vEXT;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkBuffer(params, 1);
		nglGetQueryObjecti64vEXT(id, pname, params, params.position(), function_pointer);
	}
	static native void nglGetQueryObjecti64vEXT(int id, int pname, LongBuffer params, int params_position, long function_pointer);

	/** Overloads glGetQueryObjecti64vEXT. */
	public static long glGetQueryObjectEXT(int id, int pname) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetQueryObjecti64vEXT;
		BufferChecks.checkFunctionAddress(function_pointer);
		LongBuffer params = APIUtil.getBufferLong();
		nglGetQueryObjecti64vEXT(id, pname, params, params.position(), function_pointer);
		return params.get(0);
	}

	public static void glGetQueryObjectuEXT(int id, int pname, LongBuffer params) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetQueryObjectui64vEXT;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkBuffer(params, 1);
		nglGetQueryObjectui64vEXT(id, pname, params, params.position(), function_pointer);
	}
	static native void nglGetQueryObjectui64vEXT(int id, int pname, LongBuffer params, int params_position, long function_pointer);

	/** Overloads glGetQueryObjectui64vEXT. */
	public static long glGetQueryObjectuEXT(int id, int pname) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetQueryObjectui64vEXT;
		BufferChecks.checkFunctionAddress(function_pointer);
		LongBuffer params = APIUtil.getBufferLong();
		nglGetQueryObjectui64vEXT(id, pname, params, params.position(), function_pointer);
		return params.get(0);
	}
}
