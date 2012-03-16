/* MACHINE GENERATED FILE, DO NOT EDIT */

package org.lwjgl.opencl;

import java.util.*;

public class CLDeviceCapabilities {

	public final int majorVersion;
	public final int minorVersion;

	public final boolean OpenCL11;

	public final boolean CL_AMD_device_attribute_query;
	public final boolean CL_AMD_fp64;
	public final boolean CL_AMD_media_ops;
	public final boolean CL_AMD_printf;
	final boolean CL_APPLE_ContextLoggingFunctions;
	public final boolean CL_APPLE_SetMemObjectDestructor;
	public final boolean CL_APPLE_gl_sharing;
	public final boolean CL_EXT_device_fission;
	public final boolean CL_EXT_migrate_memobject;
	public final boolean CL_KHR_3d_image_writes;
	public final boolean CL_KHR_byte_addressable_store;
	public final boolean CL_KHR_fp16;
	public final boolean CL_KHR_fp64;
	public final boolean CL_KHR_gl_event;
	public final boolean CL_KHR_gl_sharing;
	public final boolean CL_KHR_global_int32_base_atomics;
	public final boolean CL_KHR_global_int32_extended_atomics;
	public final boolean CL_KHR_int64_base_atomics;
	public final boolean CL_KHR_int64_extended_atomics;
	public final boolean CL_KHR_local_int32_base_atomics;
	public final boolean CL_KHR_local_int32_extended_atomics;
	public final boolean CL_KHR_select_fprounding_mode;
	public final boolean CL_NV_compiler_options;
	public final boolean CL_NV_device_attribute_query;
	public final boolean CL_NV_pragma_unroll;

