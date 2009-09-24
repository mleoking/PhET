package org.papervision3d.objects.special {
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.objects.DisplayObject3D;	

	public class SimpleLevelOfDetail extends DisplayObject3D
	{
		public var currentObject:DisplayObject3D;
		public var objects:Array;
		public var minDepth:Number;
		public var maxDepth:Number;
		public var distances:Array;
		
		public function SimpleLevelOfDetail(objects:Array, minDepth:Number=1000, maxDepth:Number=10000, distances:Array = null)
		{
			this.objects = objects;
			this.minDepth = minDepth;
			this.maxDepth = maxDepth;
			this.distances = distances;

			super();
			
			addChild(objects[0]);
			currentObject = objects[0];
		}
		
		public function updateLoD(index:Number=-1):void{
			
			
			var objCount:Number = objects.length;
			var depth:Number = this.screenZ - minDepth;
			var modelIndex:Number = 0; 
			
			if(index == -1){

				if(distances == null){
					if(this.screenZ < minDepth){
						modelIndex = 0;
					}else if(this.screenZ >= maxDepth){
						modelIndex =objects.length-1;
					}else{
						var segSize:Number = (maxDepth-minDepth)/objCount;
						modelIndex = int(depth/segSize);
						
					}
				}else{
					//use the distance array!
					
					for(var i:int=0;i<distances.length;i++){
						if(this.screenZ < distances[i]){
							break;
						}
						modelIndex = distances[i];
					}
					
					modelIndex = Math.min(objCount-1, modelIndex);
					
				}
				
			
			}else{
				modelIndex = index;
			}
			
			if(objects[modelIndex] == currentObject)
				return;
			
			removeChild(currentObject);
			currentObject = objects[modelIndex];
			addChild(currentObject);
				
		}
		
		public override function project(parent:DisplayObject3D, renderSessionData:RenderSessionData):Number{
			updateLoD();
			return super.project(parent, renderSessionData);
		}

		
	}
}