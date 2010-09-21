package org.papervision3d.materials
{
	import flash.display.BitmapData;
	import flash.utils.describeType;
	import flash.utils.getDefinitionByName;
	
	import org.papervision3d.core.log.PaperLogger;
	import org.papervision3d.core.render.draw.ITriangleDrawer;
	
	/**
	* The BitmapAssetMaterial class creates a texture from a Bitmap library symbol.
	*
	* Materials collects data about how objects appear when rendered.
	*
	*/
	public class BitmapAssetMaterial extends BitmapMaterial implements ITriangleDrawer
	{
		private static var _library :Object = new Object();
		private static var _count   :Object = new Object();
		
		/**
		* A texture object.
		*/		
		override public function get texture():Object
		{
			return this._texture;
		}
		/**
		* @private
		*/
		override public function set texture( asset:Object ):void
		{
			if( asset is String == false )
			{
				PaperLogger.error("BitmapMaterial.texture requires a BitmapData object for the texture");
				return;
			}
			
			bitmap   = createBitmapFromLinkageID( String(asset) );
			_texture = asset;
		}
		// ______________________________________________________________________ NEW

		/**
		* The BitmapAssetMaterial class creates a texture from a Bitmap library asset.
		*
		* @param	linkageID				The linkage name of the Bitmap symbol in the library.
		*/

		public function BitmapAssetMaterial( linkageID:String, precise:Boolean = false )
		{
			texture = linkageID;
			this.precise = precise;
		}


		// ______________________________________________________________________ CREATE BITMAP

		/**
		* [internal-use]
		*
		* @param	asset
		* @return
		*/
		protected function createBitmapFromLinkageID( asset:String ):BitmapData
		{
			// Remove previous bitmap
			if( this._texture != asset )
			{
				_count[this._texture]--;

				var prevBitmap:BitmapData = _library[this._texture];

				if( prevBitmap && _count[this._texture] == 0 )
					prevBitmap.dispose();
			}

			// Retrieve from library or...
			var bitmapOk :BitmapData;
			var bitmap   :BitmapData = _library[asset];

			// ...loadBitmap
			if( ! bitmap )
			{
				var BitmapAsset:Class = getDefinitionByName( asset ) as Class;

				var description:XML = describeType( BitmapAsset );

				// Check if Flash 9 Alpha
				if( description..constructor.length() == 0 )
					bitmap = new BitmapAsset() as BitmapData;
				else
					bitmap = new BitmapAsset( 0, 0 ) as BitmapData;
				
				bitmapOk = createBitmap( bitmap );

				_library[asset] = bitmapOk;
				_count[asset] = 0;
			}
			else
			{
				bitmapOk = bitmap;
				maxU = maxV = 1;
				_count[asset]++;
			}

			return bitmapOk;
		}
		
		
	}
}