	public CLDeviceCapabilities(final CLDevice device) {
		final String extensionList = device.getInfoString(CL10.CL_DEVICE_EXTENSIONS);
		final String version = device.getInfoString(CL10.CL_DEVICE_VERSION);
		if ( !version.startsWith("OpenCL ") )
			throw new RuntimeException("Invalid OpenCL version string: " + version);

		try {
			final StringTokenizer tokenizer = new StringTokenizer(version.substring(7), ". ");

			majorVersion = Integer.parseInt(tokenizer.nextToken());
			minorVersion = Integer.parseInt(tokenizer.nextToken());

			OpenCL11 = 1 < majorVersion || (1 == majorVersion && 1 <= minorVersion);
		} catch (RuntimeException e) {
			throw new RuntimeException("The major and/or minor OpenCL version \"" + version + "\" is malformed: " + e.getMessage());
		}

		final Set<String> extensions = APIUtil.getExtensions(extensionList);
		CL_AMD_device_attribute_query = extensions.contains("cl_amd_device_attribute_query");
		CL_AMD_fp64 = extensions.contains("cl_amd_fp64");
		CL_AMD_media_ops = extensions.contains("cl_amd_media_ops");
		CL_AMD_printf = extensions.contains("cl_amd_printf");
		CL_APPLE_ContextLoggingFunctions = extensions.contains("cl_apple_contextloggingfunctions") && CLCapabilities.CL_APPLE_ContextLoggingFunctions;
		CL_APPLE_SetMemObjectDestructor = extensions.contains("cl_apple_setmemobjectdestructor") && CLCapabilities.CL_APPLE_SetMemObjectDestructor;
		CL_APPLE_gl_sharing = extensions.contains("cl_apple_gl_sharing") && CLCapabilities.CL_APPLE_gl_sharing;
		CL_EXT_device_fission = extensions.contains("cl_ext_device_fission") && CLCapabilities.CL_EXT_device_fission;
		CL_EXT_migrate_memobject = extensions.contains("cl_ext_migrate_memobject") && CLCapabilities.CL_EXT_migrate_memobject;
		CL_KHR_3d_image_writes = extensions.contains("cl_khr_3d_image_writes");
		CL_KHR_byte_addressable_store = extensions.contains("cl_khr_byte_addressable_store");
		CL_KHR_fp16 = extensions.contains("cl_khr_fp16");
		CL_KHR_fp64 = extensions.contains("cl_khr_fp64");
		CL_KHR_gl_event = extensions.contains("cl_khr_gl_event") && CLCapabilities.CL_KHR_gl_event;
		CL_KHR_gl_sharing = extensions.contains("cl_khr_gl_sharing") && CLCapabilities.CL_KHR_gl_sharing;
		CL_KHR_global_int32_base_atomics = extensions.contains("cl_khr_global_int32_base_atomics");
		CL_KHR_global_int32_extended_atomics = extensions.contains("cl_khr_global_int32_extended_atomics");
		CL_KHR_int64_base_atomics = extensions.contains("cl_khr_int64_base_atomics");
		CL_KHR_int64_extended_atomics = extensions.contains("cl_khr_int64_extended_atomics");
		CL_KHR_local_int32_base_atomics = extensions.contains("cl_khr_local_int32_base_atomics");
		CL_KHR_local_int32_extended_atomics = extensions.contains("cl_khr_local_int32_extended_atomics");
		CL_KHR_select_fprounding_mode = extensions.contains("cl_khr_select_fprounding_mode");
		CL_NV_compiler_options = extensions.contains("cl_nv_compiler_options");
		CL_NV_device_attribute_query = extensions.contains("cl_nv_device_attribute_query");
		CL_NV_pragma_unroll = extensions.contains("cl_nv_pragma_unroll");
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public String toString() {
		final StringBuilder buf = new StringBuilder();

		buf.append("OpenCL ").append(majorVersion).append('.').append(minorVersion);

		buf.append(" - Extensions: ");
		if ( CL_AMD_device_attribute_query ) buf.append("cl_amd_device_attribute_query ");
		if ( CL_AMD_fp64 ) buf.append("cl_amd_fp64 ");
		if ( CL_AMD_media_ops ) buf.append("cl_amd_media_ops ");
		if ( CL_AMD_printf ) buf.append("cl_amd_printf ");
		if ( CL_APPLE_ContextLoggingFunctions ) buf.append("cl_apple_contextloggingfunctions ");
		if ( CL_APPLE_SetMemObjectDestructor ) buf.append("cl_apple_setmemobjectdestructor ");
		if ( CL_APPLE_gl_sharing ) buf.append("cl_apple_gl_sharing ");
		if ( CL_EXT_device_fission ) buf.append("cl_ext_device_fission ");
		if ( CL_EXT_migrate_memobject ) buf.append("cl_ext_migrate_memobject ");
		if ( CL_KHR_3d_image_writes ) buf.append("cl_khr_3d_image_writes ");
		if ( CL_KHR_byte_addressable_store ) buf.append("cl_khr_byte_addressable_store ");
		if ( CL_KHR_fp16 ) buf.append("cl_khr_fp16 ");
		if ( CL_KHR_fp64 ) buf.append("cl_khr_fp64 ");
		if ( CL_KHR_gl_event ) buf.append("cl_khr_gl_event ");
		if ( CL_KHR_gl_sharing ) buf.append("cl_khr_gl_sharing ");
		if ( CL_KHR_global_int32_base_atomics ) buf.append("cl_khr_global_int32_base_atomics ");
		if ( CL_KHR_global_int32_extended_atomics ) buf.append("cl_khr_global_int32_extended_atomics ");
		if ( CL_KHR_int64_base_atomics ) buf.append("cl_khr_int64_base_atomics ");
		if ( CL_KHR_int64_extended_atomics ) buf.append("cl_khr_int64_extended_atomics ");
		if ( CL_KHR_local_int32_base_atomics ) buf.append("cl_khr_local_int32_base_atomics ");
		if ( CL_KHR_local_int32_extended_atomics ) buf.append("cl_khr_local_int32_extended_atomics ");
		if ( CL_KHR_select_fprounding_mode ) buf.append("cl_khr_select_fprounding_mode ");
		if ( CL_NV_compiler_options ) buf.append("cl_nv_compiler_options ");
		if ( CL_NV_device_attribute_query ) buf.append("cl_nv_device_attribute_query ");
		if ( CL_NV_pragma_unroll ) buf.append("cl_nv_pragma_unroll ");

		return buf.toString();
	}

}
