package org.papervision3d.materials.shaders
{
	import flash.display.BitmapData;
	import flash.display.Graphics;
	import flash.geom.Matrix;
	import flash.utils.Dictionary;
	
	import org.papervision3d.core.log.PaperLogger;
	import org.papervision3d.core.material.TriangleMaterial;
	import org.papervision3d.core.render.command.RenderTriangle;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.render.draw.ITriangleDrawer;
	import org.papervision3d.core.render.material.IUpdateAfterMaterial;
	import org.papervision3d.core.render.material.IUpdateBeforeMaterial;
	import org.papervision3d.core.render.shader.ShaderObjectData;
	import org.papervision3d.materials.BitmapMaterial;
	import org.papervision3d.objects.DisplayObject3D;
	/**
	 * @Author Ralph Hauwert
	 */
	public class ShadedMaterial extends TriangleMaterial implements ITriangleDrawer,IUpdateBeforeMaterial,IUpdateAfterMaterial
	{
		private var _shaderCompositeMode:int;
		
		public var shader:Shader;
		public var material:BitmapMaterial;
		public var shaderObjectData:Dictionary;
		
		public function ShadedMaterial(material:BitmapMaterial, shader:Shader, compositeMode:int = 0)
		{
			super();
			this.shader = shader;
			this.material = material;
			shaderCompositeMode = compositeMode;
			init();
		}
		
		private function init():void
		{
			shaderObjectData = new Dictionary();
		}
		
		/**
		 * Localized vars
		 */
		 	 
		 private static var bmp:BitmapData;
		override public function drawTriangle(tri:RenderTriangle, graphics:Graphics, renderSessionData:RenderSessionData, altBitmap:BitmapData = null, altUV:Matrix = null):void
		{
			var sod:ShaderObjectData = ShaderObjectData(shaderObjectData[tri.renderableInstance.instance]);
			if(shaderCompositeMode == ShaderCompositeModes.PER_LAYER){
				//Render shader to layer.
				material.drawTriangle(tri, graphics, renderSessionData, sod.shaderRenderer.outputBitmap);
				shader.renderLayer(tri.triangle, renderSessionData, sod);
			}else if(shaderCompositeMode == ShaderCompositeModes.PER_TRIANGLE_IN_BITMAP){
				//Render shader per tri - TO FIX.
				bmp = sod.getOutputBitmapFor(tri.triangle);
				material.drawTriangle(tri, graphics, renderSessionData, bmp, sod.triangleUVS[tri.triangle] ? sod.triangleUVS[tri.triangle] : sod.getPerTriUVForDraw(tri.triangle));
				shader.renderTri(tri.triangle,renderSessionData,sod,bmp);
			}
		}
		
		public function updateBeforeRender(renderSessionData:RenderSessionData):void
		{
			var sod:ShaderObjectData;
			for each(sod in shaderObjectData){
				sod.shaderRenderer.inputBitmap = material.bitmap;
				if(shaderCompositeMode == ShaderCompositeModes.PER_LAYER){
					if(sod.shaderRenderer.resizedInput){
						sod.shaderRenderer.resizedInput = false;
						sod.uvMatrices = new Dictionary();
					}
					sod.shaderRenderer.clear();	
				}
				if(shader is ILightShader){
					var ls:ILightShader = shader as ILightShader;
					ls.updateLightMatrix(sod,renderSessionData);
				}
			}	
		}
		
		public function updateAfterRender(renderSessionData:RenderSessionData):void
		{
			var sod:ShaderObjectData;
			for each(sod in shaderObjectData){
				shader.updateAfterRender(renderSessionData, sod);
				if(shaderCompositeMode == ShaderCompositeModes.PER_LAYER){
					sod.shaderRenderer.render(renderSessionData);
				}
			}
		}
		
		override public function registerObject(displayObject3D:DisplayObject3D):void
		{
			super.registerObject(displayObject3D);
			var sod:ShaderObjectData = (shaderObjectData[displayObject3D] = new ShaderObjectData(displayObject3D,material,this));
			sod.shaderRenderer.inputBitmap = material.bitmap;
			shader.setContainerForObject(displayObject3D,sod.shaderRenderer.getLayerForShader(shader));
		}
		
		override public function unregisterObject(displayObject3D:DisplayObject3D):void
		{
			super.unregisterObject(displayObject3D);
			var sod:ShaderObjectData = shaderObjectData[displayObject3D];
			sod.destroy();
			delete shaderObjectData[displayObject3D];
		}
		
		public function set shaderCompositeMode(compositeMode:int):void
		{
			_shaderCompositeMode = compositeMode;
		}
		
		public function get shaderCompositeMode():int
		{
			return _shaderCompositeMode;
		}
		
		/**
		 * Debug thingy.
		 */
		public function getOutputBitmapDataFor(object:DisplayObject3D):BitmapData
		{
			if(shaderCompositeMode == ShaderCompositeModes.PER_LAYER){
				if(shaderObjectData[object]){
					var sod:ShaderObjectData = ShaderObjectData(shaderObjectData[object]);
					return sod.shaderRenderer.outputBitmap;
				}else{
					PaperLogger.warning("object not registered with shaded material");
				}
			}else{
				PaperLogger.warning("getOutputBitmapDataFor only works on per layer mode");
			}
			return null;
		}
		
		
		
		override public function destroy():void
		{
			super.destroy();
			var sod:ShaderObjectData;
			for each(sod in shaderObjectData){
				sod.destroy();
			}
			material = null;
			shader = null;
		}
		
	}
	
}
