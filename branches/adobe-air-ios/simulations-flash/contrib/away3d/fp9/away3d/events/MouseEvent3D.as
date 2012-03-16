package away3d.events
{
	import away3d.containers.*;
    import away3d.core.draw.*;
    import away3d.materials.*;
    import away3d.core.base.*;
    
    import flash.events.Event;
    
    /**
    * Passed as a parameter when a 3d mouse event occurs
    */
    public class MouseEvent3D extends Event
    {
    	/**
    	 * Defines the value of the type property of a mouseOver3d event object.
    	 */
    	public static const MOUSE_OVER:String = "mouseOver3d";
    	
    	/**
    	 * Defines the value of the type property of a mouseOut3d event object.
    	 */
    	public static const MOUSE_OUT:String = "mouseOut3d";
    	
    	/**
    	 * Defines the value of the type property of a mouseUp3d event object.
    	 */
    	public static const MOUSE_UP:String = "mouseUp3d";
    	
    	/**
    	 * Defines the value of the type property of a mouseDown3d event object.
    	 */
    	public static const MOUSE_DOWN:String = "mouseDown3d";
    	
    	/**
    	 * Defines the value of the type property of a mouseMove3d event object.
    	 */
    	public static const MOUSE_MOVE:String = "mouseMove3d";
    	
    	/**
    	 * Defines the value of the type property of a rollOver3d event object.
    	 */
    	public static const ROLL_OVER:String = "rollOver3d";
    	
    	/**
    	 * Defines the value of the type property of a rollOut3d event object.
    	 */
    	public static const ROLL_OUT:String = "rollOut3d";
    	
    	/**
    	 * The horizontal coordinate at which the event occurred in view coordinates.
    	 */
        public var screenX:Number;
        
        /**
        * The vertical coordinate at which the event occurred in view coordinates.
        */
        public var screenY:Number;
        
        /**
        * The depth coordinate at which the event occurred in view coordinates.
        */
        public var screenZ:Number;
		
		/**
		 * The x coordinate at which the event occurred in global scene coordinates.
		 */
        public var sceneX:Number;
		
		/**
		 * The y coordinate at which the event occurred in global scene coordinates.
		 */
        public var sceneY:Number;
		
		/**
		 * The z coordinate at which the event occurred in global scene coordinates.
		 */
        public var sceneZ:Number;
        	
		/**
		 * The view object inside which the event took place.
		 */
        public var view:View3D;
        	
		/**
		 * The 3d object inside which the event took place.
		 */
        public var object:Object3D;
        	
		/**
		 * The 3d element inside which the event took place.
		 */
        public var element:Object;
        	
		/**
		 * The draw primitive inside which the event took place.
		 */
        public var drawpri:DrawPrimitive;
        	
		/**
		 * The material of the 3d element inside which the event took place.
		 */
        public var material:IUVMaterial;
        	
		/**
		 * The uv coordinate inside the draw primitive where the event took place.
		 */
        public var uv:UV;
		
		/**
		 * Indicates whether the Control key is active (true) or inactive (false).
		 */
        public var ctrlKey:Boolean;
        
        /**
        * Indicates whether the Shift key is active (true) or inactive (false).
        */
        public var shiftKey:Boolean;
		
		/**
		 * Creates a new <code>MouseEvent3D</code> object.
		 * 
		 * @param	type		The type of the event. Possible values are: <code>MouseEvent3D.MOUSE_OVER</code>, <code>MouseEvent3D.MOUSE_OUT</code>, <code>MouseEvent3D.MOUSE_UP</code>, <code>MouseEvent3D.MOUSE_DOWN</code> and <code>MouseEvent3D.MOUSE_MOVE</code>.
		 */
        public function MouseEvent3D(type:String)
        {
            super(type, false, true);
        }
		
		/**
		 * Creates a copy of the MouseEvent3D object and sets the value of each property to match that of the original.
		 */
        public override function clone():Event
        {
            var result:MouseEvent3D = new MouseEvent3D(type);

			if(isDefaultPrevented())
            	result.preventDefault();

            result.screenX = screenX;
            result.screenY = screenY;
            result.screenZ = screenZ;
                                     
            result.sceneX = sceneX;
            result.sceneY = sceneY;
            result.sceneZ = sceneZ;
                                     
            result.view = view;
            result.object = object;
            result.element = element;
            result.drawpri = drawpri;
            result.material = material;
            result.uv = uv;
                                     
            result.ctrlKey = ctrlKey;
            result.shiftKey = shiftKey;

            return result;
        }
    }
}
