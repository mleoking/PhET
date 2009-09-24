package org.papervision3d.materials.special
{
	import flash.display.BitmapData;
	import flash.display.DisplayObject;
	import flash.display.Sprite;
	import flash.geom.Matrix;
	import flash.geom.Rectangle;
	
	import org.papervision3d.core.log.PaperLogger;
	
	/**
	 * Used to store the bitmap for a particle material. It also stores scale and offsets for moving the registration point of the bitmap. 
	 * 
	 * @author Seb Lee-Delisle
  	 */
  	 
	public class ParticleBitmap
	{
		public var offsetX : Number ; 
		public var offsetY : Number ; 
		public var scaleX : Number ; 
		public var scaleY : Number ; 
		public var bitmap : BitmapData; 
		public var width : int; 
		public var height : int; 
	
		
		//temporary matrix for drawing the bitmaps into
		private static var drawMatrix : Matrix = new Matrix(); 
		private static var tempSprite : Sprite = new Sprite(); 
		
		
		public function ParticleBitmap(source : * = null, scale : Number = 1, forceMipMap : Boolean = false, transparent : Boolean = true)
		{
			offsetX = 0; 
			offsetY = 0; 
			scaleX = scale; 
			scaleY = scale; 
			if(source is BitmapData)
			{
				bitmap = source as BitmapData;
				width = bitmap.width; 
				height = bitmap.height; 
			} 
			else if (source is DisplayObject)
			{
				create(source as DisplayObject, scale, transparent); 
			}
		}
		
		public function create(clip : DisplayObject, scale : Number = 1, transparent : Boolean = true) : BitmapData
		{
			var bounds : Rectangle = clip.getBounds(clip); 
			
			//expand the bounds rectangle by the scale amount and snap them to pixels
			if(scale!=1)
			{
				// is there a faster way to do floor / ceil that works equally with negative and positive numbers?
				bounds.left = Math.floor(bounds.left*scale); 
				bounds.right = Math.ceil(bounds.right*scale); 
				bounds.top = Math.floor(bounds.top*scale); 
				bounds.bottom = Math.ceil(bounds.bottom*scale);
				scaleX = scaleY = 1/scale; 
			}
			else
			{
				scaleX = scaleY = 1; 
			}
				
			width = bounds.width;
			height = bounds.height; 
			
			offsetX = (bounds.left/scale); 
			offsetY = (bounds.top/scale); 

			drawMatrix.identity(); 
			drawMatrix.translate(-offsetX, -offsetY); 
			drawMatrix.scale(1/scaleX, 1/scaleY);
			
			width = (width==0) ? 1 : width; 
			height = (height==0) ? 1 : height; 
			
			var bitmapwidth : int = roundUpToMipMap(width); 
			var bitmapheight : int = roundUpToMipMap(height); 
			
			// if the size is too big then we need to use a smaller bitmap and change the scale factors
			
			if(bitmapwidth<width) scaleX = width/bitmapwidth; 
			if(bitmapheight<height) scaleY = height/bitmapheight; 
			
			
			// if we don't have a bitmap or the bitmap is too small then make a new one
			// TODO! Make a bitmap factory! 
			if((!bitmap)||(bitmap.width<bitmapwidth)||(bitmap.height<bitmapheight) || (bitmap.height>>1 >= bitmapheight) || (bitmap.width>>1 >= bitmapwidth))
			{
				bitmap = new BitmapData(bitmapwidth, bitmapheight, transparent, 0x00000000);//0x55ff0000); 
			}
			// otherwise just clear the bitmap
			else 
			{
				bounds.x = 0; 
				bounds.y = 0; 
				bitmap.fillRect(bounds, 0x00000000);//0x550000ff); 
			}
			
			bitmap.draw(clip, drawMatrix, null, null, null, true); 
			
			return bitmap ; 
		}
		
		
		
		
		
		
		public function createExact(clip : DisplayObject, posX : Number = 1, posY : Number =1, scaleX : Number = 1, scaleY : Number = 1,  rotation : Number = 0) : BitmapData
		{
			
			//drawMatrix.identity(); 
			//if(rotation!=0) drawMatrix.rotate(rotation);
			//if(scale!=1) drawMatrix.scale(size); 
			this.scaleX = scaleX 
			this.scaleY = scaleY; 
			
			if(clip.parent)
				PaperLogger.warning("ParticleBitmap.createExact - particle movie shouldn't be a child of anything else "); 
			
			//clip.transform.matrix = drawMatrix; 
			
			tempSprite.addChild(clip); 
			clip.x = posX; 
			clip.y = posY; 
			clip.rotation = rotation; 
			clip.scaleX = scaleX; 
			clip.scaleY = scaleY; 
			
			var bounds : Rectangle = clip.getBounds(tempSprite); 
			tempSprite.removeChild(clip); 
			
			//expand the bounds rectangle by the scale amount and snap them to pixels
			
			// is there a faster way to do floor / ceil that works equally with negative and positive numbers?
			bounds.left = Math.floor(bounds.left); 
			bounds.right = Math.ceil(bounds.right); 
			bounds.top = Math.floor(bounds.top); 
			bounds.bottom = Math.ceil(bounds.bottom);

			width = bounds.width;
			height = bounds.height; 
					
			offsetX = (bounds.left/scaleX); 
			offsetY = (bounds.top/scaleY); 

			drawMatrix.identity(); 
			drawMatrix.translate(-offsetX, -offsetY); 
			drawMatrix.scale(1/scaleX, 1/scaleY);
			
			width = (width==0) ? 1 : width; 
			height = (height==0) ? 1 : height; 
			
			if((!bitmap)||(bitmap.width<width)||(bitmap.height<height))
			{
				bitmap = new BitmapData(width, height, true, 0x00000000); 
				
			}
			else 
			{
				bitmap.fillRect(bitmap.rect, 0x00000000); 
			}
			bitmap.draw(clip, drawMatrix, null, null, null, true); 
			
			return bitmap ; 
		}
		
		
		
		/** 
		 * rounds up to the nearest MIPMAP-able size to the value you pass in. 
		 * 
		 * Kudos to Jack Lang for writing this optimised function. 
		 * 
		 * 
		 * */
		protected function roundUpToMipMap ( val : Number ) : uint
		{
		    
		    var r : uint = Math.ceil ( val ) ;
		    
		    var i : uint = 0 ;
		    
		    var ret : uint ;
		    
		    var done : Boolean = false ;
		    
		    if ( r == 0 || r == 1 )
		    {
		        done = true ;
		        
		        ret = r ;
		    }
		    
		    while ( !done )
		    {
		    	// if the number is binary 10 then round down
		        if (( r == 2 ) || (r==3))
		        {
		            done = true ;
		            // round up
		            ret = Math.pow ( 2, i + 2 ) ;
		        }
		        else
		        {
		            i++ ;
		            
		            r = r >> 1 ;
		            
		            if ( i >= 10 )
		            {
		                // at max, capping
		                ret = 2048 ;
		                done = true ;
		            }
		        }
		    }
		    
		    return ret ;
		
		}

		
		
		
	
		/** 
		 * Finds the nearest MIPMAP-able size to the value you pass in. 
		 * 
		 * Kudos to Jack Lang for writing this optimised function. 
		 * 
		 * 
		 * */
		protected function getNearestMipMapSize ( val : Number ) : uint
		{
		    
		    var r : uint = Math.ceil ( val ) ;
		    
		    var i : uint = 0 ;
		    
		    var ret : uint ;
		    
		    var done : Boolean = false ;
		    
		    if ( r == 0 || r == 1 )
		    {
		        done = true ;
		        
		        ret = r ;
		    }
		    
		    while ( !done )
		    {
		    	// if the number is binary 10 then round down
		        if ( r == 2 )
		        {
		            done = true ;
		            // round down
		            ret = Math.pow ( 2, i + 1 ) ;
		        }
		        
		        // otherwise the number is binary 11 so round up
		        else if ( r == 3 )
		        {
		            done = true ;
		            // round up
		            ret = Math.pow ( 2, i + 2 ) ;
		        }
		        else
		        {
		            i++ ;
		            
		            r = r >> 1 ;
		            
		            if ( i >= 10 )
		            {
		                // at max, capping
		                ret = 2048 ;
		                done = true ;
		            }
		        }
		    }
		    
		    return ret ;
		
		}
	}

}