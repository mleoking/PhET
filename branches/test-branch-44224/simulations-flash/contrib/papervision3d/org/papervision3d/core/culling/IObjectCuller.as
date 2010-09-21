package org.papervision3d.core.culling 
{
	import org.papervision3d.objects.DisplayObject3D;
	
	/**
	 * @author Tim Knip 
	 */
	public interface IObjectCuller 
	{
		function testObject( object:DisplayObject3D ):int;
	}	
}
