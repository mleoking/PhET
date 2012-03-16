/* MACHINE GENERATED FILE, DO NOT EDIT */

#include <jni.h>
#include "extcl.h"

typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetPlatformIDsPROC) (cl_uint num_entries, cl_platform_id * platforms, cl_uint * num_platforms);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetPlatformInfoPROC) (cl_platform_id platform, cl_platform_info param_name, size_t param_value_size, cl_void * param_value, size_t * param_value_size_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetDeviceIDsPROC) (cl_platform_id platform, cl_device_type device_type, cl_uint num_entries, cl_device_id * devices, cl_uint * num_devices);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetDeviceInfoPROC) (cl_device_id device, cl_device_info param_name, size_t param_value_size, cl_void * param_value, size_t * param_value_size_ret);
typedef CL_API_ENTRY cl_context (CL_API_CALL *clCreateContextPROC) (const cl_context_properties * properties, cl_uint num_devices, const cl_device_id * devices, cl_create_context_callback pfn_notify, void * user_data, cl_int * errcode_ret);
typedef CL_API_ENTRY cl_context (CL_API_CALL *clCreateContextFromTypePROC) (const cl_context_properties * properties, cl_device_type device_type, cl_create_context_callback pfn_notify, void * user_data, cl_int * errcode_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clRetainContextPROC) (cl_context context);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clReleaseContextPROC) (cl_context context);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetContextInfoPROC) (cl_context context, cl_context_info param_name, size_t param_value_size, cl_void * param_value, size_t * param_value_size_ret);
typedef CL_API_ENTRY cl_command_queue (CL_API_CALL *clCreateCommandQueuePROC) (cl_context context, cl_device_id device, cl_command_queue_properties properties, cl_int * errcode_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clRetainCommandQueuePROC) (cl_command_queue command_queue);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clReleaseCommandQueuePROC) (cl_command_queue command_queue);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetCommandQueueInfoPROC) (cl_command_queue command_queue, cl_command_queue_info param_name, size_t param_value_size, cl_void * param_value, size_t * param_value_size_ret);
typedef CL_API_ENTRY cl_mem (CL_API_CALL *clCreateBufferPROC) (cl_context context, cl_mem_flags flags, size_t size, cl_void * host_ptr, cl_int * errcode_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueReadBufferPROC) (cl_command_queue command_queue, cl_mem buffer, cl_bool blocking_read, size_t offset, size_t cb, cl_void * ptr, cl_uint num_events_in_wait_list, const cl_event * event_wait_list, cl_event * event);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueWriteBufferPROC) (cl_command_queue command_queue, cl_mem buffer, cl_bool blocking_write, size_t offset, size_t cb, const cl_void * ptr, cl_uint num_events_in_wait_list, const cl_event * event_wait_list, cl_event * event);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueCopyBufferPROC) (cl_command_queue command_queue, cl_mem src_buffer, cl_mem dst_buffer, size_t src_offset, size_t dst_offset, size_t cb, cl_uint num_events_in_wait_list, const cl_event * event_wait_list, cl_event * event);
typedef CL_API_ENTRY cl_void * (CL_API_CALL *clEnqueueMapBufferPROC) (cl_command_queue command_queue, cl_mem buffer, cl_bool blocking_map, cl_map_flags map_flags, size_t offset, size_t cb, cl_uint num_events_in_wait_list, const cl_event * event_wait_list, cl_event * event, cl_int * errcode_ret);
typedef CL_API_ENTRY cl_mem (CL_API_CALL *clCreateImage2DPROC) (cl_context context, cl_mem_flags flags, const cl_image_format * image_format, size_t image_width, size_t image_height, size_t image_row_pitch, cl_void * host_ptr, cl_int * errcode_ret);
typedef CL_API_ENTRY cl_mem (CL_API_CALL *clCreateImage3DPROC) (cl_context context, cl_mem_flags flags, const cl_image_format * image_format, size_t image_width, size_t image_height, size_t image_depth, size_t image_row_pitch, size_t image_slice_pitch, cl_void * host_ptr, cl_int * errcode_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetSupportedImageFormatsPROC) (cl_context context, cl_mem_flags flags, cl_mem_object_type image_type, cl_uint num_entries, cl_image_format * image_formats, cl_uint * num_image_formats);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueReadImagePROC) (cl_command_queue command_queue, cl_mem image, cl_bool blocking_read, const size_t * origin, const size_t * region, size_t row_pitch, size_t slice_pitch, cl_void * ptr, cl_uint num_events_in_wait_list, const cl_event * event_wait_list, cl_event * event);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueWriteImagePROC) (cl_command_queue command_queue, cl_mem image, cl_bool blocking_write, const size_t * origin, const size_t * region, size_t input_row_pitch, size_t input_slice_pitch, const cl_void * ptr, cl_uint num_events_in_wait_list, const cl_event * event_wait_list, cl_event * event);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueCopyImagePROC) (cl_command_queue command_queue, cl_mem src_image, cl_mem dst_image, const size_t * src_origin, const size_t * dst_origin, const size_t * region, cl_uint num_events_in_wait_list, const cl_event * event_wait_list, cl_event * event);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueCopyImageToBufferPROC) (cl_command_queue command_queue, cl_mem src_image, cl_mem dst_buffer, const size_t * src_origin, const size_t * region, size_t dst_offset, cl_uint num_events_in_wait_list, const cl_event * event_wait_list, cl_event * event);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueCopyBufferToImagePROC) (cl_command_queue command_queue, cl_mem src_buffer, cl_mem dst_image, size_t src_offset, const size_t * dst_origin, const size_t * region, cl_uint num_events_in_wait_list, const cl_event * event_wait_list, cl_event * event);
typedef CL_API_ENTRY cl_void * (CL_API_CALL *clEnqueueMapImagePROC) (cl_command_queue command_queue, cl_mem image, cl_bool blocking_map, cl_map_flags map_flags, const size_t * origin, const size_t * region, size_t * image_row_pitch, size_t * image_slice_pitch, cl_uint num_events_in_wait_list, const cl_event * event_wait_list, cl_event * event, cl_int * errcode_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetImageInfoPROC) (cl_mem image, cl_image_info param_name, size_t param_value_size, cl_void * param_value, size_t * param_value_size_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clRetainMemObjectPROC) (cl_mem memobj);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clReleaseMemObjectPROC) (cl_mem memobj);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueUnmapMemObjectPROC) (cl_command_queue command_queue, cl_mem memobj, cl_void * mapped_ptr, cl_uint num_events_in_wait_list, const cl_event * event_wait_list, cl_event * event);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetMemObjectInfoPROC) (cl_mem memobj, cl_mem_info param_name, size_t param_value_size, cl_void * param_value, size_t * param_value_size_ret);
typedef CL_API_ENTRY cl_sampler (CL_API_CALL *clCreateSamplerPROC) (cl_context context, cl_bool normalized_coords, cl_addressing_mode addressing_mode, cl_filter_mode filter_mode, cl_int * errcode_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clRetainSamplerPROC) (cl_sampler sampler);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clReleaseSamplerPROC) (cl_sampler sampler);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetSamplerInfoPROC) (cl_sampler sampler, cl_sampler_info param_name, size_t param_value_size, cl_void * param_value, size_t * param_value_size_ret);
typedef CL_API_ENTRY cl_program (CL_API_CALL *clCreateProgramWithSourcePROC) (cl_context context, cl_uint count, const cl_char ** string, const size_t* lengths, cl_int * errcode_ret);
typedef CL_API_ENTRY cl_program (CL_API_CALL *clCreateProgramWithBinaryPROC) (cl_context context, cl_uint num_devices, const cl_device_id* device, const size_t* lengths, const cl_uchar ** binary, cl_int * binary_status, cl_int * errcode_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clRetainProgramPROC) (cl_program program);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clReleaseProgramPROC) (cl_program program);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clBuildProgramPROC) (cl_program program, cl_uint num_devices, const cl_device_id * device_list, const cl_char * options, cl_build_program_callback pfn_notify, void * user_data);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clUnloadCompilerPROC) ();
typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetProgramInfoPROC) (cl_program program, cl_program_info param_name, size_t param_value_size, cl_void * param_value, size_t * param_value_size_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetProgramBuildInfoPROC) (cl_program program, cl_device_id device, cl_program_build_info param_name, size_t param_value_size, cl_void * param_value, size_t * param_value_size_ret);
typedef CL_API_ENTRY cl_kernel (CL_API_CALL *clCreateKernelPROC) (cl_program program, const cl_char * kernel_name, cl_int * errcode_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clCreateKernelsInProgramPROC) (cl_program program, cl_uint num_kernels, cl_kernel * kernels, cl_uint * num_kernels_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clRetainKernelPROC) (cl_kernel kernel);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clReleaseKernelPROC) (cl_kernel kernel);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clSetKernelArgPROC) (cl_kernel kernel, cl_uint arg_index, size_t arg_size, const cl_void * arg_value);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetKernelInfoPROC) (cl_kernel kernel, cl_kernel_info param_name, size_t param_value_size, cl_void * param_value, size_t * param_value_size_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetKernelWorkGroupInfoPROC) (cl_kernel kernel, cl_device_id device, cl_kernel_work_group_info param_name, size_t param_value_size, cl_void * param_value, size_t * param_value_size_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueNDRangeKernelPROC) (cl_command_queue command_queue, cl_kernel kernel, cl_uint work_dim, const size_t * global_work_offset, const size_t * global_work_size, const size_t * local_work_size, cl_uint num_events_in_wait_list, const cl_event * event_wait_list, cl_event * event);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueTaskPROC) (cl_command_queue command_queue, cl_kernel kernel, cl_uint num_events_in_wait_list, const cl_event * event_wait_list, cl_event * event);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueNativeKernelPROC) (cl_command_queue command_queue, cl_native_kernel_func user_func, cl_void * args, size_t cb_args, cl_uint num_mem_objects, const cl_mem* mem_list, const cl_void ** args_mem_loc, cl_uint num_events_in_wait_list, const cl_event * event_wait_list, cl_event * event);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clWaitForEventsPROC) (cl_uint num_events, const cl_event * event_list);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetEventInfoPROC) (cl_event event, cl_event_info param_name, size_t param_value_size, cl_void * param_value, size_t * param_value_size_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clRetainEventPROC) (cl_event event);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clReleaseEventPROC) (cl_event event);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueMarkerPROC) (cl_command_queue command_queue, cl_event * event);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueBarrierPROC) (cl_command_queue command_queue);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueWaitForEventsPROC) (cl_command_queue command_queue, cl_uint num_events, const cl_event * event_list);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clGetEventProfilingInfoPROC) (cl_event event, cl_profiling_info param_name, size_t param_value_size, cl_void * param_value, size_t * param_value_size_ret);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clFlushPROC) (cl_command_queue command_queue);
typedef CL_API_ENTRY cl_int (CL_API_CALL *clFinishPROC) (cl_command_queue command_queue);
typedef CL_API_ENTRY void * (CL_API_CALL *clGetExtensionFunctionAddressPROC) (const cl_char * func_name);

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetPlatformIDs(JNIEnv *env, jclass clazz, jint num_entries, jobject platforms, jint platforms_position, jobject num_platforms, jint num_platforms_position, jlong function_pointer) {
	cl_platform_id *platforms_address = ((cl_platform_id *)(((char *)safeGetBufferAddress(env, platforms)) + platforms_position));
	cl_uint *num_platforms_address = ((cl_uint *)safeGetBufferAddress(env, num_platforms)) + num_platforms_position;
	clGetPlatformIDsPROC clGetPlatformIDs = (clGetPlatformIDsPROC)((intptr_t)function_pointer);
	cl_int __result = clGetPlatformIDs(num_entries, platforms_address, num_platforms_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetPlatformInfo(JNIEnv *env, jclass clazz, jlong platform, jint param_name, jlong param_value_size, jobject param_value, jint param_value_position, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	cl_void *param_value_address = ((cl_void *)(((char *)safeGetBufferAddress(env, param_value)) + param_value_position));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetPlatformInfoPROC clGetPlatformInfo = (clGetPlatformInfoPROC)((intptr_t)function_pointer);
	cl_int __result = clGetPlatformInfo((cl_platform_id)(intptr_t)platform, param_name, param_value_size, param_value_address, param_value_size_ret_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetDeviceIDs(JNIEnv *env, jclass clazz, jlong platform, jlong device_type, jint num_entries, jobject devices, jint devices_position, jobject num_devices, jint num_devices_position, jlong function_pointer) {
	cl_device_id *devices_address = ((cl_device_id *)(((char *)safeGetBufferAddress(env, devices)) + devices_position));
	cl_uint *num_devices_address = ((cl_uint *)safeGetBufferAddress(env, num_devices)) + num_devices_position;
	clGetDeviceIDsPROC clGetDeviceIDs = (clGetDeviceIDsPROC)((intptr_t)function_pointer);
	cl_int __result = clGetDeviceIDs((cl_platform_id)(intptr_t)platform, device_type, num_entries, devices_address, num_devices_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetDeviceInfo(JNIEnv *env, jclass clazz, jlong device, jint param_name, jlong param_value_size, jobject param_value, jint param_value_position, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	cl_void *param_value_address = ((cl_void *)(((char *)safeGetBufferAddress(env, param_value)) + param_value_position));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetDeviceInfoPROC clGetDeviceInfo = (clGetDeviceInfoPROC)((intptr_t)function_pointer);
	cl_int __result = clGetDeviceInfo((cl_device_id)(intptr_t)device, param_name, param_value_size, param_value_address, param_value_size_ret_address);
	return __result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10_nclCreateContext(JNIEnv *env, jclass clazz, jobject properties, jint properties_position, jint num_devices, jobject devices, jint devices_position, jlong pfn_notify, jlong user_data, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	const cl_context_properties *properties_address = ((const cl_context_properties *)(((char *)(*env)->GetDirectBufferAddress(env, properties)) + properties_position));
	const cl_device_id *devices_address = ((const cl_device_id *)(((char *)(*env)->GetDirectBufferAddress(env, devices)) + devices_position));
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateContextPROC clCreateContext = (clCreateContextPROC)((intptr_t)function_pointer);
	cl_context __result = clCreateContext(properties_address, num_devices, devices_address, (cl_create_context_callback)(intptr_t)pfn_notify, (void *)(intptr_t)user_data, errcode_ret_address);
	return (intptr_t)__result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10_nclCreateContextFromType(JNIEnv *env, jclass clazz, jobject properties, jint properties_position, jlong device_type, jlong pfn_notify, jlong user_data, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	const cl_context_properties *properties_address = ((const cl_context_properties *)(((char *)(*env)->GetDirectBufferAddress(env, properties)) + properties_position));
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateContextFromTypePROC clCreateContextFromType = (clCreateContextFromTypePROC)((intptr_t)function_pointer);
	cl_context __result = clCreateContextFromType(properties_address, device_type, (cl_create_context_callback)(intptr_t)pfn_notify, (void *)(intptr_t)user_data, errcode_ret_address);
	return (intptr_t)__result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclRetainContext(JNIEnv *env, jclass clazz, jlong context, jlong function_pointer) {
	clRetainContextPROC clRetainContext = (clRetainContextPROC)((intptr_t)function_pointer);
	cl_int __result = clRetainContext((cl_context)(intptr_t)context);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclReleaseContext(JNIEnv *env, jclass clazz, jlong context, jlong function_pointer) {
	clReleaseContextPROC clReleaseContext = (clReleaseContextPROC)((intptr_t)function_pointer);
	cl_int __result = clReleaseContext((cl_context)(intptr_t)context);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetContextInfo(JNIEnv *env, jclass clazz, jlong context, jint param_name, jlong param_value_size, jobject param_value, jint param_value_position, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	cl_void *param_value_address = ((cl_void *)(((char *)safeGetBufferAddress(env, param_value)) + param_value_position));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetContextInfoPROC clGetContextInfo = (clGetContextInfoPROC)((intptr_t)function_pointer);
	cl_int __result = clGetContextInfo((cl_context)(intptr_t)context, param_name, param_value_size, param_value_address, param_value_size_ret_address);
	return __result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10_nclCreateCommandQueue(JNIEnv *env, jclass clazz, jlong context, jlong device, jlong properties, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateCommandQueuePROC clCreateCommandQueue = (clCreateCommandQueuePROC)((intptr_t)function_pointer);
	cl_command_queue __result = clCreateCommandQueue((cl_context)(intptr_t)context, (cl_device_id)(intptr_t)device, properties, errcode_ret_address);
	return (intptr_t)__result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclRetainCommandQueue(JNIEnv *env, jclass clazz, jlong command_queue, jlong function_pointer) {
	clRetainCommandQueuePROC clRetainCommandQueue = (clRetainCommandQueuePROC)((intptr_t)function_pointer);
	cl_int __result = clRetainCommandQueue((cl_command_queue)(intptr_t)command_queue);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclReleaseCommandQueue(JNIEnv *env, jclass clazz, jlong command_queue, jlong function_pointer) {
	clReleaseCommandQueuePROC clReleaseCommandQueue = (clReleaseCommandQueuePROC)((intptr_t)function_pointer);
	cl_int __result = clReleaseCommandQueue((cl_command_queue)(intptr_t)command_queue);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetCommandQueueInfo(JNIEnv *env, jclass clazz, jlong command_queue, jint param_name, jlong param_value_size, jobject param_value, jint param_value_position, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	cl_void *param_value_address = ((cl_void *)(((char *)safeGetBufferAddress(env, param_value)) + param_value_position));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetCommandQueueInfoPROC clGetCommandQueueInfo = (clGetCommandQueueInfoPROC)((intptr_t)function_pointer);
	cl_int __result = clGetCommandQueueInfo((cl_command_queue)(intptr_t)command_queue, param_name, param_value_size, param_value_address, param_value_size_ret_address);
	return __result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10_nclCreateBuffer(JNIEnv *env, jclass clazz, jlong context, jlong flags, jlong size, jobject host_ptr, jint host_ptr_position, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	cl_void *host_ptr_address = ((cl_void *)(((char *)safeGetBufferAddress(env, host_ptr)) + host_ptr_position));
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateBufferPROC clCreateBuffer = (clCreateBufferPROC)((intptr_t)function_pointer);
	cl_mem __result = clCreateBuffer((cl_context)(intptr_t)context, flags, size, host_ptr_address, errcode_ret_address);
	return (intptr_t)__result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclEnqueueReadBuffer(JNIEnv *env, jclass clazz, jlong command_queue, jlong buffer, jint blocking_read, jlong offset, jlong cb, jobject ptr, jint ptr_position, jint num_events_in_wait_list, jobject event_wait_list, jint event_wait_list_position, jobject event, jint event_position, jlong function_pointer) {
	cl_void *ptr_address = ((cl_void *)(((char *)(*env)->GetDirectBufferAddress(env, ptr)) + ptr_position));
	const cl_event *event_wait_list_address = ((const cl_event *)(((char *)safeGetBufferAddress(env, event_wait_list)) + event_wait_list_position));
	cl_event *event_address = ((cl_event *)(((char *)safeGetBufferAddress(env, event)) + event_position));
	clEnqueueReadBufferPROC clEnqueueReadBuffer = (clEnqueueReadBufferPROC)((intptr_t)function_pointer);
	cl_int __result = clEnqueueReadBuffer((cl_command_queue)(intptr_t)command_queue, (cl_mem)(intptr_t)buffer, blocking_read, offset, cb, ptr_address, num_events_in_wait_list, event_wait_list_address, event_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclEnqueueWriteBuffer(JNIEnv *env, jclass clazz, jlong command_queue, jlong buffer, jint blocking_write, jlong offset, jlong cb, jobject ptr, jint ptr_position, jint num_events_in_wait_list, jobject event_wait_list, jint event_wait_list_position, jobject event, jint event_position, jlong function_pointer) {
	const cl_void *ptr_address = ((const cl_void *)(((char *)(*env)->GetDirectBufferAddress(env, ptr)) + ptr_position));
	const cl_event *event_wait_list_address = ((const cl_event *)(((char *)safeGetBufferAddress(env, event_wait_list)) + event_wait_list_position));
	cl_event *event_address = ((cl_event *)(((char *)safeGetBufferAddress(env, event)) + event_position));
	clEnqueueWriteBufferPROC clEnqueueWriteBuffer = (clEnqueueWriteBufferPROC)((intptr_t)function_pointer);
	cl_int __result = clEnqueueWriteBuffer((cl_command_queue)(intptr_t)command_queue, (cl_mem)(intptr_t)buffer, blocking_write, offset, cb, ptr_address, num_events_in_wait_list, event_wait_list_address, event_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclEnqueueCopyBuffer(JNIEnv *env, jclass clazz, jlong command_queue, jlong src_buffer, jlong dst_buffer, jlong src_offset, jlong dst_offset, jlong cb, jint num_events_in_wait_list, jobject event_wait_list, jint event_wait_list_position, jobject event, jint event_position, jlong function_pointer) {
	const cl_event *event_wait_list_address = ((const cl_event *)(((char *)safeGetBufferAddress(env, event_wait_list)) + event_wait_list_position));
	cl_event *event_address = ((cl_event *)(((char *)safeGetBufferAddress(env, event)) + event_position));
	clEnqueueCopyBufferPROC clEnqueueCopyBuffer = (clEnqueueCopyBufferPROC)((intptr_t)function_pointer);
	cl_int __result = clEnqueueCopyBuffer((cl_command_queue)(intptr_t)command_queue, (cl_mem)(intptr_t)src_buffer, (cl_mem)(intptr_t)dst_buffer, src_offset, dst_offset, cb, num_events_in_wait_list, event_wait_list_address, event_address);
	return __result;
}

JNIEXPORT jobject JNICALL Java_org_lwjgl_opencl_CL10_nclEnqueueMapBuffer(JNIEnv *env, jclass clazz, jlong command_queue, jlong buffer, jint blocking_map, jlong map_flags, jlong offset, jlong cb, jint num_events_in_wait_list, jobject event_wait_list, jint event_wait_list_position, jobject event, jint event_position, jobject errcode_ret, jint errcode_ret_position, jlong result_size, jlong function_pointer) {
	const cl_event *event_wait_list_address = ((const cl_event *)(((char *)safeGetBufferAddress(env, event_wait_list)) + event_wait_list_position));
	cl_event *event_address = ((cl_event *)(((char *)safeGetBufferAddress(env, event)) + event_position));
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clEnqueueMapBufferPROC clEnqueueMapBuffer = (clEnqueueMapBufferPROC)((intptr_t)function_pointer);
	cl_void * __result = clEnqueueMapBuffer((cl_command_queue)(intptr_t)command_queue, (cl_mem)(intptr_t)buffer, blocking_map, map_flags, offset, cb, num_events_in_wait_list, event_wait_list_address, event_address, errcode_ret_address);
	return safeNewBuffer(env, __result, result_size);
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10_nclCreateImage2D(JNIEnv *env, jclass clazz, jlong context, jlong flags, jobject image_format, jint image_format_position, jlong image_width, jlong image_height, jlong image_row_pitch, jobject host_ptr, jint host_ptr_position, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	const cl_image_format *image_format_address = ((const cl_image_format *)(((char *)(*env)->GetDirectBufferAddress(env, image_format)) + image_format_position));
	cl_void *host_ptr_address = ((cl_void *)(((char *)safeGetBufferAddress(env, host_ptr)) + host_ptr_position));
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateImage2DPROC clCreateImage2D = (clCreateImage2DPROC)((intptr_t)function_pointer);
	cl_mem __result = clCreateImage2D((cl_context)(intptr_t)context, flags, image_format_address, image_width, image_height, image_row_pitch, host_ptr_address, errcode_ret_address);
	return (intptr_t)__result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10_nclCreateImage3D(JNIEnv *env, jclass clazz, jlong context, jlong flags, jobject image_format, jint image_format_position, jlong image_width, jlong image_height, jlong image_depth, jlong image_row_pitch, jlong image_slice_pitch, jobject host_ptr, jint host_ptr_position, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	const cl_image_format *image_format_address = ((const cl_image_format *)(((char *)(*env)->GetDirectBufferAddress(env, image_format)) + image_format_position));
	cl_void *host_ptr_address = ((cl_void *)(((char *)safeGetBufferAddress(env, host_ptr)) + host_ptr_position));
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateImage3DPROC clCreateImage3D = (clCreateImage3DPROC)((intptr_t)function_pointer);
	cl_mem __result = clCreateImage3D((cl_context)(intptr_t)context, flags, image_format_address, image_width, image_height, image_depth, image_row_pitch, image_slice_pitch, host_ptr_address, errcode_ret_address);
	return (intptr_t)__result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetSupportedImageFormats(JNIEnv *env, jclass clazz, jlong context, jlong flags, jint image_type, jint num_entries, jobject image_formats, jint image_formats_position, jobject num_image_formats, jint num_image_formats_position, jlong function_pointer) {
	cl_image_format *image_formats_address = ((cl_image_format *)(((char *)safeGetBufferAddress(env, image_formats)) + image_formats_position));
	cl_uint *num_image_formats_address = ((cl_uint *)safeGetBufferAddress(env, num_image_formats)) + num_image_formats_position;
	clGetSupportedImageFormatsPROC clGetSupportedImageFormats = (clGetSupportedImageFormatsPROC)((intptr_t)function_pointer);
	cl_int __result = clGetSupportedImageFormats((cl_context)(intptr_t)context, flags, image_type, num_entries, image_formats_address, num_image_formats_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclEnqueueReadImage(JNIEnv *env, jclass clazz, jlong command_queue, jlong image, jint blocking_read, jobject origin, jint origin_position, jobject region, jint region_position, jlong row_pitch, jlong slice_pitch, jobject ptr, jint ptr_position, jint num_events_in_wait_list, jobject event_wait_list, jint event_wait_list_position, jobject event, jint event_position, jlong function_pointer) {
	const size_t *origin_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, origin)) + origin_position));
	const size_t *region_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, region)) + region_position));
	cl_void *ptr_address = ((cl_void *)(((char *)(*env)->GetDirectBufferAddress(env, ptr)) + ptr_position));
	const cl_event *event_wait_list_address = ((const cl_event *)(((char *)safeGetBufferAddress(env, event_wait_list)) + event_wait_list_position));
	cl_event *event_address = ((cl_event *)(((char *)safeGetBufferAddress(env, event)) + event_position));
	clEnqueueReadImagePROC clEnqueueReadImage = (clEnqueueReadImagePROC)((intptr_t)function_pointer);
	cl_int __result = clEnqueueReadImage((cl_command_queue)(intptr_t)command_queue, (cl_mem)(intptr_t)image, blocking_read, origin_address, region_address, row_pitch, slice_pitch, ptr_address, num_events_in_wait_list, event_wait_list_address, event_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclEnqueueWriteImage(JNIEnv *env, jclass clazz, jlong command_queue, jlong image, jint blocking_write, jobject origin, jint origin_position, jobject region, jint region_position, jlong input_row_pitch, jlong input_slice_pitch, jobject ptr, jint ptr_position, jint num_events_in_wait_list, jobject event_wait_list, jint event_wait_list_position, jobject event, jint event_position, jlong function_pointer) {
	const size_t *origin_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, origin)) + origin_position));
	const size_t *region_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, region)) + region_position));
	const cl_void *ptr_address = ((const cl_void *)(((char *)(*env)->GetDirectBufferAddress(env, ptr)) + ptr_position));
	const cl_event *event_wait_list_address = ((const cl_event *)(((char *)safeGetBufferAddress(env, event_wait_list)) + event_wait_list_position));
	cl_event *event_address = ((cl_event *)(((char *)safeGetBufferAddress(env, event)) + event_position));
	clEnqueueWriteImagePROC clEnqueueWriteImage = (clEnqueueWriteImagePROC)((intptr_t)function_pointer);
	cl_int __result = clEnqueueWriteImage((cl_command_queue)(intptr_t)command_queue, (cl_mem)(intptr_t)image, blocking_write, origin_address, region_address, input_row_pitch, input_slice_pitch, ptr_address, num_events_in_wait_list, event_wait_list_address, event_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclEnqueueCopyImage(JNIEnv *env, jclass clazz, jlong command_queue, jlong src_image, jlong dst_image, jobject src_origin, jint src_origin_position, jobject dst_origin, jint dst_origin_position, jobject region, jint region_position, jint num_events_in_wait_list, jobject event_wait_list, jint event_wait_list_position, jobject event, jint event_position, jlong function_pointer) {
	const size_t *src_origin_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, src_origin)) + src_origin_position));
	const size_t *dst_origin_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, dst_origin)) + dst_origin_position));
	const size_t *region_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, region)) + region_position));
	const cl_event *event_wait_list_address = ((const cl_event *)(((char *)safeGetBufferAddress(env, event_wait_list)) + event_wait_list_position));
	cl_event *event_address = ((cl_event *)(((char *)safeGetBufferAddress(env, event)) + event_position));
	clEnqueueCopyImagePROC clEnqueueCopyImage = (clEnqueueCopyImagePROC)((intptr_t)function_pointer);
	cl_int __result = clEnqueueCopyImage((cl_command_queue)(intptr_t)command_queue, (cl_mem)(intptr_t)src_image, (cl_mem)(intptr_t)dst_image, src_origin_address, dst_origin_address, region_address, num_events_in_wait_list, event_wait_list_address, event_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclEnqueueCopyImageToBuffer(JNIEnv *env, jclass clazz, jlong command_queue, jlong src_image, jlong dst_buffer, jobject src_origin, jint src_origin_position, jobject region, jint region_position, jlong dst_offset, jint num_events_in_wait_list, jobject event_wait_list, jint event_wait_list_position, jobject event, jint event_position, jlong function_pointer) {
	const size_t *src_origin_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, src_origin)) + src_origin_position));
	const size_t *region_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, region)) + region_position));
	const cl_event *event_wait_list_address = ((const cl_event *)(((char *)safeGetBufferAddress(env, event_wait_list)) + event_wait_list_position));
	cl_event *event_address = ((cl_event *)(((char *)safeGetBufferAddress(env, event)) + event_position));
	clEnqueueCopyImageToBufferPROC clEnqueueCopyImageToBuffer = (clEnqueueCopyImageToBufferPROC)((intptr_t)function_pointer);
	cl_int __result = clEnqueueCopyImageToBuffer((cl_command_queue)(intptr_t)command_queue, (cl_mem)(intptr_t)src_image, (cl_mem)(intptr_t)dst_buffer, src_origin_address, region_address, dst_offset, num_events_in_wait_list, event_wait_list_address, event_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclEnqueueCopyBufferToImage(JNIEnv *env, jclass clazz, jlong command_queue, jlong src_buffer, jlong dst_image, jlong src_offset, jobject dst_origin, jint dst_origin_position, jobject region, jint region_position, jint num_events_in_wait_list, jobject event_wait_list, jint event_wait_list_position, jobject event, jint event_position, jlong function_pointer) {
	const size_t *dst_origin_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, dst_origin)) + dst_origin_position));
	const size_t *region_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, region)) + region_position));
	const cl_event *event_wait_list_address = ((const cl_event *)(((char *)safeGetBufferAddress(env, event_wait_list)) + event_wait_list_position));
	cl_event *event_address = ((cl_event *)(((char *)safeGetBufferAddress(env, event)) + event_position));
	clEnqueueCopyBufferToImagePROC clEnqueueCopyBufferToImage = (clEnqueueCopyBufferToImagePROC)((intptr_t)function_pointer);
	cl_int __result = clEnqueueCopyBufferToImage((cl_command_queue)(intptr_t)command_queue, (cl_mem)(intptr_t)src_buffer, (cl_mem)(intptr_t)dst_image, src_offset, dst_origin_address, region_address, num_events_in_wait_list, event_wait_list_address, event_address);
	return __result;
}

