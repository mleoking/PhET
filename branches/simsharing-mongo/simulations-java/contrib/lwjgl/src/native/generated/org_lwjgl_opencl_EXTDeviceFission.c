/* MACHINE GENERATED FILE, DO NOT EDIT */

#include <jni.h>
#include "extcl.h"

typedef CL_API_ENTRY cl_int (CL_API_CALL *clRetainDeviceEXTPROC) (cl_device_id device);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clReleaseDeviceEXTPROC) (cl_device_id device);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clCreateSubDevicesEXTPROC) (cl_device_id in_device, const cl_device_partition_property_ext * properties, cl_uint num_entries, cl_device_id * out_devices, cl_uint * num_devices);

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_EXTDeviceFission_nclRetainDeviceEXT(JNIEnv *env, jclass clazz, jlong device, jlong function_pointer) {
	clRetainDeviceEXTPROC clRetainDeviceEXT = (clRetainDeviceEXTPROC)((intptr_t)function_pointer);
	cl_int __result = clRetainDeviceEXT((cl_device_id)(intptr_t)device);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_EXTDeviceFission_nclReleaseDeviceEXT(JNIEnv *env, jclass clazz, jlong device, jlong function_pointer) {
	clReleaseDeviceEXTPROC clReleaseDeviceEXT = (clReleaseDeviceEXTPROC)((intptr_t)function_pointer);
	cl_int __result = clReleaseDeviceEXT((cl_device_id)(intptr_t)device);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_EXTDeviceFission_nclCreateSubDevicesEXT(JNIEnv *env, jclass clazz, jlong in_device, jobject properties, jint properties_position, jint num_entries, jobject out_devices, jint out_devices_position, jobject num_devices, jint num_devices_position, jlong function_pointer) {
	const cl_device_partition_property_ext *properties_address = ((const cl_device_partition_property_ext *)(((char *)(*env)->GetDirectBufferAddress(env, properties)) + properties_position));
	cl_device_id *out_devices_address = ((cl_device_id *)(((char *)safeGetBufferAddress(env, out_devices)) + out_devices_position));
	cl_uint *num_devices_address = ((cl_uint *)safeGetBufferAddress(env, num_devices)) + num_devices_position;
	clCreateSubDevicesEXTPROC clCreateSubDevicesEXT = (clCreateSubDevicesEXTPROC)((intptr_t)function_pointer);
	cl_int __result = clCreateSubDevicesEXT((cl_device_id)(intptr_t)in_device, properties_address, num_entries, out_devices_address, num_devices_address);
	return __result;
}

