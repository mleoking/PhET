package away3d.events
{
	import away3d.core.render.*;
	
	import flash.events.Event;
    
    /**
    * Passed as a parameter when a session event occurs
    */
    public class SessionEvent extends Event
    {
    	/**
    	 * Defines the value of the type property of a sessionUpdated event object.
    	 */
    	public static const SESSION_UPDATED:String = "sessionUpdated";
    	
    	/**
    	 * A reference to the session object that is relevant to the event.
    	 */
        public var session:AbstractRenderSession;
		
		/**
		 * Creates a new <code>FaceEvent</code> object.
		 * 
		 * @param	type	The type of the event. Possible values are: <code>FaceEvent.UPDATED</code></code>.
		 * @param	session	A reference to the session object that is relevant to the event.
		 */
        public function SessionEvent(type:String, session:AbstractRenderSession)
        {
            super(type);
            this.session = session;
        }
		
		/**
		 * Creates a copy of the FaceEvent object and sets the value of each property to match that of the original.
		 */
        public override function clone():Event
        {
            return new SessionEvent(type, session);
        }
    }
}
