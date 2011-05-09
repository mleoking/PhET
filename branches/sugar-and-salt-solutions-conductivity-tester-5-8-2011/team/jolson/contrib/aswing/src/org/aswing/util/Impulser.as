/*
 Copyright aswing.org, see the LICENCE.txt.
*/


/**
 * Declares API to fire one or more action events after a 
 * specified delay.  
 * 
 * @author iiley
 * @author Igor Sadovskiy
 */
interface org.aswing.util.Impulser {
	
    /**
     * addActionListener(fuc:Function, obj:Object)<br>
     * addActionListener(fuc:Function)<br>
     * 
     * Adds an action listener to the <code>Impulser</code>
     * instance.
     *
     * @param fuc the listener function.
     * @param obj which context to run in by the func.
     * @return the listener just added.
     * 
     * @see org.aswing.EventDispatcher#addEventListener()
     * @see org.aswing.EventDispatcher#ON_ACT
     */	
	public function addActionListener(func:Function, obj:Object):Object;
	
    /**
     * Sets the <code>Impulser</code>'s delay between fired events.
     *
     * @param delay the delay
     * @see #setInitialDelay()
     */	
	public function setDelay(delay:Number):Void;
	
    /**
     * Returns the delay between firings of events.
     *
     * @see #setDelay()
     * @see #getInitialDelay()
     */	
	public function getDelay():Number;
	
    /**
     * Sets the <code>Impulser</code>'s initial delay,
     * which by default is the same as the between-event delay.
     * This is used only for the first action event.
     * Subsequent events are spaced using the delay property.
     * 
     * @param initialDelay the delay 
     *                     between the invocation of the <code>start</code>
     *                     method and the first event
     *                     fired by this impulser
     *
     * @see #setDelay()
     */	
	public function setInitialDelay(initialDelay:Number):Void;
	
    /**
     * Returns the <code>Impulser</code>'s initial delay.
     *
     * @see #setInitialDelay()
     * @see #setDelay()
     */	
	public function getInitialDelay():Number;
	
	/**
     * If <code>flag</code> is <code>false</code>,
     * instructs the <code>Impulser</code> to send only once
     * action event to its listeners after a start.
     *
     * @param flag specify <code>false</code> to make the impulser
     *             stop after sending its first action event.
	 */
	public function setRepeats(flag:Boolean):Void;
	
    /**
     * Returns <code>true</code> (the default)
     * if the <code>Impulser</code> will send
     * an action event to its listeners multiple times.
     *
     * @see #setRepeats()
     */	
	public function isRepeats():Boolean;
	
    /**
     * Starts the <code>Impulser</code>,
     * causing it to start sending action events
     * to its listeners.
     *
     * @see #stop()
     */
    public function start():Void;
    
    /**
     * Returns <code>true</code> if the <code>Impulser</code> is running.
     *
     * @see #start()
     */
    public function isRunning():Boolean;
    
    /**
     * Stops the <code>Impulser</code>,
     * causing it to stop sending action events
     * to its listeners.
     *
     * @see #start()
     */
    public function stop():Void;
    
    /**
     * Restarts the <code>Impulser</code>,
     * canceling any pending firings and causing
     * it to fire with its initial delay.
     */
    public function restart():Void;
    
}
