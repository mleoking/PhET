package away3d.primitives
{
    import away3d.core.base.*;
    import away3d.materials.*;

    /**
    * QTVR-style 360 panorama renderer that is initialized with six images.
    * A skybox contains six sides that are arranged like the inside of a cube.
    */ 
    public class Skybox extends Mesh
    {
    	
		/**
		 * Creates a new <code>Skybox</code> object.
		 *
		 * @param	front		The material to use for the skybox front.
		 * @param	left		The material to use for the skybox left.
		 * @param	back		The material to use for the skybox back.
		 * @param	right		The material to use for the skybox right.
		 * @param	up			The material to use for the skybox up.
		 * @param	down		The material to use for the skybox down.
		 * 
		 */
        public function Skybox(front:ITriangleMaterial, left:ITriangleMaterial, back:ITriangleMaterial, right:ITriangleMaterial, up:ITriangleMaterial, down:ITriangleMaterial)
        {
            super();

            var width:Number = 800000;
            var height:Number = 800000;
            var depth:Number = 800000;

            var v000:Vertex = new Vertex(-width/2, -height/2, -depth/2); 
            var v001:Vertex = new Vertex(-width/2, -height/2, depth/2); 
            var v010:Vertex = new Vertex(-width/2, +height/2, -depth/2); 
            var v011:Vertex = new Vertex(-width/2, +height/2, depth/2); 
            var v100:Vertex = new Vertex(width/2, -height/2, -depth/2); 
            var v101:Vertex = new Vertex(width/2, -height/2, depth/2); 
            var v110:Vertex = new Vertex(width/2, +height/2, -depth/2); 
            var v111:Vertex = new Vertex(width/2, +height/2, depth/2); 

            var uva:UV = new UV(1, 0);
            var uvb:UV = new UV(0, 0);
            var uvc:UV = new UV(0, 1);
            var uvd:UV = new UV(1, 1);

            addFace(new Face(v000, v010, v100, back, uva, uvd, uvb));
            addFace(new Face(v010, v110, v100, back, uvd, uvc, uvb));
                                           
            addFace(new Face(v001, v101, v011, front, uvb, uva, uvc));
            addFace(new Face(v011, v101, v111, front, uvc, uva, uvd));
                                           
            addFace(new Face(v000, v100, v001, down, uvb, uva, uvc));
            addFace(new Face(v100, v101, v001, down, uva, uvd, uvc));
                                                 
            addFace(new Face(v010, v011, v110,  up, uvc, uvb, uvd));
            addFace(new Face(v110, v011, v111,  up, uvd, uvb, uva));

            addFace(new Face(v000, v001, v010, left, uvb, uva, uvc));
            addFace(new Face(v001, v011, v010, left, uva, uvd, uvc));
                                                                
            addFace(new Face(v110, v101, v100, right, uvd, uvb, uva));
            addFace(new Face(v110, v111, v101, right, uvd, uvc, uvb));

            quarterFaces();
            quarterFaces();

            mouseEnabled = false;
			
			type = "Skybox";
        	url = "primitive";
        }
    }
    
}