JNIEXPORT jobject JNICALL Java_org_lwjgl_opencl_CL10_nclEnqueueMapImage(JNIEnv *env, jclass clazz, jlong command_queue, jlong image, jint blocking_map, jlong map_flags, jobject origin, jint origin_position, jobject region, jint region_position, jobject image_row_pitch, jint image_row_pitch_position, jobject image_slice_pitch, jint image_slice_pitch_position, jint num_events_in_wait_list, jobject event_wait_list, jint event_wait_list_position, jobject event, jint event_position, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	const size_t *origin_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, origin)) + origin_position));
	const size_t *region_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, region)) + region_position));
	size_t *image_row_pitch_address = ((size_t *)(((char *)(*env)->GetDirectBufferAddress(env, image_row_pitch)) + image_row_pitch_position));
	size_t *image_slice_pitch_address = ((size_t *)(((char *)safeGetBufferAddress(env, image_slice_pitch)) + image_slice_pitch_position));
	const cl_event *event_wait_list_address = ((const cl_event *)(((char *)safeGetBufferAddress(env, event_wait_list)) + event_wait_list_position));
	cl_event *event_address = ((cl_event *)(((char *)safeGetBufferAddress(env, event)) + event_position));
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clEnqueueMapImagePROC clEnqueueMapImage = (clEnqueueMapImagePROC)((intptr_t)function_pointer);
	cl_void * __result = clEnqueueMapImage((cl_command_queue)(intptr_t)command_queue, (cl_mem)(intptr_t)image, blocking_map, map_flags, origin_address, region_address, image_row_pitch_address, image_slice_pitch_address, num_events_in_wait_list, event_wait_list_address, event_address, errcode_ret_address);
	return safeNewBuffer(env, __result, extcl_CalculateImageSize(region_address, *image_row_pitch_address, image_slice_pitch == NULL ? 0 : *image_slice_pitch_address));
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetImageInfo(JNIEnv *env, jclass clazz, jlong image, jint param_name, jlong param_value_size, jobject param_value, jint param_value_position, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	cl_void *param_value_address = ((cl_void *)(((char *)safeGetBufferAddress(env, param_value)) + param_value_position));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetImageInfoPROC clGetImageInfo = (clGetImageInfoPROC)((intptr_t)function_pointer);
	cl_int __result = clGetImageInfo((cl_mem)(intptr_t)image, param_name, param_value_size, param_value_address, param_value_size_ret_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclRetainMemObject(JNIEnv *env, jclass clazz, jlong memobj, jlong function_pointer) {
	clRetainMemObjectPROC clRetainMemObject = (clRetainMemObjectPROC)((intptr_t)function_pointer);
	cl_int __result = clRetainMemObject((cl_mem)(intptr_t)memobj);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclReleaseMemObject(JNIEnv *env, jclass clazz, jlong memobj, jlong function_pointer) {
	clReleaseMemObjectPROC clReleaseMemObject = (clReleaseMemObjectPROC)((intptr_t)function_pointer);
	cl_int __result = clReleaseMemObject((cl_mem)(intptr_t)memobj);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclEnqueueUnmapMemObject(JNIEnv *env, jclass clazz, jlong command_queue, jlong memobj, jobject mapped_ptr, jint mapped_ptr_position, jint num_events_in_wait_list, jobject event_wait_list, jint event_wait_list_position, jobject event, jint event_position, jlong function_pointer) {
	cl_void *mapped_ptr_address = ((cl_void *)(((char *)(*env)->GetDirectBufferAddress(env, mapped_ptr)) + mapped_ptr_position));
	const cl_event *event_wait_list_address = ((const cl_event *)(((char *)safeGetBufferAddress(env, event_wait_list)) + event_wait_list_position));
	cl_event *event_address = ((cl_event *)(((char *)safeGetBufferAddress(env, event)) + event_position));
	clEnqueueUnmapMemObjectPROC clEnqueueUnmapMemObject = (clEnqueueUnmapMemObjectPROC)((intptr_t)function_pointer);
	cl_int __result = clEnqueueUnmapMemObject((cl_command_queue)(intptr_t)command_queue, (cl_mem)(intptr_t)memobj, mapped_ptr_address, num_events_in_wait_list, event_wait_list_address, event_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetMemObjectInfo(JNIEnv *env, jclass clazz, jlong memobj, jint param_name, jlong param_value_size, jobject param_value, jint param_value_position, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	cl_void *param_value_address = ((cl_void *)(((char *)safeGetBufferAddress(env, param_value)) + param_value_position));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetMemObjectInfoPROC clGetMemObjectInfo = (clGetMemObjectInfoPROC)((intptr_t)function_pointer);
	cl_int __result = clGetMemObjectInfo((cl_mem)(intptr_t)memobj, param_name, param_value_size, param_value_address, param_value_size_ret_address);
	return __result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10_nclCreateSampler(JNIEnv *env, jclass clazz, jlong context, jint normalized_coords, jint addressing_mode, jint filter_mode, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateSamplerPROC clCreateSampler = (clCreateSamplerPROC)((intptr_t)function_pointer);
	cl_sampler __result = clCreateSampler((cl_context)(intptr_t)context, normalized_coords, addressing_mode, filter_mode, errcode_ret_address);
	return (intptr_t)__result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclRetainSampler(JNIEnv *env, jclass clazz, jlong sampler, jlong function_pointer) {
	clRetainSamplerPROC clRetainSampler = (clRetainSamplerPROC)((intptr_t)function_pointer);
	cl_int __result = clRetainSampler((cl_sampler)(intptr_t)sampler);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclReleaseSampler(JNIEnv *env, jclass clazz, jlong sampler, jlong function_pointer) {
	clReleaseSamplerPROC clReleaseSampler = (clReleaseSamplerPROC)((intptr_t)function_pointer);
	cl_int __result = clReleaseSampler((cl_sampler)(intptr_t)sampler);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetSamplerInfo(JNIEnv *env, jclass clazz, jlong sampler, jint param_name, jlong param_value_size, jobject param_value, jint param_value_position, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	cl_void *param_value_address = ((cl_void *)(((char *)safeGetBufferAddress(env, param_value)) + param_value_position));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetSamplerInfoPROC clGetSamplerInfo = (clGetSamplerInfoPROC)((intptr_t)function_pointer);
	cl_int __result = clGetSamplerInfo((cl_sampler)(intptr_t)sampler, param_name, param_value_size, param_value_address, param_value_size_ret_address);
	return __result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10_nclCreateProgramWithSource(JNIEnv *env, jclass clazz, jlong context, jint count, jobject string, jint string_position, jlong lengths, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	const cl_char *string_address = ((const cl_char *)(*env)->GetDirectBufferAddress(env, string)) + string_position;
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateProgramWithSourcePROC clCreateProgramWithSource = (clCreateProgramWithSourcePROC)((intptr_t)function_pointer);
	cl_program __result = clCreateProgramWithSource((cl_context)(intptr_t)context, count, (const cl_char **)&string_address, (const size_t*)&lengths, errcode_ret_address);
	return (intptr_t)__result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10_nclCreateProgramWithSource2(JNIEnv *env, jclass clazz, jlong context, jint count, jobject strings, jint strings_position, jobject lengths, jint lengths_position, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	const cl_char *strings_address = ((const cl_char *)(((char *)(*env)->GetDirectBufferAddress(env, strings)) + strings_position));
	unsigned int _str_i;
	cl_char *_str_address;
	cl_char **strings_str = (cl_char **) malloc(count * sizeof(cl_char *));
	const size_t *lengths_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, lengths)) + lengths_position));
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateProgramWithSourcePROC clCreateProgramWithSource = (clCreateProgramWithSourcePROC)((intptr_t)function_pointer);
	cl_program __result;
	_str_i = 0;
	_str_address = (cl_char *)strings_address;
	while ( _str_i < count ) {
		strings_str[_str_i] = _str_address;
		_str_address += lengths_address[_str_i++];
	}
	__result = clCreateProgramWithSource((cl_context)(intptr_t)context, count, (const cl_char **)strings_str, lengths_address, errcode_ret_address);
	free(strings_str);
	return (intptr_t)__result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10_nclCreateProgramWithSource3(JNIEnv *env, jclass clazz, jlong context, jint count, jobjectArray strings, jobject lengths, jint lengths_position, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	unsigned int _ptr_i;
	jobject _ptr_object;
	cl_char **strings_ptr = (cl_char **) malloc(count * sizeof(cl_char *));
	const size_t *lengths_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, lengths)) + lengths_position));
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateProgramWithSourcePROC clCreateProgramWithSource = (clCreateProgramWithSourcePROC)((intptr_t)function_pointer);
	cl_program __result;
	_ptr_i = 0;
	while ( _ptr_i < count ) {
		_ptr_object = (*env)->GetObjectArrayElement(env, strings, _ptr_i);
		strings_ptr[_ptr_i++] = (cl_char *)(intptr_t)getPointerWrapperAddress(env, _ptr_object);
	}
	__result = clCreateProgramWithSource((cl_context)(intptr_t)context, count, (const cl_char **)strings_ptr, lengths_address, errcode_ret_address);
	free(strings_ptr);
	return (intptr_t)__result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10_nclCreateProgramWithSource4(JNIEnv *env, jclass clazz, jlong context, jint count, jobject strings, jint strings_position, jobject lengths, jint lengths_position, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	const cl_char *strings_address = ((const cl_char *)(*env)->GetDirectBufferAddress(env, strings)) + strings_position;
	unsigned int _str_i;
	cl_char *_str_address;
	cl_char **strings_str = (cl_char **) malloc(count * sizeof(cl_char *));
	const size_t *lengths_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, lengths)) + lengths_position));
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateProgramWithSourcePROC clCreateProgramWithSource = (clCreateProgramWithSourcePROC)((intptr_t)function_pointer);
	cl_program __result;
	_str_i = 0;
	_str_address = (cl_char *)strings_address;
	while ( _str_i < count ) {
		strings_str[_str_i] = _str_address;
		_str_address += lengths_address[_str_i++];
	}
	__result = clCreateProgramWithSource((cl_context)(intptr_t)context, count, (const cl_char **)strings_str, lengths_address, errcode_ret_address);
	free(strings_str);
	return (intptr_t)__result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10_nclCreateProgramWithBinary(JNIEnv *env, jclass clazz, jlong context, jint num_devices, jlong device, jlong lengths, jobject binary, jint binary_position, jobject binary_status, jint binary_status_position, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	const cl_uchar *binary_address = ((const cl_uchar *)(*env)->GetDirectBufferAddress(env, binary)) + binary_position;
	cl_int *binary_status_address = ((cl_int *)(*env)->GetDirectBufferAddress(env, binary_status)) + binary_status_position;
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateProgramWithBinaryPROC clCreateProgramWithBinary = (clCreateProgramWithBinaryPROC)((intptr_t)function_pointer);
	cl_program __result = clCreateProgramWithBinary((cl_context)(intptr_t)context, num_devices, (const cl_device_id*)(cl_device_id)(intptr_t)&device, (const size_t*)&lengths, (const cl_uchar **)&binary_address, binary_status_address, errcode_ret_address);
	return (intptr_t)__result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10_nclCreateProgramWithBinary2(JNIEnv *env, jclass clazz, jlong context, jint num_devices, jobject device_list, jint device_list_position, jobject lengths, jint lengths_position, jobject binaries, jint binaries_position, jobject binary_status, jint binary_status_position, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	const cl_device_id *device_list_address = ((const cl_device_id *)(((char *)(*env)->GetDirectBufferAddress(env, device_list)) + device_list_position));
	const size_t *lengths_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, lengths)) + lengths_position));
	const cl_uchar *binaries_address = ((const cl_uchar *)(((char *)(*env)->GetDirectBufferAddress(env, binaries)) + binaries_position));
	unsigned int _str_i;
	cl_uchar *_str_address;
	cl_uchar **binaries_str = (cl_uchar **) malloc(num_devices * sizeof(cl_uchar *));
	cl_int *binary_status_address = ((cl_int *)(*env)->GetDirectBufferAddress(env, binary_status)) + binary_status_position;
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateProgramWithBinaryPROC clCreateProgramWithBinary = (clCreateProgramWithBinaryPROC)((intptr_t)function_pointer);
	cl_program __result;
	_str_i = 0;
	_str_address = (cl_uchar *)binaries_address;
	while ( _str_i < num_devices ) {
		binaries_str[_str_i] = _str_address;
		_str_address += lengths_address[_str_i++];
	}
	__result = clCreateProgramWithBinary((cl_context)(intptr_t)context, num_devices, device_list_address, lengths_address, (const cl_uchar **)binaries_str, binary_status_address, errcode_ret_address);
	free(binaries_str);
	return (intptr_t)__result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10_nclCreateProgramWithBinary3(JNIEnv *env, jclass clazz, jlong context, jint num_devices, jobject device_list, jint device_list_position, jobject lengths, jint lengths_position, jobjectArray binaries, jobject binary_status, jint binary_status_position, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	const cl_device_id *device_list_address = ((const cl_device_id *)(((char *)(*env)->GetDirectBufferAddress(env, device_list)) + device_list_position));
	const size_t *lengths_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, lengths)) + lengths_position));
	unsigned int _ptr_i;
	jobject _ptr_object;
	cl_uchar **binaries_ptr = (cl_uchar **) malloc(num_devices * sizeof(cl_uchar *));
	cl_int *binary_status_address = ((cl_int *)(*env)->GetDirectBufferAddress(env, binary_status)) + binary_status_position;
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateProgramWithBinaryPROC clCreateProgramWithBinary = (clCreateProgramWithBinaryPROC)((intptr_t)function_pointer);
	cl_program __result;
	_ptr_i = 0;
	while ( _ptr_i < num_devices ) {
		_ptr_object = (*env)->GetObjectArrayElement(env, binaries, _ptr_i);
		binaries_ptr[_ptr_i++] = (cl_uchar *)(intptr_t)getPointerWrapperAddress(env, _ptr_object);
	}
	__result = clCreateProgramWithBinary((cl_context)(intptr_t)context, num_devices, device_list_address, lengths_address, (const cl_uchar **)binaries_ptr, binary_status_address, errcode_ret_address);
	free(binaries_ptr);
	return (intptr_t)__result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclRetainProgram(JNIEnv *env, jclass clazz, jlong program, jlong function_pointer) {
	clRetainProgramPROC clRetainProgram = (clRetainProgramPROC)((intptr_t)function_pointer);
	cl_int __result = clRetainProgram((cl_program)(intptr_t)program);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclReleaseProgram(JNIEnv *env, jclass clazz, jlong program, jlong function_pointer) {
	clReleaseProgramPROC clReleaseProgram = (clReleaseProgramPROC)((intptr_t)function_pointer);
	cl_int __result = clReleaseProgram((cl_program)(intptr_t)program);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclBuildProgram(JNIEnv *env, jclass clazz, jlong program, jint num_devices, jobject device_list, jint device_list_position, jobject options, jint options_position, jlong pfn_notify, jlong user_data, jlong function_pointer) {
	const cl_device_id *device_list_address = ((const cl_device_id *)(((char *)safeGetBufferAddress(env, device_list)) + device_list_position));
	const cl_char *options_address = ((const cl_char *)(*env)->GetDirectBufferAddress(env, options)) + options_position;
	clBuildProgramPROC clBuildProgram = (clBuildProgramPROC)((intptr_t)function_pointer);
	cl_int __result = clBuildProgram((cl_program)(intptr_t)program, num_devices, device_list_address, options_address, (cl_build_program_callback)(intptr_t)pfn_notify, (void *)(intptr_t)user_data);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclUnloadCompiler(JNIEnv *env, jclass clazz, jlong function_pointer) {
	clUnloadCompilerPROC clUnloadCompiler = (clUnloadCompilerPROC)((intptr_t)function_pointer);
	cl_int __result = clUnloadCompiler();
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetProgramInfo(JNIEnv *env, jclass clazz, jlong program, jint param_name, jlong param_value_size, jobject param_value, jint param_value_position, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	cl_void *param_value_address = ((cl_void *)(((char *)safeGetBufferAddress(env, param_value)) + param_value_position));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetProgramInfoPROC clGetProgramInfo = (clGetProgramInfoPROC)((intptr_t)function_pointer);
	cl_int __result = clGetProgramInfo((cl_program)(intptr_t)program, param_name, param_value_size, param_value_address, param_value_size_ret_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetProgramInfo2(JNIEnv *env, jclass clazz, jlong program, jint param_name, jlong param_value_size, jobject sizes, jint sizes_position, jobject param_value, jint param_value_position, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	const size_t *sizes_address = ((const size_t *)(((char *)(*env)->GetDirectBufferAddress(env, sizes)) + sizes_position));
	cl_uchar *param_value_address = ((cl_uchar *)(((char *)(*env)->GetDirectBufferAddress(env, param_value)) + param_value_position));
	unsigned int _str_i;
	cl_uchar *_str_address;
	cl_uchar **param_value_str = (cl_uchar **) malloc(param_value_size * sizeof(cl_uchar *));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetProgramInfoPROC clGetProgramInfo = (clGetProgramInfoPROC)((intptr_t)function_pointer);
	cl_int __result;
	_str_i = 0;
	_str_address = (cl_uchar *)param_value_address;
	while ( _str_i < param_value_size ) {
		param_value_str[_str_i] = _str_address;
		_str_address += sizes_address[_str_i++];
	}
	__result = clGetProgramInfo((cl_program)(intptr_t)program, param_name, param_value_size, (cl_uchar **)param_value_str, param_value_size_ret_address);
	free(param_value_str);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetProgramInfo3(JNIEnv *env, jclass clazz, jlong program, jint param_name, jlong param_value_size, jobjectArray param_value, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	unsigned int _ptr_i;
	jobject _ptr_object;
	cl_uchar **param_value_ptr = (cl_uchar **) malloc(param_value_size * sizeof(cl_uchar *));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetProgramInfoPROC clGetProgramInfo = (clGetProgramInfoPROC)((intptr_t)function_pointer);
	cl_int __result;
	_ptr_i = 0;
	while ( _ptr_i < param_value_size ) {
		_ptr_object = (*env)->GetObjectArrayElement(env, param_value, _ptr_i);
		param_value_ptr[_ptr_i++] = (cl_uchar *)(intptr_t)getPointerWrapperAddress(env, _ptr_object);
	}
	__result = clGetProgramInfo((cl_program)(intptr_t)program, param_name, param_value_size, (cl_uchar **)param_value_ptr, param_value_size_ret_address);
	free(param_value_ptr);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetProgramBuildInfo(JNIEnv *env, jclass clazz, jlong program, jlong device, jint param_name, jlong param_value_size, jobject param_value, jint param_value_position, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	cl_void *param_value_address = ((cl_void *)(((char *)safeGetBufferAddress(env, param_value)) + param_value_position));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetProgramBuildInfoPROC clGetProgramBuildInfo = (clGetProgramBuildInfoPROC)((intptr_t)function_pointer);
	cl_int __result = clGetProgramBuildInfo((cl_program)(intptr_t)program, (cl_device_id)(intptr_t)device, param_name, param_value_size, param_value_address, param_value_size_ret_address);
	return __result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10_nclCreateKernel(JNIEnv *env, jclass clazz, jlong program, jobject kernel_name, jint kernel_name_position, jobject errcode_ret, jint errcode_ret_position, jlong function_pointer) {
	const cl_char *kernel_name_address = ((const cl_char *)(*env)->GetDirectBufferAddress(env, kernel_name)) + kernel_name_position;
	cl_int *errcode_ret_address = ((cl_int *)safeGetBufferAddress(env, errcode_ret)) + errcode_ret_position;
	clCreateKernelPROC clCreateKernel = (clCreateKernelPROC)((intptr_t)function_pointer);
	cl_kernel __result = clCreateKernel((cl_program)(intptr_t)program, kernel_name_address, errcode_ret_address);
	return (intptr_t)__result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclCreateKernelsInProgram(JNIEnv *env, jclass clazz, jlong program, jint num_kernels, jobject kernels, jint kernels_position, jobject num_kernels_ret, jint num_kernels_ret_position, jlong function_pointer) {
	cl_kernel *kernels_address = ((cl_kernel *)(((char *)safeGetBufferAddress(env, kernels)) + kernels_position));
	cl_uint *num_kernels_ret_address = ((cl_uint *)safeGetBufferAddress(env, num_kernels_ret)) + num_kernels_ret_position;
	clCreateKernelsInProgramPROC clCreateKernelsInProgram = (clCreateKernelsInProgramPROC)((intptr_t)function_pointer);
	cl_int __result = clCreateKernelsInProgram((cl_program)(intptr_t)program, num_kernels, kernels_address, num_kernels_ret_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclRetainKernel(JNIEnv *env, jclass clazz, jlong kernel, jlong function_pointer) {
	clRetainKernelPROC clRetainKernel = (clRetainKernelPROC)((intptr_t)function_pointer);
	cl_int __result = clRetainKernel((cl_kernel)(intptr_t)kernel);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclReleaseKernel(JNIEnv *env, jclass clazz, jlong kernel, jlong function_pointer) {
	clReleaseKernelPROC clReleaseKernel = (clReleaseKernelPROC)((intptr_t)function_pointer);
	cl_int __result = clReleaseKernel((cl_kernel)(intptr_t)kernel);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclSetKernelArg(JNIEnv *env, jclass clazz, jlong kernel, jint arg_index, jlong arg_size, jobject arg_value, jint arg_value_position, jlong function_pointer) {
	const cl_void *arg_value_address = ((const cl_void *)(((char *)(*env)->GetDirectBufferAddress(env, arg_value)) + arg_value_position));
	clSetKernelArgPROC clSetKernelArg = (clSetKernelArgPROC)((intptr_t)function_pointer);
	cl_int __result = clSetKernelArg((cl_kernel)(intptr_t)kernel, arg_index, arg_size, arg_value_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetKernelInfo(JNIEnv *env, jclass clazz, jlong kernel, jint param_name, jlong param_value_size, jobject param_value, jint param_value_position, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	cl_void *param_value_address = ((cl_void *)(((char *)safeGetBufferAddress(env, param_value)) + param_value_position));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetKernelInfoPROC clGetKernelInfo = (clGetKernelInfoPROC)((intptr_t)function_pointer);
	cl_int __result = clGetKernelInfo((cl_kernel)(intptr_t)kernel, param_name, param_value_size, param_value_address, param_value_size_ret_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetKernelWorkGroupInfo(JNIEnv *env, jclass clazz, jlong kernel, jlong device, jint param_name, jlong param_value_size, jobject param_value, jint param_value_position, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	cl_void *param_value_address = ((cl_void *)(((char *)safeGetBufferAddress(env, param_value)) + param_value_position));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetKernelWorkGroupInfoPROC clGetKernelWorkGroupInfo = (clGetKernelWorkGroupInfoPROC)((intptr_t)function_pointer);
	cl_int __result = clGetKernelWorkGroupInfo((cl_kernel)(intptr_t)kernel, (cl_device_id)(intptr_t)device, param_name, param_value_size, param_value_address, param_value_size_ret_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclEnqueueNDRangeKernel(JNIEnv *env, jclass clazz, jlong command_queue, jlong kernel, jint work_dim, jobject global_work_offset, jint global_work_offset_position, jobject global_work_size, jint global_work_size_position, jobject local_work_size, jint local_work_size_position, jint num_events_in_wait_list, jobject event_wait_list, jint event_wait_list_position, jobject event, jint event_position, jlong function_pointer) {
	const size_t *global_work_offset_address = ((const size_t *)(((char *)safeGetBufferAddress(env, global_work_offset)) + global_work_offset_position));
	const size_t *global_work_size_address = ((const size_t *)(((char *)safeGetBufferAddress(env, global_work_size)) + global_work_size_position));
	const size_t *local_work_size_address = ((const size_t *)(((char *)safeGetBufferAddress(env, local_work_size)) + local_work_size_position));
	const cl_event *event_wait_list_address = ((const cl_event *)(((char *)safeGetBufferAddress(env, event_wait_list)) + event_wait_list_position));
	cl_event *event_address = ((cl_event *)(((char *)safeGetBufferAddress(env, event)) + event_position));
	clEnqueueNDRangeKernelPROC clEnqueueNDRangeKernel = (clEnqueueNDRangeKernelPROC)((intptr_t)function_pointer);
	cl_int __result = clEnqueueNDRangeKernel((cl_command_queue)(intptr_t)command_queue, (cl_kernel)(intptr_t)kernel, work_dim, global_work_offset_address, global_work_size_address, local_work_size_address, num_events_in_wait_list, event_wait_list_address, event_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclEnqueueTask(JNIEnv *env, jclass clazz, jlong command_queue, jlong kernel, jint num_events_in_wait_list, jobject event_wait_list, jint event_wait_list_position, jobject event, jint event_position, jlong function_pointer) {
	const cl_event *event_wait_list_address = ((const cl_event *)(((char *)safeGetBufferAddress(env, event_wait_list)) + event_wait_list_position));
	cl_event *event_address = ((cl_event *)(((char *)safeGetBufferAddress(env, event)) + event_position));
	clEnqueueTaskPROC clEnqueueTask = (clEnqueueTaskPROC)((intptr_t)function_pointer);
	cl_int __result = clEnqueueTask((cl_command_queue)(intptr_t)command_queue, (cl_kernel)(intptr_t)kernel, num_events_in_wait_list, event_wait_list_address, event_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclEnqueueNativeKernel(JNIEnv *env, jclass clazz, jlong command_queue, jlong user_func, jobject args, jint args_position, jlong cb_args, jint num_mem_objects, jobjectArray mem_list, jint num_events_in_wait_list, jobject event_wait_list, jint event_wait_list_position, jobject event, jint event_position, jlong function_pointer) {
	cl_void *args_address = ((cl_void *)(((char *)(*env)->GetDirectBufferAddress(env, args)) + args_position));
	unsigned int _ptr_i;
	jobject _ptr_object;
	cl_mem *mem_list_ptr = num_mem_objects == 0 ? NULL : (cl_mem *) malloc(num_mem_objects * sizeof(cl_mem ));
	const cl_event *event_wait_list_address = ((const cl_event *)(((char *)safeGetBufferAddress(env, event_wait_list)) + event_wait_list_position));
	cl_event *event_address = ((cl_event *)(((char *)safeGetBufferAddress(env, event)) + event_position));
	clEnqueueNativeKernelPROC clEnqueueNativeKernel = (clEnqueueNativeKernelPROC)((intptr_t)function_pointer);
	cl_int __result;
	void **args_mem_loc = num_mem_objects == 0 ? NULL : (void **)malloc(num_mem_objects * sizeof(void *));
	_ptr_i = 0;
	while ( _ptr_i < num_mem_objects ) {
		_ptr_object = (*env)->GetObjectArrayElement(env, mem_list, _ptr_i);
		mem_list_ptr[_ptr_i++] = (cl_mem)(intptr_t)getPointerWrapperAddress(env, _ptr_object);
	}
	_ptr_i = 0;
	while ( _ptr_i < num_mem_objects ) {
		args_mem_loc[_ptr_i] = (cl_void *)((char *)args_address + (4 + _ptr_i * (4 + sizeof(void *))));
		_ptr_i++;
	}
	__result = clEnqueueNativeKernel((cl_command_queue)(intptr_t)command_queue, (cl_native_kernel_func)(intptr_t)user_func, args_address, cb_args, num_mem_objects, (const cl_mem*)mem_list_ptr, (const void**)args_mem_loc, num_events_in_wait_list, event_wait_list_address, event_address);
	free(args_mem_loc);
	free(mem_list_ptr);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclWaitForEvents(JNIEnv *env, jclass clazz, jint num_events, jobject event_list, jint event_list_position, jlong function_pointer) {
	const cl_event *event_list_address = ((const cl_event *)(((char *)(*env)->GetDirectBufferAddress(env, event_list)) + event_list_position));
	clWaitForEventsPROC clWaitForEvents = (clWaitForEventsPROC)((intptr_t)function_pointer);
	cl_int __result = clWaitForEvents(num_events, event_list_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetEventInfo(JNIEnv *env, jclass clazz, jlong event, jint param_name, jlong param_value_size, jobject param_value, jint param_value_position, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	cl_void *param_value_address = ((cl_void *)(((char *)safeGetBufferAddress(env, param_value)) + param_value_position));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetEventInfoPROC clGetEventInfo = (clGetEventInfoPROC)((intptr_t)function_pointer);
	cl_int __result = clGetEventInfo((cl_event)(intptr_t)event, param_name, param_value_size, param_value_address, param_value_size_ret_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclRetainEvent(JNIEnv *env, jclass clazz, jlong event, jlong function_pointer) {
	clRetainEventPROC clRetainEvent = (clRetainEventPROC)((intptr_t)function_pointer);
	cl_int __result = clRetainEvent((cl_event)(intptr_t)event);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclReleaseEvent(JNIEnv *env, jclass clazz, jlong event, jlong function_pointer) {
	clReleaseEventPROC clReleaseEvent = (clReleaseEventPROC)((intptr_t)function_pointer);
	cl_int __result = clReleaseEvent((cl_event)(intptr_t)event);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclEnqueueMarker(JNIEnv *env, jclass clazz, jlong command_queue, jobject event, jint event_position, jlong function_pointer) {
	cl_event *event_address = ((cl_event *)(((char *)(*env)->GetDirectBufferAddress(env, event)) + event_position));
	clEnqueueMarkerPROC clEnqueueMarker = (clEnqueueMarkerPROC)((intptr_t)function_pointer);
	cl_int __result = clEnqueueMarker((cl_command_queue)(intptr_t)command_queue, event_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclEnqueueBarrier(JNIEnv *env, jclass clazz, jlong command_queue, jlong function_pointer) {
	clEnqueueBarrierPROC clEnqueueBarrier = (clEnqueueBarrierPROC)((intptr_t)function_pointer);
	cl_int __result = clEnqueueBarrier((cl_command_queue)(intptr_t)command_queue);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclEnqueueWaitForEvents(JNIEnv *env, jclass clazz, jlong command_queue, jint num_events, jobject event_list, jint event_list_position, jlong function_pointer) {
	const cl_event *event_list_address = ((const cl_event *)(((char *)(*env)->GetDirectBufferAddress(env, event_list)) + event_list_position));
	clEnqueueWaitForEventsPROC clEnqueueWaitForEvents = (clEnqueueWaitForEventsPROC)((intptr_t)function_pointer);
	cl_int __result = clEnqueueWaitForEvents((cl_command_queue)(intptr_t)command_queue, num_events, event_list_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclGetEventProfilingInfo(JNIEnv *env, jclass clazz, jlong event, jint param_name, jlong param_value_size, jobject param_value, jint param_value_position, jobject param_value_size_ret, jint param_value_size_ret_position, jlong function_pointer) {
	cl_void *param_value_address = ((cl_void *)(((char *)safeGetBufferAddress(env, param_value)) + param_value_position));
	size_t *param_value_size_ret_address = ((size_t *)(((char *)safeGetBufferAddress(env, param_value_size_ret)) + param_value_size_ret_position));
	clGetEventProfilingInfoPROC clGetEventProfilingInfo = (clGetEventProfilingInfoPROC)((intptr_t)function_pointer);
	cl_int __result = clGetEventProfilingInfo((cl_event)(intptr_t)event, param_name, param_value_size, param_value_address, param_value_size_ret_address);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclFlush(JNIEnv *env, jclass clazz, jlong command_queue, jlong function_pointer) {
	clFlushPROC clFlush = (clFlushPROC)((intptr_t)function_pointer);
	cl_int __result = clFlush((cl_command_queue)(intptr_t)command_queue);
	return __result;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_CL10_nclFinish(JNIEnv *env, jclass clazz, jlong command_queue, jlong function_pointer) {
	clFinishPROC clFinish = (clFinishPROC)((intptr_t)function_pointer);
	cl_int __result = clFinish((cl_command_queue)(intptr_t)command_queue);
	return __result;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opencl_CL10_nclGetExtensionFunctionAddress(JNIEnv *env, jclass clazz, jobject func_name, jint func_name_position, jlong function_pointer) {
	const cl_char *func_name_address = ((const cl_char *)(*env)->GetDirectBufferAddress(env, func_name)) + func_name_position;
	clGetExtensionFunctionAddressPROC clGetExtensionFunctionAddress = (clGetExtensionFunctionAddressPROC)((intptr_t)function_pointer);
	void * __result = clGetExtensionFunctionAddress(func_name_address);
	return (intptr_t)__result;
}

