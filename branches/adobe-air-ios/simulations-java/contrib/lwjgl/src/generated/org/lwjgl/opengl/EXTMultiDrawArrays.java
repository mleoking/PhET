/* MACHINE GENERATED FILE, DO NOT EDIT */

package org.lwjgl.opengl;

import org.lwjgl.*;
import java.nio.*;

public final class EXTMultiDrawArrays {

	private EXTMultiDrawArrays() {}

	public static void glMultiDrawArraysEXT(int mode, IntBuffer piFirst, IntBuffer piCount) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glMultiDrawArraysEXT;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(piFirst);
		BufferChecks.checkBuffer(piCount, piFirst.remaining());
		nglMultiDrawArraysEXT(mode, piFirst, piFirst.position(), piCount, piCount.position(), piFirst.remaining(), function_pointer);
	}
	static native void nglMultiDrawArraysEXT(int mode, IntBuffer piFirst, int piFirst_position, IntBuffer piCount, int piCount_position, int piFirst_primcount, long function_pointer);
}
