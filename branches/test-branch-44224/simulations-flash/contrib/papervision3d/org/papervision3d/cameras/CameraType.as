package org.papervision3d.cameras
{
	/**
	 * CameraTypes are mainly used with BasicView to define which camera to use.
	 * 
	 * @author Tim Knip
	 * @see org.papervision3d.cameras.Camera3D
	 * @see org.papervision3d.cameras.DebugCamera3D
	 * @see org.papervision3d.view.BasicView
	 * 
	 */ 
	public class CameraType
	{
		/**
		 * The TARGET constant defines a Camera3D targeting x:0, y:0, z:0
		 */
		public static var TARGET	:String = "Target";
		/**
		 * The FREE constant defines a Camera3D with no target
		 */
		public static var FREE		:String = "Free";
		/**
		 * The DEBUG constant defines a DebugCamera3D
		 */
		public static var DEBUG		:String = "Debug";
		/*
		 * the SPRING constant defines a SpringCamera3D
		 */
		public static var SPRING		:String = "Spring";
	}
}