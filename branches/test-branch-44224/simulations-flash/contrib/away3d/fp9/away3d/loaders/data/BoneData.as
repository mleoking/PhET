package away3d.loaders.data
{
	import away3d.core.math.*;
	/**
	 * Data class for a bone used in SkinAnimation.
	 */
	public class BoneData extends ContainerData
	{
		/**
		 * Transform information for the joint in a SkinAnimation
		 */
		public var jointTransform:Matrix3D = new Matrix3D();
	}
}