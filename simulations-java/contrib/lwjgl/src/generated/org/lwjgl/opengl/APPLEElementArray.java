/* MACHINE GENERATED FILE, DO NOT EDIT */

package org.lwjgl.opengl;

import org.lwjgl.*;
import java.nio.*;

public final class APPLEElementArray {

	/**
	 *  Accepted by the &lt;array&gt; parameter of EnableClientState and
	 *  DisableClientState and the &lt;value&gt; parameter of IsEnabled:
	 */
	public static final int GL_ELEMENT_ARRAY_APPLE = 0x8768;

	/**
	 *  Accepted by the &lt;value&gt; parameter of GetBooleanv, GetIntegerv,
	 *  GetFloatv, and GetDoublev:
	 */
	public static final int GL_ELEMENT_ARRAY_TYPE_APPLE = 0x8769;

	/**
	 * Accepted by the &lt;pname&gt; parameter of GetPointerv: 
	 */
	public static final int GL_ELEMENT_ARRAY_POINTER_APPLE = 0x876A;

	private APPLEElementArray() {}

	public static void glElementPointerAPPLE(ByteBuffer pointer) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glElementPointerAPPLE;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(pointer);
		nglElementPointerAPPLE(GL11.GL_UNSIGNED_BYTE, pointer, pointer.position(), function_pointer);
	}
	public static void glElementPointerAPPLE(IntBuffer pointer) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glElementPointerAPPLE;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(pointer);
		nglElementPointerAPPLE(GL11.GL_UNSIGNED_INT, pointer, pointer.position() << 2, function_pointer);
	}
	public static void glElementPointerAPPLE(ShortBuffer pointer) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glElementPointerAPPLE;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(pointer);
		nglElementPointerAPPLE(GL11.GL_UNSIGNED_SHORT, pointer, pointer.position() << 1, function_pointer);
	}
	static native void nglElementPointerAPPLE(int type, Buffer pointer, int pointer_position, long function_pointer);

	public static void glDrawElementArrayAPPLE(int mode, int first, int count) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glDrawElementArrayAPPLE;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglDrawElementArrayAPPLE(mode, first, count, function_pointer);
	}
	static native void nglDrawElementArrayAPPLE(int mode, int first, int count, long function_pointer);

	public static void glDrawRangeElementArrayAPPLE(int mode, int start, int end, int first, int count) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glDrawRangeElementArrayAPPLE;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglDrawRangeElementArrayAPPLE(mode, start, end, first, count, function_pointer);
	}
	static native void nglDrawRangeElementArrayAPPLE(int mode, int start, int end, int first, int count, long function_pointer);

	public static void glMultiDrawElementArrayAPPLE(int mode, IntBuffer first, IntBuffer count) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glMultiDrawElementArrayAPPLE;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(first);
		BufferChecks.checkBuffer(count, first.remaining());
		nglMultiDrawElementArrayAPPLE(mode, first, first.position(), count, count.position(), first.remaining(), function_pointer);
	}
	static native void nglMultiDrawElementArrayAPPLE(int mode, IntBuffer first, int first_position, IntBuffer count, int count_position, int first_primcount, long function_pointer);

	public static void glMultiDrawRangeElementArrayAPPLE(int mode, int start, int end, IntBuffer first, IntBuffer count) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glMultiDrawRangeElementArrayAPPLE;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(first);
		BufferChecks.checkBuffer(count, first.remaining());
		nglMultiDrawRangeElementArrayAPPLE(mode, start, end, first, first.position(), count, count.position(), first.remaining(), function_pointer);
	}
	static native void nglMultiDrawRangeElementArrayAPPLE(int mode, int start, int end, IntBuffer first, int first_position, IntBuffer count, int count_position, int first_primcount, long function_pointer);
}
