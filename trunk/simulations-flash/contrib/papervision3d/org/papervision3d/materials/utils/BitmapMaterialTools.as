package org.papervision3d.materials.utils
{
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.geom.Matrix;
	
	import org.papervision3d.materials.BitmapMaterial;

	
	public class BitmapMaterialTools
	{
		public static function createBitmapMaterial(bitmapClass:Class, oneSided:Boolean=true):BitmapMaterial
		{
			var texture:BitmapData = getTexture(bitmapClass);
			var material:BitmapMaterial = new BitmapMaterial(texture);
			material.oneSide = oneSided;
			return material;
		}
		
		public static function getTexture(bitmapClass:Class):BitmapData
		{
			var bm:Bitmap = Bitmap(new bitmapClass());
			var texture  :BitmapData = new BitmapData(bm.width, bm.height, true,0xFFFFFF);
			texture.draw(bm, new Matrix());
			return texture;
		}

		/**
		 * Mirrors the bitmap over its X axis
		 * 
		 * @param	bitmap The bitmap to mirror.
		 */ 
		public static function mirrorBitmapX(bitmap:BitmapData):void
		{
			var tmp:Bitmap = new Bitmap(bitmap.clone());
			tmp.scaleX = -1;
			tmp.x = bitmap.width;
			bitmap.draw(tmp, tmp.transform.matrix);
			tmp.bitmapData.dispose();
		}
				
		/**
		 * Mirrors the bitmap over its Y axis
		 * 
		 * @param	bitmap The bitmap to mirror.
		 */ 
		public static function mirrorBitmapY(bitmap:BitmapData):void
		{
			var tmp:Bitmap = new Bitmap(bitmap.clone());
			tmp.scaleY = -1;
			tmp.y = bitmap.height;
			bitmap.draw(tmp, tmp.transform.matrix);
			tmp.bitmapData.dispose();
		}
	}
}