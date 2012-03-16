/*
 Copyright aswing.org, see the LICENCE.txt.
*/
package org.aswing.util{
	
import flash.utils.clearInterval;
import flash.utils.setInterval;
import org.aswing.event.AWEvent;
/**
 * Fires one or more action events after a specified delay.  
 * For example, an animation object can use a <code>Timer</code>
 * as the trigger for drawing its frames.
 *
 * <p>
 * Setting up a timer
 * involves creating a <code>Timer</code> object,
 * registering one or more action listeners on it,
 * and starting the timer using
 * the <code>start</code> method.
 * For example, 
 * the following code creates and starts a timer
 * that fires an action event once per second
 * (as specified by the first argument to the <code>Timer</code> constructor).
 * The second argument to the <code>Timer</code> constructor
 * specifies a listener to receive the timer's action events.
 * </p>
 * <pre>
 *  var delay:Number = 1000; //milliseconds
 *  var listener:Object = new Object();
 *  listener.taskPerformer = function(e:Event) {
 *          <em>//...Perform a task...</em>
 *      }
 *  var timer:Timer = new Timer(delay);
 *  timer.addActionListener(listener.taskPerformer);
 *  timer.start();
 * </pre>
 *
 * <p>
 * @author iiley
 */
public class Timer extends AbstractImpulser implements Impulser{
	private var intervalID:int;
	
	/**
	 * Construct Timer.
	 * @see #setDelay()
     * @throws Error when init delay <= 0 or delay == null
	 */
	public function Timer(delay:uint, repeats:Boolean=true){
		super(delay, repeats);
		this.intervalID = 0;
	}
	
    /**
     * Starts the <code>Timer</code>,
     * causing it to start sending action events
     * to its listeners.
     *
     * @see #stop()
     */
    override public function start():void{
    	isInitalFire = true;
    	clearInterval(intervalID);
    	intervalID = setInterval(fireActionPerformed, getInitialDelay());
    }
    
    /**
     * Returns <code>true</code> if the <code>Timer</code> is running.
     *
     * @see #start()
     */
    override public function isRunning():Boolean{
    	return intervalID != 0;
    }
    
    /**
     * Stops the <code>Timer</code>,
     * causing it to stop sending action events
     * to its listeners.
     *
     * @see #start()
     */
    override public function stop():void{
    	clearInterval(intervalID);
    	intervalID = 0;
    }
    
    /**
     * Restarts the <code>Timer</code>,
     * canceling any pending firings and causing
     * it to fire with its initial delay.
     */
    override public function restart():void{
        stop();
        start();
    }
    
    private function fireActionPerformed():void{
    	if(isInitalFire){
    		isInitalFire = false;
    		if(repeats){
    			clearInterval(intervalID);
    			intervalID = setInterval(fireActionPerformed,getDelay());
    		}else{
    			stop();
    		}
    	}
    	dispatchEvent(new AWEvent(AWEvent.ACT));
    }
    

	
}
}