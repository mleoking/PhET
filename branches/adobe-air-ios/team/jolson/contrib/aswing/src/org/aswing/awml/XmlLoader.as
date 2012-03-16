/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.XmlUtils;
import org.aswing.EventDispatcher;
import org.aswing.util.Delegate;
import org.aswing.util.Impulser;
import org.aswing.util.Timer;

/**
 * Provides load handling routines for XML files. 
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.XmlLoader extends EventDispatcher {

    /**
     * When the loading is started.
     *<br>
     * onLoadStart(loader:XmlLoader)
     */ 
    public static var ON_LOAD_START:String = "onLoadStart"; 

    /**
     * When the loading is completed.
     *<br>
     * onLoadComplete(loader:XmlLoader, xml:XMLNode)
     */ 
    public static var ON_LOAD_COMPLETE:String = "onLoadComplete";   

    /**
     * When the loading is failed.
     *<br>
     * onLoadFail(loader:XmlLoader)
     */ 
    public static var ON_LOAD_FAIL:String = "onLoadFail";   

	/**
	 * Fires load progress event in period specified with #setLoadProgressInterval method.
	 *<br>
	 * onLoadProgress(loader:XmlLoader, loadedBytes:Number, totalBytes:Number)
	 */	
	public static var ON_LOAD_PROGRESS:String = "onLoadProgress";


	/**
	 * Default interval between firing #ON_PROGRESS_LOAD events. Equals to 1 second.
	 */
	public static var LOAD_PROGRESS_INTERVAL:Number = 1000;
    
    
    /** 
     * <code>LoadVars</code> instance for loading the content. 
     */
    private var loader:LoadVars;
    
    /**
     * Result XML.
     */
    private var xml:XMLNode;
    
    /**
     * Fires #ON_LOAD_PROGRESS event in specified interval.
     */
    private var loadProgressTracker:Impulser;
    
    /** 
     * Constructors new <code>XmlLoader</code> instance.
     */
    public function XmlLoader() {
        super();
        
        loadProgressTracker = new Timer(LOAD_PROGRESS_INTERVAL, true);
        loadProgressTracker.addActionListener(fireLoadProgressEvent, this);
        
        loader = new LoadVars();
        loader.onData = Delegate.create(this, __onData);
    }
    
    /**
     * addActionListener(fuc:Function, obj:Object)<br>
     * addActionListener(fuc:Function)<br>
     * Adds a action listener to the loader. Loader fires a action event when 
     * XML file is loaded.
     * @param fuc the listener function.
     * @param obj which context to run in by the func.
     * @return the listener just added.
     * @see EventDispatcher#ON_ACT
     */
    public function addActionListener(fuc:Function, obj:Object):Object{
        return addEventListener(ON_ACT, fuc, obj);
    }
    
    /**
     * Start loading XML file from the specified <code>url</code>.
     * 
     * @param url the URL there XML file is located. 
     */
    public function load(url:String):Void {
        if (loader.load(url)) {
            fireLoadStartEvent();
            loadProgressTracker.start();   
        } else {
            fireLoadFailEvent();    
        }
    } 
    
    /**
     * Checks if XML file loading is currently in progress.
     * 
     * @return <code>true</code> if XML file is loading currently and <code>false</code> if not. 
     */
    public function isLoading():Boolean {
    	return loadProgressTracker.isRunning();	
    }
    
    /**
     * Sets interval between firing #ON_LOAD_PROGRESS events in milliseconds. If specified interval 
     * is undefined or not a number, default #LOAD_PROGRESS_INTERVAL is used.
     * 
     * @param interval the interval between events.
     */
    public function setLoadProgressInterval(interval:Number):Void {
    	if (interval == null || isNaN(interval)) interval = LOAD_PROGRESS_INTERVAL;
    	
    	if (loadProgressTracker.getDelay() != interval) {
    		loadProgressTracker.setDelay(interval);
    		if (loadProgressTracker.isRunning()) {
    			loadProgressTracker.restart();
    		}
    	}
    }
    
    /**
     * Returns current interval between firing #ON_LOAD_PROGRESS events in milliseconds.
     * 
     * @return interval between events.
     */
    public function getLoadProgressInterval(Void):Number {
    	return loadProgressTracker.getDelay();
    }
    
    /**
     * Returns loaded XML.
     * 
     * @return loaded XML
     */
    public function getXML():XMLNode {
    	return xml;	
    }
    
    /**
     * Fires #ON_LOAD_START event.
     */
    private function fireLoadStartEvent():Void {
        dispatchEvent(createEventObj(ON_LOAD_START));
    }

    /**
     * Fires #ON_LOAD_COMPLETE event.
     */
    private function fireLoadCompleteEvent(xml:XMLNode):Void {
        dispatchEvent(createEventObj(ON_LOAD_COMPLETE, xml));
    }
        
    /**
     * Fires #ON_LOAD_FAIL event.
     */
    private function fireLoadFailEvent():Void {
        dispatchEvent(createEventObj(ON_LOAD_FAIL));
    }

    /**
     * Fires #ON_LOAD_PROGRESS event.
     */
    private function fireLoadProgressEvent():Void {
    	if (loadProgressTracker.isRunning() && loader.getBytesTotal() != undefined) {
        	dispatchEvent(createEventObj(ON_LOAD_PROGRESS, loader.getBytesLoaded(), loader.getBytesTotal()));
    	}
    }

    /**
     * Handles <code>LoadVars#onData</code> event.
     */
    private function __onData(result:String):Void {
    	
    	// fire last load progress event
    	fireLoadProgressEvent();
    	
    	// stop load progress tracker
    	loadProgressTracker.stop();
    	
    	if (result != null) {
            xml = XmlUtils.strToXml(result);
            fireLoadCompleteEvent(xml);
        } else {
            fireLoadFailEvent();
        }
    }
    
}