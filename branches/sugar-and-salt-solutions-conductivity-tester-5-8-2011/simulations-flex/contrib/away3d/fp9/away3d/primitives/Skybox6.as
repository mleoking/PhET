package away3d.primitives
{
    import away3d.core.base.*;
    import away3d.materials.*;

    /**
    * QTVR-style 360 panorama renderer that is initialized with one solid image.
    * A skybox contains six sides that are arranged like the inside of a cube.
    */ 
    public class Skybox6 extends Mesh
    {
    	
		/**
		 * Creates a new <code>Skybox6</code> object.
		 *
		 * @param	material		The material to use for generating all six skybox sides.
		 * 
		 */
        public function Skybox6(material:ITriangleMaterial)
        {
            super({material:material});

            var udelta:Number = 1 / 600;
            var vdelta:Number = 1 / 400;

            if (material is IUVMaterial)
            {
                var uvm:IUVMaterial = material as IUVMaterial;
                udelta = 1 / uvm.width;
                vdelta = 1 / uvm.height;
            }

            var width:Number = 800000;
            var height:Number = 800000;
            var depth:Number = 800000;

            var v000:Vertex = new Vertex(-width/2, -height/2, -depth/2); 
            var v001:Vertex = new Vertex(-width/2, -height/2, +depth/2); 
            var v010:Vertex = new Vertex(-width/2, +height/2, -depth/2); 
            var v011:Vertex = new Vertex(-width/2, +height/2, +depth/2); 
            var v100:Vertex = new Vertex(+width/2, -height/2, -depth/2); 
            var v101:Vertex = new Vertex(+width/2, -height/2, +depth/2); 
            var v110:Vertex = new Vertex(+width/2, +height/2, -depth/2); 
            var v111:Vertex = new Vertex(+width/2, +height/2, +depth/2); 

            var uvrighta:UV = new UV(0/3, 1/2+vdelta);
            var uvrightb:UV = new UV(1/3, 1/2+vdelta);
            var uvrightc:UV = new UV(1/3, 2/2);
            var uvrightd:UV = new UV(0/3, 2/2);

            var uvfronta:UV = new UV(1/3, 1/2+vdelta);
            var uvfrontb:UV = new UV(2/3, 1/2+vdelta);
            var uvfrontc:UV = new UV(2/3, 2/2);
            var uvfrontd:UV = new UV(1/3, 2/2);

            var uvlefta:UV = new UV(2/3, 1/2+vdelta);
            var uvleftb:UV = new UV(3/3, 1/2+vdelta);
            var uvleftc:UV = new UV(3/3, 2/2);
            var uvleftd:UV = new UV(2/3, 2/2);

            var uvbacka:UV = new UV(0/3, 0/2);
            var uvbackb:UV = new UV(1/3-udelta, 0/2);
            var uvbackc:UV = new UV(1/3-udelta, 1/2-vdelta);
            var uvbackd:UV = new UV(0/3, 1/2-0.001);

            var uvupa:UV = new UV(1/3+udelta, 0/2);
            var uvupb:UV = new UV(2/3-udelta, 0/2);
            var uvupc:UV = new UV(2/3-udelta, 1/2-vdelta);
            var uvupd:UV = new UV(1/3+udelta, 1/2-vdelta);

            var uvdowna:UV = new UV(2/3+udelta, 0/2);
            var uvdownb:UV = new UV(3/3, 0/2);
            var uvdownc:UV = new UV(3/3, 1/2-vdelta);
            var uvdownd:UV = new UV(2/3+udelta, 1/2-vdelta);

            addFace(new Face(v011, v001, v101, null, uvrightd, uvrighta, uvrightb));
            addFace(new Face(v011, v101, v111, null, uvrightd, uvrightb, uvrightc));

            addFace(new Face(v100, v110, v101, null, uvfrontb, uvfrontc, uvfronta));
            addFace(new Face(v110, v111, v101, null, uvfrontc, uvfrontd, uvfronta));

            addFace(new Face(v000, v010, v100, null, uvleftb, uvleftc, uvlefta));
            addFace(new Face(v100, v010, v110, null, uvlefta, uvleftc, uvleftd));

            addFace(new Face(v000, v001, v010, null, uvbacka, uvbackb, uvbackd));
            addFace(new Face(v010, v001, v011, null, uvbackd, uvbackb, uvbackc));

            addFace(new Face(v010, v011, v110, null, uvupd, uvupa, uvupc));
			addFace(new Face(v011, v111, v110, null, uvupa, uvupb, uvupc));


            addFace(new Face(v000, v100, v001, null, uvdowna, uvdownb, uvdownd));
            addFace(new Face(v001, v100, v101, null, uvdownd, uvdownb, uvdownc));

            quarterFaces();
            quarterFaces();

            mouseEnabled = false;
			
			type = "Skybox6";
        	url = "primitive";
        }
    }
    
}