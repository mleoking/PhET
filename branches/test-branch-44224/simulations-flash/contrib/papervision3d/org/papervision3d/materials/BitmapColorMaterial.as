package org.papervision3d.materials
{
	import flash.display.BitmapData;
	import flash.display.Graphics;
	import flash.display.Sprite;
	import flash.geom.Matrix;
	
	import org.papervision3d.core.render.command.RenderTriangle;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.render.draw.ITriangleDrawer;

	public class BitmapColorMaterial extends BitmapMaterial implements ITriangleDrawer
	{
		private var uvMatrix:Matrix;
		
		private static const BITMAP_WIDTH:int = 16;
		private static const BITMAP_HEIGHT:int = 16;
		
		public function BitmapColorMaterial(color:Number=0xFF00FF, alpha:Number=1)
		{
			bitmap = new BitmapData(BITMAP_WIDTH,BITMAP_HEIGHT,fillAlpha > 1,0x00000000);
			fillColor = color;
			fillAlpha = alpha;
			precise = false;
			init();
		}
		
		private function init():void
		{
			createBitmapData();
			createStaticUVMatrix();
		}
		
		override public function drawTriangle(tri:RenderTriangle, graphics:Graphics, renderSessionData:RenderSessionData,altBitmap:BitmapData=null, altUV:Matrix = null):void
		{
			if(bitmap){
				var x0:Number = tri.v0.x;
				var y0:Number = tri.v0.y;
				var x1:Number = tri.v1.x;
				var y1:Number = tri.v1.y;
				var x2:Number = tri.v2.x;
				var y2:Number = tri.v2.y;
				
				_triMatrix.a = x1 - x0;
				_triMatrix.b = y1 - y0;
				_triMatrix.c = x2 - x0;
				_triMatrix.d = y2 - y0;
				_triMatrix.tx = x0;
				_triMatrix.ty = y0;
					
				_localMatrix.a = uvMatrix.a;
				_localMatrix.b = uvMatrix.b;
				_localMatrix.c = uvMatrix.c;
				_localMatrix.d = uvMatrix.d;
				_localMatrix.tx = uvMatrix.tx;
				_localMatrix.ty = uvMatrix.ty;
				_localMatrix.concat(_triMatrix);
				
				graphics.beginBitmapFill( bitmap, _localMatrix, tiled, smooth);
				graphics.moveTo( x0, y0 );
				graphics.lineTo( x1, y1 );
				graphics.lineTo( x2, y2 );
				graphics.lineTo( x0, y0 );
				graphics.endFill();
				renderSessionData.renderStatistics.triangles++;
			}
			
		}
		
		private function createBitmapData():void
		{
			var sprite:Sprite = new Sprite();
			var graphics:Graphics = sprite.graphics;
			graphics.beginFill(fillColor, fillAlpha);
			graphics.drawRect(0,0,BITMAP_WIDTH,BITMAP_HEIGHT);
			graphics.endFill();
			bitmap.draw(sprite);
		}
		
		private function createStaticUVMatrix():void
		{
			var w  :Number = BITMAP_WIDTH;
			var h  :Number = BITMAP_HEIGHT;

			var u0 :Number = w;
			var v0 :Number = 0;
			var u1 :Number = 0;
			var v1 :Number = 0;
			var u2 :Number = w;
			var v2 :Number = h;
			
			// Precalculate matrix & correct for mip mapping
			var at :Number = ( u1 - u0 );
			var bt :Number = ( v1 - v0 );
			var ct :Number = ( u2 - u0 );
			var dt :Number = ( v2 - v0 );

			uvMatrix = new Matrix( at, bt, ct, dt, u0, v0 );
			uvMatrix.invert();
		}
		
		

	}
}