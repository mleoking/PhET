package edu.colorado.phet.rotator {
import flash.events.NetStatusEvent;
import flash.media.Video;
import flash.net.LocalConnection;
import flash.net.NetConnection;
import flash.net.NetStream;

/**
 * A preview that loads a FLV video using streaming
 */
public class StreamPreview extends Preview {

    /**
     * Video object node in scene graph
     */
    private var video : Video;

    /**
     * stream responsible for handling the FLV video itself
     */
    private var netStream : NetStream;

    public function StreamPreview( title : String, url : String, sim : String ) {

        video = new Video();
        video.width = Rotator.PREVIEW_WIDTH;
        video.height = Rotator.PREVIEW_HEIGHT;
        addChild(video);

        var customClient:Object = new Object();
        customClient.onCuePoint = function( infoObject:Object ):void { };
        customClient.onMetaData = function( infoObject:Object ):void { };

        var netConnection:NetConnection = new NetConnection();
        netConnection.connect(null);
        netStream = new NetStream(netConnection);
        netStream.checkPolicyFile = false;
        netStream.client = customClient;
        video.attachNetStream(netStream);

        netStream.addEventListener(NetStatusEvent.NET_STATUS, function( event:NetStatusEvent ) {
            if ( event.info.code == "NetStream.Play.Stop" ) {
                // seeking to 0 when it is stopped keeps this preview repeating the video
                netStream.seek(0);
            }
            else if ( event.info.code == "NetStream.Buffer.Full" ) {
                // this signifies that the buffer is full enough to start playing the animation. we call finish here
                finish();
            }
        });

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
            // TODO: remove after dev
            domain = "192.168.1.64";
        }

        netStream.play("http://" + domain + "/files/rotator/" + getSim() + ".flv");
    }


    override protected function pause():void {
        if ( !isLoaded() ) {
            return;
        }
        setPaused(true);
        netStream.pause();
        video.visible = false;
    }

    override protected function resume():void {
        if ( !isLoaded() ) {
            return;
        }
        setPaused(false);
        video.visible = true;
        netStream.resume();
    }
}
}