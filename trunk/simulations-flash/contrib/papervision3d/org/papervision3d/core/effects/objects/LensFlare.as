package org.papervision3d.core.effects.objects
{
	import flash.display.BlendMode;
	import flash.display.DisplayObject;
	
	import org.papervision3d.core.proto.LightObject3D;
	import org.papervision3d.objects.DisplayObject3D;
	import org.papervision3d.view.layer.ViewportLayer;
	
	public class LensFlare extends ViewportLayer
	{
		
		public var light:LightObject3D;

		public var flareWidth:Number = 0;
		public var flareHeight:Number = 0;
		
		public var edgeOffset:Number = 1.15;
		private var flareArray:Array;
		
		
		public function LensFlare(light:LightObject3D, flareArray:Array, width:Number, height:Number, positions:Array = null)
		{
			super(null, light, false);	
			this.light = light;
			flareWidth = width;
			flareHeight = height;

			if(positions)
				this.positions = positions;
				
			setFlareArray(flareArray);
	
		}
		
		public function setFlareArray(flareArray:Array):void{
			emptyFlareArray();
			this.flareArray = flareArray;
			buildFlareArray();
		}
		
		private function emptyFlareArray():void{
			for each(var f:DisplayObject in flareArray){
				this.removeChild(f);
			}
			flareArray = null;
		}
		
		private function buildFlareArray():void{

			
			for each(var f:DisplayObject in flareArray){
				this.addChild(f);
				f.visible = false;
				f.blendMode = BlendMode.ADD;
			}
		}
		
		public function updateFlare(showFlare:Boolean = true, testHit:DisplayObject = null):void{
			if(showFlare){
			
				//check to see if it hits anything
				if(testHit){
					 var lx:Number = int(light.screen.x+flareWidth*0.5);
           			 var ly:Number = int(light.screen.y+flareHeight*0.5);
           			 if(testHit.hitTestPoint(lx, ly, true)){
           			 	hideFlare();
           			 	return;
           			 }
				}
				
				drawFlare();
			}else
				hideFlare();
		}
		
		public function hideFlare():void{
			
			for each(var f:DisplayObject in flareArray){
				f.visible = false;
			}
		}
		
		private function drawFlare():void{
			
			
			//don't draw light if behind camera
			if(light.screen.z <= 0){
				hideFlare();
				return;
			}
			
			var w:Number = flareWidth*0.5;
			var h:Number = flareHeight*0.5;
			var lx:Number = light.screen.x;
			var ly:Number = light.screen.y;
			
			var alx:Number = Math.abs(lx);
			var aly:Number = Math.abs(ly);
			
			
			
			if(alx > w*edgeOffset || aly > h*edgeOffset){
				hideFlare();
				return;
			}
			
			
			
			var distance:Number = Math.sqrt(lx*lx+ly*ly);
			var angle:Number = Math.atan2(ly, lx);
			
			var f:DisplayObject;
			var pos:Object;
			var dx:Number;
			var dy:Number;
			var scaleX:Number;
			var scaleY:Number;
			var scale:Number;
			
			
			for(var i:Number = 0;i<flareArray.length;i++){
				
				f = flareArray[i] as DisplayObject;
				pos = positions[i];
				
				f.visible = true;
				
				dx = Math.cos(angle)*pos.distance*distance;
				dy = Math.sin(angle)*pos.distance*distance;
				
				scaleX = scaleY = pos.scale;
			
				
				if(pos.dScale){
					scaleX += ((Math.abs(dx))/w)*pos.dScale;
					scaleY += ((Math.abs(dy))/h)*pos.dScale;
				}
				
				scale = Math.max(scaleX, scaleY);
				
				f.scaleX = f.scaleY = scale;
				
				if(pos.rotate)
					f.rotation = angle*(180/Math.PI)-180;
				
				f.x = dx;
				f.y = dy;
				
				
				
				if(pos.alpha)
					f.alpha = 1 - Math.max(alx/w, aly/h)*pos.alpha;
				
			}
			
		}
		
		//VARS FOR POSITIONS:
		//distance: relative to light projected distance from center
		//scale: initial scaled size
		//dScale: how much it scales in addition based on light distance
		//alpha: how transparent
		//rotate: rotate to always have left side pointing towards center
		
		public var positions:Array = 
		[
		 {distance:1, scale:1, dScale:0, alpha:0},
		 {distance:1.24, scale:0.85, dScale:0, alpha:0.5},
		 {distance:0.5, scale:0.5, dScale:0, alpha:0.5},
		 {distance:0.33, scale:0.25, dScale:0, alpha:0.8},
		 {distance:0.125, scale:1, dScale:0, alpha:0.8},
		 {distance:-0.181818, scale:0.25, dScale:1.2, alpha:0.9},
		 {distance:-0.25, scale:0.25, dScale:1.5, alpha:0.8, rotate:true},
		 {distance:-0.5, scale:0.5, dScale:1.1, alpha:0.9}
		
		];

	}
}