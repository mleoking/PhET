package away3d.overlays
{
	import away3d.cameras.Camera3D;
	import away3d.core.base.Object3D;
	import away3d.core.draw.ScreenVertex;
	import away3d.core.math.Number2D;
	
	import flash.display.Sprite;
	import flash.geom.ColorTransform;
	
	//import gs.TweenMax;
	
	public class LensFlare extends Sprite implements IOverlay
	{
		public static const BURN_METHOD_COLOR_TRANSFORM:uint = 0;
		public static const BURN_METHOD_BRIGHTNESS:uint = 1;
		
		public var spreadFactor:Number = 0.5;
		
		public var useScaling:Boolean = true;
		public var haloScaleFactor:Number = 50;
		public var scaleFactor:Number = 50;
		public var scaleMax:Number = 3;
		
		public var useAlpha:Boolean = true;
		public var alphaFactor:Number = 50;
		public var alphaMax:Number = 1;
		public var alphaMin:Number = 0.5;
		
		public var useRotation:Boolean = true;
		public var rotationFactor:Number = 0.25;
		
		public var useBurning:Boolean = true;
		public var burnFactor:Number = 10;
		
		private var _camera:Camera3D;
		private var _flares:Array;
		private var _lightSource:Object3D;
		private var _sourceProjection:ScreenVertex;
		private var _projectionVector:Number2D;
		private var _projectionVersor:Number2D;
		private var _projectionLength:Number;
		private var _halo:Sprite;
		private var _burnClip:Sprite;
		private var _burnMethod:uint = BURN_METHOD_BRIGHTNESS;
		private var _ct:ColorTransform;
		
		public function LensFlare(source:Object3D, camera:Camera3D)
		{
			_lightSource = source;
			_camera = camera;
			
			_flares = [];
		}
		
		public function set burnMethod(value:uint):void
		{
			if(value != LensFlare.BURN_METHOD_BRIGHTNESS && value != LensFlare.BURN_METHOD_COLOR_TRANSFORM)
				return;
				
			_burnMethod = value;
		}
		
		public function setHaloAsset(sprite:Sprite):void
		{
			sprite.visible = false;
			
			if(_halo)
				removeChild(_halo);
			
			_halo = sprite;
			addChild(_halo);
		}
		
		public function setBurnClip(sprite:Sprite):void
		{
			_burnClip = sprite;
		}
		
		public function addFlareAsset(sprite:Sprite, cacheAsBitmap:Boolean = false):void
		{
			sprite.visible = false;
			
			_flares.push(sprite);
			addChild(sprite);
			
			sprite.cacheAsBitmap = cacheAsBitmap;
		}
		
		public function update():void
		{
			_sourceProjection = _camera.screen(_lightSource);
			
			if(!_sourceProjection)
				return;
			
			_projectionVector = new Number2D(-_sourceProjection.x, -_sourceProjection.y);
			_projectionLength = _projectionVector.modulo;
			
			_projectionVersor = _projectionVector;
			_projectionVersor.normalize();
						
			if(useBurning && _burnClip)
			{
				if(_burnMethod == LensFlare.BURN_METHOD_BRIGHTNESS)
				{
					var bsVal:Number = 5*burnFactor/_projectionLength;
					bsVal = bsVal < 1 ? 1 : bsVal;
					bsVal = bsVal > 3 ? 3 : bsVal;
					//TweenMax.to(_burnClip, 0, {colorMatrixFilter:{contrast:bsVal, brightness:bsVal}});
					//TODO: setup colorMatrixFilter tween without TweenMax
					var ctVal:Number = 500*burnFactor/_projectionLength;
					_ct = new ColorTransform(1, 1, 1, 1, ctVal, ctVal, ctVal, 0);
					_burnClip.transform.colorTransform = _ct;
				}
				else if(_burnMethod == LensFlare.BURN_METHOD_COLOR_TRANSFORM)
				{
					var ctVal:Number = 500*burnFactor/_projectionLength;
					_ct = new ColorTransform(1, 1, 1, 1, ctVal, ctVal, ctVal, 0);
					_burnClip.transform.colorTransform = _ct;
				}
			}
						
			if(_halo)
				placeItem(-1, _halo);
						
			for(var i:uint; i<_flares.length; i++)
				placeItem(i, _flares[i]);
		}
		
		private function placeItem(index:int, item:Sprite):void
		{
			var i:uint = index + 1;
			
			item.x = _sourceProjection.x + _projectionVersor.x*i*_projectionLength*spreadFactor;
			item.y = _sourceProjection.y + _projectionVersor.y*i*_projectionLength*spreadFactor;
			
			if(useScaling)
			{
				var sc:Number;
				
				if(i == 0)
					sc = haloScaleFactor/_projectionLength;
				else
					sc = i*scaleFactor/_projectionLength;
				
				sc = sc < scaleMax ? sc : scaleMax;
				
				item.scaleX = item.scaleY = sc;
			}
			
			if(useAlpha)
			{			
				var a:Number = alphaFactor/_projectionLength;
				a = a < alphaMax ? a : alphaMax;
				a = a > alphaMin ? a : alphaMin;
				item.alpha = a;
			}
			
			if(useRotation)
			{
				var r:Number = rotationFactor*_projectionLength;
				item.rotation = r;
			}
			
			if(!item.visible)
				item.visible = true;
		}
	}
}