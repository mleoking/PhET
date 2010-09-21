package org.papervision3d.core.animation.channel.geometry 
{
	import org.papervision3d.core.proto.GeometryObject3D;	
	import org.papervision3d.core.animation.channel.Channel3D;
	
	/**
	 * @author Tim Knip / floorplanner.com
	 */
	public class GeometryChannel3D extends Channel3D 
	{
		/**
		 * The targeted geometry.
		 */
		protected var _geometry : GeometryObject3D;
		
		/**
		 * Constructor.
		 * 
		 * @param geometry
		 */
		public function GeometryChannel3D(geometry : GeometryObject3D) 
		{
			super();
			
			this.geometry = geometry;
		}
		
		/**
		 * The targeted geometry.
		 */
		public function set geometry(value : GeometryObject3D) : void
		{
			if(value && value.vertices && value.vertices.length)
			{
				_geometry = value;
			}
		}
		
		/**
		 * 
		 */
		public function get goemetry() : GeometryObject3D
		{
			return _geometry;
		}
	}
}
