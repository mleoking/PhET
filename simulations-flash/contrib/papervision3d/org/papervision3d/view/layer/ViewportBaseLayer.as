package org.papervision3d.view.layer
{
	import org.papervision3d.objects.DisplayObject3D;
	import org.papervision3d.view.Viewport3D;

	/**
	 * @Author Ralph Hauwert
	 */
	public class ViewportBaseLayer extends ViewportLayer
	{
		public function ViewportBaseLayer(viewport:Viewport3D)
		{
			super(viewport,null);
		}
		
		public override function getChildLayer(do3d:DisplayObject3D, createNew:Boolean=true, recurse:Boolean = false):ViewportLayer{
		
			/* var index:Number = childLayerIndex(do3d);
			if(index > -1)
				return childLayers[index];
				
			
			
			for each(var vpl:ViewportLayer in childLayers){
				var tmpLayer:ViewportLayer = vpl.getChildLayer(do3d, false);
				if(tmpLayer)
					return tmpLayer;
			}	 */
			
			if(layers[do3d])
				return layers[do3d];
			
			
			//no layer found = return a new one
			if(createNew || do3d.useOwnContainer)
				return getChildLayerFor(do3d, recurse);
			else{
				//trace("using container?!?!?");
				return this;
			}
			
		}
		
		public override function updateBeforeRender():void{
			
			clear();
			
			for(var i:int=childLayers.length-1;i>=0;i--){
				if(childLayers[i].dynamicLayer){
					removeLayerAt(i);
				}
			} 
			
			super.updateBeforeRender();
			
		}

	}
}