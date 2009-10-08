package away3d.animators
{
	import away3d.containers.ObjectContainer3D;
	

    /**
    * Interface for objects containing animation information for meshes.
    */
    public interface IMeshAnimation
    {
    	function update(time:Number, interpolate:Boolean = true):void;
    	
    	function clone(object:ObjectContainer3D):IMeshAnimation;
    }
}
