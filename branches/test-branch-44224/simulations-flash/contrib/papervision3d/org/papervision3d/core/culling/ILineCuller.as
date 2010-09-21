package org.papervision3d.core.culling 
{
	import org.papervision3d.core.geom.renderables.Line3D;	

	/**
	 * @author Seb Lee-Delisle
	 */
	
	public interface ILineCuller
	{
		function testLine(line : Line3D) : Boolean;
	}
}