package org.papervision3d.core.render.data
{
	
	/**
	 * @Author Ralph Hauwert
	 */
	 
	import flash.display.Sprite;
	
	import org.papervision3d.core.clipping.DefaultClipping;
	import org.papervision3d.core.culling.IParticleCuller;
	import org.papervision3d.core.culling.ITriangleCuller;
	import org.papervision3d.core.proto.CameraObject3D;
	import org.papervision3d.core.proto.SceneObject3D;
	import org.papervision3d.core.render.IRenderEngine;
	import org.papervision3d.view.Viewport3D;
	
	
	public class RenderSessionData
	{
		//Replacement for camera.sorted.
		public var sorted:Boolean;
		
		public var triangleCuller:ITriangleCuller;
		public var particleCuller:IParticleCuller;
		
		public var viewPort:Viewport3D;
		public var container:Sprite;
		public var scene:SceneObject3D;
		public var camera:CameraObject3D;
		public var renderer:IRenderEngine;
		public var renderStatistics:RenderStatistics;
		public var renderObjects:Array;
		public var renderLayers:Array;
		public var clipping:DefaultClipping;
		public var quadrantTree:QuadTree;
		
		public function RenderSessionData():void
		{
			this.renderStatistics = new RenderStatistics();
		}
		
		public function destroy():void
		{
			triangleCuller = null;
			particleCuller = null;
			viewPort = null;
			container = null;
			scene = null;
			camera = null;
			renderer = null;
			renderStatistics = null;
			renderObjects = null;
			renderLayers = null;
			clipping = null;
			quadrantTree = null;
		}
		
		public function clone():RenderSessionData
		{
			var c:RenderSessionData = new RenderSessionData();
			c.triangleCuller = triangleCuller;
			c.particleCuller = particleCuller;
			c.viewPort = viewPort;
			c.container = container;
			c.scene = scene;
			c.camera = camera;
			c.renderer = renderer;
			c.renderStatistics = renderStatistics.clone();
			c.clipping = clipping;
			c.quadrantTree = quadrantTree;
			return c;
		}
		
	}
}