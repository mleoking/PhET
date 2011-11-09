/* MACHINE GENERATED FILE, DO NOT EDIT */

package org.lwjgl.opengl;

import org.lwjgl.*;
import java.nio.*;

public final class AMDPinnedMemory {

	/**
	 *  <strong>Official spec not released yet. Info from AMD developer forums:</strong><br/>
	 *  Create a buffer object, bind it to the GL_EXTERNAL_VIRTUAL_MEMORY_AMD target and call glBufferData to 'allocate' space.
	 *  When the driver sees you do this, it will use the pointer you supply directly rather than copying the data (that is, the
	 *  GPU will access your application's memory). You can then use the buffer for other purposes such as a UBO, TBO or VBO by
	 *  binding it to the appropriate targets. Synchronization is left to the application - make use of glFenceSync and glWaitSync.
	 *  To release the memory, simply call glBufferData again on the buffer object on a different target, or delete the buffer
	 *  object. Don't free the memory in the application until you've detached it from the buffer object or bad stuff will happen.
	 *  <p/>
	 *  Keep in mind that any memory you access will go over the PCIe bus which will be limited to 3-4 GB/s. This will work much
	 *  better on Fusion systems (APUs). Theoretically, there isn't a limit to the amount of memory that can be pinned. However,
	 *  when the OS pins memory, it removes it from the regular pagable pool and cannot swap it to disk (this is what pinning
	 *  means). If you ask for too much, the OS will refuse to do it and the call will fail (the GL driver will return generate
	 *  a GL_OUT_OF_MEMORY error). It is very likely that you'll hit this limit long before you run out of address space on the
	 *  GPU, although in practice we do impose a moderate limit on the amount of pinned memory so as to not impact system stability
	 *  and performance.
	 */
	public static final int GL_EXTERNAL_VIRTUAL_MEMORY_AMD = 0x9160;

	private AMDPinnedMemory() {}
}
