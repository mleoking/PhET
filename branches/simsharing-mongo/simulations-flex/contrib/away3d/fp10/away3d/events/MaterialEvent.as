package away3d.events
{
    import away3d.materials.*;
 
    import flash.events.Event;
    
    /**
    * Passed as a parameter when a material event occurs
    */
    public class MaterialEvent extends Event
    {
    	/**
    	 * Defines the value of the type property of a loadError event object.
    	 */
    	public static const LOAD_ERROR:String = "loadError";
    	
    	/**
    	 * Defines the value of the type property of a loadProgress event object.
    	 */
    	public static const LOAD_PROGRESS:String = "loadProgress";
    	
    	/**
    	 * Defines the value of the type property of a loadSuccess event object.
    	 */
    	public static const LOAD_SUCCESS:String = "loadSuccess";
    	
    	/**
    	 * Defines the value of the type property of a materialUpdated event object.
    	 */
    	public static const MATERIAL_UPDATED:String = "materialUpdated";
    	
    	/**
    	 * Defines the value of the type property of a materialChanged event object.
    	 */
    	public static const MATERIAL_CHANGED:String = "materialChanged";
    	
    	/**
    	 * A reference to the material object that is relevant to the event.
    	 */
        public var material:IMaterial;
		
    	/**
    	 * A reference to a user defined extra object that is relevant to the event.
    	 */
        public var extra:Object;
        
		/**
		 * Creates a new <code>MaterialEvent</code> object.
		 * 
		 * @param	type		The type of the event. Possible values are: <code>MaterialEvent.RESIZED</code>.
		 * @param	material	A reference to the material object that is relevant to the event.
		 */
        public function MaterialEvent(type:String, material:IMaterial)
        {
            super(type);
            this.material = material;
        }
		
		/**
		 * Creates a copy of the MaterialEvent object and sets the value of each property to match that of the original.
		 */
        public override function clone():Event
        {
            return new MaterialEvent(type, material);
        }
    }
}
