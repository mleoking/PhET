package{
	import flash.display.*;
	import fl.motion.Color;
	//import flash.geom.ColorTransform;
	//A handle is a draggable point on a curve
	
	public class Handle extends Sprite{
		var myWidth:Number = 6;
		var myHeight:Number = 40
		var handleColor:Color;
		var thisGraphics
		var index:int;			//index labeling handle, handle x-position  = index*pixelsPerHandle
		var selected:Boolean;  	//true if selected by MOUSE_DOWN
		
		public function Handle(ind:int):void{
			this.index = ind;
			thisGraphics = this.graphics;
			//this.buttonMode = true;
			this.handleColor = new Color();
			this.selected = false;
			this.drawSelf();
			this.setColor(0xff0000, 1.0);
			this.alpha = 0;
		}//end of constructor
		
		private function drawSelf():void{
			with(thisGraphics){
				clear();
				lineStyle(1, 0x0000ff);
				beginFill(0xff0000);
				drawRect(-myWidth/2,-myHeight/2,myWidth,myHeight);
				endFill()
			}
		}//end of drawSelf
		
		public function setSelected(tOrF):void{
			this.selected = tOrF;
		}
		
		public function getSelected():Boolean{
			return this.selected;
		}
		
		public function setWidth(widthInPixels:int):void{
			this.width = widthInPixels;
			this.x = -widthInPixels/2;
		}
		
		public function setColor(tint:uint, tintMultiplier:Number){
			this.handleColor.setTint(tint, tintMultiplier);
			this.transform.colorTransform = this.handleColor;
		}
	}//end of class
}//end of package