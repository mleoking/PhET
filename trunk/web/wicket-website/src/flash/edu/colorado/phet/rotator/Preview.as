package edu.colorado.phet.rotator {
import flash.display.MovieClip;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.URLRequest;
import flash.net.navigateToURL;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;

/**
 * Abstract preview class.
 */
public class Preview extends MovieClip{

    /**
     * What to display as a simulation title
     */
    private var title : String;

    /**
     * The URL to which to redirect the user when they click on this preview
     */
    private var url : String;

    /**
     * The internal name of the simulation
     */
    private var sim : String;

    /**
     * Whether we have started the downloading (loading) process
     */
    private var started : Boolean = false;

    /**
     * Whether we have completed (enough) loading
     */
    private var loaded : Boolean = false;

    /**
     * Whether the preview is paused
     */
    private var paused : Boolean = false;

    public static var LOADED : String = "previewLoaded";

    public static function createPreview( title : String, url : String, sim : String ) : Preview {
        return new StreamPreview(title, url, sim);
        //return new SwfPreview(title, url, sim);
    }

    public function Preview( title : String, url : String, sim : String ) {
        super();
        this.title = title;
        this.url = url;
        this.sim = sim;

        // add a text label for the simulation title

        var titleText : TextField = new TextField();
        titleText.autoSize = TextFieldAutoSize.LEFT;
        titleText.text = title + " >>";
        titleText.mouseEnabled = false;
        Rotator.styleText(titleText);
        titleText.y = Rotator.PREVIEW_HEIGHT - titleText.height - 1;
        addChild(titleText);

        this.addEventListener(MouseEvent.CLICK, function( evt : Event ) {
            navigateToURL(new URLRequest(getUrl()), '_self');
        });
    }

    /**
     * When subclasses start loading, remember this
     */
    public function load() : void {
        started = true;
    }

    public function isLoaded() : Boolean {
        return loaded;
    }

    /**
     * Subclasses should call this when they have finished enough loading
     */
    public function finish() : void {
        if ( loaded ) { return; }
        loaded = true;
        this.dispatchEvent(new Event(LOADED));
    }

    public function getTitle():String {
        return title;
    }

    public function getUrl():String {
        return url;
    }

    public function getSim():String {
        return sim;
    }

    public function isStarted():Boolean {
        return started;
    }

    public function isPaused() : Boolean {
        return paused;
    }

    protected function pause() : void {

    }

    protected function resume() : void {

    }

    protected function setPaused( p : Boolean ) : void {
        paused = p;
    }

    /**
     * Enable this preview so it can be viewed
     */
    public function enable() : void {
        this.visible = true;
        if ( paused ) {
            resume();
        }
    }

    /**
     * Temporarily disable this preview so it won't use up processor cycles. Shouldn't be seen.
     */
    public function disable() : void {
        this.visible = false;
        if ( !paused ) {
            pause();
        }
    }
}
}