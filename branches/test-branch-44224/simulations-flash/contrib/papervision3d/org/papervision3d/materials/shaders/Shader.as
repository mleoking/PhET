package org.papervision3d.materials.shaders
{
	import flash.display.BitmapData;
	import flash.display.BlendMode;
	import flash.display.Sprite;
	import flash.events.EventDispatcher;
	import flash.filters.BitmapFilter;
	import flash.utils.Dictionary;
	
	import org.papervision3d.core.geom.renderables.Triangle3D;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.render.shader.ShaderObjectData;
	import org.papervision3d.objects.DisplayObject3D;
	
	/**
	 * @Author Ralph Hauwert
	 */
	public class Shader extends EventDispatcher implements IShader
	{
		protected var _filter:BitmapFilter;
		protected var _blendMode:String = BlendMode.MULTIPLY;
		protected var _object:DisplayObject3D;
		protected var layers:Dictionary;
		
		public function Shader()
		{
			super();
			this.layers = new Dictionary(true);
		}
				
		public function renderLayer(triangle:Triangle3D, renderSessionData:RenderSessionData, sod:ShaderObjectData):void
		{
			
		}
		
		public function renderTri(triangle:Triangle3D, renderSessionData:RenderSessionData, sod:ShaderObjectData, bmp:BitmapData):void
		{
			
		}
		
		public function destroy():void
		{
			
		}
		
		public function setContainerForObject(object:DisplayObject3D, layer:Sprite):void
		{
			layers[object] = layer;
		}
		
		public function set filter(filter:BitmapFilter):void
		{
			_filter = filter;
		}
		
		public function get filter():BitmapFilter
		{
			return _filter;	
		}
		
		public function set layerBlendMode(blendMode:String):void
		{
			_blendMode = blendMode;
		}
		
		public function get layerBlendMode():String
		{
			return _blendMode;
		}
		
		public function updateAfterRender(renderSessionData:RenderSessionData, sod:ShaderObjectData):void
		{
			
		}
		
	}
}