package away3d.animators.skin
{
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.math.*;
	
    public class SkinController
    {
    	public var name:String;
		public var joint:ObjectContainer3D;
        public var bindMatrix:MatrixAway3D;
        public var sceneTransform:MatrixAway3D = new MatrixAway3D();
        public var inverseTransform:MatrixAway3D;
        public var updated:Boolean;
        
        public function update():void
        {
        	if (!joint)
        		return;
        	
        	if (!joint.scene.updatedObjects[joint]) {
        		updated = false;
        		return;
        	} else {
        		updated = true;
        	}
        	
        	sceneTransform.multiply(joint.sceneTransform, bindMatrix);
        	sceneTransform.multiply(inverseTransform, sceneTransform);
        }
        
    }
}
