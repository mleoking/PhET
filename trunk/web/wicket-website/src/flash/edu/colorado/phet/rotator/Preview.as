package edu.colorado.phet.rotator {
import flash.display.MovieClip;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.URLRequest;
import flash.net.navigateToURL;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;

public class Preview extends MovieClip{

    private var title : String;
    private var url : String;
    private var sim : String;
    private var started : Boolean = false;
    private var loaded : Boolean = false;
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

        var titleText : TextField = new TextField();
        titleText.autoSize = TextFieldAutoSize.LEFT;
        titleText.text = title + " >>";
        titleText.mouseEnabled = false;
        Rotator.styleText(titleText);
        titleText.y = Rotator.HEIGHT - titleText.height - 1;
        addChild(titleText);

        this.addEventListener(MouseEvent.CLICK, function( evt : Event ) {
            navigateToURL(new URLRequest(getUrl()), '_self');
        });
    }

    public function load() : void {
        started = true;
    }

    public function isLoaded() : Boolean {
        return loaded;
    }

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

    public function enable() : void {
        this.visible = true;
        if ( paused ) {
            resume();
        }
    }

    public function disable() : void {
        this.visible = false;
        if ( !paused ) {
            pause();
        }
    }
}
}