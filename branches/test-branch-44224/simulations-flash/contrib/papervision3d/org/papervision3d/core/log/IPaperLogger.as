package org.papervision3d.core.log
{
	
	/**
	 * @author Ralph Hauwert
	 */
	
	public interface IPaperLogger
	{
		function log(msg:String, object:Object = null, arguments:Array = null):void;
		function info(msg:String, object:Object = null, arguments:Array = null):void;
		function debug(msg:String, object:Object = null, arguments:Array = null):void;
		function warning(msg:String, object:Object = null, arguments:Array = null):void;
		function error(msg:String, object:Object = null, arguments:Array = null):void;
		function fatal(msg:String, object:Object = null, arguments:Array = null):void;
			
	}
}