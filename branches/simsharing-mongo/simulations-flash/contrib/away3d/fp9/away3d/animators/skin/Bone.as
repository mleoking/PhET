package away3d.animators.skin
{
	import away3d.containers.*;
	import away3d.arcane;
	import away3d.core.base.*;
	
	use namespace arcane;
	
    public class Bone extends ObjectContainer3D
    {
    	public var joint:ObjectContainer3D;
    	
		//Collada 3.05B
		public var boneId:String;
		
    	/**
    	 * Defines the euler angle of rotation of the 3d object around the x-axis, relative to the local coordinates of the parent <code>ObjectContainer3D</code>.
    	 */
        public function get jointRotationX():Number
        {
            return joint.rotationX;
        }
    
        public function set jointRotationX(rot:Number):void
        {
            joint.rotationX = rot;
        }
		
    	/**
    	 * Defines the euler angle of rotation of the 3d object around the y-axis, relative to the local coordinates of the parent <code>ObjectContainer3D</code>.
    	 */
        public function get jointRotationY():Number
        {
            return joint.rotationY;
        }
    
        public function set jointRotationY(rot:Number):void
        {
            joint.rotationY = rot;
        }
		
    	/**
    	 * Defines the euler angle of rotation of the 3d object around the z-axis, relative to the local coordinates of the parent <code>ObjectContainer3D</code>.
    	 */
        public function get jointRotationZ():Number
        {
            return joint.rotationZ;
        }
    
        public function set jointRotationZ(rot:Number):void
        {
            joint.rotationZ = rot;
        }
		
    	/**
    	 * Defines the scale of the 3d object along the x-axis, relative to local coordinates.
    	 */
        public function get jointScaleX():Number
        {
            return joint.scaleX;
        }
    
        public function set jointScaleX(scale:Number):void
        {
        	joint.scaleX = scale;
        }
		
    	/**
    	 * Defines the scale of the 3d object along the y-axis, relative to local coordinates.
    	 */
        public function get jointScaleY():Number
        {
            return joint.scaleY;
        }
    
        public function set jointScaleY(scale:Number):void
        {
			joint.scaleY = scale;
        }
		
    	/**
    	 * Defines the scale of the 3d object along the z-axis, relative to local coordinates.
    	 */
        public function get jointScaleZ():Number
        {
            return joint.scaleZ;
        }
    
        public function set jointScaleZ(scale:Number):void
        {
			joint.scaleZ = scale;
        }
        
        public function Bone(init:Object = null, ...childarray) : void
        {
			super(init);
			
			//create the joint for the bone
			addChild(joint = new ObjectContainer3D());
			//addChild(new Sphere({radius:3}));
        }
		
		/**
		 * Duplicates the 3d object's properties to another <code>Bone</code> object
		 * 
		 * @param	object	[optional]	The new object instance into which all properties are copied
		 * @return						The new object instance with duplicated properties applied
		 */
        public override function clone(object:Object3D = null):Object3D
        {
            var bone:Bone = (object as Bone) || new Bone();
            super.clone(bone);
            
            bone.joint = bone.children[0];
            
            return bone;
        }
        
        public override function cloneAll(object:Object3D = null):Object3D
        {
            var bone:Bone = (object as Bone) || new Bone();
            bone.removeChild(joint);
            
            super.cloneAll(bone);
            
            bone.boneId = boneId;
            bone.joint = bone.children[0];
            
            return bone;
        }
    }
}
