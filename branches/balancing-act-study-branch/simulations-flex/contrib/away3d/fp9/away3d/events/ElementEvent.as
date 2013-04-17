package away3d.events
{
    import away3d.core.base.*;
    
    import flash.events.Event;
    
    /**
    * Passed as a parameter when a meshelement event occurs
    */
    public class ElementEvent extends Event
    {
    	/**
    	 * Defines the value of the type property of a mappingChanged event object.
    	 */
    	public static const MAPPING_CHANGED:String = "mappingChanged";
    	/**
    	 * Defines the value of the type property of a vertexChanged event object.
    	 */
    	public static const VERTEX_CHANGED:String = "vertexChanged";
    	
    	/**
    	 * Defines the value of the type property of a vertexvalueChanged event object.
    	 */
    	public static const VERTEXVALUE_CHANGED:String = "vertexvalueChanged";
    	
    	/**
    	 * Defines the value of the type property of a visibleChanged event object.
    	 */
    	public static const VISIBLE_CHANGED:String = "visibleChanged";
    	
    	/**
    	 * A reference to the element object that is relevant to the event.
    	 */
        public var element:Element;
		
		/**
		 * Creates a new <code>ElementEvent</code> object.
		 * 
		 * @param	type		The type of the event. Possible values are: <code>ElementEvent.VERTEX_CHANGED</code>, <code>ElementEvent.VERTEXVALUE_CHANGED</code> and <code>ElementEvent.VISIBLE_CHANGED</code>.
		 * @param	element		A reference to the element object that is relevant to the event.
		 */
        public function ElementEvent(type:String, element:Element)
        {
            super(type);
            this.element = element;
        }
		
		/**
		 * Creates a copy of the ElementEvent object and sets the value of each property to match that of the original.
		 */
        public override function clone():Event
        {
            return new ElementEvent(type, element);
        }
    }
}
