package edu.colorado.phet.rotator {

import flash.display.*;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.URLRequest;
import flash.net.navigateToURL;
import flash.system.Security;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

public class Rotator extends MovieClip {

    private var loaders : Array = new Array();
    private var quantity : Number;
    private var idx : Number = 0;
    private var loadidx : Number = -1;
    private var loaderHolder : MovieClip = new MovieClip();

    private var debug : TextField = new TextField();

    public function Rotator() {
        Security.allowDomain("192.168.1.64", "phetsims.colorado.edu", "phet.colorado.edu");

        debug.text = "";

        var li : LoaderInfo = this.root.loaderInfo;

        if ( !li.parameters.quantity ) {
            // TODO: remove after dev
            quantity = 2;
            loaders.push(new Preview("Masses & Springs", "/en/simulation/mass-spring-lab", "mass-spring-lab"));
            loaders.push(new Preview("Circuit Construction Kit (DC Only)", "/en/simulation/circuit-construction-kit/circuit-construction-kit-dc", "circuit-construction-kit-dc"));
        }
        else {
            quantity = Number(li.parameters.quantity);
            for ( var i : Number = 1; i <= quantity; i++ ) {
                var title : String = li.parameters["title" + String(i)];
                var url : String = li.parameters["url" + String(i)];
                var sim : String = li.parameters["sim" + String(i)];
                loaders.push(new Preview(title, url, sim));
            }
        }

        startLoad();

        addChild(loaderHolder);
        loaderHolder.addChild(loaders[0]);

        this.useHandCursor = true;
        this.buttonMode = true;

        var nextHolder : Sprite = new Sprite();
        nextHolder.mouseEnabled = true;
        nextHolder.useHandCursor = true;
        nextHolder.buttonMode = true;

        var nextText : TextField = new TextField();
        nextText.text = " > ";
        nextText.mouseEnabled = false;
        styleText(nextText);
        nextHolder.addChild(nextText);
//        nextHolder.y = 200 - nextText.height - 1;
        nextHolder.x = 300 - nextText.width - 1;
        addChild(nextHolder);

        var prevHolder : Sprite = new Sprite();
        prevHolder.mouseEnabled = true;
        prevHolder.useHandCursor = true;
        prevHolder.buttonMode = true;

        var prevText : TextField = new TextField();
        prevText.text = " < ";
        prevText.mouseEnabled = false;
        styleText(prevText);
        prevHolder.addChild(prevText);
//        prevHolder.y = 200 - prevText.height - 1;
        prevHolder.x = 300 - prevText.width - 1 - nextText.width;
        addChild(prevHolder);

        nextHolder.addEventListener(MouseEvent.CLICK, function( evt:Event ) {
            //loaderHolder.visible = false;
            next();
        });

        prevHolder.addEventListener(MouseEvent.CLICK, function( evt:Event ) {
            //loaderHolder.visible = true;
            previous();
        });

        //addChild( debug );

    }

    private function next() : void {
        loaderHolder.removeChild(loaders[idx]);
        idx++;
        if ( idx == quantity ) {
            idx = 0;
        }
        loaderHolder.addChild(loaders[idx]);
    }

    private function previous() : void {
        loaderHolder.removeChild(loaders[idx]);
        idx--;
        if ( idx < 0 ) {
            idx = quantity - 1;
        }
        loaderHolder.addChild(loaders[idx]);
    }

    private function startLoad() : void {
        loadidx++;
        if ( loadidx < quantity ) {
            loaders[loadidx].getLoader().contentLoaderInfo.addEventListener(Event.COMPLETE, loadEvt);
            loaders[loadidx].load();
        }
    }

    private function loadEvt( evt : Event ) : void {
        //debug.text += "L";
        startLoad();
    }

    public static function styleText( tf : TextField ) : void {
        var format : TextFormat = new TextFormat();
        format.size = 12;
        format.bold = true;
        format.color = 0x555555;

        tf.autoSize = TextFieldAutoSize.LEFT;
        tf.backgroundColor = 0xFFFFFF;
        tf.background = true;
        tf.borderColor = 0x777777;
        tf.border = true;
        tf.setTextFormat(format);
    }
}

}