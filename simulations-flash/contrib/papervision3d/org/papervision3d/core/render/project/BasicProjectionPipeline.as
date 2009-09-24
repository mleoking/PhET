package org.papervision3d.core.render.project {
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.objects.DisplayObject3D;	

	public class BasicProjectionPipeline extends ProjectionPipeline
	{
		
		public function BasicProjectionPipeline()
		{
			super();
			init();
		}
		
		protected function init():void
		{
				
		}
		
		/**
		 * project(renderSessionData:RenderSessionData);
		 * 
		 * Projects all base objects
		 * 
		 * @returns void;
		 */
		override public function project(renderSessionData:RenderSessionData):void
		{
			// Transform camera
			renderSessionData.camera.transformView();
			
			//Start looping through all objects in the scene.
			var objects:Array = renderSessionData.renderObjects;
			var p:DisplayObject3D;
			var i:Number = objects.length;
			var test:Number;
			
			//The frustum camera requires 4x4 matrices.
			if( renderSessionData.camera.useProjectionMatrix ){
				for each(p in objects){
					//Test if the object is set to visible
					if(p.visible){
						//If we filter objects per viewport..then....
						if(renderSessionData.viewPort.viewportObjectFilter){
							//...test if the object should be rendered to this viewport.
							test = renderSessionData.viewPort.viewportObjectFilter.testObject(p)
							if(test){
								// project it.
								projectObject(p, renderSessionData, test);
							}else{
								//...if the object shouldn't be rendered on this viewport
								renderSessionData.renderStatistics.filteredObjects++;
							}
						}else{
							//If we don't filter objects.
							projectObject(p, renderSessionData, 1);
						}
					}
				}
			}else{
				for each(p in objects){
					//Test if the object is set to visible
					
					if( p.visible){
						//If we filter objects per viewport..then....
						if(renderSessionData.viewPort.viewportObjectFilter){
							test = renderSessionData.viewPort.viewportObjectFilter.testObject(p); 
							if(test){
								// project it.
								projectObject(p, renderSessionData, test);
							}else{
								//The object is filtered.
								renderSessionData.renderStatistics.filteredObjects++;
							}
						}else{
							// project it
							projectObject(p, renderSessionData, 1);
						}
					}
				}
			}
		}
		
		protected function projectObject(object:DisplayObject3D, renderSessionData:RenderSessionData, test:Number):void
		{
			//Collect everything from the object
			object.cullTest = test;
			
			if(object.parent)
				object.project(object.parent as DisplayObject3D, renderSessionData);
			else
				object.project(renderSessionData.camera, renderSessionData);
			
		}
		
	}
}