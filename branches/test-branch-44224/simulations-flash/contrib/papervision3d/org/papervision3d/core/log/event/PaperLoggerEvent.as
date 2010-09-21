package org.papervision3d.core.log.event
{
	import flash.events.Event;
	
	import org.papervision3d.core.log.PaperLogVO;
	
	/**
	 * @author Ralph Hauwert
	 */
	public class PaperLoggerEvent extends Event
	{
		public static const TYPE_LOGEVENT:String = "logEvent";
		
		public var paperLogVO:PaperLogVO;
		
		public function PaperLoggerEvent(paperLogVO:PaperLogVO)
		{
			super(TYPE_LOGEVENT);
			this.paperLogVO = paperLogVO;
		}
		
	}
}