package away3d.events
{
	import away3d.containers.*;
	
	import flash.events.Event;
    
    /**
    * Passed as a parameter when a view3d event occurs
    */
    public class ViewEvent extends Event
    {
    	/**
    	 * Defines the value of the type property of a updateScene event object.
    	 */
    	public static const UPDATE_SCENE:String = "updateScene";
    	
    	/**
    	 * Defines the value of the type property of a renderComplete event object.
    	 */
    	public static const RENDER_COMPLETE:String = "renderComplete";
    	
    	/**
    	 * A reference to the view object that is relevant to the event.
    	 */
        public var view:View3D;
		
		/**
		 * Creates a new <code>FaceEvent</code> object.
		 * 
		 * @param	type	The type of the event. Possible values are: <code>ViewEvent.UPDATE_SCENE</code>.
		 * @param	view	A reference to the view object that is relevant to the event.
		 */
        public function ViewEvent(type:String, view:View3D)
        {
            super(type);
            this.view = view;
        }
		
		/**
		 * Creates a copy of the FaceEvent object and sets the value of each property to match that of the original.
		 */
        public override function clone():Event
        {
            return new ViewEvent(type, view);
        }
    }
}
