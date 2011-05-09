package away3d.core.base
{
    /**
    * Keyframe animation morpher
    */
    public class Morpher extends Object3D
    {
        private var weight:Number;
        private var vertices:Mesh;
    	private var _vertices:Array;
    	private var _verticesComp:Array;
		/**
		 * Creates a new <code>Morpher</code> object.
		 *
		 * @param	vertices	A mesh object used to define the starting vertices.
		 */
        public function Morpher(vertices:Mesh)
        {
            this.vertices = vertices;
        }
		
		/**
		 * resets all vertex objects to 0,0,0
		 */
        public function start():void
        {
            weight = 0;
            _vertices = vertices.geometry.vertices;
            for each (var v:Vertex in _vertices)
            {
                v.reset();
            }
        }
		
		/**
		 * interpolates the vertex objects position values between the current vertex positions and the external vertex positions
		 * 
		 * @param	comp	The external mesh used for interpolating values
		 * @param	k		The increment used on the weighting value 
		 */
        public function mix(comp:Mesh, k:Number):void
        {
            weight += k;
            _vertices = vertices.geometry.vertices;
            _verticesComp = comp.geometry.vertices;
            
            var length:int = _vertices.length;
            for (var i:int = 0; i < length; ++i)
            {
                _vertices[i].x += _verticesComp[i].x * k;
                _vertices[i].y += _verticesComp[i].y * k;
                _vertices[i].z += _verticesComp[i].z * k;
            }
        }
		
		/**
		 * resets all vertex objects to the external mesh positions
		 * 
		 * @param	comp	The external mesh used for vertex values
		 */
        public function finish(comp:Mesh):void
        {
            mix(comp, 1 - weight);
            weight = 1;
        }
    }
}
