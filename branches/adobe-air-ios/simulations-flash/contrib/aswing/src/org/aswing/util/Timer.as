/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.util.AbstractImpulser;
import org.aswing.util.Impulser;
 
/**
 * Fires one or more action events after a specified delay.  
 * For example, an animation object can use a <code>Timer</code>
 * as the trigger for drawing its frames.
 *
 *<p>
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
 *
 *<pre>
 *  var delay:Number = 1000; //milliseconds
 *  var listener:Object = new Object();
 *  listener.taskPerformer = function() {
 *          <em>//...Perform a task...</em>
 *      }
 *  var timer:Timer = new Timer(delay);
 *  timer.addActionListener(listener.taskPerformer, listener);
 *  timer.start();
 * </pre>
 *
 * <p>
 * @author iiley
 */
class org.aswing.util.Timer extends AbstractImpulser implements Impulser {
	
	private var intervalID:Number;
	
	/**
	 * Construct Timer.
	 * @see #setDelay()
     * @throws Error when init delay <= 0 or delay == null
	 */
	public function Timer(delay:Number, repeats:Boolean){
		super(delay, repeats);
		this.intervalID = null;
	}
	
    /**
     * Starts the <code>Timer</code>,
     * causing it to start sending action events
     * to its listeners.
     *
     * @see #stop()
     */
    public function start():Void{
    	isInitalFire = true;
    	clearInterval(intervalID);
    	intervalID = setInterval(this, "fireActionPerformed", getInitialDelay());
    }
    
    /**
     * Returns <code>true</code> if the <code>Timer</code> is running.
     *
     * @see #start()
     */
    public function isRunning():Boolean{
    	return intervalID != null;
    }
    
    /**
     * Stops the <code>Timer</code>,
     * causing it to stop sending action events
     * to its listeners.
     *
     * @see #start()
     */
    public function stop():Void{
    	clearInterval(intervalID);
    	intervalID = null;
    }
    
    /**
     * Restarts the <code>Timer</code>,
     * canceling any pending firings and causing
     * it to fire with its initial delay.
     */
    public function restart():Void{
        stop();
        start();
    }
    
    private function fireActionPerformed():Void{
    	if(isInitalFire){
    		isInitalFire = false;
    		if(repeats){
    			clearInterval(intervalID);
    			intervalID = setInterval(this, "fireActionPerformed", getDelay());
    		}else{
    			stop();
    		}
    	}
    	fireActionEvent();
    }
}
