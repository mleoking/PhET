/*
 Copyright aswing.org, see the LICENCE.txt.
*/
package org.aswing.util{
	
 
import flash.events.EventDispatcher;
import org.aswing.event.AWEvent;

/**
 * Provides common routines for classes implemented
 * <code>Impulse</code> interface. 
 *
 * @author iiley
 * @author Igor Sadovskiy
 */
public class AbstractImpulser extends EventDispatcher implements Impulser{
	protected var delay:uint;
	protected var initialDelay:int;
	protected var repeats:Boolean;
	protected var isInitalFire:Boolean;
		
	/**
	 * Constructs <code>AbstractImpulser</code>.
     * @throws Error when init delay <= 0 or delay == null
	 */
	public function AbstractImpulser(delay:uint, repeats:Boolean=true){
		this.delay = delay;
		this.initialDelay = 0;
		this.repeats = repeats;
		this.isInitalFire = true;
	}
	
    /**
     * Adds an action listener to the <code>AbstractImpulser</code>
     * instance.
     *
	 * @param listener the listener
	 * @param priority the priority
	 * @param useWeakReference Determines whether the reference to the listener is strong or weak.
	 * @see org.aswing.event.AWEvent#ACT
     */	
	public function addActionListener(listener:Function, priority:int=0, useWeakReference:Boolean=false):void{
		 addEventListener(AWEvent.ACT, listener, false, priority, useWeakReference);
	}
	
	/**
	 * Removes a action listener.
	 * @param listener the listener to be removed.
	 * @see org.aswing.event.AWEvent#ACT
	 */
	public function removeActionListener(listener:Function):void{
		this.removeEventListener(AWEvent.ACT, listener);
	}
	
    /**
     * Sets the <code>AbstractImpulser</code>'s delay between 
     * fired events.
     *
     * @param delay the delay
     * @see #setInitialDelay()
     * @throws Error when set delay <= 0 or delay == null
     */	
	public function setDelay(delay:uint):void{
		this.delay = delay;
	}
	
    /**
     * Returns the delay between firings of events.
     *
     * @see #setDelay()
     * @see #getInitialDelay()
     */	
	public function getDelay():uint{
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
	public function setInitialDelay(initialDelay:uint):void{

		this.initialDelay = initialDelay;
	}
	
    /**
     * Returns the <code>AbstractImpulser</code>'s initial delay.
     *
     * @see #setInitialDelay()
     * @see #setDelay()
     */	
	public function getInitialDelay():uint{
		if(initialDelay == 0){
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
	public function setRepeats(flag:Boolean):void{
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
	
	public function isRunning():Boolean{
		return false;
	}
	
	public function stop():void{}
	
	public function start():void{}
	
	public function restart():void{}
}
}