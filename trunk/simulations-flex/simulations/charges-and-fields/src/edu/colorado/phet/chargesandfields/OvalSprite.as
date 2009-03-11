package edu.colorado.phet.chargesandfields {
	import flash.display.DisplayObject;
        import flash.display.Graphics;
        import flash.display.MovieClip;
        import flash.display.Sprite;
        import flash.display.Stage
        import flash.display.StageAlign;
        import flash.display.StageScaleMode;
        import flash.events.Event;
        import flash.events.KeyboardEvent;
        import flash.events.MouseEvent;
        import flash.geom.ColorTransform;
        import flash.text.TextField;
        import flash.ui.Keyboard;
	
	public class OvalSprite extends Sprite {
		private var myWidth : Number;
		private var myHeight : Number;
		
		private static var factor : Number = 15;
		
		public function OvalSprite(w : Number, h : Number) {
			
			//this.hitArea = this;
			this.useHandCursor = true;
			this.buttonMode = true;
			
			setSize(w, h);
			
			addEventListener(MouseEvent.MOUSE_DOWN, mouseDown);
			addEventListener(MouseEvent.MOUSE_UP, mouseUp);
		}
		
		public function setSize(w : Number, h : Number) : void {
			myWidth = w / factor;
			myHeight = h / factor;
			drawOval();
		}
		
		public function drawOval() : void {
			this.graphics.clear();
			this.graphics.beginFill(0xFF0000);
			this.graphics.drawEllipse(-myWidth/2, -myHeight/2, myWidth, myHeight);
			this.graphics.endFill();
		}
		
		public function mouseDown(evt : MouseEvent) : void {
			startDrag();
		}
		
		public function mouseUp(evt : MouseEvent) : void {
			stopDrag();
		}
		
		public function setPosition(x : Number, y : Number) : void {
			this.x = x;
			this.y = y;
		}
	}
}
