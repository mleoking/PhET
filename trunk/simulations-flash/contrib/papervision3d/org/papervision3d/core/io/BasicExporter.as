package org.papervision3d.core.io
{
	import flash.utils.ByteArray;
	
	import org.papervision3d.core.io.exporters.*;
	import org.papervision3d.core.log.PaperLogger;
	import org.papervision3d.core.proto.DisplayObjectContainer3D;
	import org.papervision3d.core.proto.SceneObject3D;
	import org.papervision3d.materials.BitmapFileMaterial;
	import org.papervision3d.materials.ColorMaterial;
	import org.papervision3d.materials.WireframeMaterial;
	import org.papervision3d.objects.DisplayObject3D;
	import org.papervision3d.objects.primitives.Sphere;
	import org.papervision3d.scenes.Scene3D;
	
	/**
	 * @author Tim Knip
	 */
	public class BasicExporter
	{
		/**
		 * Exports an object and its children to the specified file format.
		 * NOTE: its best to only export after at least one renderpass (all object-internals like 
		 * rotation-matrices etc. will then be initialized).
		 * 
		 * @param object The object to export. Supported: DisplayObject3D, SceneObject3D.
		 * @param exportFileFormat The file export format. @see org.papervision3d.core.io.exporters.ExportFileFormat
		 * 
		 * @return ByteArray
		 */ 
		public static function export( object : DisplayObjectContainer3D, exportFileFormat : uint = 0 ):ByteArray
		{
			var ba:ByteArray = new ByteArray();
			var root:DisplayObject3D;
			
			if(object is SceneObject3D)
			{
				var scene:SceneObject3D = object as SceneObject3D;
				root = new DisplayObject3D("SceneObjectNode");
				for(var i:int = 0; i < scene.objects.length; i++)
					root.addChild(scene.objects[i]);
			}
			else if(object is DisplayObject3D)
			{
				root = object as DisplayObject3D;	
			}
			else
			{
				PaperLogger.error("ModelExporter#export : don't know how to export this object : " + object);
				return null;
			}
			
			switch(exportFileFormat)
			{
				case ExportFileFormat.COLLADA:
					ba.writeMultiByte(ExportCollada.export(root), "iso-8859-1");
					break;
					
				default:
					PaperLogger.error("ModelExporter#export : unsupported file-format for export!");
					return null;
			}
			return ba;
		}
		
		/**
		 * Test
		 */ 
		public static function test():void
		{
			var object:Sphere = new Sphere(new WireframeMaterial(0xffff00), 20);
			
			object.geometry.faces[0].material = new ColorMaterial(0x00ff00);
			
			var childX:Sphere = new Sphere(new WireframeMaterial(0xff0000));
			object.addChild(childX);
			childX.x = 400;
			childX.name = "SphereOnXAxis";
			
			var childY:Sphere = new Sphere(new WireframeMaterial(0x00ff00));
			object.addChild(childY);
			childY.y = 400;
			childY.name = "SphereOnYAxis";
			
			var material:BitmapFileMaterial = new BitmapFileMaterial("grunt.jpg");
			
			var childZ:Sphere = new Sphere(material);
			childZ.name = "SphereOnZAxis";
			object.addChild(childZ);
			childZ.z = 400;
			
			var scene:Scene3D = new Scene3D();
			scene.addChild(object);

			var ba:ByteArray = export(scene, ExportFileFormat.COLLADA);
		
			// create XML from the ByteArray
			var xml:XML = new XML(ba);
			
			PaperLogger.info(xml.toString());
		}
	}
}