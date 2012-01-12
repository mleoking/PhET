package away3d.core.utils
{
	import flash.display.BitmapData;
	
	public class FaceDictionaryVO
	{
		public var bitmap:BitmapData;
		public var dirty:Boolean;
		
		public function FaceDictionaryVO(width:Number = 0, height:Number = 0)
		{
			if (width && height)
				bitmap = new BitmapData(width, height, true, 0x00000000);
		}
		
		public function clear():void
		{
			if (bitmap)
				bitmap.fillRect(bitmap.rect, 0x00000000);
	        dirty = true;
		}
		
		public function reset(width:Number, height:Number):void
		{
			if (bitmap)
				bitmap.dispose();
			bitmap = new BitmapData(width, height, true, 0x00000000);
			dirty = true;
		}
	}
}