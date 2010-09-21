package away3d.primitives
{
    import away3d.core.base.*;
    
    /**
    * Creates a 3d triangle.
    */ 
    public class Triangle extends Mesh
    {
        private var _face:Face;
        
    	private function buildTriangle(edge:Number, yUp:Boolean):void
        {
            var s3:Number = 1 / Math.sqrt(3);
        	
        	if (yUp)
            	_face = new Face(new Vertex(0, 0, 2*s3*edge), new Vertex(edge, 0, - s3*edge), new Vertex(-edge, 0, - s3*edge), null, new UV(0, 0), new UV(1, 0), new UV(0, 1));
            else
            	_face = new Face(new Vertex(0, 2*s3*edge, 0), new Vertex(edge, - s3*edge, 0), new Vertex(-edge, - s3*edge, 0), null, new UV(0, 0), new UV(1, 0), new UV(0, 1));
            
            addFace(_face);
			
			type = "Triangle";
        	url = "primitive";
        }
        
		/**
		 * Defines the first vertex that makes up the triangle.
		 */
        public function get a():Vertex
        {
            return _face.v0;
        }

        public function set a(value:Vertex):void
        {
            _face.v0 = value;
        }
		
		/**
		 * Defines the second vertex that makes up the triangle.
		 */
        public function get b():Vertex
        {
            return _face.v1;
        }

        public function set b(value:Vertex):void
        {
            _face.v1 = value;
        }
		
		/**
		 * Defines the third vertex that makes up the triangle.
		 */
        public function get c():Vertex
        {
            return _face.v2;
        }

        public function set c(value:Vertex):void
        {
            _face.v2 = value;
        }
		
		/**
		 * Creates a new <code>Triangle</code> object.
		 *
		 * @param	init			[optional]	An initialisation object for specifying default instance properties.
		 */
        public function Triangle(init:Object = null)
        {
            super(init);

            var edge:Number = ini.getNumber("edge", 100, {min:0}) / 2;
			var yUp:Boolean = ini.getBoolean("yUp", true);
			
			buildTriangle(edge, yUp);
        }
    }
}