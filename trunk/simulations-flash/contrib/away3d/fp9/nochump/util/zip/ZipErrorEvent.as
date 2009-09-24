package nochump.util.zip
{
	import flash.events.Event;

	public class ZipErrorEvent extends Event
	{
		// Event constants
		public static const PARSE_ERROR:String = "entryParseError";
		
		private var err:int = 0;
		public function ZipErrorEvent(
				_type:String, 
				_bubbles:Boolean = false, 
				_cancelable:Boolean = false,
				_err:int = 0)
		{
			super(_type, _bubbles, _cancelable);
			err = _err;
		}
		
		
		public override function clone():Event
		{
			return new ZipErrorEvent(type);
		}			
		
	}
}