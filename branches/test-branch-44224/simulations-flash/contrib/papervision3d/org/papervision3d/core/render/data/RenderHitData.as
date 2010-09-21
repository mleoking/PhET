package org.papervision3d.core.render.data
{
	/**
	 * @Author Ralph Hauwert
	 */
	 
	import org.papervision3d.core.geom.renderables.IRenderable;
	import org.papervision3d.core.proto.MaterialObject3D;
	import org.papervision3d.objects.DisplayObject3D;
	
	public class RenderHitData
	{
		public var startTime:int = 0;
		public var endTime:int = 0;
		public var hasHit:Boolean = false;
		
		public var displayObject3D:DisplayObject3D;
		public var material:MaterialObject3D;
		
		public var renderable:IRenderable;
		
		public var u:Number;
		public var v:Number;
		
		public var x:Number;
		public var y:Number;
		public var z:Number;
		
		public function RenderHitData():void
		{
			
		}
		
		public function toString():String
		{
			return displayObject3D +" "+renderable;
		}
		
		public function clear():void
		{
			startTime = 0;
			endTime = 0;
			hasHit = false;
			displayObject3D = null;
			material = null;
			renderable = null;
			u = 0;
			v = 0;
			x = 0;
			y = 0;
			z = 0;
		}
		
		public function clone():RenderHitData
		{
			var rhd:RenderHitData = new RenderHitData();
			
			rhd.startTime = startTime;
			rhd.endTime = endTime;
			rhd.hasHit = hasHit;
			rhd.displayObject3D = displayObject3D;
			rhd.material = material;
			rhd.renderable = renderable;
			rhd.u = u;
			rhd.v = v;
			rhd.x = x;
			rhd.y = y;
			rhd.z = z;
			
			return rhd;
		}
	}
}