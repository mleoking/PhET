package away3d.core.light
{
	import away3d.containers.*;
	import away3d.core.math.*;
	import away3d.events.*;
	import away3d.lights.*;
	
	import flash.utils.*;

    /**
    * Point light primitive
    */
    public class PointLight extends LightPrimitive
    {
    	private var _light:PointLight3D;
    	
        /**
        * Positions dictionary for the view positions used by shading materials.
        */
        public var viewPositions:Dictionary;
        
    	/**
    	 * A reference to the <code>PointLight3D</code> object used by the light primitive.
    	 */
        public function get light():PointLight3D
        {
        	return _light;
        }
        public function set light(val:PointLight3D):void
        {
        	_light = val;
        	val.addOnSceneTransformChange(updatePosition);
        }
        
    	/**
    	 * Updates the position of the point light.
    	 */
        public function updatePosition(e:Object3DEvent):void
        {
        	clearViewPositions();
        }
        
        /**
        * Clears the position dictionaries used in the shading materials.
        */
        public function clearViewPositions():void
        {
        	viewPositions = new Dictionary(true);
        }
        
        /**
        * Updates the view position.
        */
        public function setViewPosition(view:View3D):void
        {
        	if (!viewPositions[view])
        		viewPositions[view] = new Number3D();
        	
        	viewPositions[view].clone(view.cameraVarsStore.viewTransformDictionary[_light].position);
        }
    }
}

