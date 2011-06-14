
/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * All Events are constructed with a reference to the object, 
 * the "source", that is logically deemed to be the object upon 
 * which the Event in question initially occurred upon. The type 
 * is the event's type, generally it is same to the event's handler 
 * function name.
 * <p>
 * Different Events has different extra properties. Extra properties 
 * are passed to event handler as additional arguments. The extra properties 
 * usually commented in the event name comments.
 * <p>
 * For example a comments of a Event Type Declaration is:
 * <pre>
 * 	//onMoved(source:Component, oldPos:Point, newPos:Point)
 * 	public static var ON_MOVED:String = "onMoved";
 * </pre>
 * It is meaning that this Event's source is a Component instance, its type is "onMoved",
 * and it has extra properties oldPos and newPos, both are Point instances.
 * 
 * @see org.aswing.EventDispatcher
 * @author iiley
 * @author Igor Sadovskiy
 */
 class org.aswing.Event {
	private var _source:Object;
	private var _type:String;
	private var _argumets:Array;
	
	/**
	 * Event(source:Object, type:String)
	 * Event(source:Object, type:String, additionalParams:Array)
	 * 
	 * Creates a Event instance by specified source and type.
	 * @param source the source of the event
	 * @param type the type of the event
	 * @param additionalParams the the additional arguments in an array to be passed to the event handler
	 */
	public function Event(source:Object, type:String, additionalParams:Array){
		this._source = source;
		this._type = type;
		this._argumets = ([_source]).concat(additionalParams);
	}
	
	/**
	 * Returns the type of the event.
	 * @return the type of the event
	 */
	public function getType():String{
		return _type;
	}
	
	/**
	 * Returns the source of the event.
	 * @return the source of the event
	 */
	public function getSource():Object{
		return _source;
	}
	
	/**
	 * Returns array with arguments to be passed to event handler.
	 * @return array with arguments to be passed to event handler
	 */
	public function getArguments():Array{
		return _argumets;
	}
	
}
