package org.papervision3d.objects.parsers
{
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.net.URLLoader;
	import flash.net.URLLoaderDataFormat;
	import flash.net.URLRequest;
	import flash.utils.ByteArray;
	import flash.utils.Endian;
	
	import org.papervision3d.Papervision3D;
	import org.papervision3d.core.geom.TriangleMesh3D;
	import org.papervision3d.core.geom.renderables.Triangle3D;
	import org.papervision3d.core.geom.renderables.Vertex3D;
	import org.papervision3d.core.math.NumberUV;
	import org.papervision3d.core.proto.MaterialObject3D;
	import org.papervision3d.events.FileLoadEvent;
	import org.papervision3d.materials.BitmapFileMaterial;
	import org.papervision3d.materials.ColorMaterial;
	import org.papervision3d.materials.utils.MaterialsList;
	import org.papervision3d.objects.DisplayObject3D;

	/**
	 * 3DS File parser.
	 * 
	 * @author Tim Knip (based on Away3D's Max3DS class : http://away3d.com)
	 */ 
	public class Max3DS extends DisplayObject3D
	{
		/** */
		public var filename:String;
		
		/**
		 * Constuctor
		 * 
		 * @param	name
		 */ 
		public function Max3DS(name:String=null)
		{
			super(name);
			_textureExtensionReplacements = new Object();
		}
		
		/**
		 * Load.
		 * 
		 * @param	asset
		 * @param	materials
		 * @param	textureDir
		 */ 
		public function load(asset:*, materials:MaterialsList=null, textureDir:String="./image/"):void
		{
			this.materials = materials || new MaterialsList();
			
			_textureDir = textureDir || _textureDir;
			
			if(asset is ByteArray)
			{
				this.filename = "NoName.3ds";
				parse(ByteArray(asset));
			}
			else if(asset is String)
			{
				this.filename = String(asset);
				
				var loader:URLLoader = new URLLoader();
				
				loader.dataFormat = URLLoaderDataFormat.BINARY;
				loader.addEventListener(Event.COMPLETE, onFileLoadComplete);
				loader.addEventListener(IOErrorEvent.IO_ERROR, onFileLoadError);
				loader.load(new URLRequest(this.filename));
			}
			else
				throw new Error("Need String or ByteArray!");
		}
		
		/**
		 * Replaces a texture extension with an alternative extension.
		 * 
		 * @param	originalExtension	For example "bmp", "gif", etc
		 * @param	preferredExtension	For example "png"
		 */ 
		public function replaceTextureExtension(originalExtension:String, preferredExtension:String="png"):void
		{
			_textureExtensionReplacements[originalExtension] = preferredExtension;
		}
		
		/**
		 * Build a mesh
		 * 
		 * @param	meshData
		 */ 
		private function buildMesh(meshData:MeshData):void
		{
			var i:int;
			var mesh:TriangleMesh3D = new TriangleMesh3D(null, meshData.vertices, [], meshData.name);
			
			for(i = 0; i < meshData.faces.length; i++)
			{
				var f:Array = meshData.faces[i];
				
				var v0:Vertex3D = mesh.geometry.vertices[f[0]];
				var v1:Vertex3D = mesh.geometry.vertices[f[1]];
				var v2:Vertex3D = mesh.geometry.vertices[f[2]];
				
				var hasUV:Boolean = (meshData.uvs.length == meshData.vertices.length);
				
				var t0:NumberUV = hasUV ? meshData.uvs[f[0]] : new NumberUV();
				var t1:NumberUV = hasUV ? meshData.uvs[f[1]] : new NumberUV();
				var t2:NumberUV = hasUV ? meshData.uvs[f[2]] : new NumberUV();
				
				if(Papervision3D.useRIGHTHANDED)
					mesh.geometry.faces.push(new Triangle3D(mesh, [v2, v1, v0], null, [t2, t1, t0]));
				else
					mesh.geometry.faces.push(new Triangle3D(mesh, [v0, v1, v2], null, [t0, t1, t2]));
			}
			
			for(i = 0; i < meshData.materials.length; i++)
			{
				var mat:MaterialData = meshData.materials[i];
				var material:MaterialObject3D = this.materials.getMaterialByName(mat.name) || MaterialObject3D.DEFAULT;
				
				for(var j:int = 0; j < mat.faces.length; j++)
				{
					var faceIdx:int = mat.faces[j];
					var tri:Triangle3D = mesh.geometry.faces[faceIdx];
					tri.material = material;
				}
			}
			
			mesh.geometry.ready = true;
			mesh.rotationX = Papervision3D.useDEGREES ? -90 : -90 * (Math.PI/180);
			//mesh.rotationY = Papervision3D.useDEGREES ? 180 : 180 * (Math.PI/180);
			
			addChild(mesh);
		}
		
		/**
		 * 
		 * @param	event
		 */ 
		private function onFileLoadComplete(event:Event=null):void
		{
			var loader:URLLoader = event.target as URLLoader;
		
			parse(ByteArray(loader.data));
		}
		
		/**
		 * 
		 * @param	event
		 */ 
		private function onFileLoadError(event:IOErrorEvent):void
		{
			dispatchEvent(new FileLoadEvent(FileLoadEvent.LOAD_ERROR, this.filename));
		}
		
		/**
		 * Parse.
		 * 
		 * @param	data
		 */ 
		private function parse(data:ByteArray):void
		{
			if(!data)
				throw new Error("Invalid ByteArray!");
			
			_data = data;
			_data.endian = Endian.LITTLE_ENDIAN;
			_data.position = 0;
			
			//first chunk is always the primary, so we simply read it and parse it
			var chunk:Chunk3ds = new Chunk3ds();
			readChunk(chunk);
			parse3DS(chunk);
			
			dispatchEvent(new FileLoadEvent(FileLoadEvent.LOAD_COMPLETE, this.filename));
		}
		
		/**
		 * Read the base 3DS object.
		 * 
		 * @param chunk
		 * 
		 */		
		private function parse3DS(chunk:Chunk3ds):void
		{
			while (chunk.bytesRead < chunk.length)
			{
				var subChunk:Chunk3ds = new Chunk3ds();
				readChunk(subChunk);
				switch (subChunk.id)
				{
					case EDIT3DS:
						parseEdit3DS(subChunk);
						break;
					case KEYF3DS:
						skipChunk(subChunk);
						break;
					default:
						skipChunk(subChunk);
				}
				chunk.bytesRead += subChunk.length;
			}
		}
		
		/**
		 * Read the Edit chunk
		 * 
		 * @param chunk
		 */
		private function parseEdit3DS(chunk:Chunk3ds):void
		{
			while (chunk.bytesRead < chunk.length)
			{
				var subChunk:Chunk3ds = new Chunk3ds();
				readChunk(subChunk);
				switch (subChunk.id)
				{
					case MATERIAL:
						parseMaterial(subChunk);
						//skipChunk(subChunk);
						break;
					case MESH:
						var meshData:MeshData = new MeshData();
						meshData.name = readASCIIZString(_data);
						
						subChunk.bytesRead += meshData.name.length + 1;
						
						meshData.vertices = new Array();
						meshData.faces = new Array();
						meshData.uvs = new Array();
						meshData.materials = new Array();
						
						parseMesh(subChunk, meshData);
						
						buildMesh(meshData);
						break;
					default:
						skipChunk(subChunk);
				}
				
				chunk.bytesRead += subChunk.length;
			}
		}
		
		/**
		 * Read a material chunk.
		 * 
		 * @param	chunk
		 */ 
		private function parseMaterial(chunk:Chunk3ds):String
		{
			var ret:String = null;
			var mat:Object = new Object();
			var subChunk:Chunk3ds = new Chunk3ds();
			var colorChunk:Chunk3ds = new Chunk3ds();
				
			mat.textures = new Array();
			
			while (chunk.bytesRead < chunk.length)
			{				
				readChunk(subChunk);
				var p:uint = 0;
				
				switch(subChunk.id)
				{
					case MAT_NAME:
						mat.name = readASCIIZString(_data);
						//trace(mat.name);
						subChunk.bytesRead = subChunk.length;
						break;
					case MAT_AMBIENT:
						p = _data.position;
						readChunk(colorChunk);
						mat.ambient = readColor(colorChunk);
						_data.position = p + colorChunk.length;
						//trace("ambient:"+mat.ambient.toString(16));
						break;
					case MAT_DIFFUSE:
						p = _data.position;
						readChunk(colorChunk);
						mat.diffuse = readColor(colorChunk);
						_data.position = p + colorChunk.length;
						//trace("diffuse:"+mat.diffuse.toString(16));
						break;
					case MAT_SPECULAR:
						p = _data.position;
						readChunk(colorChunk);
						mat.specular = readColor(colorChunk);
						_data.position = p + colorChunk.length;
						//trace("specular:"+mat.specular.toString(16));
						break;
					case MAT_TEXMAP:
						mat.textures.push(parseMaterial(subChunk));
						break;
					case MAT_TEXFLNM:
						ret = readASCIIZString(_data);
						subChunk.bytesRead = subChunk.length;
						break;
					default:
						skipChunk(subChunk);
				}
				chunk.bytesRead += subChunk.length;
			}
			
			if(mat.name && !this.materials.getMaterialByName(mat.name))
			{
				if(mat.textures.length)
				{
					var bitmap:String = mat.textures[0].toLowerCase();
					
					for(var ext:String in _textureExtensionReplacements)
					{
						if(bitmap.indexOf("."+ext) == -1)
							continue;
						var pattern:RegExp = new RegExp("\."+ext, "i");
						bitmap = bitmap.replace(pattern, "."+_textureExtensionReplacements[ext]);
					}
					
					this.materials.addMaterial(new BitmapFileMaterial(_textureDir+bitmap), mat.name);
				}
				else if(mat.diffuse)
				{
					this.materials.addMaterial(new ColorMaterial(mat.diffuse), mat.name);
				}
			}
			
			return ret;
		}
		
		private function parseMesh(chunk:Chunk3ds, meshData:MeshData):void
		{
			while (chunk.bytesRead < chunk.length)
			{
				var subChunk:Chunk3ds = new Chunk3ds();
				readChunk(subChunk);
				switch (subChunk.id)
				{
					case MESH_OBJECT:
						parseMesh(subChunk, meshData);
						break;
					case MESH_VERTICES:
						meshData.vertices = readMeshVertices(subChunk);
						break;
					case MESH_FACES:
						meshData.faces = readMeshFaces(subChunk);
						parseMesh(subChunk, meshData);
						break;
					case MESH_MATER:
						readMeshMaterial(subChunk, meshData);
						break;
					case MESH_TEX_VERT:
						meshData.uvs = readMeshTexVert(subChunk);
						break;
					default:
						skipChunk(subChunk);
				}
				chunk.bytesRead += subChunk.length;
			}
		}
		
		/**
		 * 
		 * @param	chunk
		 */  
		private function readMeshFaces(chunk:Chunk3ds):Array
		{
			var faces:Array = new Array();
			var numFaces:int = _data.readUnsignedShort();
			chunk.bytesRead += 2;
			
			for (var i:int = 0; i < numFaces; i++)
			{
				var v2:uint = _data.readUnsignedShort();
				var v1:uint = _data.readUnsignedShort();
				var v0:uint = _data.readUnsignedShort();
				var visible:Boolean = (_data.readUnsignedShort() as Boolean);
				chunk.bytesRead += 8;
				
				faces.push([v0, v1, v2]);
			}
			return faces;
		}
		
		/**
		 * Read the Mesh Material chunk
		 * 
		 * @param chunk
		 */
		private function readMeshMaterial(chunk:Chunk3ds, meshData:MeshData):void
		{
			var material:MaterialData = new MaterialData();
			
			material.name = readASCIIZString(_data);
			material.faces = new Array();
			
			chunk.bytesRead += material.name.length +1;
			
			var numFaces:int = _data.readUnsignedShort();
			chunk.bytesRead += 2;
			for (var i:int = 0; i < numFaces; i++)
			{
				material.faces.push(_data.readUnsignedShort());
				chunk.bytesRead += 2;
			}
			
			meshData.materials.push(material);
		}
		
		/**
		 * 
		 * @param	chunk
		 *
		 * @return
		 */ 
		private function readMeshTexVert(chunk:Chunk3ds):Array
		{
			var uvs:Array = new Array();
			var numUVs:int = _data.readUnsignedShort();
			chunk.bytesRead += 2;
			
			for (var i:int = 0; i < numUVs; i++)
			{
				uvs.push(new NumberUV(_data.readFloat(), _data.readFloat()));
				chunk.bytesRead += 8;
			}
			return uvs;
		}
		
		/**
		 * 
		 * @param	chunk
		 */ 
		private function readMeshVertices(chunk:Chunk3ds):Array
		{
			var vertices:Array = new Array();
			var numVerts:int = _data.readUnsignedShort();
			chunk.bytesRead += 2;
			
			for (var i:int = 0; i < numVerts; i++)
			{
				vertices.push(new Vertex3D(_data.readFloat(), _data.readFloat(), _data.readFloat()));
				chunk.bytesRead += 12;
			}
			
			return vertices;
		}
		
		/**
		 * Reads a null-terminated ascii string out of a byte array.
		 * 
		 * @param data The byte array to read from.
		 * 
		 * @return The string read, without the null-terminating character.
		 */		
		private function readASCIIZString(data:ByteArray):String
		{
			var readLength:int = 0; // length of string to read
			var l:int = data.length - data.position;
			var tempByteArray:ByteArray = new ByteArray();
			
			for (var i:int = 0; i < l; i++)
			{
				var c:int = data.readByte();
				
				if (c == 0)
				{
					break;
				}
				tempByteArray.writeByte(c);
			}
			
			var asciiz:String = "";
			tempByteArray.position = 0;
			for (i = 0; i < tempByteArray.length; i++)
			{
				asciiz += String.fromCharCode(tempByteArray.readByte());
			}
			return asciiz;
		}
		
		/**
		 * 
		 */ 
		private function readColor(colorChunk:Chunk3ds):int
		{
			var color:int = 0;
			switch(colorChunk.id) 
			{
				case COLOR_RGB:
					color = readColorRGB(colorChunk);
					break;
				case COLOR_F:
					color = readColorScale(colorChunk);
					break;
				default:
					throw new Error("Unknown color chunk: " + colorChunk.id);
			}
			return color;
		}
		
		/**
		 * Read Scaled Color
		 * 
		 * @param	chunk
		 */ 
		private function readColorScale(chunk:Chunk3ds):int
		{
			var color:int = 0;

			for (var i:int = 0; i < 3; i++)
			{
				var c:Number = _data.readFloat();
				var bv:int = 255 * c;
				bv <<= (8 * (2 - i));
				color |= bv;													 
				chunk.bytesRead += 4;
			}
			
			return color;
		}
		
		/**
		 * Read RGB
		 * 
		 * @param	chunk
		 */ 
		private function readColorRGB(chunk:Chunk3ds):int
		{
			var color:int = 0;
			
			for (var i:int = 0; i < 3; i++)
			{
				var c:int = _data.readUnsignedByte();
				color += c*Math.pow(0x100, 2-i);
				chunk.bytesRead++;
			}
			
			return color;
		}
		
		/**
		 * Read id and length of 3ds chunk
		 * 
		 * @param chunk
		 */		
		private function readChunk(chunk:Chunk3ds):void
		{
			chunk.id = _data.readUnsignedShort();
			chunk.length = _data.readUnsignedInt();
			chunk.bytesRead = 6;
		}
		
		/**
		 * Skips past a chunk. If we don't understand the meaning of a chunk id,
		 * we just skip past it.
		 * 
		 * @param chunk
		 */		
		private function skipChunk(chunk:Chunk3ds):void
		{
			_data.position += chunk.length - chunk.bytesRead;
			chunk.bytesRead = chunk.length;
		}
		
		//>----- Color Types --------------------------------------------------------
		
		public const AMBIENT:String = "ambient";
		public const DIFFUSE:String = "diffuse";
		public const SPECULAR:String = "specular";
		
		//>----- Main Chunks --------------------------------------------------------
		
		public const PRIMARY:int = 0x4D4D;
		public const EDIT3DS:int = 0x3D3D;  // Start of our actual objects
		public const KEYF3DS:int = 0xB000;  // Start of the keyframe information
		
		//>----- General Chunks -----------------------------------------------------
		
		public const VERSION:int = 0x0002;
		public const MESH_VERSION:int = 0x3D3E;
		public const KFVERSION:int = 0x0005;
		public const COLOR_F:int = 0x0010;
		public const COLOR_RGB:int = 0x0011;
		public const LIN_COLOR_24:int = 0x0012;
		public const LIN_COLOR_F:int = 0x0013;
		public const INT_PERCENTAGE:int = 0x0030;
		public const FLOAT_PERC:int = 0x0031;
		public const MASTER_SCALE:int = 0x0100;
		public const IMAGE_FILE:int = 0x1100;
		public const AMBIENT_LIGHT:int = 0X2100;
		
		//>----- Object Chunks -----------------------------------------------------
		
		public const MESH:int = 0x4000;
		public const MESH_OBJECT:int = 0x4100;
		public const MESH_VERTICES:int = 0x4110;
		public const VERTEX_FLAGS:int = 0x4111;
		public const MESH_FACES:int = 0x4120;
		public const MESH_MATER:int = 0x4130;
		public const MESH_TEX_VERT:int = 0x4140;
		public const MESH_XFMATRIX:int = 0x4160;
		public const MESH_COLOR_IND:int = 0x4165;
		public const MESH_TEX_INFO:int = 0x4170;
		public const HEIRARCHY:int = 0x4F00;
		
		//>----- Material Chunks ---------------------------------------------------
		
		public const MATERIAL:int = 0xAFFF;
		public const MAT_NAME:int = 0xA000;
		public const MAT_AMBIENT:int = 0xA010;
		public const MAT_DIFFUSE:int = 0xA020;
		public const MAT_SPECULAR:int = 0xA030;
		public const MAT_SHININESS:int = 0xA040;
		public const MAT_FALLOFF:int = 0xA052;
		public const MAT_EMISSIVE:int = 0xA080;
		public const MAT_SHADER:int = 0xA100;
		public const MAT_TEXMAP:int = 0xA200;
		public const MAT_TEXFLNM:int = 0xA300;
		public const OBJ_LIGHT:int = 0x4600;
		public const OBJ_CAMERA:int = 0x4700;
		
		//>----- KeyFrames Chunks --------------------------------------------------
		
		public const ANIM_HEADER:int = 0xB00A;
		public const ANIM_OBJ:int = 0xB002;
		public const ANIM_NAME:int = 0xB010;
		public const ANIM_POS:int = 0xB020;
		public const ANIM_ROT:int = 0xB021;
		public const ANIM_SCALE:int = 0xB022;
	
		private var _data		:ByteArray;
		
		private var _textureDir	:String = "./image/";
		private var _textureExtensionReplacements:Object;
	}
}

class Chunk3ds
{	
	public var id:int;
	public var length:int;
	public var bytesRead:int;	 
}

class MeshData
{
	public var name:String;
	public var vertices:Array;
	public var faces:Array;
	public var uvs:Array;
	public var materials:Array;
}

class MaterialData
{
	public var name:String;
	public var faces:Array;
}
