package nochump.util.zip {
	import flash.events.Event;
	import flash.utils.ByteArray;	

	public class ZipEvent extends Event
	{
		// Event constants
		public static const ENTRY_PARSED:String = "entryParsed";

		public var entry : ByteArray;

		public function ZipEvent(
				_type:String, 
				_bubbles:Boolean = false, 
				_cancelable:Boolean = false,
				_entry:ByteArray = null)
		{
			super(_type, _bubbles, _cancelable);
			entry = _entry;
		}
		
		
		public override function clone():Event
		{
			return new ZipEvent(type);
		}			
		
	}
}