package org.papervision3d.core.animation.clip 
{

	/**
	 * @author Tim Knip / floorplanner.com
	 */
	public class AnimationClip3D 
	{
		/**
		 * 
		 */
		public var name : String;
		
		/**
		 * 
		 */
		public var startTime : Number;
		
		/**
		 * 
		 */
		public var endTime : Number;
		
		/**
		 * 
		 */
		public function AnimationClip3D(name : String, startTime : Number = 0.0, endTime : Number = 0.0)
		{
			this.name = name;
			this.startTime = startTime;
			this.endTime = endTime;	
		}
		
		/**
		 * Clone.
		 * 
		 * @return
		 */
		public function clone() : AnimationClip3D
		{
			return new AnimationClip3D(this.name, this.startTime, this.endTime);
		}
 	}
}
