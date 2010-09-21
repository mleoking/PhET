/**
* @author De'Angelo Richardson 
*/
package org.papervision3d.core.utils
{
	import org.papervision3d.core.geom.renderables.Triangle3D;
	import org.papervision3d.core.math.Matrix3D;
	import org.papervision3d.core.math.Number3D;
	import org.papervision3d.core.render.data.RenderHitData;
	import org.papervision3d.objects.DisplayObject3D;

	/**
	 * Mouse3D tracks the mouse's position in relation to 3d space
	 */
	public class Mouse3D extends DisplayObject3D
	{
		static private var UP 								:Number3D = new Number3D(0, 1, 0);
		
		/**
		 * A boolean flag to enable or disable the mouse updating in the
		 * <code>InteractiveSceneManager</code>
		 */
		static public var enabled							:Boolean = false;
		
		//private var _position:Number3D = new Number3D(0, 0, 0);
		private var target:Number3D = new Number3D();
		
		public function Mouse3D():void
		{
			
		}
		
		/**
		 * updates the mouse position
		 * 
		 * @param rhd		the data used to update the mouse position
		 */
		public function updatePosition( rhd:RenderHitData ):void
		{			
			var face3d:Triangle3D = rhd.renderable as Triangle3D;
			var look:Matrix3D;
			
			target.x = face3d.faceNormal.x; 
			target.y = face3d.faceNormal.y; 
			target.z = face3d.faceNormal.z;
				
			var zAxis:Number3D = Number3D.sub(target, position);
			zAxis.normalize();
				
			if (zAxis.modulo > 0.1)
			{
				var xAxis:Number3D = Number3D.cross(zAxis, UP);
				xAxis.normalize();
				
				var yAxis:Number3D = Number3D.cross(zAxis, xAxis);
				yAxis.normalize();
				
				look = this.transform;
					
				look.n11 = xAxis.x;
				look.n21 = xAxis.y;
				look.n31 = xAxis.z;
				
				look.n12 = -yAxis.x;
				look.n22 = -yAxis.y;
				look.n32 = -yAxis.z;
				
				look.n13 = zAxis.x;
				look.n23 = zAxis.y;
				look.n33 = zAxis.z;
			}else{
				look = Matrix3D.IDENTITY;
			}
			
			this.transform = Matrix3D.multiply(face3d.instance.world, look);
			
			x = rhd.x;
			y = rhd.y;
			z = rhd.z;
		}
	}
}