package org.papervision3d.core.render.data
{
	
	/**
	 * @Author Ralph Hauwert
	 */
	 
	public class RenderStatistics
	{
		public var projectionTime:int = 0;
		public var renderTime:int = 0;
		public var rendered:int = 0;
		public var triangles:int = 0;
		public var culledTriangles:int = 0;
		public var particles:int = 0;
		public var culledParticles:int = 0;
		public var lines:int = 0;
		public var shadedTriangles:int = 0;
		public var filteredObjects:int = 0;
		public var culledObjects:int = 0;
		
		public function RenderStatistics()
		{
			
		}
		
		public function clear():void
		{
			projectionTime = 0;
			renderTime = 0;
			rendered = 0;
			particles = 0;
			triangles = 0;
			culledTriangles = 0;
			culledParticles = 0;
			lines = 0;
			shadedTriangles = 0;
			filteredObjects = 0;
			culledObjects = 0;
		}
		
		public function clone():RenderStatistics
		{
			var rs:RenderStatistics = new RenderStatistics();
			rs.projectionTime = projectionTime;
			rs.renderTime = renderTime;
			rs.rendered = rendered;
			rs.particles = particles;
			rs.triangles = triangles;
			rs.culledTriangles = culledTriangles;
			rs.lines = lines;
			rs.shadedTriangles = shadedTriangles;
			rs.filteredObjects = filteredObjects;
			rs.culledObjects = culledObjects;
			return rs;
		}
		
		public function toString():String
		{
			return new String("ProjectionTime:"+projectionTime+" RenderTime:"+renderTime+" Particles:"+particles+" CulledParticles :"+culledParticles+" Triangles:"+triangles+" ShadedTriangles :"+shadedTriangles+" CulledTriangles:"+culledTriangles+" FilteredObjects:"+filteredObjects+" CulledObjects:"+culledObjects+"");
		}
		
	}
}