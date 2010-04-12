package edu.colorado.phet.rotator {
import flash.display.Loader;
import flash.display.MovieClip;
import flash.display.Sprite;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.LocalConnection;
import flash.net.URLRequest;
import flash.net.navigateToURL;
import flash.text.TextField;

public class Preview extends MovieClip{

    private var title : String;
    private var url : String;
    private var sim : String;
    private var started : Boolean = false;
    private var loaded : Boolean = false;
    private var loader : Loader = new Loader();

    public function Preview( title : String, url : String, sim : String ) {
        super();
        this.title = title;
        this.url = url;
        this.sim = sim;

        addChild(loader);

        loader.contentLoaderInfo.addEventListener(Event.COMPLETE, function( evt : Event ) {
            var clip : MovieClip = loader.content as MovieClip;
            clip.useHandCursor = true;
            clip.mouseEnabled = true;
            clip.buttonMode = true;
            loaded = true;
        });

        loader.mouseEnabled = true;

        var titleText : TextField = new TextField();
        titleText.text = title + " >>";
        titleText.mouseEnabled = false;
        Rotator.styleText(titleText);
        titleText.y = Rotator.HEIGHT - titleText.height - 1;
        addChild(titleText);
    }

    public function load() : void {
        if ( started ) {
            return;
        }
        started = true;
        var lc : LocalConnection = new LocalConnection();
        var domain : String = lc.domain;
        if( domain == "localhost" ) {
            domain = "192.168.1.64";
        }
        var request : URLRequest = new URLRequest("http://" + domain + "/files/rotator/" + sim + "-anim.swf");
        loader.load(request);

        loader.addEventListener(MouseEvent.CLICK, function( evt : Event ) {
            navigateToURL(new URLRequest(url), '_self');
        });
    }

    public function isLoaded() : Boolean {
        return loaded;
    }

    public function getLoader() : Loader {
        return loader;
    }
}
}