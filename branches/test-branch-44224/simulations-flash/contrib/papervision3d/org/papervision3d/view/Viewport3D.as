package org.papervision3d.view 
{
	import flash.display.Sprite;
	import flash.display.StageAlign;
	import flash.display.StageScaleMode;
	import flash.events.Event;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.utils.Dictionary;
	
	import org.papervision3d.core.culling.DefaultLineCuller;
	import org.papervision3d.core.culling.DefaultParticleCuller;
	import org.papervision3d.core.culling.DefaultTriangleCuller;
	import org.papervision3d.core.culling.ILineCuller;
	import org.papervision3d.core.culling.IParticleCuller;
	import org.papervision3d.core.culling.ITriangleCuller;
	import org.papervision3d.core.culling.RectangleLineCuller;
	import org.papervision3d.core.culling.RectangleParticleCuller;
	import org.papervision3d.core.culling.RectangleTriangleCuller;
	import org.papervision3d.core.culling.ViewportObjectFilter;
	import org.papervision3d.core.geom.renderables.Triangle3D;
	import org.papervision3d.core.log.PaperLogger;
	import org.papervision3d.core.render.IRenderEngine;
	import org.papervision3d.core.render.command.IRenderListItem;
	import org.papervision3d.core.render.command.RenderableListItem;
	import org.papervision3d.core.render.data.RenderHitData;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.utils.InteractiveSceneManager;
	import org.papervision3d.core.view.IViewport3D;
	import org.papervision3d.objects.DisplayObject3D;
	import org.papervision3d.view.layer.ViewportBaseLayer;
	import org.papervision3d.view.layer.ViewportLayer;

	/**
	 * @Author Ralph Hauwert
	 */
	 
	/* Changed to protected methods on 11/27/2007 by John */
	/* Added LineCulling on 22 May 08 by Seb Lee-Delisle */
	public class Viewport3D extends Sprite implements IViewport3D
	{
		//use namespace org.papervision3d.core.ns.pv3dview;
		/** @private */
		protected var _width:Number;
		/** @private */
		protected var _hWidth:Number;
		/** @private */
		protected var _height:Number;
		/** @private */
		protected var _hHeight:Number;
		/** @private */
		protected var _autoClipping:Boolean;
		/** @private */
		protected var _autoCulling:Boolean;
		/** @private */
		protected var _autoScaleToStage:Boolean;
		/** @private */
		protected var _interactive:Boolean;
		/** @private */
		protected var _lastRenderer:IRenderEngine;
		/** @private */
		protected var _viewportObjectFilter:ViewportObjectFilter;
		/** @private */
		protected var _containerSprite:ViewportBaseLayer;
		/** @private */
		protected var _layerInstances:Dictionary;
		/**
		 * sizeRectangle stores the width and the height of the Viewport3D sprite
		 * @see #viewportWidth
		 * @see #viewportHeight
		 */
		public var sizeRectangle:Rectangle;
		/**
		 * cullingRectangle stores the width, height, x, y of the culling rectangle. It's used to determine the bounds in which the triangles are drawn.
		 * @see #autoCulling
		 */
		public var cullingRectangle:Rectangle;
		/**
		 * triangleCuller uses the cullingRectangle to determine which triangles will not be rendered in BasicRenderEngine
		 * @see #autoCulling
		 */
		public var triangleCuller:ITriangleCuller;
		/**
		 * particleCuller uses the cullingRectangle to determine which particles will not be rendered in BasicRenderEngine
		 * @see #autoCulling
		 */
		public var particleCuller:IParticleCuller;
		/**
		 * lineCuller uses the culling Rectangle to determine which particles will not be rendered in BasicRenderEngine
		 * @see #autoCulling
		 */
		public var lineCuller:ILineCuller;
		/**
		 * lastRenderList stores RenderableListItems (Triangles, Lines, Pixels, Particles, Fog) of everything that was rendered in the last pass. This list is used to determine hitTests in hitTestPoint2D.
		 * @see #hitTestPoint2D()
		 */
		public var lastRenderList:Array;
		/**
		 * interactiveSceneManager manages the interaction between the user's mouse and the Papervision3D scene. This is done by checking the mouse against renderHitData. renderHitData is generated from hitTestPoint2D and passed into the interactiveSceneManager to check agains the various mouse actions.
		 * @see #hitTestPoint2D()
		 * @see org.papervision3d.core.utils.InteractiveSceneManager#renderHitData
		 */
		public var interactiveSceneManager:InteractiveSceneManager;
		/** @private */
		protected var renderHitData:RenderHitData;
		private var stageScaleModeSet :Boolean = false; 
		
		/**
		 * @param viewportWidth 	Width of the viewport
		 * @param viewportHeight 	Height of the viewport
		 * @param autoScaleToStage 	Determines whether the viewport should resize when the stage resizes
		 * @param interactive 		Determines whether the viewport should listen for Mouse events by creating an <code>InteractiveSceneManager</code>
		 * @param autoClipping 		Determines whether DisplayObject3Ds outside the rectangle of the viewport should be rendered
		 * @param autoCulling 		Detemines whether only the objects in front of the camera should be rendered. In other words, if a triangle is hidden by another triangle from the camera, it will not be rendered.
		 */
		public function Viewport3D(viewportWidth:Number = 640, viewportHeight:Number = 480, autoScaleToStage:Boolean = false, interactive:Boolean = false, autoClipping:Boolean = true, autoCulling:Boolean = true)
		{
			super();
			init();
			
			this.interactive = interactive;
			
			this.viewportWidth = viewportWidth;
			this.viewportHeight = viewportHeight;
			
			this.autoClipping = autoClipping;
			this.autoCulling = autoCulling;
			
			this.autoScaleToStage = autoScaleToStage;
			
			this._layerInstances = new Dictionary(true);
		}

		/**
		 * Removes all references and sets the viewport's
		 * InteractiveSceneManager to null for a future
		 * garbage collection sweep
		 */
		public function destroy():void
		{
			if(interactiveSceneManager)
			{
				interactiveSceneManager.destroy();
				interactiveSceneManager = null;
			}
			lastRenderList = null;
		}

		/**
		 * @private
		 */
		protected function init():void
		{
			this.renderHitData = new RenderHitData();
			
			lastRenderList = new Array();
			sizeRectangle = new Rectangle();
			cullingRectangle = new Rectangle();
			
			_containerSprite = new ViewportBaseLayer(this);
			_containerSprite.doubleClickEnabled = true;
			
			addChild(_containerSprite);
		
			addEventListener(Event.ADDED_TO_STAGE, onAddedToStage);
			addEventListener(Event.REMOVED_FROM_STAGE, onRemovedFromStage);
		}

		/**
		 * Checks the Mouse x and y against the <code>RenderHitData</code>
		 * @return RenderHitData of the current mouse location
		 */		
		public function hitTestMouse():RenderHitData
		{
			var p:Point = new Point(containerSprite.mouseX, containerSprite.mouseY);
			return hitTestPoint2D(p);
		}

		/**
		 * Checks a <code>Point</code> against the <code>RenderHitData</code>
		 * of the viewport
		 * @param point		a 2d <code>Point</code> you want to analyze into 3d space
		 * @return <code>RenderHitData</code> of the given <code>Point</code>
		 */
		public function hitTestPoint2D(point:Point):RenderHitData
		{
			renderHitData.clear();
			if(interactive)
			{
				var rli:RenderableListItem;
				var rhd:RenderHitData = renderHitData;
				var rc:IRenderListItem;
				for(var i:uint = lastRenderList.length;rc = lastRenderList[--i]; )
				{
					if(rc is RenderableListItem)
					{
						rli = rc as RenderableListItem;
						rhd = rli.hitTestPoint2D(point, rhd);
						if(rhd.hasHit)
						{				
							return rhd;
						}
					}
				}
			}
			return renderHitData;
		}
		
		public function hitTestPointObject(point:Point, object:DisplayObject3D):RenderHitData
		{
			if(interactive){
				var rli:RenderableListItem;
				var rhd:RenderHitData = new RenderHitData();
				var rc:IRenderListItem;
				
				for(var i:uint = lastRenderList.length; rc = lastRenderList[--i]; )
				{
					if(rc is RenderableListItem)
					{
						rli = rc as RenderableListItem;
						
						if(rli.renderableInstance is Triangle3D){
							if(Triangle3D(rli.renderableInstance).instance != object)
								continue;
						}else{
							continue;
						}
						
						rhd = rli.hitTestPoint2D(point, rhd);
						
						if(rhd.hasHit)
						{				
							return rhd;
						}
					}
				}
			}
			
			return new RenderHitData();
		}

		/**
		 * Creates or receives a <code>ViewportLayer</code> of the given <code>DisplayObject3D</code>
		 * @param do3d			A <code>DisplayObject3D</code> used to either find the layer or create a new one
		 * @param createNew		Forces the creation of a new layer
		 * @param recurse		Adds the <code>DisplayObject3D</code> as well as all of its children to a new layer
		 * @return <code>ViewportLayer</code> of the given <code>DisplayObject3D</code>
		 */
		public function getChildLayer(do3d:DisplayObject3D, createNew:Boolean = true, recurse:Boolean = true):ViewportLayer
		{
			return containerSprite.getChildLayer(do3d, createNew, recurse);
		}

		/**
		 * Gets the layer of the RenderListItem. Most-likely internal use.
		 * @param rc			A RenderableListItem to look for
		 * @param setInstance	sets the container to the layer
		 * @return 				The found <code>ViewportLayer</code>
		 */
		public function accessLayerFor(rc:RenderableListItem, setInstance:Boolean = false):ViewportLayer
		{
			var do3d:DisplayObject3D;
			
			if(rc.renderableInstance)
			{
				do3d = rc.renderableInstance.instance;

				do3d = do3d.parentContainer ? do3d.parentContainer : do3d;
				
				if(containerSprite.layers[do3d])
				{
					if(setInstance)
					{
						do3d.container = containerSprite.layers[do3d];
					}
					return containerSprite.layers[do3d];
				}else if(do3d.useOwnContainer)
				{
					return containerSprite.getChildLayer(do3d, true, true);	
				}
			}
			
			return containerSprite;
		}

		/**
		 * Triggered when added to the stage to start listening to stage resizing
		 */
		protected function onAddedToStage(event:Event):void
		{
			if(_autoScaleToStage) 
			{
				setStageScaleMode();
			}
			stage.addEventListener(Event.RESIZE, onStageResize);
			onStageResize();
		}

		/**
		 * Triggered when removed from the stage to remove the stage resizing listener
		 */
		protected function onRemovedFromStage(event:Event):void
		{
			stage.removeEventListener(Event.RESIZE, onStageResize);
		}
		
		/**
		 * Resizes the viewport when the stage is resized (if autoScaleToStage == true)
		 */
		protected function onStageResize(event:Event = null):void
		{
			if(_autoScaleToStage)
			{
				viewportWidth = stage.stageWidth;
				viewportHeight = stage.stageHeight;
			}
		}

		protected function setStageScaleMode() : void
		{
			if(!stageScaleModeSet)
			{
				PaperLogger.info("Viewport autoScaleToStage : Papervision has changed the Stage scale mode."); 
			
            	stage.align = StageAlign.TOP_LEFT;
            	stage.scaleMode = StageScaleMode.NO_SCALE;	
            	stageScaleModeSet = true; 		
			}
			
		}

		/**
		 * Sets the viewport width
		 * @param width		A number designating the width of the viewport
		 */
		public function set viewportWidth(width:Number):void
		{
			_width = width;
			_hWidth = width / 2;
			containerSprite.x = _hWidth;
			
			cullingRectangle.x = -_hWidth;
			cullingRectangle.width = width;
			
			sizeRectangle.width = width;
			if(_autoClipping)
			{
				scrollRect = sizeRectangle;
			}
		}

		/**
		 * Width of the <code>Viewport3D</code>
		 */
		public function get viewportWidth():Number
		{
			return _width;
		}

		/**
		 * Sets the the height of the <code>Viewport3D</code>
		 * @param height 	A number designating the height of the <code>Viewport3D</code>
		 */
		public function set viewportHeight(height:Number):void
		{
			_height = height;
			_hHeight = height / 2;
			containerSprite.y = _hHeight;
			
			cullingRectangle.y = -_hHeight;
			cullingRectangle.height = height;
			
			sizeRectangle.height = height;
			if(_autoClipping)
			{
				scrollRect = sizeRectangle;
			}
		}

		/**
		 * Height of the Viewport
		 */
		public function get viewportHeight():Number
		{
			return _height;
		}

		/**
		 * The <code>Sprite</code> holding the <code>Viewport3D</code>
		 */
		public function get containerSprite():ViewportLayer
		{
			return _containerSprite;	
		}
		
		/**
		 * Whether clipping is enabled (not rendering bitmap data outside the rectangle of the viewport by making use of the <code>Sprite.scrollRect</code>)
		 * @see flash.display.Sprite#scrollRect
		 * @see http://www.gskinner.com/blog/archives/2006/11/understanding_d.html
		 */
		public function get autoClipping():Boolean
		{
			return _autoClipping;	
		}

		public function set autoClipping(clip:Boolean):void
		{
			if(clip)
			{
				scrollRect = sizeRectangle;
			}else
			{
				scrollRect = null;
			}
			_autoClipping = clip;
		}
		
		/**
		 * Whether culling is enabled (not rendering triangles hidden behind other triangles)
		 * @see #lineCuller
		 * @see #particleCuller
		 * @see #triangleCuller
		 */
		public function get autoCulling():Boolean
		{
			return _autoCulling;
		}
		
		public function set autoCulling(culling:Boolean):void
		{
			if(culling)
			{
				triangleCuller = new RectangleTriangleCuller(cullingRectangle);
				particleCuller = new RectangleParticleCuller(cullingRectangle);
				lineCuller = new RectangleLineCuller(cullingRectangle);
			}else if(!culling)
			{
				triangleCuller = new DefaultTriangleCuller();
				particleCuller = new DefaultParticleCuller();
				lineCuller = new DefaultLineCuller();
			}
			_autoCulling = culling;	
		}

		/**
		 * Whether the <code>Viewport3D</code> should scale with the <code>Stage</code>
		 */
		public function set autoScaleToStage(scale:Boolean):void
		{
			_autoScaleToStage = scale;
			if(scale && stage != null)
			{
				setStageScaleMode();
				onStageResize();
			}
			
		}
		
		/**
		 * The auto scale to stage boolean flag
		 */
		public function get autoScaleToStage():Boolean
		{
			return _autoScaleToStage;
		}
		
		/**
		 * Whether the <code>Viewport3D</code> should listen for <code>Mouse</code> events and create an <code>InteractiveSceneManager</code>
		 */
		public function set interactive(b:Boolean):void
		{
			if(b != _interactive)
			{
				if(_interactive && interactiveSceneManager)
				{
					interactiveSceneManager.destroy();
					interactiveSceneManager = null;
				}
				_interactive = b;
				if(b)
				{
					interactiveSceneManager = new InteractiveSceneManager(this);
				}
			}
		}

		/**
		 * The interactive boolean flag
		 */
		public function get interactive():Boolean
		{
			return _interactive;
		}

		/**
		 * Updates a <code>ViewportLayer</code> prior to the 3d data being rendered into the 2d scene
		 * @param renderSessionData		All the information regarding the current renderSession packed into one class	
		 */
		public function updateBeforeRender(renderSessionData:RenderSessionData):void
		{
			lastRenderList.length = 0;
			
			if(renderSessionData.renderLayers)
			{
				for each(var vpl:ViewportLayer in renderSessionData.renderLayers)
				{ 
					vpl.updateBeforeRender();
				}
			}else
			{
				_containerSprite.updateBeforeRender();
			}
			
			_layerInstances = new Dictionary(true);
		}

		/**
		 * Updates a <code>ViewportLayer</code> after the 3d data is rendered into the 2d scene
		 * @param renderSessionData		All the information regarding the current renderSession packed into one class	
		 */
		public function updateAfterRender(renderSessionData:RenderSessionData):void
		{
			if(interactive)
			{
				interactiveSceneManager.updateAfterRender();
			}
			
			if(renderSessionData.renderLayers)
			{
				for each(var vpl:ViewportLayer in renderSessionData.renderLayers) 
				{
					vpl.updateInfo();
					vpl.sortChildLayers();
					vpl.updateAfterRender();
				}
			}else
			{
				containerSprite.updateInfo();
				containerSprite.updateAfterRender();
			}
			
			containerSprite.sortChildLayers();
		}

		/**
		 * Sets the <code>ViewportObjectFilter</code> of the <code>Viewport3D</code>
		 * @param vof		The <code>ViewportObjectFilter</code> you want applied
		 */
		public function set viewportObjectFilter(vof:ViewportObjectFilter):void
		{
			_viewportObjectFilter = vof;
		}

		/**
		 * The <code>ViewportObjectFilter</code>
		 */
		public function get viewportObjectFilter():ViewportObjectFilter
		{
			return _viewportObjectFilter;
		}
	}
}