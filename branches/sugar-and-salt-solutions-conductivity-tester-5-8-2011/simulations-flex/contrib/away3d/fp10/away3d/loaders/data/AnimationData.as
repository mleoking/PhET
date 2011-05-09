package away3d.loaders.data
{
	import away3d.animators.*;
	import away3d.containers.*;
	import away3d.core.base.*;
	
	import flash.utils.*;
	
	/**
	 * Data class for the animation of a mesh.
	 * 
	 * @see away3d.loaders.data.MeshData
	 */
	public class AnimationData
	{
		/**
		 * String representing a vertex animation.
		 */
		public static const VERTEX_ANIMATION:String = "vertexAnimation";
		
		/**
		 * String representing a skin animation.
		 */
		public static const SKIN_ANIMATION:String = "skinAnimation";
		
		/**
		 * The name of the animation used as a unique reference.
		 */
		public var name:String;
		
		/**
		 * Reference to the animation object of the resulting animation.
		 */
		public var animation:IMeshAnimation;
		
		/**
		 * Reference to the time the animation starts.
		 */
		public var start:Number = Infinity;
		
		/**
		 * Reference to the number of seconds the animation ends.
		 */
		public var end:Number = 0;
		
		/**
		 * String representing the animation type.
		 */
		public var animationType:String = SKIN_ANIMATION;
		
		/**
		 * Dictonary of names representing the animation channels used in the animation.
		 */
		public var channels:Dictionary = new Dictionary(true);
		
		public function clone(object:Object3D):AnimationData
		{
			var animationData:AnimationData = object.animationLibrary.addAnimation(name);
			
    		animationData.start = start;
    		animationData.end = end;
    		animationData.animationType = animationType;
    		animationData.animation = animation.clone(object as ObjectContainer3D);
    		
    		return animationData;
		}
	}
}