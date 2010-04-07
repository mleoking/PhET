package edu.colorado.phet.rotator {

import flash.display.*;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.URLRequest;
import flash.net.navigateToURL;
import flash.system.Security;
import flash.text.TextField;

public class Rotator extends MovieClip {

    public function Rotator() {
        Security.allowDomain("192.168.1.64", "phetsims.colorado.edu", "phet.colorado.edu");
        var request : URLRequest = new URLRequest("http://192.168.1.64/files/rotator/mass-spring-lab-anim.swf");
        var loader : Loader = new Loader();

        loader.contentLoaderInfo.addEventListener(Event.COMPLETE, function( evt : Event ) {
            var clip : MovieClip = loader.content as MovieClip;
            clip.useHandCursor = true;
            clip.mouseEnabled = true;
            clip.buttonMode = true;
        });

        loader.load(request);
        addChild(loader);

        this.useHandCursor = true;

        var li : LoaderInfo = this.root.loaderInfo;
        var url1 : String = li.parameters.url1;
        if ( url1 ) {
            var tf : TextField = new TextField();
            tf.text = url1;
            addChild(tf);
        }

        loader.addEventListener(MouseEvent.CLICK, function( evt : Event ) {
            navigateToURL(new URLRequest(url1), '_self');
        });


    }

}

}