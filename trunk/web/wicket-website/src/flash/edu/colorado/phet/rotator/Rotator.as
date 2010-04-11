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

    public static var WIDTH : Number = 300;
    public static var HEIGHT : Number = 200;

    private var loaders : Array = new Array();
    private var quantity : Number;
    private var idx : Number = 0;
    private var loadidx : Number = -1;
    private var loaderHolder : MovieClip = new MovieClip();
    private var offset : Number = 0;

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
            loaderHolder.addChild(loaders[0]);
            loaderHolder.addChild(loaders[1]);
            loaders[1].visible = false;
        }
        else {
            quantity = Number(li.parameters.quantity);
            for ( var i : Number = 1; i <= quantity; i++ ) {
                var title : String = li.parameters["title" + String(i)];
                var url : String = li.parameters["url" + String(i)];
                var sim : String = li.parameters["sim" + String(i)];
                var loader:Preview = new Preview(title, url, sim);
                loaders.push(loader);
                loaderHolder.addChild(loader);
                if( i == 1 ) {
                    loader.visible = true;
                } else {
                    loader.visible = false;
                }
            }
        }

        startLoad();

        addChild(loaderHolder);

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
        nextHolder.x = WIDTH - nextText.width - 1;
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
        prevHolder.x = WIDTH - prevText.width - 1 - nextText.width;
        addChild(prevHolder);

        nextHolder.addEventListener(MouseEvent.CLICK, function( evt:Event ) {
            next();
        });

        prevHolder.addEventListener(MouseEvent.CLICK, function( evt:Event ) {
            previous();
        });

        //addChild(debug);

        this.addEventListener(Event.ENTER_FRAME, function( evt:Event ) {
            if ( offset == 0 ) {
                return;
            }

            var LOWER : Number = 10;

            var bounce : Number = offset / 5;
            if( Math.abs(bounce) < LOWER) {
                bounce = offset > 0 ? LOWER : -LOWER;
            }
            if( Math.abs(offset) < LOWER ) {
                bounce = offset;
            }

            offset -= bounce;

            var totalWidth : Number = WIDTH * quantity;

            for ( var i : Number = 0; i < quantity; i++ ) {
                var x : Number = ((i - idx) * WIDTH + offset) % totalWidth;
                if ( x < 0 ) { x += totalWidth; }
                if ( totalWidth - x <= WIDTH ) {
                    x -= totalWidth;
                }
                if ( x < WIDTH ) {
                    loaders[i].x = x;
                    loaders[i].visible = true;
                    //debug.text += " " + String(i) + "V";
                }
                else {
                    loaders[i].visible = false;
                    //debug.text += " " + String(i) + "I";
                }
            }
        });

    }

    private function next() : void {
        if ( !nextPreview().isLoaded() ) {
            return;
        }
        idx = nextIdx(idx);
        offset += WIDTH;
    }

    private function previous() : void {
        if ( !prevPreview().isLoaded() ) {
            return;
        }
        idx = prevIdx(idx);
        offset -= WIDTH;
    }

    private function nextIdx( i : Number ) : Number { return (i + 1) < quantity ? i + 1 : 0;}

    private function prevIdx( i : Number ) : Number { return (i - 1) >= 0 ? i - 1 : quantity - 1; }

    private function nextPreview() : Preview { return loaders[nextIdx(idx)]; }

    private function prevPreview() : Preview { return loaders[prevIdx(idx)]; }

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