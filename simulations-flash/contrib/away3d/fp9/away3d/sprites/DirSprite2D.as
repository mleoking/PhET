package away3d.sprites
{
    import away3d.core.base.*;
    import away3d.core.project.*;
    import away3d.core.utils.*;
    
    import flash.display.BitmapData;
    import flash.utils.Dictionary;
	
	/**
	 * Spherical billboard (always facing the camera) sprite object that uses an array of bitmapData objects defined with viewing direction vectors.
	 * Draws 2d directional image dependent on viewing angle inline with z-sorted triangles in a scene.
	 */
    public class DirSprite2D extends Object3D
    {
        private var _vertices:Array = [];
        private var _bitmaps:Dictionary = new Dictionary(true);
        
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
    	
    	public function get vertices():Array
    	{
    		return _vertices;
    	}
    	
    	public function get bitmaps():Dictionary
    	{
    		return _bitmaps;
    	}
    	
		/**
		 * Creates a new <code>DirSprite2D</code> object.
		 * 
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
        public function DirSprite2D(init:Object = null)
        {
            super(init);
    
            scaling = ini.getNumber("scaling", 1, {min:0});
            rotation = ini.getNumber("rotation", 0);
            smooth = ini.getBoolean("smooth", false);
            deltaZ = ini.getNumber("deltaZ", 0);

            var btmps:Array = ini.getArray("bitmaps");
            for each (var btmp:Init in btmps)
            {
                btmp = Init.parse(btmp);
                var x:Number = btmp.getNumber("x", 0);
                var y:Number = btmp.getNumber("y", 0);
                var z:Number = btmp.getNumber("z", 0);
                var b:BitmapData = btmp.getBitmap("bitmap");
                add(x, y, z, b);
            }
            
            projectorType = ProjectorType.DIR_SPRITE;
        }
		
		/**
		 * Adds a new bitmap definition to the array of directional textures.
		 * 
		 * @param		x			The x coordinate of the directional texture.
		 * @param		y			The y coordinate of the directional texture.
		 * @param		z			The z coordinate of the directional texture.
		 * @param		bitmap		The bitmapData object to be used as the directional texture.
		 */
        public function add(x:Number, y:Number, z:Number, bitmap:BitmapData):void
        {
            if (bitmap)
            for each (var v:Vertex in _vertices)
                if ((v.x == x) && (v.y == y) && (v.z == z))
                {
                    Debug.warning("Same base point for two bitmaps: "+v);
                    return;
                }

            var vertex:Vertex = new Vertex(x, y, z);
            _vertices.push(vertex);
            _bitmaps[vertex] = bitmap;
        }
    }
}
