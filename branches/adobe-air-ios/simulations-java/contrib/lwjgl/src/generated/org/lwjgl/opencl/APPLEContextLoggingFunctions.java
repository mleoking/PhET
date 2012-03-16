/* MACHINE GENERATED FILE, DO NOT EDIT */

package org.lwjgl.opencl;

import org.lwjgl.*;
import java.nio.*;

final class APPLEContextLoggingFunctions {

	private APPLEContextLoggingFunctions() {}

	static void clLogMessagesToSystemLogAPPLE(ByteBuffer errstr, ByteBuffer private_info, ByteBuffer user_data) {
		long function_pointer = CLCapabilities.clLogMessagesToSystemLogAPPLE;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(errstr);
		BufferChecks.checkDirect(private_info);
		BufferChecks.checkDirect(user_data);
		nclLogMessagesToSystemLogAPPLE(errstr, errstr.position(), private_info, private_info.position(), private_info.remaining(), user_data, user_data.position(), function_pointer);
	}
	static native void nclLogMessagesToSystemLogAPPLE(ByteBuffer errstr, int errstr_position, ByteBuffer private_info, int private_info_position, long private_info_cb, ByteBuffer user_data, int user_data_position, long function_pointer);

	static void clLogMessagesToStdoutAPPLE(ByteBuffer errstr, ByteBuffer private_info, ByteBuffer user_data) {
		long function_pointer = CLCapabilities.clLogMessagesToStdoutAPPLE;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(errstr);
		BufferChecks.checkDirect(private_info);
		BufferChecks.checkDirect(user_data);
		nclLogMessagesToStdoutAPPLE(errstr, errstr.position(), private_info, private_info.position(), private_info.remaining(), user_data, user_data.position(), function_pointer);
	}
	static native void nclLogMessagesToStdoutAPPLE(ByteBuffer errstr, int errstr_position, ByteBuffer private_info, int private_info_position, long private_info_cb, ByteBuffer user_data, int user_data_position, long function_pointer);

	static void clLogMessagesToStderrAPPLE(ByteBuffer errstr, ByteBuffer private_info, ByteBuffer user_data) {
		long function_pointer = CLCapabilities.clLogMessagesToStderrAPPLE;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(errstr);
		BufferChecks.checkDirect(private_info);
		BufferChecks.checkDirect(user_data);
		nclLogMessagesToStderrAPPLE(errstr, errstr.position(), private_info, private_info.position(), private_info.remaining(), user_data, user_data.position(), function_pointer);
	}
	static native void nclLogMessagesToStderrAPPLE(ByteBuffer errstr, int errstr_position, ByteBuffer private_info, int private_info_position, long private_info_cb, ByteBuffer user_data, int user_data_position, long function_pointer);
}
