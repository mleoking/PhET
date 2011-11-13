/* MACHINE GENERATED FILE, DO NOT EDIT */

package org.lwjgl.opencl;

import java.util.*;

public class CLPlatformCapabilities {

	public final int majorVersion;
	public final int minorVersion;

	public final boolean OpenCL11;

	public final boolean CL_KHR_d3d10_sharing;
	public final boolean CL_KHR_gl_event;
	public final boolean CL_KHR_gl_sharing;
	public final boolean CL_KHR_icd;

	public CLPlatformCapabilities(final CLPlatform platform) {
		final String extensionList = platform.getInfoString(CL10.CL_PLATFORM_EXTENSIONS);
		final String version = platform.getInfoString(CL10.CL_PLATFORM_VERSION);
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
		CL_KHR_d3d10_sharing = extensions.contains("cl_khr_d3d10_sharing");
		CL_KHR_gl_event = extensions.contains("cl_khr_gl_event") && CLCapabilities.CL_KHR_gl_event;
		CL_KHR_gl_sharing = extensions.contains("cl_khr_gl_sharing") && CLCapabilities.CL_KHR_gl_sharing;
		CL_KHR_icd = extensions.contains("cl_khr_icd") && CLCapabilities.CL_KHR_icd;
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
		if ( CL_KHR_d3d10_sharing ) buf.append("cl_khr_d3d10_sharing ");
		if ( CL_KHR_gl_event ) buf.append("cl_khr_gl_event ");
		if ( CL_KHR_gl_sharing ) buf.append("cl_khr_gl_sharing ");
		if ( CL_KHR_icd ) buf.append("cl_khr_icd ");

		return buf.toString();
	}

}
