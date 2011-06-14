package away3d.primitives
{
	import away3d.arcane;
	import away3d.core.base.*;
	
	import wumedia.parsers.swf.DefineFont;
	import wumedia.vector.VectorText;
	
	use namespace arcane;
	
	public class TextField3D extends AbstractPrimitive
	{
		private var _font:String;
		private var _size:Number;
		private var _leading:Number;
		private var _kerning:Number;
		private var _text:String;
		private var _textWidth:Number;
		private var _align:String;
		private var _face:Face;
		
		/**
		 * @inheritDoc
		 */
    	protected override function buildPrimitive():void
    	{
    		super.buildPrimitive();
    		
			geometry.graphics.clear();
			VectorText.write(geometry.graphics, _font, _size, _leading, _kerning, _text, 0, 0, _textWidth, _align, false);
			
			//clear the materials on the shapes
			for each (_face in geometry.faces)
				_face.material = null;
    	}
    	
		/**
    	 * Defines the size of the text in pixels. Defaults to 20.
    	 */
		public function get size():Number
		{
			return _size;
		}
		
		public function set size(val:Number):void
		{
			if (_size == val)
    			return;
    		
			_size = val;
			_primitiveDirty = true;
		}
		
		/**
    	 * Defines the amount of vertical space between lines. Defaults to 20.
    	 */
		public function get leading():Number
		{
			return _leading;
		}
		
		public function set leading(val:Number):void
		{
			if (_leading == val)
    			return;
    		
			_leading = val;
			_primitiveDirty = true;
		}
		
		/**
    	 * Defines the amount of horizontal padding between characters. Defaults to 0.
    	 */
		public function get kerning():Number
		{
			return _kerning;
		}
		
		public function set kerning(val:Number):void
		{
			if (_kerning == val)
    			return;
    		
			_kerning = val;
			_primitiveDirty = true;
		}
				
		/**
    	 * Defines the current text in the textfield.
    	 */
		public function get text():String
		{
			return _text;
		}
		
		public function set text(val:String):void
		{
			if (_text == val)
    			return;
    		
			_text = val;
			_primitiveDirty = true;
		}
				
		/**
    	 * Defines the fixed width of the textfield.
    	 */
		public function get textWidth():Number
		{
			return _textWidth;
		}
		
		public function set textWidth(val:Number):void
		{
			if (_textWidth == val)
    			return;
    		
			_textWidth = val;
			_primitiveDirty = true;
		}
				
		/**
    	 * Defines the paragraph alignment of the textfield. Defaults to TL (Top Left).
    	 */
		public function get align():String
		{
			return _align;
		}
		
		public function set align(val:String):void
		{
			if (_align == val)
    			return;
    		
			_align = val;
			_primitiveDirty = true;
		}
    	
		/**
		 * Creates a new <code>TextField3D</code> object.
		 *
		 * @param	font				The name of the font to be used in the textfield.
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
		public function TextField3D(font:String, init:Object = null)
		{
			super(init);
			
			_font = font;
			
			_size = ini.getNumber("size", 20);
			_leading = ini.getNumber("leading", 20);
			_kerning = ini.getNumber("kerning", 0);
			_text = ini.getString("text", "");
			_textWidth = ini.getNumber("textWidth", 500);
			_align = ini.getString("align", "TL");
			
			_primitiveDirty = true;
			
			this.bothsides = true;
			
			type = "TextField3D";
        	url = "primitive";
        	
		}
	}
}