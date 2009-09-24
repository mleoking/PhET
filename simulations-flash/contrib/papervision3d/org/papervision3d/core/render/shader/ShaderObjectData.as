package org.papervision3d.core.render.shader
{
	import flash.display.BitmapData;
	import flash.geom.Matrix;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.utils.Dictionary;
	
	import org.papervision3d.core.geom.renderables.Triangle3D;
	import org.papervision3d.materials.BitmapMaterial;
	import org.papervision3d.materials.shaders.ShadedMaterial;
	import org.papervision3d.objects.DisplayObject3D;
	
	/**
	 * Author Ralph Hauwert
	 */
	public class ShaderObjectData
	{
		private var origin:Point = new Point(0,0);
		
		public var shaderRenderer:ShaderRenderer;
		public var uvMatrices:Dictionary;
		public var lightMatrices:Dictionary;
		public var object:DisplayObject3D;
		public var material:BitmapMaterial;
		public var shadedMaterial:ShadedMaterial;
		
		public var triangleUVS:Dictionary;
		public var renderTriangleUVS:Dictionary;
		protected var triangleBitmaps:Dictionary;
		public var triangleRects:Dictionary;
		
		public function ShaderObjectData(object:DisplayObject3D, material:BitmapMaterial, shadedMaterial:ShadedMaterial):void
		{
			shaderRenderer = new ShaderRenderer();
			
			lightMatrices = new Dictionary();
			uvMatrices = new Dictionary();
			
			this.object = object;
			this.material = material;
			this.shadedMaterial = shadedMaterial;
			
			triangleUVS = new Dictionary();
			renderTriangleUVS = new Dictionary();
			triangleBitmaps = new Dictionary();
			triangleRects = new Dictionary();
		}
		
		/**
		 * Returns a matrix for the original texturemap coordinates
		 */
		public function getUVMatrixForTriangle(triangle:Triangle3D, perturb:Boolean = false):Matrix
		{
			var mat:Matrix;
			if(!(mat = uvMatrices[triangle])){
				mat = new Matrix();
				if( perturb )
				{
					perturbUVMatrix(mat, triangle, 2);
				}
				else
				{
					if(material.bitmap){
						var txWidth:Number = material.bitmap.width;
						var txHeight:Number = material.bitmap.height;
						var x0:Number = triangle.uv[0].u * txWidth;
						var y0:Number = (1-triangle.uv[0].v) * txHeight;
						var x1:Number = triangle.uv[1].u * txWidth;
						var y1:Number = (1-triangle.uv[1].v) * txHeight;
						var x2:Number = triangle.uv[2].u * txWidth;
						var y2:Number = (1-triangle.uv[2].v) * txHeight;
						mat.tx = x0;
						mat.ty = y0;
						mat.a = (x1 - x0);
						mat.b = (y1 - y0);
						mat.c = (x2 - x0);
						mat.d = (y2 - y0);
					}
				}
				if(material.bitmap){			
					uvMatrices[triangle] = mat;	
				}
			}
			return mat;
		}
		
		/**
		 * Returns a per tri bitmap to use to render to screen.
		 */
		public function getOutputBitmapFor(triangle:Triangle3D):BitmapData
		{
			var r:Rectangle;
			if(!triangleBitmaps[triangle])
			{
				r = getRectFor(triangle);
				var bmp:BitmapData = triangleBitmaps[triangle] = new BitmapData(Math.ceil(r.width), Math.ceil(r.height),false,0);
				var r2:Rectangle = new Rectangle(0,0,bmp.width,bmp.height);
				bmp.copyPixels(material.bitmap, r2, origin);
			}else{
				r = getRectFor(triangle);
			}
			if(material.bitmap && r){
				triangleBitmaps[triangle].copyPixels(material.bitmap, r, origin);
			}
			return triangleBitmaps[triangle];
		}
		
		/**
		 * For per tri mode. Returns a correct uvmap for the material to draw to an individual bitmap to screen.
		 */
		public function getPerTriUVForDraw(triangle:Triangle3D):Matrix
		{
			var mat:Matrix;
			if(!triangleUVS[triangle]){
					mat = (triangleUVS[triangle] = new Matrix());
					var txWidth:Number = material.bitmap.width;
					var txHeight:Number = material.bitmap.height;
					var x0:Number = triangle.uv[0].u * txWidth;
					var y0:Number = (1-triangle.uv[0].v) * txHeight;
					var x1:Number = triangle.uv[1].u * txWidth;
					var y1:Number = (1-triangle.uv[1].v) * txHeight;
					var x2:Number = triangle.uv[2].u * txWidth;
					var y2:Number = (1-triangle.uv[2].v) * txHeight;
					var r:Rectangle = getRectFor(triangle);
					mat.tx = x0-r.x;
					mat.ty = y0-r.y;
					mat.a = (x1 - x0);
					mat.b = (y1 - y0);
					mat.c = (x2 - x0);
					mat.d = (y2 - y0);
					mat.invert();
			}
			return triangleUVS[triangle];
		}
		
		/**
		 * For per tri mode. Returns a correct uvmap for the shader to draw to an individual bitmap.
		 */
		public function getPerTriUVForShader(triangle:Triangle3D):Matrix
		{
			var mat:Matrix;
			if(!renderTriangleUVS[triangle]){
					mat = (renderTriangleUVS[triangle] = new Matrix());
					var txWidth:Number = material.bitmap.width;
					var txHeight:Number = material.bitmap.height;
					var x0:Number = triangle.uv[0].u * txWidth;
					var y0:Number = (1-triangle.uv[0].v) * txHeight;
					var x1:Number = triangle.uv[1].u * txWidth;
					var y1:Number = (1-triangle.uv[1].v) * txHeight;
					var x2:Number = triangle.uv[2].u * txWidth;
					var y2:Number = (1-triangle.uv[2].v) * txHeight;
					var r:Rectangle = getRectFor(triangle);
					mat.tx = x0-r.x;
					mat.ty = y0-r.y;
					mat.a = (x1 - x0);
					mat.b = (y1 - y0);
					mat.c = (x2 - x0);
					mat.d = (y2 - y0);
			}
			return renderTriangleUVS[triangle];
		}
	
		/**
		 * For PER_TRI MODE. Returns a rectangle for the surface size to draw too.
		 */
		public function getRectFor(triangle:Triangle3D):Rectangle
		{
			if(!triangleRects[triangle]){
				var w:Number = material.bitmap.width;
				var h:Number = material.bitmap.height;
				var u0 :Number = triangle.uv[0].u *w ;
				var v0 :Number = ( 1 - triangle.uv[0].v )*h;
				var u1 :Number = triangle.uv[1].u *w;
				var v1 :Number = ( 1 - triangle.uv[1].v )*h;
				var u2 :Number = triangle.uv[2].u*w;
				var v2 :Number = ( 1 - triangle.uv[2].v )*h;
				var minU:Number = Math.min(Math.min(u0, u1), u2);
				var minV:Number = Math.min(Math.min(v0, v1), v2);
				var maxU:Number = Math.max(Math.max(u0, u1), u2);
				var maxV:Number = Math.max(Math.max(v0, v1), v2);
				var rw:Number = maxU - minU;
				var rh:Number = maxV - minV;
				if(rw <= 0){
					rw = 1;
				}
				if(rh <=0){
					rh = 1;
				}
				return (triangleRects[triangle] = new Rectangle(minU, minV, rw,rh));
			}
			return triangleRects[triangle];
		}
		
		public function updateBeforeRender():void
		{
			
		}
		
		public function destroy():void
		{
			var o:Object;
			for each(o in uvMatrices){
				uvMatrices[o] = null;
			}
			uvMatrices = null;
			shaderRenderer.destroy();
			shaderRenderer = null;
			lightMatrices = null;
		}
		
		private function perturbUVMatrix( matrix:Matrix, triangle:Triangle3D, numPixels:Number = 2 ):void
		{
			var txWidth:Number = material.bitmap.width;
			var txHeight:Number = material.bitmap.height;
			var u0:Number = triangle.uv[0].u;
			var v0:Number = (1-triangle.uv[0].v);
			var u1:Number = triangle.uv[1].u;
			var v1:Number = (1-triangle.uv[1].v);
			var u2:Number = triangle.uv[2].u;
			var v2:Number = (1-triangle.uv[2].v);
			
			var x0:Number = u0 * txWidth;
			var y0:Number = v0 * txHeight;
			var x1:Number = u1 * txWidth;
			var y1:Number = v1 * txHeight;
			var x2:Number = u2 * txWidth;
			var y2:Number = v2 * txHeight;
				
			var centroidX:Number = ((u2 + u1 + u0)/3);
			var centroidY:Number = ((v2 + v1 + v0)/3);
			
			var xdir0:Number = u0 - centroidX;
			var ydir0:Number = v0 - centroidY;
			var xdir1:Number = u1 - centroidX;
			var ydir1:Number = v1 - centroidY;
			var xdir2:Number = u2 - centroidX;
			var ydir2:Number = v2 - centroidY;
			
			var xAbsDir0:Number = (xdir0 < 0) ? -xdir0 : xdir0;
			var yAbsDir0:Number = (ydir0 < 0) ? -ydir0 : ydir0;
			var xAbsDir1:Number = (xdir1 < 0) ? -xdir1 : xdir1;
			var yAbsDir1:Number = (ydir1 < 0) ? -ydir1 : ydir1;
			var xAbsDir2:Number = (xdir2 < 0) ? -xdir2 : xdir2;
			var yAbsDir2:Number = (ydir2 < 0) ? -ydir2 : ydir2;
			
			// choose whichever vector component has the greater slope and move  1/component % towards to.  This
			//  gaurantees that the movement will cause a 1 pixel change in from's position.
			var percentDist0:Number = (xAbsDir0 > yAbsDir0) ? 1/xAbsDir0 : 1/yAbsDir0;
			var percentDist1:Number = (xAbsDir1 > yAbsDir1) ? 1/xAbsDir1 : 1/yAbsDir1;
			var percentDist2:Number = (xAbsDir2 > yAbsDir2) ? 1/xAbsDir2 : 1/yAbsDir2;
			
			x0 -= -xdir0*percentDist0*numPixels;
			y0 -= -ydir0*percentDist0*numPixels;
			x1 -= -xdir1*percentDist1*numPixels;
			y1 -= -ydir1*percentDist1*numPixels;
			x2 -= -xdir2*percentDist2*numPixels;
			y2 -= -ydir2*percentDist2*numPixels;
			
			matrix.tx = x0;
			matrix.ty = y0;
			matrix.a = (x1 - x0);
			matrix.b = (y1 - y0);
			matrix.c = (x2 - x0);
			matrix.d = (y2 - y0);
		}
	}
}