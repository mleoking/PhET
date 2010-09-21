package org.papervision3d.core.log
{
	import org.papervision3d.core.log.event.PaperLoggerEvent;
	
	/**
	 * @author Ralph Hauwert
	 */
	
	public class AbstractPaperLogger implements IPaperLogger
	{
		public function AbstractPaperLogger()
		{
			
		}
		
		protected function onLogEvent(event:PaperLoggerEvent):void
		{
			var logVO:PaperLogVO = event.paperLogVO;
			switch(logVO.level){
				case LogLevel.LOG:
					log(logVO.msg, logVO.object, logVO.arg);
				break;
				case LogLevel.INFO:
					info(logVO.msg, logVO.object, logVO.arg);
				break;
				case LogLevel.ERROR:
					error(logVO.msg, logVO.object, logVO.arg);
				break;
				case LogLevel.DEBUG:
					debug(logVO.msg, logVO.object, logVO.arg);
				break;
				case LogLevel.WARNING:
					warning(logVO.msg, logVO.object, logVO.arg);
				break;
				case LogLevel.FATAL:
					fatal(logVO.msg, logVO.object, logVO.arg);
				break;
				default :
					log(logVO.msg, logVO.object, logVO.arg);
				break;
			}	
		}
		
		public function log(msg:String, object:Object = null, arg:Array = null):void
		{
			
		}
		
		public function info(msg:String, object:Object = null, arg:Array = null):void
		{
			
		}
		
		public function debug(msg:String, object:Object = null, arg:Array = null):void
		{
			
		}
		
		public function warning(msg:String, object:Object = null, arg:Array = null):void
		{
			
		}
		
		public function error(msg:String, object:Object = null, arg:Array = null):void
		{
			
		}
		
		public function fatal(msg:String, object:Object = null, arg:Array = null):void
		{
			
		}
		
		public function registerWithPaperLogger(paperLogger:PaperLogger):void
		{
			paperLogger.addEventListener(PaperLoggerEvent.TYPE_LOGEVENT, onLogEvent);
		}
		
		public function unregisterFromPaperLogger(paperLogger:PaperLogger):void
		{
			paperLogger.removeEventListener(PaperLoggerEvent.TYPE_LOGEVENT, onLogEvent);
		}
		
	}
}