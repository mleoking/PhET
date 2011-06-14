package away3d.cameras
{
	import away3d.containers.*;
    import away3d.core.base.*;
    import away3d.core.math.*;
    import away3d.core.utils.*;
	
	
    /**
    * Extended camera used to automatically look at a specified target object.
    * 
    * @see away3d.containers.View3D
    */
    public class TargetCamera3D extends Camera3D
    {
        /**
        * The 3d object targeted by the camera.
        */
        public var target:Object3D;
    	
	    /**
	    * Creates a new <code>TargetCamera3D</code> object.
	    * 
	    * @param	init	[optional]	An initialisation object for specifying default instance properties.
	    */
        public function TargetCamera3D(init:Object = null)
        {
            super(init);

            target = ini.getObject3D("target") || new Object3D();
        }
        
		/**
		 * @inheritDoc
		 */
        public override function get viewMatrix():MatrixAway3D
        {
            if (target != null)
                lookAt(target.scene ? target.scenePosition : target.position);
    
            return super.viewMatrix;
        }
        
		/**
		 * Cannot parent a <code>TargetCamera3D</code> object.
		 * 
		 * @throws	Error	TargetCamera can't be parented.
		 */
        public override function set parent(value:ObjectContainer3D):void
        {
            if (value != null)
                throw new Error("TargetCamera can't be parented");
        }

    }

}   
