package edu.colorado.phet.rotator {
import flash.display.Loader;
import flash.display.MovieClip;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.LocalConnection;
import flash.net.URLRequest;
import flash.net.navigateToURL;

public class SwfPreview extends Preview {

    private var loader : Loader = new Loader();

    public function SwfPreview( title : String, url : String, sim : String ) {

        addChild(loader);

        loader.contentLoaderInfo.addEventListener(Event.COMPLETE, function( evt : Event ) {
            var clip : MovieClip = loader.content as MovieClip;
            clip.mouseEnabled = false;
            finish();
        });

        loader.mouseEnabled = false;

        super(title, url, sim);
    }

    override public function load() : void {
        if ( isStarted() ) {
            return;
        }
        super.load();

        var lc : LocalConnection = new LocalConnection();
        var domain : String = lc.domain;
        if ( domain == "localhost" ) {
            domain = "192.168.1.64";
        }
        var request : URLRequest = new URLRequest("http://" + domain + "/files/rotator/" + getSim() + "-anim.swf");
        loader.load(request);

        loader.addEventListener(MouseEvent.CLICK, function( evt : Event ) {
            navigateToURL(new URLRequest(getUrl()), '_self');
        });
    }

}
}