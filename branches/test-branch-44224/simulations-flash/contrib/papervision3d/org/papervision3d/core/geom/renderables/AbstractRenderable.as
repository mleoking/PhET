package org.papervision3d.core.geom.renderables
{
	import org.papervision3d.core.data.UserData;
	import org.papervision3d.core.render.command.IRenderListItem;
	import org.papervision3d.objects.DisplayObject3D;

	public class AbstractRenderable implements IRenderable
	{
		
		public var _userData:UserData;
		public var instance:DisplayObject3D;
		
		
		public function AbstractRenderable()
		{
			super();
		}

		public function getRenderListItem():IRenderListItem
		{
			return null;
		}
		
		/**
		 * userData UserData
		 * 
		 * Optional extra data to be added to this object.
		 */
		public function set userData(userData:UserData):void
		{
			_userData = userData;
		}
		
		public function get userData():UserData
		{
			return _userData;	
		}
		
	}
}