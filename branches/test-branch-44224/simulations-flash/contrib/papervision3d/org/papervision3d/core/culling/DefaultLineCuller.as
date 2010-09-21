package org.papervision3d.core.culling {	import org.papervision3d.core.geom.renderables.Line3D;		import org.papervision3d.core.culling.ILineCuller;
	
	/**	 * @author Seb Lee-Delisle	 */	public class DefaultLineCuller implements ILineCuller 	{				public function DefaultLineCuller() 		{					}				public function testLine(line : Line3D) : Boolean 		{			// culls if one of the points is behind the camera... 			return ((line.v0.vertex3DInstance.visible)&&(line.v1.vertex3DInstance.visible));
		}
	}}