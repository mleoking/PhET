package org.papervision3d.events
{
	import flash.events.Event;
	
	/**
	* The AnimationEvent class represents events that are dispatched by the animation engine.
	*/
	public class AnimationEvent extends Event
	{
		public static const ANIMATION_COMPLETE 			:String = "animationComplete";
		public static const ANIMATION_ERROR    			:String = "animationError";
		public static const ANIMATION_NEXT_FRAME		:String = "animationNextFrame";
		
		public var currentFrame:uint;
		public var totalFrames:uint;
		public var message:String = "";	
		public var dataObj:Object = null;

		public function AnimationEvent( type:String, currentFrame:uint, totalFrames:uint, message:String="", dataObj:Object = null, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super( type, bubbles, cancelable );
			this.currentFrame = currentFrame;
			this.totalFrames = totalFrames;
			this.message = message;
			this.dataObj = dataObj;
		}
		
		override public function clone():Event
		{
			return new AnimationEvent(type, currentFrame, totalFrames, message, dataObj, bubbles, cancelable);
		}
	}
}