package org.papervision3d.render
{
	
	/**
	 * @Author Ralph Hauwert
	 */
	import flash.geom.Point;
	
	import org.papervision3d.core.clipping.DefaultClipping;
	import org.papervision3d.core.proto.CameraObject3D;
	import org.papervision3d.core.proto.SceneObject3D;
	import org.papervision3d.core.render.AbstractRenderEngine;
	import org.papervision3d.core.render.IRenderEngine;
	import org.papervision3d.core.render.command.IRenderListItem;
	import org.papervision3d.core.render.command.RenderableListItem;
	import org.papervision3d.core.render.data.RenderHitData;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.render.data.RenderStatistics;
	import org.papervision3d.core.render.filter.BasicRenderFilter;
	import org.papervision3d.core.render.filter.IRenderFilter;
	import org.papervision3d.core.render.material.MaterialManager;
	import org.papervision3d.core.render.project.BasicProjectionPipeline;
	import org.papervision3d.core.render.project.ProjectionPipeline;
	import org.papervision3d.core.render.sort.BasicRenderSorter;
	import org.papervision3d.core.render.sort.IRenderSorter;
	import org.papervision3d.core.utils.StopWatch;
	import org.papervision3d.events.RendererEvent;
	import org.papervision3d.view.Viewport3D;
	import org.papervision3d.view.layer.ViewportLayer;
	
	/**
	 * <code>BasicRenderEngine</code> links <code>Viewport3D</code>s, 
	 * <code>Scene3D</code>, and <code>Camera3D</code>s together
	 *  by gathering in all of their data, rendering the data, then calling the 
	 *  necessary functions to update from the rendered data
	 */	
	public class BasicRenderEngine extends AbstractRenderEngine implements IRenderEngine
	{
		/**
		 * The type of projection pipeline used for projecting and culling. Defaults
		 * to BasicProjectionPipeline
		 * 
		 * @see org.papervision3d.core.render.project.BasicProjectionPipeline
		 */
		public var projectionPipeline:ProjectionPipeline;
		/**
		 * The type of z-sorting to be used with the rendered data based on 
		 * their respective screen depth. Defaults to <code>BasicRenderSorter</code>.
		 * 
		 * @see org.papervision3d.core.render.sort.BasicRenderSorter
		 */
		public var sorter:IRenderSorter;
		
		public var clipping:DefaultClipping;
		
		/**
		 * A filter (such as FogFilter) to be used in the renderList. Defaults to 
		 * <code>BasicRenderFilter</code>
		 * 
		 * @see org.papervision3d.core.render.filter.BasicRenderFilter
		 * @see org.papervision3d.core.render.filter.FogFilter
		 */
		public var filter:IRenderFilter;
		/** @private */
		protected var renderDoneEvent:RendererEvent;
		/** @private */
		protected var projectionDoneEvent:RendererEvent;
		/** @private */
		protected var renderStatistics:RenderStatistics;
		/** @private */
		protected var renderList:Array;
		/** @private */
		protected var renderSessionData:RenderSessionData;
		/** @private */
		protected var cleanRHD:RenderHitData = new RenderHitData();
		/** @private */
		protected var stopWatch:StopWatch;
		
		
	
		/**
		 * Creates and prepares all the objects and events needed for rendering
		 */
		public function BasicRenderEngine():void
		{
			init();			 
		}
		
		/**
		 * Destroys all of <code>BasicRenderEngine</code>'s objects for Garbage Collection purposes.
		 */
		public function destroy():void
		{
			renderDoneEvent = null;
			projectionDoneEvent = null;
			projectionPipeline = null;
			sorter = null;
			filter = null;
			renderStatistics = null;
			renderList = null;
			renderSessionData.destroy();
			renderSessionData = null;
			cleanRHD = null;
			stopWatch = null;
			clipping = null;
		}
		/** @private */
		protected function init():void
		{
			renderStatistics = new RenderStatistics();
			
			projectionPipeline = new BasicProjectionPipeline();
			
			stopWatch = new StopWatch();
				
			sorter = new BasicRenderSorter();
			filter = new BasicRenderFilter();
			
			renderList = new Array();
			clipping = null;
			
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
		
		/**
		 * Works similarly to <code>renderScene</code>, but also takes an array 
		 * of specific <code>ViewportLayer3D</code>'s to
		 * render
		 * 
		 * @param camera				The <code>CameraObject3D</code> looking at the scene
		 * @param scene					The <code>Scene3D</code> holding the <code>DisplayObject3D</code>'s you want rendered
		 * @param viewPort				The <code>Viewport3D</code> that will display your scene
		 * 
		 * @return RenderStatistics		The <code>RenderStatistics</code> objectholds all the data from the last render
		 * 
		 * @see #renderScene
		 */
		public function renderLayers(scene:SceneObject3D, camera:CameraObject3D, viewPort:Viewport3D, layers:Array = null):RenderStatistics
		{
			//Update the renderSessionData object.
			renderSessionData.scene = scene;
			renderSessionData.camera = camera;
			renderSessionData.viewPort = viewPort;
			renderSessionData.container = viewPort.containerSprite;
			renderSessionData.triangleCuller = viewPort.triangleCuller;
			renderSessionData.particleCuller = viewPort.particleCuller;
			renderSessionData.renderObjects = getLayerObjects(layers);
			renderSessionData.renderLayers = layers;
			renderSessionData.renderStatistics.clear();
			renderSessionData.clipping = clipping;

			//Clear the viewport.
		
			viewPort.updateBeforeRender(renderSessionData);
			
			//Project the Scene (this will fill up the renderlist).
			projectionPipeline.project(renderSessionData);
			if(hasEventListener(RendererEvent.PROJECTION_DONE)){
				dispatchEvent(projectionDoneEvent);
			}
			
			//Render the Scene.
			doRender(renderSessionData);
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
		protected function doRender(renderSessionData:RenderSessionData, layers:Array = null):RenderStatistics
		{
			stopWatch.reset();
			stopWatch.start();
			
			//Update Materials.
			MaterialManager.getInstance().updateMaterialsBeforeRender(renderSessionData);

			//Filter the list
			filter.filter(renderList);
			
			//Sort entire list.
			sorter.sort(renderList);
			
			var rc:RenderableListItem;
			var viewport:Viewport3D = renderSessionData.viewPort;
			var vpl:ViewportLayer;

			while(rc = renderList.pop())
			{
				
				vpl = viewport.accessLayerFor(rc, true);
				rc.render(renderSessionData, vpl.graphicsChannel);
				viewport.lastRenderList.push(rc);
				vpl.processRenderItem(rc);
			}
			
			//Update Materials
			MaterialManager.getInstance().updateMaterialsAfterRender(renderSessionData);
			
			renderSessionData.renderStatistics.renderTime = stopWatch.stop();
			renderSessionData.viewPort.updateAfterRender(renderSessionData);
			return renderStatistics;
		}
		
		//TODO: Redundant? Someone please tell me a use case scenario: John L.
		/**
		 * @private
		 */
		public function hitTestPoint2D(point:Point, viewPort3D:Viewport3D):RenderHitData
		{
			return viewPort3D.hitTestPoint2D(point);
		}
		
		/**
		 * Adds a <code>renderCommand</code> to the <code>renderList</code>
		 * 
		 * @param renderCommand		A command to be used in the <code>renderList</code>
		 * 
		 * @return int				An integer representing the length of the <code>renderList</code>
		 */
		override public function addToRenderList(renderCommand:RenderableListItem):int
		{
			return renderList.push(renderCommand);
		}
		
		/**
		 * Removes a <code>renderCommand</code> from the <code>renderList</code>
		 * 
		 * @param renderCommand		A command to be removed from the <code>renderList</code>
		 * 
		 * @return int				An integer representing the length of the <code>renderList</code>
		 */
		override public function removeFromRenderList(renderCommand:IRenderListItem):int
		{
			return renderList.splice(renderList.indexOf(renderCommand),1);
		}

	}
}