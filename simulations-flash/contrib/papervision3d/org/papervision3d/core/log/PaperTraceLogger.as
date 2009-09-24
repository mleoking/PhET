package org.papervision3d.core.log
{
	public class PaperTraceLogger extends AbstractPaperLogger implements IPaperLogger
	{
		public function PaperTraceLogger()
		{
			super();
		}
		
		override public function log(msg:String, object:Object=null, arguments:Array=null):void
		{
			trace("LOG:",msg, arguments);
		}
		
		override public function info(msg:String, object:Object=null, arguments:Array=null):void
		{
			trace("INFO:",msg, arguments);
		}
		
		override public function debug(msg:String, object:Object=null, arguments:Array=null):void
		{
			trace("DEBUG:",msg, arguments);
		}
		
		override public function warning(msg:String, object:Object=null, arguments:Array=null):void
		{
			trace("WARNING:",msg, arguments);
		}
		
		override public function error(msg:String, object:Object=null, arguments:Array=null):void
		{
			trace("ERROR:",msg, arguments);
		}
		
		override public function fatal(msg:String, object:Object=null, arguments:Array=null):void
		{
			trace("FATAL:",msg, arguments);
		}
		
	}
}