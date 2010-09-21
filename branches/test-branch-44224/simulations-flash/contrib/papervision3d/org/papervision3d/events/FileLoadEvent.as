package org.papervision3d.events
{
	import flash.events.Event;

	/**
	* The FileLoadEvent class represents events that are dispatched when files are loaded.
	*/
	public class FileLoadEvent extends Event
	{
		public static const LOAD_COMPLETE 				:String = "loadComplete";
		public static const LOAD_ERROR    				:String = "loadError";
		public static const SECURITY_LOAD_ERROR			:String = "securityLoadError";
		public static const COLLADA_MATERIALS_DONE		:String = "colladaMaterialsDone";
		public static const LOAD_PROGRESS 				:String = "loadProgress";
		public static const ANIMATIONS_COMPLETE			:String = "animationsComplete";
		public static const ANIMATIONS_PROGRESS			:String = "animationsProgress";
			
		public var file:String = "";
		public var bytesLoaded:Number = -1;
		public var bytesTotal:Number = -1;	
		public var message:String = "";	
		public var dataObj:Object = null;

		public function FileLoadEvent( type:String, file:String="", bytesLoaded:Number=-1, bytesTotal:Number=-1, message:String="", dataObj:Object = null, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super( type, bubbles, cancelable );
			this.file = file;
			this.bytesLoaded = bytesLoaded;
			this.bytesTotal = bytesTotal;
			this.message = message;
			this.dataObj = dataObj;
		} 
		
		public override function clone():Event
		{
			return new FileLoadEvent(type, file, bytesLoaded, bytesTotal, message, dataObj, bubbles, cancelable);
		}
	}
}