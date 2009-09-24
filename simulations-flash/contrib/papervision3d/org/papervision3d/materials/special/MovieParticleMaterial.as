package org.papervision3d.materials.special
{
	import flash.display.DisplayObject;
	import flash.utils.Dictionary;
	
	import org.papervision3d.core.geom.renderables.Particle;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.render.material.IUpdateBeforeMaterial;	

	/**
	 * @author Seb Lee-Delisle
	 * 
	 * version 0.1 of MovieAssetParticleMaterial that uses a reference to a
	 * library symbol to create a particle.
	 * 
	 */
	public class MovieParticleMaterial extends BitmapParticleMaterial implements IUpdateBeforeMaterial //, IUpdateAfterMaterial
	{
		
	
		// TODO create object to store bitmap and spriterectdata (and USECOUNT!!!) for each type of bitmap
		public static var bitmapLibrary : Dictionary = new Dictionary(true); // add usecount to ParticleBitmap
		
		//private var _animated : Boolean; // animated movieclip - FOR FUTURE USE! NOT IMPLEMENTED YET :-) 
		
		//public var createUnique : Boolean = false; 
		
		/**
		* The MovieClip that is used as a texture.
		*/
		public var movie :DisplayObject;

		/**
		* A Boolean value that determines whether the MovieClip is transparent. The default value is true, which, 
		* although slower, is usually what you need for particles.
		*/
		public var movieTransparent :Boolean;
		
		public var animated : Boolean; 
		public var actualSize : Boolean = false; 
	
		
		// __________________________________________________ NEW
		
		/**
		 * A Particle material that is made from a single DisplayObject (Sprite, MovieClip, etc) or a
		 * Class that extends a DisplayObject (ie a library symbol)
		 * 
		 * v0.1 - TODO implement reusable assets in the same way as MovieAssetMaterial
		 * 
		 * @param linkageID		The Library symbol to make the material out of.
		 * @param transparent	[optional] - whether the image is transparent or not
		 * @param animated		[optional] NOT IMPLEMENTED YET! Please do not use!
		 * @param createUnique	If true, we'll make a bitmap especially for use with this instance of the material, otherwise we'll use a cached version (if there is one)
		 * 
		 */

		public function MovieParticleMaterial(displayobject: DisplayObject, transparent:Boolean = true, animated : Boolean = false)
		{
			
			if(displayobject) movie = displayobject;
			this.animated = animated; 
			this.movieTransparent = transparent; 
			
			updateParticleBitmap();
			
			super(particleBitmap);
			
		}
		
		
		
		public function updateParticleBitmap(scale : Number = 1, posX : Number=0, posY : Number=0) : void
		{
			if(particleBitmap)				
				particleBitmap.create(movie, scale, movieTransparent);
			else
				particleBitmap = new ParticleBitmap(movie, scale, false, movieTransparent);
			
		}
		
		override public function updateRenderRect(particle: Particle) : void
		{
			
			
	
			if(actualSize)
			{
				updateParticleBitmap(particle.renderScale*particle.size, particle.vertex3D.vertex3DInstance.x, particle.vertex3D.vertex3DInstance.y); 
				
			} 
			else if(animated)
			{
				updateParticleBitmap(scale); 
				
				
			}			
			super.updateRenderRect(particle); 
			
			if(actualSize) 
			{
				//particle.drawMatrix.translate(-(particle.vertex3D.vertex3DInstance.x%1), -(particle.vertex3D.vertex3DInstance.y%1)); 
		
			}
		}
		
		public function updateBeforeRender(renderSessionData:RenderSessionData):void
		{
			
			
		}
		
		
		
	}
}
