package org.papervision3d.view.layer {
	import flash.display.Graphics;
	import flash.display.Sprite;
	import flash.utils.Dictionary;
	
	import org.papervision3d.core.log.PaperLogger;
	import org.papervision3d.core.ns.pv3dview;
	import org.papervision3d.core.render.command.RenderableListItem;
	import org.papervision3d.objects.DisplayObject3D;
	import org.papervision3d.view.Viewport3D;
	import org.papervision3d.view.layer.util.ViewportLayerSortMode;	
	/**
	 * @Author Ralph Hauwert
	 */
	public class ViewportLayer extends Sprite
	{
		use namespace pv3dview;
		
		public var childLayers			:Array;
		public var layers				:Dictionary = new Dictionary(true);
		public var displayObject3D		:DisplayObject3D;
		public var displayObjects		:Dictionary = new Dictionary(true);
		
		public var layerIndex			:Number;
		public var forceDepth			:Boolean = false;
		public var screenDepth			:Number = 0;
		public var originDepth			:Number = 0;
		public var weight				:Number = 0;
		public var sortMode				:String = ViewportLayerSortMode.Z_SORT;
		public var dynamicLayer			:Boolean = false;
		public var graphicsChannel		:Graphics;
		protected var viewport			:Viewport3D;		
		public function ViewportLayer(viewport:Viewport3D, do3d:DisplayObject3D, isDynamic:Boolean = false)
		{
			super();
			this.viewport = viewport;
			this.displayObject3D = do3d;
			this.dynamicLayer = isDynamic;
			this.graphicsChannel = this.graphics;
		
			if(isDynamic){
				this.filters = do3d.filters;
				this.blendMode = do3d.blendMode;
				this.alpha = do3d.alpha;
			}
			
			if(do3d){
				addDisplayObject3D(do3d);
				do3d.container = this;
			}
			
			init();
		}
		
		public function addDisplayObject3D(do3d:DisplayObject3D, recurse:Boolean = false):void{
			
			if(!do3d) return;
			
			displayObjects[do3d] = do3d;
			dispatchEvent(new ViewportLayerEvent(ViewportLayerEvent.CHILD_ADDED, do3d, this));
			
			if(recurse)
				do3d.addChildrenToLayer(do3d, this);
		}
		
		public function removeDisplayObject3D(do3d:DisplayObject3D):void{
			displayObjects[do3d] = null;
			dispatchEvent(new ViewportLayerEvent(ViewportLayerEvent.CHILD_REMOVED, do3d, this));
		}
		
		public function hasDisplayObject3D(do3d:DisplayObject3D):Boolean{
			return (displayObjects[do3d] != null);
		}
		
		protected function init():void
		{
			childLayers = new Array();
		}
		
		public function getChildLayer(do3d:DisplayObject3D, createNew:Boolean = true, recurse:Boolean = false):ViewportLayer{
			
			do3d = do3d.parentContainer?do3d.parentContainer:do3d;	
			
			/* var index:Number = childLayerIndex(do3d);
			
			if(index > -1)
				return childLayers[index];
			
			for each(var vpl:ViewportLayer in childLayers){
				var tmpLayer:ViewportLayer = vpl.getChildLayer(do3d, false);
				if(tmpLayer)
					return tmpLayer;
			}	
			 */
			 
			if(layers[do3d]){
				return layers[do3d];
			}
				
			 
			//no layer found = return a new one
			if(createNew)
				return getChildLayerFor(do3d, recurse);
			else
				return null;
		}
		
		protected function getChildLayerFor(displayObject3D:DisplayObject3D, recurse:Boolean = false):ViewportLayer
		{
			
			if(displayObject3D){
				var vpl:ViewportLayer = new ViewportLayer(viewport,displayObject3D, displayObject3D.useOwnContainer);
				addLayer(vpl);

				if(recurse)
					displayObject3D.addChildrenToLayer(displayObject3D, vpl);
				
				return vpl;
			}else{
				PaperLogger.warning("Needs to be a do3d");
			}
			return null;
		}
		
		public function childLayerIndex(do3d:DisplayObject3D):Number{
			
			do3d = do3d.parentContainer?do3d.parentContainer:do3d;
			
			for(var i:int=0;i<childLayers.length;i++){
				if(childLayers[i].hasDisplayObject3D(do3d)){
					return i;
				}
			}
			return -1;
		}
		
		public function addLayer(vpl:ViewportLayer):void{
			
			var do3d:DisplayObject3D;
						if(childLayers.indexOf(vpl)!=-1) 			{								PaperLogger.warning("Child layer already exists in ViewportLayer"); 				return; 						}
			childLayers.push(vpl);
			addChild(vpl);
			
			vpl.addEventListener(ViewportLayerEvent.CHILD_ADDED, onChildAdded);
			vpl.addEventListener(ViewportLayerEvent.CHILD_REMOVED, onChildRemoved);
			
			for each(do3d in vpl.displayObjects){
				linkChild(do3d, vpl);
			}
			
			for each(var v:ViewportLayer in vpl.layers){
				for each(do3d in v.displayObjects){
					linkChild(do3d, v);
				}
			}
		}
		
		private function linkChild(do3d:DisplayObject3D, vpl:ViewportLayer, e:ViewportLayerEvent = null):void{
			
			layers[do3d] = vpl;
			dispatchEvent(new ViewportLayerEvent(ViewportLayerEvent.CHILD_ADDED, do3d, vpl));
			
		}
		
		private function unlinkChild(do3d:DisplayObject3D, e:ViewportLayerEvent = null):void{
			layers[do3d ] = null;
			dispatchEvent(new ViewportLayerEvent(ViewportLayerEvent.CHILD_REMOVED, do3d));
		}
		
		private function onChildAdded(e:ViewportLayerEvent):void{
			if(e.do3d){
				linkChild(e.do3d, e.layer, e);
			}
		}
		
		private function onChildRemoved(e:ViewportLayerEvent):void{
			if(e.do3d){
				unlinkChild(e.do3d, e);
			}
		}
		
		public function updateBeforeRender():void{
			clear();
			for each(var vpl:ViewportLayer in childLayers){
				vpl.updateBeforeRender();
			}
		}
		
		public function updateAfterRender():void{
			for each(var vpl:ViewportLayer in childLayers){
				vpl.updateAfterRender();
			}
		}
		
		public function removeLayer(vpl:ViewportLayer):void{
			
			var index:int = getChildIndex(vpl);
			if(index >-1){
				removeLayerAt(index);
			}else{
				PaperLogger.error("Layer not found for removal.");
			}
		}
		
		public function removeLayerAt(index:Number):void{
			
			for each(var do3d:DisplayObject3D in childLayers[index].displayObjects){
				unlinkChild(do3d);
			}
			removeChild(childLayers[index]);
			childLayers.splice(index, 1);
			
		}
		
		public function getLayerObjects(ar:Array = null):Array{
		
			if(!ar)
				ar = new Array();

			for each(var do3d:DisplayObject3D in this.displayObjects){
				if(do3d){
					ar.push(do3d);
				}
			}
			
			for each(var vpl:ViewportLayer in childLayers){
				vpl.getLayerObjects(ar);
			}
			
			
			
			return ar;
			
		}
		
		
		
		public function clear():void
		{
				
			/* var vpl:ViewportLayer;
			for each(vpl in childLayers){
				
				vpl.clear();
			} */
			graphicsChannel.clear();
			reset();
		}
		
		protected function reset():void{
			
			if( !forceDepth)
			{
				screenDepth = 0;
				originDepth = 0;
			}
				
			this.weight = 0;
			
		}
		
		public function sortChildLayers():void		{
			switch( sortMode )
			{
				case ViewportLayerSortMode.Z_SORT:
					childLayers.sortOn( "screenDepth", Array.DESCENDING | Array.NUMERIC );
					break;
				
				case ViewportLayerSortMode.INDEX_SORT:
					childLayers.sortOn( "layerIndex", Array.NUMERIC );
					break;
				
				case ViewportLayerSortMode.ORIGIN_SORT:
					childLayers.sortOn( [ "originDepth", "screenDepth" ] , [ Array.DESCENDING | Array.NUMERIC, Array.DESCENDING | Array.NUMERIC ] );
					break;
			}
					
			orderLayers();
		}
		
		protected function orderLayers():void{
			for(var i:int = 0;i<childLayers.length;i++)
			{
				var layer : ViewportLayer = childLayers[i]; 
				if(this.getChildIndex(layer)!=i) this.setChildIndex(layer, i);
				layer.sortChildLayers();
			}
		}
		
		public function processRenderItem(rc:RenderableListItem):void{
			if(!forceDepth)			{				if(!isNaN(rc.screenZ))				{				
					this.screenDepth += rc.screenZ;					if( rc.instance )					{
						this.originDepth += rc.instance.world.n34;
						this.originDepth += rc.instance.screen.z;					}
					this.weight++;
								}			}		}
		
		public function updateInfo():void{
			
			//this.screenDepth /= this.weight;
			
			for each(var vpl:ViewportLayer in childLayers){
				vpl.updateInfo();
				if(!forceDepth){					// screenDepth is sometimes NaN if the child objects are invisible or empty					if(!isNaN(vpl.screenDepth))					{						this.weight += vpl.weight;
						this.screenDepth += (vpl.screenDepth*vpl.weight);
						this.originDepth += (vpl.originDepth*vpl.weight);					}				
				}
			}
			
			if(!forceDepth)
			{
				this.screenDepth /= this.weight;
				this.originDepth /= this.weight;
			}		
			
		}
		
		public function removeAllLayers():void{
			for(var i:int=childLayers.length-1;i>=0;i--){
				removeLayerAt(i);
			}
		}
	}
}