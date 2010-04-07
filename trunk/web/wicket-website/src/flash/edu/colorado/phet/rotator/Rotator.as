package edu.colorado.phet.rotator {

import flash.display.*;
import flash.net.URLRequest;

public class Rotator extends MovieClip {

    public function Rotator() {
        var request : URLRequest = new URLRequest("http://192.168.1.64/files/rotator/mass-spring-lab-anim.swf");
        var loader : Loader = new Loader();
        loader.load( request );
        addChild( loader );
    }

}

}