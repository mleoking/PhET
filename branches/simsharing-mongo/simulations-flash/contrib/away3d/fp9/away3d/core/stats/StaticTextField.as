package away3d.core.stats 
{
	import flash.text.TextField;
	import flash.text.TextFormat;
	
	public class StaticTextField extends TextField
	{
		public var defaultText:String;
		
		public function StaticTextField(text:String=null, textFormat:TextFormat=null)
		{
			super();
			
			defaultTextFormat = textFormat?textFormat:new TextFormat("Verdana", 10, 0x000000);
			
			selectable = false;
			mouseEnabled = false;
			mouseWheelEnabled = false;
			
			autoSize = "left";
			tabEnabled = false;
			
			if(text)this.htmlText = text;
		}
	}
}
	