/* MACHINE GENERATED FILE, DO NOT EDIT */

package org.lwjgl.opencl;

import org.lwjgl.*;
import java.nio.*;

public final class EXTMigrateMemobject {

	/**
	 *  Besides a value of zero, the following cl_mem_migration_flags_ext values are
	 *  allowed:
	 */
	public static final int CL_MIGRATE_MEM_OBJECT_HOST_EXT = 0x1;

	/**
	 *  Returned in the &lt;param_value&gt; parameter of the clGetEventInfo when
	 *  &lt;param_name&gt; is CL_EVENT_COMMAND_TYPE:
	 */
	public static final int CL_COMMAND_MIGRATE_MEM_OBJECT_EXT = 0x4040;

	private EXTMigrateMemobject() {}

	public static int clEnqueueMigrateMemObjectEXT(CLCommandQueue command_queue, PointerBuffer mem_objects, long flags, PointerBuffer event_wait_list, PointerBuffer event) {
		long function_pointer = CLCapabilities.clEnqueueMigrateMemObjectEXT;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkBuffer(mem_objects, 1);
		if (event_wait_list != null)
			BufferChecks.checkDirect(event_wait_list);
		if (event != null)
			BufferChecks.checkBuffer(event, 1);
		int __result = nclEnqueueMigrateMemObjectEXT(command_queue.getPointer(), mem_objects.remaining(), mem_objects.getBuffer(), mem_objects.positionByte(), flags, (event_wait_list == null ? 0 : event_wait_list.remaining()), event_wait_list != null ? event_wait_list.getBuffer() : null, event_wait_list != null ? event_wait_list.positionByte() : 0, event != null ? event.getBuffer() : null, event != null ? event.positionByte() : 0, function_pointer);
		if ( __result == CL10.CL_SUCCESS ) command_queue.registerCLEvent(event);
		return __result;
	}
	static native int nclEnqueueMigrateMemObjectEXT(long command_queue, int mem_objects_num_mem_objects, ByteBuffer mem_objects, int mem_objects_position, long flags, int event_wait_list_num_events_in_wait_list, ByteBuffer event_wait_list, int event_wait_list_position, ByteBuffer event, int event_position, long function_pointer);

	/** Overloads clEnqueueMigrateMemObjectEXT. */
	public static int clEnqueueMigrateMemObjectEXT(CLCommandQueue command_queue, CLMem mem_object, long flags, PointerBuffer event_wait_list, PointerBuffer event) {
		long function_pointer = CLCapabilities.clEnqueueMigrateMemObjectEXT;
		BufferChecks.checkFunctionAddress(function_pointer);
		if (event_wait_list != null)
			BufferChecks.checkDirect(event_wait_list);
		if (event != null)
			BufferChecks.checkBuffer(event, 1);
		int __result = nclEnqueueMigrateMemObjectEXT(command_queue.getPointer(), 1, APIUtil.getBufferPointer().put(0, mem_object).getBuffer(), 0, flags, (event_wait_list == null ? 0 : event_wait_list.remaining()), event_wait_list != null ? event_wait_list.getBuffer() : null, event_wait_list != null ? event_wait_list.positionByte() : 0, event != null ? event.getBuffer() : null, event != null ? event.positionByte() : 0, function_pointer);
		if ( __result == CL10.CL_SUCCESS ) command_queue.registerCLEvent(event);
		return __result;
	}
}
