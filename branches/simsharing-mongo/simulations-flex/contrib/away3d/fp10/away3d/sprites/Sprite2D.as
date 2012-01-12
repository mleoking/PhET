package away3d.sprites
{
    import away3d.core.base.*;
    import away3d.core.project.*;
    import away3d.core.utils.*;
    
    import flash.display.BitmapData;
	
	/**
	 * Spherical billboard (always facing the camera) sprite object that uses a bitmapData object as it's texture.
	 * Draws 2d images inline with z-sorted triangles in a scene.
	 */
    public class Sprite2D extends Object3D
    {
		/**
		 * Defines the bitmapData object to use for the sprite texture.
		 */
        public var bitmap:BitmapData;
        
        /**
        * Defines the overall scaling of the sprite object
        */
        public var scaling:Number;
        
        /**
        * Defines the overall 2d rotation of the sprite object
        */
        public var rotation:Number;
		
    	/**
    	 * Defines whether the texture bitmap is smoothed (bilinearly filtered) when drawn to screen
    	 */
        public var smooth:Boolean;
        
        /**
        * An optional offset value added to the z depth used to sort the sprite
        */
        public var deltaZ:Number;
    	
		/**
		 * Creates a new <code>Sprite2D</code> object.
		 * 
		 * @param	bitmap				The bitmapData object to be used as the sprite's texture.
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
        public function Sprite2D(bitmap:BitmapData, init:Object = null)
        {
        	this.bitmap = bitmap;
        	
            super(init);
    
            scaling = ini.getNumber("scaling", 1, {min:0});
            rotation = ini.getNumber("rotation", 0);
            smooth = ini.getBoolean("smooth", false);
            deltaZ = ini.getNumber("deltaZ", 0);
            
            projectorType = ProjectorType.SPRITE;
        }
    }
}
