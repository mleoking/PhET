/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.EventDispatcher;
 
/**
 * Provides common routines for classes implemented
 * {@link org.aswing.util.Impulse} interface. 
 *
 * @author iiley
 * @author Igor Sadovskiy
 */
class org.aswing.util.AbstractImpulser extends EventDispatcher  {
	
	private var delay:Number;
	private var initialDelay:Number;
	private var repeats:Boolean;
	private var isInitalFire:Boolean;
	
	/**
	 * Constructs <code>AbstractImpulser</code>.
     * @throws Error when init delay <= 0 or delay == null
	 */
	private function AbstractImpulser(delay:Number, repeats:Boolean){
		if(delay == undefined || delay <=0){
			trace("Delay must > 0! when create a Impulser");
			throw new Error("Delay must > 0! when create a Impulser");
		}
		this.delay = delay;
		this.initialDelay = undefined;
		this.repeats = (repeats != false);
		this.isInitalFire = true;
	}
	
    /**
     * addActionListener(fuc:Function, obj:Object)<br>
     * addActionListener(fuc:Function)<br>
     * 
     * Adds an action listener to the <code>AbstractImpulser</code>
     * instance.
     *
     * @param fuc the listener function.
     * @param obj which context to run in by the func.
     * @return the listener just added.
     * 
     * @see org.aswing.EventDispatcher#addEventListener()
     * @see org.aswing.EventDispatcher#ON_ACT
     */	
	public function addActionListener(func:Function, obj:Object):Object{
		return addEventListener(ON_ACT, func, obj);
	}
	
    /**
     * Sets the <code>AbstractImpulser</code>'s delay between 
     * fired events.
     *
     * @param delay the delay
     * @see #setInitialDelay()
     * @throws Error when set delay <= 0 or delay == null
     */	
	public function setDelay(delay:Number):Void{
		if(delay == undefined || delay <= 0){
			trace("Impulser should be specified delay>0! Error delay = " + delay);
			throw new Error("Impulser should be specified delay>0! Error delay = " + delay);
		}
		this.delay = delay;
	}
	
    /**
     * Returns the delay between firings of events.
     *
     * @see #setDelay()
     * @see #getInitialDelay()
     */	
	public function getDelay():Number{
		return delay;
	}
	
    /**
     * Sets the <code>AbstractImpulser</code>'s initial delay,
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
     * @throws Error when set initialDelay <= 0 or initialDelay == null
     */	
	public function setInitialDelay(initialDelay:Number):Void{
		if(initialDelay == undefined || initialDelay <= 0){
			trace("Impulser should be specified initialDelay>0! Error initialDelay = " + initialDelay);
			throw new Error("Impulser should be specified initialDelay>0! Error initialDelay = " + initialDelay);
		}
		this.initialDelay = initialDelay;
	}
	
    /**
     * Returns the <code>AbstractImpulser</code>'s initial delay.
     *
     * @see #setInitialDelay()
     * @see #setDelay()
     */	
	public function getInitialDelay():Number{
		if(initialDelay == undefined){
			return delay;
		}else{
			return initialDelay;
		}
	}
	
	/**
     * If <code>flag</code> is <code>false</code>,
     * instructs the <code>AbstractImpulser</code> to send only once
     * action event to its listeners after a start.
     *
     * @param flag specify <code>false</code> to make the impulser
     *             stop after sending its first action event.
     *             Default value is true.
	 */
	public function setRepeats(flag:Boolean):Void{
		repeats = flag;
	}
	
    /**
     * Returns <code>true</code> (the default)
     * if the <code>AbstractImpulser</code> will send
     * an action event to its listeners multiple times.
     *
     * @see #setRepeats()
     */	
	public function isRepeats():Boolean{
		return repeats;
	}
	    
}
