/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlUtils;
import org.aswing.awml.XmlLoader;
import org.aswing.util.SuspendedCall;

/**
 * Provides load handling routines for AWML files. 
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.AwmlLoader extends XmlLoader {

    /**
     * When the parsing is completed.
     *<br>
     * onParseComplete(loader:AwmlLoader)
     */ 
    public static var ON_PARSE_COMPLETE:String = "onParseComplete";   

    /**
     * When the parsing is failed.
     *<br>
     * onParseFail(loader:AwmlLoader, message:String)
     */ 
    public static var ON_PARSE_FAIL:String = "onParseFail";   

    
    /** 
     * Specifies if AWML needs to be parsed on load ot not
     */
    private var parseOnLoad:Boolean;
    
        
    /** 
     * AwmlLoader()<br>
     * AwmlLoader(parseOnLoad)<br>
     * 
     * <p>Constructors new <code>AwmlLoader</code> instance. If <code>parseOnLoad</code>
     * argument is <code>true</code> AWML will be parsed after loading automatically.
     * 
     * <p>Also if <code>parseOnLoad</code> is <code>true</code> action listeners will be
     * invoked after AWML will be parsed. Else action listeners will be invoked once AWML 
     * will be loaded.    
     * 
     * @param parseOnLoad (optional) the flag specified if AWML needs to be
     * parsed on load or not. Default is <code>true</code> 
     */
    public function AwmlLoader(parseOnLoad:Boolean) {
        super();
        
        this.parseOnLoad = (parseOnLoad != null) ? parseOnLoad : true;
    }
    
    /**
     * Assumes if AWML needs to be parsed on load or not.
     * 
     * @param parseOnLoad the flag specified if AWML needs to be parsed on load or not.
     */
    public function setParseOnLoad(parseOnLoad:Boolean):Void {
        this.parseOnLoad = parseOnLoad;
    }

    /** 
     * Returns <code>true</code> if current <code>AwmlLoader</code> instance
     * will parse AWML on load and <code>false</code> if not.
     * 
     * @return whrever AWML will be parsed on load or not
     */
    public function isParseOnLoad():Boolean {
        return parseOnLoad;
    }
    
    /**
     * Fires #ON_PARSE_COMPLETE event.
     */
    private function fireParseCompleteEvent():Void {
        dispatchEvent(createEventObj(ON_PARSE_COMPLETE));
    }

    /**
     * Fires #ON_PARSE_FAIL event.
     */
    private function fireParseFailEvent(msg:String):Void {
        dispatchEvent(createEventObj(ON_PARSE_FAIL, msg));
    }
    
    /**
     * Handles <code>LoadVars#onData</code> event.
     */
    private function __onData(awmlStr:String):Void {
    	
    	// fire last load progress event
    	fireLoadProgressEvent();
    	
    	// stop load progress tracker
    	loadProgressTracker.stop();
    	
    	if (awmlStr != null) {
            
            // get awml
            var awml:XMLNode = AwmlUtils.strToXml(awmlStr);
            fireLoadCompleteEvent(awml);
            
            // parse awml
            if (parseOnLoad) {
            	try {
            		AwmlParser.parseAll(awml);
            	} catch (e:Error) {
            		fireParseFailEvent(e.message);	
            		return;
            	}
            	SuspendedCall.createCall(__onParse, this, 1);
            } else {
                fireActionEvent(awml);
            }
        } else {
            fireLoadFailEvent();
        }
    }
    
    /**
     * Handles AWML parsing complete event.
     */
    private function __onParse():Void {
        fireParseCompleteEvent();
        fireActionEvent();
    }
}