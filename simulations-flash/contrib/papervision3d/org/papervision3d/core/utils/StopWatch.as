package org.papervision3d.core.utils
{
	import flash.events.EventDispatcher;
	import flash.utils.getTimer;

	/**
	 * StopWatch times how long certain actions (e.g., a render) take
	 */
	public class StopWatch extends EventDispatcher
	{
		private var startTime:int;
		private var stopTime:int;
		private var elapsedTime:int;
		private var isRunning:Boolean;
		
		public function StopWatch()
		{
			super();
		}
		
		/**
		 * Starts the timer
		 */
		public function start():void
		{
			if(!isRunning){
				startTime = getTimer();
				isRunning = true;
			}
		}
		
		/**
		 * Stops the timer
		 */
		public function stop():int
		{
			if(isRunning){
				stopTime = getTimer();
				elapsedTime = stopTime-startTime;
				isRunning = false;
				return elapsedTime;
			}else{
				return 0;
			}
		}
		
		/**
		 * Resets the timer
		 */
		public function reset():void
		{
			isRunning = false;
		}
	
	}
}