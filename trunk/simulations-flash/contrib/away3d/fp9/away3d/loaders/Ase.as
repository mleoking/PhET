package away3d.loaders
{
	import away3d.arcane;
	import away3d.core.base.*;
	import away3d.core.utils.*;

	use namespace arcane;
	
    /**
    * File loader for the ASE file format.
    */
    public class Ase extends AbstractParser
    {
    	/** @private */
        arcane override function prepareData(data:*):void
        {
        	ase = Cast.string(data)
        	
            var lines:Array = ase.split('\r\n');
            var vertices:Array = [];
            var uvs:Array = [];
            var faces:Array = [];
            
            if (lines.length == 1)
            	lines = ase.split('\n');
            
            while (lines.length > 0)
            {
                var line:String = lines.shift();
                
                line = line.substr(line.indexOf('*') + 1);
    
                if (line.indexOf('}') >= 0)
                    continue;
    
                var chunk:String = line.substr(0, line.indexOf(' '));
                
                switch (chunk)
                {
                    case 'MESH_VERTEX_LIST':
                        while (true)
                        {
                            var vertexline:String = lines.shift();
                            
                            if (vertexline.indexOf('}') >= 0)
                                break;
                                
                            vertexline = vertexline.substr(vertexline.indexOf('*') + 1);
    
                            var mvl:Array = vertexline.split('\t');
    
                            // Swapped Y and Z
                            var x:Number = parseFloat(mvl[1]) * scaling;
                            var z:Number = parseFloat(mvl[2]) * scaling; 
                            var y:Number = parseFloat(mvl[3]) * scaling;
    
                            vertices.push(new Vertex(x, y, z));
                        }
                        break;
                    case 'MESH_FACE_LIST':
                        while (true)
                        {
                            var faceline:String = lines.shift();
                            
                            if (faceline.indexOf('}') >= 0)
                                break;
                                
                            faceline = faceline.substr(faceline.indexOf('*') + 1);
    
                            var mfl:String = faceline.split('\t')[0]; // ignore: [MESH_SMOOTHING,MESH_MTLID]
                            var drc:Array = mfl.split( ':' ); // separate here
    
                            var con:String;
                            con = drc[2];
                            var a:Vertex = vertices[parseInt(con.substr(0, con.lastIndexOf(' ')))];
    
                            con = drc[3];
                            var b:Vertex = vertices[parseInt(con.substr(0, con.lastIndexOf(' ')))];
    
                            con = drc[4];
                            var c:Vertex = vertices[parseInt(con.substr(0, con.lastIndexOf(' ')))];
    
                            faces.push(new Face(a, b, c));
                        }
                        break;
    
                    case 'MESH_TVERTLIST':
                        while (true)
                        {
                            var textureline:String = lines.shift();
                            
                            if (textureline.indexOf('}') >= 0)
                                break;

                            textureline = textureline.substr(textureline.indexOf('*') + 1);

                            var mtvl:Array = textureline.split('\t');
                            uvs.push(new UV(parseFloat(mtvl[1]), parseFloat(mtvl[2])));
                        }
                        break;
                    case 'MESH_TFACELIST':
                        var num:int = 0;
    
                        while (true)
                        {
                            var mapline:String = lines.shift();
                            
                            if (mapline.indexOf('}') >= 0)
                                break;

                            mapline = mapline.substr(mapline.indexOf('*') + 1);

                            var mtfl:Array = mapline.split('\t');
    
                            var face:Face = faces[num];
                            face.uv0 = uvs[parseInt(mtfl[1])];
                            face.uv1 = uvs[parseInt(mtfl[2])];
                            face.uv2 = uvs[parseInt(mtfl[3])];
                            num++;
    
                        }
                        break;
                }
            }
            
            for each (var f:Face in faces)
                mesh.addFace(f);
            
            mesh.type = ".Ase";
        	
        }
        
        private var ase:String;
        private var mesh:Mesh;
		
    	/**
    	 * A scaling factor for all geometry in the model. Defaults to 100.
    	 */
        public var scaling:Number;
        
		/**
		 * Creates a new <code>Ase</code> object..
		 * 
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 * 
		 * @see away3d.loaders.Ase#parse()
		 * @see away3d.loaders.Ase#load()
		 */
        public function Ase(init:Object = null)
        {
            super(init);
			
            scaling = ini.getNumber("scaling", 100);
			
            mesh = (container = new Mesh(ini)) as Mesh;
            
            binary = false;
        }

		/**
		 * Creates a 3d mesh object from the raw ascii data of the ase file.
		 * 
		 * @param	data				The ascii data of a loaded file.
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 * @param	loader	[optional]	Not intended for direct use.
		 * 
		 * @return						A 3d mesh object representation of the ase file.
		 */
        public static function parse(data:*, init:Object = null):Mesh
        {
            return Loader3D.parseGeometry(data, Ase, init).handle as Mesh;
        }
    	
    	/**
    	 * Loads and parses an ase file into a 3d mesh object.
    	 * 
    	 * @param	url					The url location of the file to load.
    	 * @param	init	[optional]	An initialisation object for specifying default instance properties.
    	 * 
    	 * @return						A 3d loader object that can be used as a placeholder in a scene while the file is loading.
    	 */
        public static function load(url:String, init:Object = null):Loader3D
        {
            return Loader3D.loadGeometry(url, Ase, init);
        }
    }
}
