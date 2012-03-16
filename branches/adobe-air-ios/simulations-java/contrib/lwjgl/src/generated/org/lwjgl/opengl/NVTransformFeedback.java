/* MACHINE GENERATED FILE, DO NOT EDIT */

package org.lwjgl.opengl;

import org.lwjgl.*;
import java.nio.*;

public final class NVTransformFeedback {

	/**
	 *  Accepted by the &lt;target&gt; parameters of BindBuffer, BufferData,
	 *  BufferSubData, MapBuffer, UnmapBuffer, GetBufferSubData,
	 *  GetBufferPointerv, BindBufferRangeNV, BindBufferOffsetNV and
	 *  BindBufferBaseNV:
	 */
	public static final int GL_TRANSFORM_FEEDBACK_BUFFER_NV = 0x8C8E;

	/**
	 *  Accepted by the &lt;param&gt; parameter of GetIntegerIndexedvEXT and
	 *  GetBooleanIndexedvEXT:
	 */
	public static final int GL_TRANSFORM_FEEDBACK_BUFFER_START_NV = 0x8C84,
		GL_TRANSFORM_FEEDBACK_BUFFER_SIZE_NV = 0x8C85,
		GL_TRANSFORM_FEEDBACK_RECORD_NV = 0x8C86;

	/**
	 *  Accepted by the &lt;param&gt; parameter of GetIntegerIndexedvEXT and
	 *  GetBooleanIndexedvEXT, and by the &lt;pname&gt; parameter of GetBooleanv,
	 *  GetDoublev, GetIntegerv, and GetFloatv:
	 */
	public static final int GL_TRANSFORM_FEEDBACK_BUFFER_BINDING_NV = 0x8C8F;

	/**
	 *  Accepted by the &lt;bufferMode&gt; parameter of TransformFeedbackAttribsNV and
	 *  TransformFeedbackVaryingsNV:
	 */
	public static final int GL_INTERLEAVED_ATTRIBS_NV = 0x8C8C,
		GL_SEPARATE_ATTRIBS_NV = 0x8C8D;

	/**
	 *  Accepted by the &lt;target&gt; parameter of BeginQuery, EndQuery, and
	 *  GetQueryiv:
	 */
	public static final int GL_PRIMITIVES_GENERATED_NV = 0x8C87,
		GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN_NV = 0x8C88;

	/**
	 *  Accepted by the &lt;cap&gt; parameter of Enable, Disable, and IsEnabled, and by
	 *  the &lt;pname&gt; parameter of GetBooleanv, GetIntegerv, GetFloatv, and
	 *  GetDoublev:
	 */
	public static final int GL_RASTERIZER_DISCARD_NV = 0x8C89;

	/**
	 *  Accepted by the &lt;pname&gt; parameter of GetBooleanv, GetDoublev, GetIntegerv,
	 *  and GetFloatv:
	 */
	public static final int GL_MAX_TRANSFORM_FEEDBACK_INTERLEAVED_COMPONENTS_NV = 0x8C8A,
		GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_ATTRIBS_NV = 0x8C8B,
		GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_COMPONENTS_NV = 0x8C80,
		GL_TRANSFORM_FEEDBACK_ATTRIBS_NV = 0x8C7E;

	/**
	 * Accepted by the &lt;pname&gt; parameter of GetProgramiv: 
	 */
	public static final int GL_ACTIVE_VARYINGS_NV = 0x8C81,
		GL_ACTIVE_VARYING_MAX_LENGTH_NV = 0x8C82,
		GL_TRANSFORM_FEEDBACK_VARYINGS_NV = 0x8C83;

	/**
	 *  Accepted by the &lt;pname&gt; parameter of GetBooleanv, GetDoublev, GetIntegerv,
	 *  GetFloatv, and GetProgramiv:
	 */
	public static final int GL_TRANSFORM_FEEDBACK_BUFFER_MODE_NV = 0x8C7F;

