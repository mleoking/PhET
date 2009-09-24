package org.papervision3d.core.io.exporters
{
	import flash.utils.Dictionary;
	
	import org.papervision3d.core.geom.TriangleMesh3D;
	import org.papervision3d.core.geom.renderables.Triangle3D;
	import org.papervision3d.core.geom.renderables.Vertex3D;
	import org.papervision3d.core.math.Matrix3D;
	import org.papervision3d.core.proto.MaterialObject3D;
	import org.papervision3d.materials.BitmapFileMaterial;
	import org.papervision3d.materials.WireframeMaterial;
	import org.papervision3d.materials.utils.MaterialsList;
	import org.papervision3d.objects.DisplayObject3D;
	
	/**
	 * This class lets you export a DisplayObject3D to the Collada file format (*.dae).
	 * <p></p>
	 * 
	 * @author Tim Knip
	 */ 
	public class ExportCollada
	{
		public static var DEFAULT_TEXTURE_DIR:String = ".";
		
		/** Default visuals scene id and name. */
		public static var VISUAL_SCENE_NAME:String = "PapervisionScene";
		
		/** Number of fraction digits to use for floats. */
		public static var FRACTION_DIGITS:int = 5;
		
		/** Boolean indicatin whether to flip faces. */
		public static var REVERSE_WINDING:Boolean = true;
		
		/**
		 * 
		 */ 
		public static function export(object:DisplayObject3D):String
		{
			_hasImages = false;
			_numImages = 0;
			_materialToImageId = new Dictionary(true);
			_numInstances = 0;
			_numMaterials = 0;
			_materialTargets = new Dictionary(true);
			
			prepareMaterials(object);
			
			var xml:String = printLine('<?xml version="1.0" encoding="utf-8"?>');
			xml += printLine('<COLLADA xmlns="http://www.collada.org/2005/11/COLLADASchema" version="1.4.1">');
			
			// export main asset element
			xml += printLine('<asset>', 1);
			xml += printLine('<contributor>', 2);
			xml += printLine('<author>Tim Knip</author>', 3);
			xml += printLine('<authoring_tool>Papervision3D - ColladaExport</authoring_tool>', 3);
			xml += printLine('<comments></comments>', 3);
			xml += printLine('<source_data></source_data>', 3);
			xml += printLine('</contributor>', 2);
			xml += printLine('<created>2008-05-07T00:05:39Z</created>', 2);
			xml += printLine('<modified>2008-05-07T00:05:39Z</modified>', 2);
			xml += printLine('<unit meter="0.01" name="centimeter"/>', 2);
			xml += printLine('<up_axis>Y_UP</up_axis>', 2);
			xml += printLine('</asset>', 1);
			
			// export textures if needed
			if(_hasImages)
			{
				xml += printLine('<library_images>', 1);
				xml += exportImages(object, 2);
				xml += printLine('</library_images>', 1);	
			}
			
			// export materials
			xml += printLine('<library_materials>', 1);
			xml += exportMaterials(object, 2);
			xml += printLine('</library_materials>', 1);
			
			// export effects
			xml += printLine('<library_effects>', 1);
			xml += exportEffects(object, 2);
			xml += printLine('</library_effects>', 1);
			
			// export geometries
			xml += printLine('<library_geometries>', 1);
			xml += exportGeometries(object, 2);
			xml += printLine('</library_geometries>', 1);
			
			// export scenegraph
			xml += printLine('<library_visual_scenes>', 1);
			xml += printLine('<visual_scene id="'+VISUAL_SCENE_NAME+'" name="'+VISUAL_SCENE_NAME+'">', 2);
			xml += exportVisualScene(object, 3);
			xml += printLine('</visual_scene>', 2);
			xml += printLine('</library_visual_scenes>', 1);
			
			// export a default collada-scene
			xml += printLine('<scene>', 1);
			xml += printLine('<instance_visual_scene url="#'+VISUAL_SCENE_NAME+'" />', 2);
			xml += printLine('</scene>', 1);
			
			xml += printLine('</COLLADA>');
			return xml;
		}
		
		/**
		 * Exports the <color> element.
		 * 
		 * @param material
		 * @param indent
		 * 
		 * @return XML string 
		 */ 
		private static function exportColor(material:MaterialObject3D=null, indent:int=0):String 
		{
			var rgba:Array = new Array();
			
			if(material)
			{
				var color:uint = material is WireframeMaterial ? material.lineColor : material.fillColor;
				
				var r:Number = ((color >> 16) & 0xff) / 0xff;
				var g:Number = ((color >> 8) & 0xff) / 0xff;
				var b:Number = (color & 0xff) / 0xff;
				
				rgba.push(r.toFixed(FRACTION_DIGITS), g.toFixed(FRACTION_DIGITS), b.toFixed(FRACTION_DIGITS), 1.0);
			}
			else
			{
				rgba.push(0.0, 0.0, 0.0, 1.0);
			}
			return printLine('<color>' + rgba.join(" ") + '</color>', indent);
		}
		
		/**
		 * Exports the <effect> elements.
		 * 
		 * @param object
		 * @param indent
		 * 
		 * @return XML string 
		 */ 
		private static function exportEffects(object:DisplayObject3D, indent:int=0):String 
		{
			var xml:String = "";
			
			for(var name:String in object.materials.materialsByName)
			{
				var material:MaterialObject3D = object.materials.materialsByName[name];
				var tgt:String = _materialTargets[ material ];
				
				var textureId:String = _materialToImageId[material];
				
				xml += printLine('<effect id="'+tgt+'-fx">', indent);
				xml += printLine('<profile_COMMON>', indent+1);
				
				if(textureId)
				{
					xml += printLine('<newparam sid="'+textureId+'-surface">', indent+2);
        			xml += printLine('<surface type="2D">', indent+3);	
        			xml += printLine('<init_from>'+textureId+'</init_from>', indent+4);	
        			xml += printLine('<format>A8R8G8B8</format>', indent+4);	
        			xml += printLine('</surface>', indent+3);	
        			xml += printLine('</newparam>', indent+2);	
        			
        			xml += printLine('<newparam sid="'+textureId+'-sampler">', indent+2);
        			xml += printLine('<sampler2D>', indent+3);	
        			xml += printLine('<source>'+textureId+'-surface</source>', indent+4);	
        			xml += printLine('<minfilter>LINEAR_MIPMAP_LINEAR</minfilter>', indent+4);
        			xml += printLine('<magfilter>LINEAR</magfilter>', indent+4);
        			xml += printLine('</sampler2D>', indent+3);
        			xml += printLine('</newparam>', indent+2);	
				}
				
				xml += printLine('<technique sid="common">', indent+2);
				xml += printLine('<phong>', indent+3);
				
				xml += printLine('<emission>', indent+4);
				xml += exportColor(null, indent + 5);
				xml += printLine('</emission>', indent+4);
				
				xml += printLine('<ambient>', indent+4);
				xml += exportColor(null, indent + 5);
				xml += printLine('</ambient>', indent+4);
				
				xml += printLine('<diffuse>', indent+4);
				if(textureId)
				{
					xml += printLine('<texture texture="'+textureId+'-sampler" texcoord="TEX0">', indent+5);
					xml += printLine('</texture>', indent+5);
				}
				else
					xml += exportColor(material, indent + 5);
				xml += printLine('</diffuse>', indent+4);
				
				xml += printLine('<specular>', indent+4);
				xml += exportColor(null, indent + 5);
				xml += printLine('</specular>', indent+4);
				
				xml += printLine('<shininess>', indent+4);
				xml += printLine('<float>20.0</float>', indent+5);
				xml += printLine('</shininess>', indent+4);
				
				xml += printLine('<reflectivity>', indent+4);
				xml += printLine('<float>20.0</float>', indent+5);
				xml += printLine('</reflectivity>', indent+4);
				
				xml += printLine('<transparent>', indent+4);
				xml += printLine('<color>1 1 1 1</color>', indent+5);
				xml += printLine('</transparent>', indent+4);
				
				xml += printLine('<transparency>', indent+4);
				xml += printLine('<float>1.0</float>', indent+5);
				xml += printLine('</transparency>', indent+4);
				
				xml += printLine('</phong>', indent+3);
				xml += printLine('</technique>', indent+2);
				xml += printLine('</profile_COMMON>', indent+1);
				xml += printLine('</effect>', indent);
			}
			
			for each(var child:DisplayObject3D in object.children)
				xml += exportEffects(child, indent);
				
			return xml;
		}
		
		/**
		 * Exports a <source> element with float data.
		 * 
		 * @param id
		 * @param values
		 * @param params
		 * @param indent
		 * 
		 * @return XML String
		 */ 
		private static function exportFloatSource(id:String, values:Array, params:Array, indent:int = 0):String
		{
			var xml:String = printLine('<source id="' + id + '">', indent);
			
			var fid:String = id + "-array";
			var cnt:int = values.length;
			var data:String = values.join(" ");
			var i:int;
			
			var line:String = '<float_array id="'+fid+'" count="'+cnt+'">' + data + '</float_array>';
			xml += printLine(line, indent + 1);
			
			xml += printLine('<technique_common>', indent + 1);
			
			var stride:int = params.length;
			cnt = cnt / stride;
			
			xml += printLine('<accessor source="#'+fid+'" count="'+cnt+'" stride="'+stride+'">', indent + 2);
			
			for(i = 0; i < params.length; i++)
				xml += printLine('<param name="'+params[i]+'" type="float" />', indent + 3);
			
			xml += printLine('</accessor>', indent + 2);
			xml += printLine('</technique_common>', indent + 1);
			xml += printLine('</source>', indent);
			return xml;
		}
		
		/**
		 * Export all geometries and child-geometries for a specific DisplayObject3D
		 * 
		 * @param instance
		 * @param indent
		 * 
		 * @return XML string 
		 */ 
		private static function exportGeometries(instance:DisplayObject3D, indent:int=0):String
		{
			var xml:String = "";

			if(instance is TriangleMesh3D)
				xml += exportGeometry(instance as TriangleMesh3D, getInstanceName(instance)+"-geometry", indent);
				
			for each(var child:DisplayObject3D in instance.children)
				xml += exportGeometries(child, indent);
				
			return xml;	
		}
		
		/**
		 * Exports a mesh's geometry as a Collada <geometry> element.
		 * 
		 * @param mesh
		 * @param id
		 * @param indent
		 * 
		 * @return XML string
		 */ 
		private static function exportGeometry(mesh:TriangleMesh3D, id:String, indent:int=0):String 
		{
			var xml:String = printLine('<geometry id="' + id + '" name="'+ id + '">', indent);
			var tri:Triangle3D;
			var v:Vertex3D;
			var trianglesByMaterial:Object = new Object();
			var uvs:Array = new Array();
			var vData:Array = new Array();
			var uvData:Array = new Array();
			var materialName:String;
			var i:int;
			var vindices:Dictionary = new Dictionary(true);
			var uvindices:Dictionary = new Dictionary(true);
			
			xml += printLine('<mesh>', indent + 1);
			
			for(i = 0; i < mesh.geometry.vertices.length; i++)
			{
				v = mesh.geometry.vertices[i];
				vindices[v] = i;
				vData.push(v.x.toFixed(FRACTION_DIGITS));
				vData.push(v.y.toFixed(FRACTION_DIGITS));
				vData.push(v.z.toFixed(FRACTION_DIGITS));
			}
			
			for(i = 0; i < mesh.geometry.faces.length; i++)
			{
				tri = mesh.geometry.faces[i];	
				
				materialName = findMaterialName(tri.material, mesh.materials);	
				
				if(!(trianglesByMaterial[materialName] is Array))
					trianglesByMaterial[materialName] = new Array();
				trianglesByMaterial[materialName].push(i);	
				
				var idx:int = uvs.length;
				
				uvindices[ tri ] = [idx, idx+1, idx+2];
				
				uvs.push(tri.uv0, tri.uv1, tri.uv2);
			}	
			
			// build uv source-data
			for(i = 0; i < uvs.length; i++)
				uvData.push(uvs[i].u.toFixed(FRACTION_DIGITS), uvs[i].v.toFixed(FRACTION_DIGITS));
			
			// export <source> elements for vertices and uvs
			xml += exportFloatSource(id+"-positions", vData, ["X", "Y", "Z"], indent + 2);
			xml += exportFloatSource(id+"-texcoords", uvData, ["S", "T"], indent + 2);
			
			// export <vertices> element
			xml += printLine('<vertices id="'+id+'-vertices">', indent + 2);
			xml += printLine('<input semantic="POSITION" source="#'+id+'-positions" />', indent + 3);
			xml += printLine('</vertices>', indent + 2);
			
			// export a <triangles> element for each material used by the geometry
			for(materialName in trianglesByMaterial)
			{
				var tris:Array = trianglesByMaterial[materialName];
				var p:Array = new Array();
				
				for(i = 0; i < tris.length; i++)
				{
					tri = mesh.geometry.faces[ tris[i] ];
					
					var uva:Array = uvindices[ tri ];
					
					if(REVERSE_WINDING)
					{
						p.push(vindices[tri.v2], uva[2]); 
						p.push(vindices[tri.v1], uva[1]); 
						p.push(vindices[tri.v0], uva[0]); 
					}
					else
					{
						p.push(vindices[tri.v0], uva[0]);
						p.push(vindices[tri.v1], uva[1]);
						p.push(vindices[tri.v2], uva[2]); 
					}
				}
				
				xml += printLine('<triangles material="'+materialName+'" count="'+tris.length+'">', indent + 2);
				xml += printLine('<input semantic="VERTEX" source="#'+id+'-vertices" offset="0" />', indent + 3);
				xml += printLine('<input semantic="TEXCOORD" source="#'+id+'-texcoords" offset="1" set="0" />', indent + 3);
				xml += printLine('<p>'+p.join(" ")+'</p>', indent + 3);
				xml += printLine('</triangles>', indent + 2);
			}
			
			// all done
			xml += printLine('</mesh>', indent + 1);
			xml += printLine('</geometry>', indent);
			return xml;
		}
		
		/**
		 * Exports the <image> elements.
		 * 
		 * @param object
		 * @param indent
		 * 
		 * @return XML string 
		 */ 
		private static function exportImages(object:DisplayObject3D, indent:int=0):String 
		{
			var xml:String = "";
			
			for(var name:String in object.materials.materialsByName)
			{
				var material:MaterialObject3D = object.materials.materialsByName[name];
				
				if(material is BitmapFileMaterial)
				{
					var id:String = "psdFileTex" + (_numImages++);
					var url:String = BitmapFileMaterial(material).url;
					url = url.split("\\").join("/");
					if(url.indexOf("/") != -1)
					{
						var parts:Array = url.split("/");
						url = String(parts.pop());
					}
					
					url = DEFAULT_TEXTURE_DIR + "/" + url;
					url = url.replace(/\/\//, "/");
					
					xml += printLine('<image id="'+id+'" name="'+id+'">', indent);
					xml += printLine('<init_from>'+url+'</init_from>', indent+1);
					xml += printLine('</image>', indent);
					
					_materialToImageId[ material ] = id;
				}
			}
			
			for each(var child:DisplayObject3D in object.children)
				xml += exportImages(child, indent);
				
			return xml;
		}
		
		/**
		 * Exports the <material> elements.
		 * 
		 * @param object
		 * @param indent
		 * 
		 * @return XML string 
		 */ 
		private static function exportMaterials(object:DisplayObject3D, indent:int=0):String 
		{
			var xml:String = "";
			
			for(var name:String in object.materials.materialsByName)
			{
				var material:MaterialObject3D = object.materials.materialsByName[name];
				var tgt:String = _materialTargets[ material ];
				 
				xml += printLine('<material id="'+tgt+'" name="'+tgt+'">', indent);
				xml += printLine('<instance_effect url="#'+tgt+'-fx" />', indent + 1);
				xml += printLine('</material>', indent);
			}
			
			for each(var child:DisplayObject3D in object.children)
				xml += exportMaterials(child, indent);
				
			return xml;
		}
		
		/**
		 * Exports a <matrix> element.
		 * 
		 * @param matrix
		 * @param indent
		 * 
		 * @return XML string 
		 */ 
		private static function exportMatrix(matrix:Matrix3D, indent:int=0):String 
		{
			var data:Array = [
				matrix.n11, matrix.n12, matrix.n13, matrix.n14,
				matrix.n21, matrix.n22, matrix.n23, matrix.n24,
				matrix.n31, matrix.n32, matrix.n33, matrix.n34,
				matrix.n41, matrix.n42, matrix.n43, matrix.n44
			];
			return printLine('<matrix>' + data.join(" ") + '</matrix>', indent);	
		}
		
		/**
		 * Exports a <visual_scene> element.
		 * 
		 * @param object
		 * @param indent
		 * 
		 * @return XML string 
		 */ 
		private static function exportVisualScene(object:DisplayObject3D, indent:int=0):String
		{				
			var id:String = getInstanceName(object);

			var xml:String = printLine('<node id="'+id+'" name="'+id+'">', indent);	
			
			xml += exportMatrix(object.transform, indent + 1);
			
			if(object is TriangleMesh3D)
			{
				xml += printLine('<instance_geometry url="#'+id+'-geometry">', indent + 1);
				xml += printLine('<bind_material>', indent + 2);
				xml += printLine('<technique_common>', indent + 3);
				
				for(var materialName:String in object.materials.materialsByName)
				{
					var material:MaterialObject3D = object.materials.materialsByName[materialName];
					var tgt:String = _materialTargets[material];
					xml += printLine('<instance_material symbol="'+materialName+'" target="#'+tgt+'" />', indent + 4);
				}
				
				xml += printLine('</technique_common>', indent + 3);
				xml += printLine('</bind_material>', indent + 2);
				xml += printLine('</instance_geometry>', indent + 1);
			}
			
			for each(var child:DisplayObject3D in object.children)
				xml += exportVisualScene(child, indent + 1);
			
			xml += printLine('</node>', indent);	
			return xml;
		}
		
		/**
		 * 
		 */ 
		private static function getInstanceName(instance:DisplayObject3D):String 
		{
			if(instance.name && instance.name.length > 2)
				return instance.name;
			instance.name = "Node" + (_numInstances++);
			return instance.name;
		}
		
		/**
		 * 
		 */ 
		private static function prepareMaterials(object:DisplayObject3D):void
		{
			object.materials = object.materials || new MaterialsList();	
			
			if(object.geometry && object.geometry.faces)
			{
				for each(var triangle:Triangle3D in object.geometry.faces)
				{
					var name:String = findMaterialName(triangle.material, object.materials);
					if(!name)
					{
						name = "Material" + _numMaterials;
						object.materials.addMaterial(triangle.material, name);
						_numMaterials++;
					}
				}
			}
			
			for(var materialName:String in object.materials.materialsByName)
			{
				var material:MaterialObject3D = object.materials.materialsByName[ materialName ];
				
				if(material is BitmapFileMaterial)
					_hasImages = true;
	
				_materialTargets[material] = materialName.toLowerCase() + "-target";	
			}
			
			for each(var child:DisplayObject3D in object.children)
				prepareMaterials(child);
		}
		
		/**
		 * 
		 */ 
		private static function findMaterialName(find:MaterialObject3D, list:MaterialsList):String
		{
			for(var name:String in list.materialsByName)
			{
				if(list.materialsByName[name] === find)
					return name;
			}
			return null;
		}
		
		private static function printLine(str:String, indent:int = 0):String
		{
			var s:String = "";
			for(var i:int = 0; i < indent; i++)
				s += "\t";
			return s + str + "\n";
		}
		
		private static var _numInstances		: int = 0;
		private static var _numMaterials		: int = 0;
		private static var _materialTargets		: Dictionary;
		private static var _hasImages			: Boolean;
		private static var _numImages			: int = 0;
		private static var _materialToImageId	: Dictionary;
	}
}