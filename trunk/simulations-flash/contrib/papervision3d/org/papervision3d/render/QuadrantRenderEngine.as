package org.papervision3d.render
{
	
	/**
	 * @Author Ralph Hauwert
	 */
	import org.papervision3d.cameras.Camera3D;
	import org.papervision3d.core.clipping.draw.Clipping;
	import org.papervision3d.core.clipping.draw.RectangleClipping;
	import org.papervision3d.core.proto.CameraObject3D;
	import org.papervision3d.core.proto.SceneObject3D;
	import org.papervision3d.core.render.IRenderEngine;
	import org.papervision3d.core.render.command.RenderableListItem;
	import org.papervision3d.core.render.data.QuadTree;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.render.data.RenderStatistics;
	import org.papervision3d.core.render.filter.AbstractQuadrantFilter;
	import org.papervision3d.core.render.filter.BasicRenderFilter;
	import org.papervision3d.core.render.filter.QuadrantFilter;
	import org.papervision3d.core.render.filter.QuadrantZFilter;
	import org.papervision3d.core.render.material.MaterialManager;
	import org.papervision3d.core.render.project.BasicProjectionPipeline;
	import org.papervision3d.core.render.sort.BasicRenderSorter;
	import org.papervision3d.core.utils.StopWatch;
	import org.papervision3d.events.RendererEvent;
	import org.papervision3d.scenes.Scene3D;
	import org.papervision3d.view.Viewport3D;
	import org.papervision3d.view.layer.ViewportLayer;
	
	/**
	 * <code>BasicRenderEngine</code> links <code>Viewport3D</code>s, 
	 * <code>Scene3D</code>, and <code>Camera3D</code>s together
	 *  by gathering in all of their data, rendering the data, then calling the 
	 *  necessary functions to update from the rendered data
	 */	
	public class QuadrantRenderEngine extends BasicRenderEngine implements IRenderEngine
	{
		
		public var quadTree:QuadTree = new QuadTree();
		private var clip:Clipping;
		//private var quadFilter:AbstractQuadrantFilter;// = new QuadrantFilter();
		
		public var quadFilters:Array = [];
		
		public static var CORRECT_Z_FILTER:Number = 0x01;
		public static var QUAD_SPLIT_FILTER:Number = 0x02;
		public static var ALL_FILTERS:Number = CORRECT_Z_FILTER + QUAD_SPLIT_FILTER;
	
		/**
		 * Creates and prepares all the objects and events needed for rendering
		 */
		public function QuadrantRenderEngine(type:Number = 3):void
		{
			
			if(type & QUAD_SPLIT_FILTER){
				
				quadFilters.push(new QuadrantFilter());
			}
			
			if(type & CORRECT_Z_FILTER){
				
				quadFilters.push(new QuadrantZFilter());
			}	
			
			
			init();			 
		}
		
		/** @private */
		protected override function init():void
		{
			renderStatistics = new RenderStatistics();
			
			projectionPipeline = new BasicProjectionPipeline();
			
			stopWatch = new StopWatch();
				
			sorter = new BasicRenderSorter();
			filter = new BasicRenderFilter();
			
			renderList = new Array();
			clipping = null;
			
			clip = new Clipping();
			
			renderSessionData = new RenderSessionData();
			renderSessionData.renderer = this;
			
			projectionDoneEvent = new RendererEvent(RendererEvent.PROJECTION_DONE, renderSessionData);
			renderDoneEvent = new RendererEvent(RendererEvent.RENDER_DONE, renderSessionData);
		}
		
		/**
		 * Takes the data from the scene, camera, and viewport, renders it, then updates the viewport
		 * 
		 * @param camera			The <code>CameraObject3D</code> looking at the scene
		 * @param scene				The <code>Scene3D</code> holding the <code>DisplayObject3D</code>'s you want rendered
		 * @param viewPort			The <code>Viewport3D</code> that will display your scene
		 * 
		 * @return RenderStatistics		The <code>RenderStatistics</code> objectholds all the data from the last render
		 */
		override public function renderScene(scene:SceneObject3D, camera:CameraObject3D, viewPort:Viewport3D):RenderStatistics
		{
			// Set the camera's viewport so it can resize its frustum.
			
			camera.viewport = viewPort.sizeRectangle;
			
			//Update the renderSessionData object.
			renderSessionData.scene = scene;
			renderSessionData.camera = camera;
			renderSessionData.viewPort = viewPort;
			renderSessionData.container = viewPort.containerSprite;
			renderSessionData.triangleCuller = viewPort.triangleCuller;
			renderSessionData.particleCuller = viewPort.particleCuller;
			renderSessionData.renderObjects = scene.objects;
			renderSessionData.renderLayers = null;
			renderSessionData.renderStatistics.clear();
			renderSessionData.clipping = clipping;
			renderSessionData.quadrantTree = quadTree;
			
			//quadFilter = new QuadrantFilter();
			
			quadTree.clip = clip;
			
			if(clipping)
				clipping.reset(renderSessionData);
			
			//Clear the viewport.
			viewPort.updateBeforeRender(renderSessionData);
			
			//Project the Scene (this will fill up the renderlist).
			projectionPipeline.project(renderSessionData);
			if(hasEventListener(RendererEvent.PROJECTION_DONE)){
				dispatchEvent(projectionDoneEvent);
			}
			
			//Render the Scene. TODO: delete null if layers is deleted from doRender
			doRender(renderSessionData, null);
			if(hasEventListener(RendererEvent.RENDER_DONE)){
				dispatchEvent(renderDoneEvent);
			}
			
			return renderSessionData.renderStatistics;
		}
		
		
		
		/** @private */
		private function getLayerObjects(layers:Array):Array{
			var array:Array = new Array();
			
			for each (var vpl:ViewportLayer in layers){
				array = array.concat(vpl.getLayerObjects());
			}
			return array;
		}
		
		//TODO: layers parameter isn't used. Delete?
		/** @private */
		protected override function doRender(renderSessionData:RenderSessionData, layers:Array = null):RenderStatistics
		{
			stopWatch.reset();
			stopWatch.start();
			
			//Update Materials.
			MaterialManager.getInstance().updateMaterialsBeforeRender(renderSessionData);

			//Filter the list
			filter.filter(renderList);

			clip = new RectangleClipping(-renderSessionData.viewPort.viewportWidth/2, -renderSessionData.viewPort.viewportHeight/2, renderSessionData.viewPort.viewportWidth/2, renderSessionData.viewPort.viewportHeight/2);//new Clipping();
			
			for each(var qf:AbstractQuadrantFilter in quadFilters){
				qf.filterTree(quadTree, Scene3D(renderSessionData.scene), Camera3D(renderSessionData.camera), clip);
			}
			
			quadTree.render(renderSessionData, renderSessionData.viewPort.containerSprite.graphicsChannel);
			
			//Update Materials
			MaterialManager.getInstance().updateMaterialsAfterRender(renderSessionData);
			
			renderSessionData.renderStatistics.renderTime = stopWatch.stop();
			renderSessionData.viewPort.updateAfterRender(renderSessionData);
			return renderStatistics;
		}
		
		override public function addToRenderList(renderCommand:RenderableListItem):int{
			quadTree.add(renderCommand);
			return 1;
		}
		
		
	}
}