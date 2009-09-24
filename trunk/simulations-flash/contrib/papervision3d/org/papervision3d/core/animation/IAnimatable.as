package org.papervision3d.core.animation
{
	public interface IAnimatable
	{
		/**
		 * Pauses the animation.
		 */ 
		function pause():void;
		
		/**
		 * Plays the animation.
		 * 
		 * @param 	clip	Clip to play. Default is "all"
		 * @param 	loop	Whether the animation should loop. Default is true.
		 */ 
		function play(clip:String="all", loop:Boolean=true):void;
		
		/**
		 * Resumes a paused animation.
		 * 
		 * @param loop 	Whether the animation should loop. Defaults is true.
		 */ 
		function resume(loop:Boolean=true):void;
		
		/**
		 * Stops the animation.
		 */ 
		function stop():void;
		
		/**
		 * Whether the animation is playing. This property is read-only.
		 */
		function get playing() : Boolean;
	}
}