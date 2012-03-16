/* MACHINE GENERATED FILE, DO NOT EDIT */

package org.lwjgl.opencl;

import org.lwjgl.*;
import java.nio.*;
import org.lwjgl.opengl.GLSync;

public final class KHRGLEvent {

	/**
	 * Returned by clGetEventInfo when param_name is CL_EVENT_COMMAND_TYPE: 
	 */
	public static final int CL_COMMAND_GL_FENCE_SYNC_OBJECT_KHR = 0x200D;

	private KHRGLEvent() {}

	public static CLEvent clCreateEventFromGLsyncKHR(CLContext context, GLSync sync, IntBuffer errcode_ret) {
		long function_pointer = CLCapabilities.clCreateEventFromGLsyncKHR;
		BufferChecks.checkFunctionAddress(function_pointer);
		if (errcode_ret != null)
			BufferChecks.checkBuffer(errcode_ret, 1);
		CLEvent __result = new CLEvent(nclCreateEventFromGLsyncKHR(context.getPointer(), sync.getPointer(), errcode_ret, errcode_ret != null ? errcode_ret.position() : 0, function_pointer), context);
		return __result;
	}
	static native long nclCreateEventFromGLsyncKHR(long context, long sync, IntBuffer errcode_ret, int errcode_ret_position, long function_pointer);
}