	/**
	 * Accepted by the &lt;attribs&gt; parameter of TransformFeedbackAttribsNV: 
	 */
	public static final int GL_BACK_PRIMARY_COLOR_NV = 0x8C77,
		GL_BACK_SECONDARY_COLOR_NV = 0x8C78,
		GL_TEXTURE_COORD_NV = 0x8C79,
		GL_CLIP_DISTANCE_NV = 0x8C7A,
		GL_VERTEX_ID_NV = 0x8C7B,
		GL_PRIMITIVE_ID_NV = 0x8C7C,
		GL_GENERIC_ATTRIB_NV = 0x8C7D,
		GL_LAYER_NV = 0x8DAA;

	private NVTransformFeedback() {}

	public static void glBindBufferRangeNV(int target, int index, int buffer, long offset, long size) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glBindBufferRangeNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglBindBufferRangeNV(target, index, buffer, offset, size, function_pointer);
	}
	static native void nglBindBufferRangeNV(int target, int index, int buffer, long offset, long size, long function_pointer);

	public static void glBindBufferOffsetNV(int target, int index, int buffer, long offset) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glBindBufferOffsetNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglBindBufferOffsetNV(target, index, buffer, offset, function_pointer);
	}
	static native void nglBindBufferOffsetNV(int target, int index, int buffer, long offset, long function_pointer);

	public static void glBindBufferBaseNV(int target, int index, int buffer) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glBindBufferBaseNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglBindBufferBaseNV(target, index, buffer, function_pointer);
	}
	static native void nglBindBufferBaseNV(int target, int index, int buffer, long function_pointer);

	public static void glTransformFeedbackAttribsNV(IntBuffer attribs, int bufferMode) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glTransformFeedbackAttribsNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(attribs);
		nglTransformFeedbackAttribsNV(attribs.remaining(), attribs, attribs.position(), bufferMode, function_pointer);
	}
	static native void nglTransformFeedbackAttribsNV(int attribs_count, IntBuffer attribs, int attribs_position, int bufferMode, long function_pointer);

	public static void glTransformFeedbackVaryingsNV(int program, IntBuffer locations, int bufferMode) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glTransformFeedbackVaryingsNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(locations);
		nglTransformFeedbackVaryingsNV(program, locations.remaining(), locations, locations.position(), bufferMode, function_pointer);
	}
	static native void nglTransformFeedbackVaryingsNV(int program, int locations_count, IntBuffer locations, int locations_position, int bufferMode, long function_pointer);

	public static void glBeginTransformFeedbackNV(int primitiveMode) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glBeginTransformFeedbackNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglBeginTransformFeedbackNV(primitiveMode, function_pointer);
	}
	static native void nglBeginTransformFeedbackNV(int primitiveMode, long function_pointer);

	public static void glEndTransformFeedbackNV() {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glEndTransformFeedbackNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglEndTransformFeedbackNV(function_pointer);
	}
	static native void nglEndTransformFeedbackNV(long function_pointer);

	public static int glGetVaryingLocationNV(int program, ByteBuffer name) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetVaryingLocationNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(name);
		BufferChecks.checkNullTerminated(name);
		int __result = nglGetVaryingLocationNV(program, name, name.position(), function_pointer);
		return __result;
	}
	static native int nglGetVaryingLocationNV(int program, ByteBuffer name, int name_position, long function_pointer);

	/** Overloads glGetVaryingLocationNV. */
	public static int glGetVaryingLocationNV(int program, CharSequence name) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetVaryingLocationNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		int __result = nglGetVaryingLocationNV(program, APIUtil.getBufferNT(name), 0, function_pointer);
		return __result;
	}

	public static void glGetActiveVaryingNV(int program, int index, IntBuffer length, IntBuffer size, IntBuffer type, ByteBuffer name) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetActiveVaryingNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		if (length != null)
			BufferChecks.checkBuffer(length, 1);
		BufferChecks.checkBuffer(size, 1);
		BufferChecks.checkBuffer(type, 1);
		BufferChecks.checkDirect(name);
		nglGetActiveVaryingNV(program, index, name.remaining(), length, length != null ? length.position() : 0, size, size.position(), type, type.position(), name, name.position(), function_pointer);
	}
	static native void nglGetActiveVaryingNV(int program, int index, int name_bufSize, IntBuffer length, int length_position, IntBuffer size, int size_position, IntBuffer type, int type_position, ByteBuffer name, int name_position, long function_pointer);

	/**
	 * Overloads glGetActiveVaryingNV.
	 * <p>
	 * Overloads glGetActiveVaryingNV. This version returns both size and type in the sizeType buffer (at .position() and .position() + 1). 
	 */
	public static String glGetActiveVaryingNV(int program, int index, int bufSize, IntBuffer sizeType) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetActiveVaryingNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkBuffer(sizeType, 2);
		IntBuffer name_length = APIUtil.getLengths();
		ByteBuffer name = APIUtil.getBufferByte(bufSize);
		nglGetActiveVaryingNV(program, index, bufSize, name_length, 0, sizeType, sizeType.position(), sizeType, sizeType.position() + 1, name, name.position(), function_pointer);
		name.limit(name_length.get(0));
		return APIUtil.getString(name);
	}

	/**
	 * Overloads glGetActiveVaryingNV.
	 * <p>
	 * Overloads glGetActiveVaryingNV. This version returns only the varying name. 
	 */
	public static String glGetActiveVaryingNV(int program, int index, int bufSize) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetActiveVaryingNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		IntBuffer name_length = APIUtil.getLengths();
		ByteBuffer name = APIUtil.getBufferByte(bufSize);
		nglGetActiveVaryingNV(program, index, bufSize, name_length, 0, APIUtil.getBufferInt(), 0, APIUtil.getBufferInt(), 1, name, name.position(), function_pointer);
		name.limit(name_length.get(0));
		return APIUtil.getString(name);
	}

	/**
	 * Overloads glGetActiveVaryingNV.
	 * <p>
	 * Overloads glGetActiveVaryingNV. This version returns only the varying size. 
	 */
	public static int glGetActiveVaryingSizeNV(int program, int index) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetActiveVaryingNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		IntBuffer size = APIUtil.getBufferInt();
		nglGetActiveVaryingNV(program, index, 0, null, 0, size, size.position(), size, 1, APIUtil.getBufferByte(0), 0, function_pointer);
		return size.get(0);
	}

	/**
	 * Overloads glGetActiveVaryingNV.
	 * <p>
	 * Overloads glGetActiveVaryingNV. This version returns only the varying type. 
	 */
	public static int glGetActiveVaryingTypeNV(int program, int index) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetActiveVaryingNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		IntBuffer type = APIUtil.getBufferInt();
		nglGetActiveVaryingNV(program, index, 0, null, 0, type, 1, type, type.position(), APIUtil.getBufferByte(0), 0, function_pointer);
		return type.get(0);
	}

	public static void glActiveVaryingNV(int program, ByteBuffer name) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glActiveVaryingNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(name);
		BufferChecks.checkNullTerminated(name);
		nglActiveVaryingNV(program, name, name.position(), function_pointer);
	}
	static native void nglActiveVaryingNV(int program, ByteBuffer name, int name_position, long function_pointer);

	/** Overloads glActiveVaryingNV. */
	public static void glActiveVaryingNV(int program, CharSequence name) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glActiveVaryingNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglActiveVaryingNV(program, APIUtil.getBufferNT(name), 0, function_pointer);
	}

	public static void glGetTransformFeedbackVaryingNV(int program, int index, IntBuffer location) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetTransformFeedbackVaryingNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkBuffer(location, 1);
		nglGetTransformFeedbackVaryingNV(program, index, location, location.position(), function_pointer);
	}
	static native void nglGetTransformFeedbackVaryingNV(int program, int index, IntBuffer location, int location_position, long function_pointer);

	/** Overloads glGetTransformFeedbackVaryingNV. */
	public static int glGetTransformFeedbackVaryingNV(int program, int index) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glGetTransformFeedbackVaryingNV;
		BufferChecks.checkFunctionAddress(function_pointer);
		IntBuffer location = APIUtil.getBufferInt();
		nglGetTransformFeedbackVaryingNV(program, index, location, location.position(), function_pointer);
		return location.get(0);
	}
}
