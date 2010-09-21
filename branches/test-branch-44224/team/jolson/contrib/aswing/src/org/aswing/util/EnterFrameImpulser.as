/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.util.AbstractImpulser;
import org.aswing.util.EnterFrameEventDispatcher;
import org.aswing.util.Impulser;
 
/**
 * Fires one or more action events after a specified 
 * amount of the <code>onEnterFrame</code> impulses.  
 *
 * <p>
 * Setting up a <code>EnterFrameImpulser</code>
 * involves creating a <code>EnterFrameImpulser</code> object,
 * registering one or more action listeners on it,
 * and starting the impulser using
 * the <code>start</code> method.
 * 
 * <p>For example, the following code creates and starts an impulser
 * that fires an action event once per enter frame impulse
 * (as specified by the first argument to the 
 * <code>EnterFrameImpulser</code> constructor).
 * The second argument to the <code>EnterFrameImpulser</code> constructor
 * specifies a listener to receive the impulser's action events.
 *
 * <pre>
 *  var delay:Number = 1; 
 *  var listener:Object = new Object();
 *  listener.taskPerformer = function() {
 *          <em>//...Perform a task...</em>
 *      }
 *  var impulser:Impulser = new EnterFrameImpulser(delay);
 *  impulser.addActionListener(listener.taskPerformer, listener);
 *  impulser.start();
 * </pre>
 *
 * @author iiley
 * @author Igor Sadovskiy
 */
class org.aswing.util.EnterFrameImpulser extends AbstractImpulser implements Impulser {
	
	private var listener:Object;
	private var tickCount:Number;
	
	/**
	 * Constructs <code>EnterFrameImpulser</code>.
	 * @see #setDelay()
	 */
	public function EnterFrameImpulser(delay:Number, repeats:Boolean){
		super(delay, repeats);
	}
		
    /**
     * Starts the <code>EnterFrameImpulser</code>,
     * causing it to start sending action events
     * to its listeners.
     *
     * @see #stop()
     */
    public function start():Void{
    	isInitalFire = true;
    	tickCount = 0;
    	listener = EnterFrameEventDispatcher.addEventListener(onEnterFrame, this);
    }
    
    /**
     * Returns <code>true</code> if the <code>EnterFrameImpulser</code> is running.
     *
     * @see #start()
     */
    public function isRunning():Boolean{
    	return (listener != null);
    }
    
    /**
     * Stops the <code>EnterFrameImpulser</code>,
     * causing it to stop sending action events
     * to its listeners.
     *
     * @see #start()
     */
    public function stop():Void{
    	EnterFrameEventDispatcher.removeEventListener(listener);
    	delete listener;
    	listener = null;
    }
    
    /**
     * Restarts the <code>EnterFrameImpulser</code>,
     * canceling any pending firings and causing
     * it to fire with its initial delay.
     */
    public function restart():Void{
        stop();
        start();
    }
    
    /**
     * Handles onEnterFrame impulses.
     */
    private function onEnterFrame():Void {
    	if (isInitalFire && tickCount >= getInitialDelay()) {
    		isInitalFire = false;
    		if(repeats){
    			tickCount = 0;
    		}else{
    			stop();
    		}
    		fireActionEvent();
    	} else if (! isInitalFire && tickCount >= getDelay()) {
    		 fireActionEvent();
    		 tickCount = 0;
    	}
    	tickCount++;
    }
        
}
