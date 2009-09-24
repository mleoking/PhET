package org.papervision3d.view.stats
{
	import flash.system.System;
	import flash.text.TextField;
	import flash.text.TextFieldAutoSize;
	import flash.text.TextFormat;
	
	import org.papervision3d.core.render.AbstractRenderEngine;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.render.data.RenderStatistics;
	import org.papervision3d.objects.DisplayObject3D;
	import org.papervision3d.scenes.Scene3D;

	public class StatsView extends AbstractStatsView
	{
		public static function countPolys(obj:DisplayObject3D):Number
		{
			var polygonCount:Number = 0;
			polygonCount = recurseDisplayObject(obj, polygonCount);
			return polygonCount;
		}
				
		protected static function recurseDisplayObject(obj:DisplayObject3D, polygonCount:Number):Number
		{
			var polys:Number = 0;
			for each (var childObj:DisplayObject3D in obj.children)
			{
				 polys += recurseDisplayObject(childObj, polygonCount);
			}
			
			if( obj.geometry && obj.geometry.faces ){ polys += obj.geometry.faces.length;}
			
			return polys;
		}
		
		
		protected var statsFormat:TextFormat;
		public var totalPolyCount:Number = 0;
		
		protected var polyCountField:TextField;
		protected var memInfoTestField:TextField;
		protected var fpsInfoTextField:TextField;
		protected var objectInfoTextField:TextField;
		protected var renderInfoTextField:TextField;
		protected var cullingInfoTextField:TextField;
		
		public function StatsView(renderEngine:AbstractRenderEngine)
		{
			super();
			this.renderEngine = renderEngine;
			init();
		}
		
		protected function init():void
		{
			setupView();
		}
		
		protected function setupView():void
		{
			opaqueBackground = 0;
			
			statsFormat = new TextFormat("Arial", 12, 0xFFFFFF, false, false, false);
			
			fpsInfoTextField = new TextField();
			fpsInfoTextField.y = 0;
			fpsInfoTextField.autoSize = TextFieldAutoSize.LEFT;
			fpsInfoTextField.defaultTextFormat = statsFormat;
			addChild(fpsInfoTextField);
			
			objectInfoTextField = new TextField();
			objectInfoTextField.y = 12;
			objectInfoTextField.autoSize = TextFieldAutoSize.LEFT;
			objectInfoTextField.defaultTextFormat = statsFormat;
			addChild(objectInfoTextField);
			
			renderInfoTextField = new TextField();
			renderInfoTextField.y = 24;
			renderInfoTextField.autoSize = TextFieldAutoSize.LEFT;
			renderInfoTextField.defaultTextFormat = statsFormat;
			addChild(renderInfoTextField);
			
			cullingInfoTextField = new TextField();
			cullingInfoTextField.y = 36;
			cullingInfoTextField.autoSize = TextFieldAutoSize.LEFT;
			cullingInfoTextField.defaultTextFormat = statsFormat;
			addChild(cullingInfoTextField);
			
			memInfoTestField = new TextField();
			memInfoTestField.y = 48;
			memInfoTestField.autoSize = TextFieldAutoSize.LEFT;
			memInfoTestField.defaultTextFormat = statsFormat;
			addChild(memInfoTestField);
			
			polyCountField = new TextField();
			polyCountField.y = 60;
			polyCountField.autoSize = TextFieldAutoSize.LEFT;
			polyCountField.defaultTextFormat = statsFormat;
			addChild(polyCountField);
		}
		
		public function updatePolyCount(scene:Scene3D):void
		{
			totalPolyCount = 0;
			
			for each( var obj:DisplayObject3D in scene.children) 
			{
				totalPolyCount += countPolys(obj);
			}
		}
		
		override public function set renderSessionData(renderSessionData:RenderSessionData):void
		{
			var stats:RenderStatistics = renderSessionData.renderStatistics;
			
			objectInfoTextField.text = "Tri : "+stats.triangles+" Sha : "+stats.shadedTriangles+" Lin : "+stats.lines+" Par : "+stats.particles;
			renderInfoTextField.text = "Ren: "+stats.rendered+" RT : "+stats.renderTime+" PT : "+stats.projectionTime;
			cullingInfoTextField.text = "COb : "+stats.culledObjects+ " CTr : "+stats.culledTriangles+" CPa : "+stats.culledParticles+" FOb : "+stats.filteredObjects;
			
			memInfoTestField.text = "Mem : "+(System.totalMemory/1024/1024).toFixed(2) + "MB";
			
			polyCountField.text = "poly count : " + totalPolyCount;
		}
		
		override public function set fps(fps:int):void
		{
			fpsInfoTextField.text = "FPS : "+fps;
			fpsInfoTextField.setTextFormat(statsFormat);
		}
	
	}
}