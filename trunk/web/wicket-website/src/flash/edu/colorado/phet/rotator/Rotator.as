package edu.colorado.phet.rotator {

import flash.display.*;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.system.Security;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

public class Rotator extends MovieClip {

    public static var WIDTH : Number = 300;
    public static var HEIGHT : Number = 200;
    public static var FRAMES_BETWEEN_SWITCH : Number = 7 * 30;

    private var loaders : Array = new Array();
    private var quantity : Number;
    private var idx : Number = 0;
    private var loadidx : Number = -1;
    private var loaderHolder : MovieClip = new MovieClip();
    private var offset : Number = 0;
    private var timer : Number = FRAMES_BETWEEN_SWITCH;

    private var velocity : Number = 0;

    private var debug : TextField = new TextField();

    public function Rotator() {
        Security.allowDomain("192.168.1.64", "phetsims.colorado.edu", "phet.colorado.edu");

        debug.text = "";

        var li : LoaderInfo = this.root.loaderInfo;

        if ( !li.parameters.quantity ) {
            // TODO: remove after dev
            quantity = 2;
            loaders.push(Preview.createPreview("Masses & Springs", "/en/simulation/mass-spring-lab", "mass-spring-lab"));
            loaders.push(Preview.createPreview("Circuit Construction Kit (DC Only)", "/en/simulation/circuit-construction-kit-dc", "circuit-construction-kit-dc"));
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
                var loader:Preview = Preview.createPreview(title, url, sim);
                loaders.push(loader);
                loaderHolder.addChild(loader);
                loader.visible = i == 1;
            }
        }

        loaders[0].alpha = 0;

        startLoad();

        addChild(loaderHolder);

        this.useHandCursor = true;
        this.buttonMode = true;

        var farRight : Boolean = true;

        var nextHolder : Sprite = new Sprite();
        nextHolder.mouseEnabled = true;
        nextHolder.useHandCursor = true;
        nextHolder.buttonMode = true;

        var nextText : TextField = new TextField();
        nextText.autoSize = TextFieldAutoSize.LEFT;
        nextText.text = " " + (li.parameters.next ? li.parameters.next : "next") + " > ";
        nextText.mouseEnabled = false;
        styleText(nextText, 10, 0x444444);
        nextHolder.addChild(nextText);
        nextHolder.x = farRight ? WIDTH - nextText.width - 1 : WIDTH / 2;
        nextHolder.y = HEIGHT;
        addChild(nextHolder);

        var prevHolder : Sprite = new Sprite();
        prevHolder.mouseEnabled = true;
        prevHolder.useHandCursor = true;
        prevHolder.buttonMode = true;

        var prevText : TextField = new TextField();
        prevText.autoSize = TextFieldAutoSize.LEFT;
        prevText.text = " < " + (li.parameters.previous ? li.parameters.previous : "previous" ) + " ";
        prevText.mouseEnabled = false;
        styleText(prevText, 10, 0x444444);
        prevHolder.addChild(prevText);
        prevHolder.x = farRight ? WIDTH - prevText.width - 1 - nextText.width : WIDTH / 2 - prevText.width;
        prevHolder.y = HEIGHT;
        addChild(prevHolder);

        nextHolder.addEventListener(MouseEvent.CLICK, function( evt:Event ) {
            next();
        });

        prevHolder.addEventListener(MouseEvent.CLICK, function( evt:Event ) {
            previous();
        });

        //addChild(debug);

        this.addEventListener(Event.ENTER_FRAME, function( evt:Event ) {
            if ( !loaders[0].isLoaded() ) {
                return;
            }
            if ( loaders[0].alpha < 1 ) {
                loaders[0].alpha += 0.1;
            }
            timer--;
            if ( timer == 0 ) {
                next();
            }

            if ( offset == 0 ) {
                return;
            }

            //            var LOWER : Number = 5;
            //
            //            var bounce : Number = 3 * Math.sqrt(Math.abs(offset));
            //            bounce *= (offset > 0 ? 1 : -1);
            //            if ( Math.abs(bounce) < LOWER ) {
            //                bounce = offset > 0 ? LOWER : -LOWER;
            //            }
            //            if ( Math.abs(offset) < LOWER ) {
            //                bounce = offset;
            //            }
            //
            //            offset -= bounce;

            var c0 : Number = 0.2;
            var c1 : Number = 0.015;
            var a : Number = -c0 * velocity - c1 * offset;
            var bounce : Number = velocity + 0.5 * a;
            velocity += a;

            var minv : Number = 5;
            var slide : Number = 50;
            if ( Math.abs(offset) < slide ) {
                var frac : Number = Math.abs(offset) / slide;
                //                frac -= 0.15;
                if ( frac < 0 ) { frac = 0; }
                //                bounce = (bounce > 0 ? minv : -minv) * (1 - frac) + frac * bounce;
                //                bounce *= (1 - frac) * bounce;
                bounce *= 2 - frac;
            }
            if ( Math.abs(offset) < minv ) {
                bounce = -offset;
            }

            offset += bounce;

            var totalWidth : Number = WIDTH * quantity;

            for ( var i : Number = 0; i < quantity; i++ ) {
                var x : Number = ((i - idx) * WIDTH + offset) % totalWidth;
                if ( x < 0 ) { x += totalWidth; }
                if ( totalWidth - x < WIDTH ) {
                    x -= totalWidth;
                }
                if ( x < WIDTH ) {
                    loaders[i].x = x;
                    loaders[i].visible = true;
                    //debug.text += " " + String(i) + "V";
                }
                else {
                    loaders[i].x = -5000;
                    loaders[i].visible = false;
                    //debug.text += " " + String(i) + "I";
                }
            }
        });

    }

    private function resetTimer() : void {
        timer = FRAMES_BETWEEN_SWITCH;
    }

    private function next() : void {
        resetTimer();
        if ( !nextPreview().isLoaded() ) {
            return;
        }
        idx = nextIdx(idx);
        offset += WIDTH;
    }

    private function previous() : void {
        resetTimer();
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
            //loaders[loadidx].getLoader().contentLoaderInfo.addEventListener(Event.COMPLETE, loadEvt);
            loaders[loadidx].addEventListener(Preview.LOADED, loadEvt);
            loaders[loadidx].load();
        }
    }

    private function loadEvt( evt : Event ) : void {
        //debug.text += "L";
        startLoad();
    }

    public static function styleText( tf : TextField, size : Number = 12, color : Number = 0x555555 ) : void {
        var format : TextFormat = new TextFormat();
        format.size = size;
        format.bold = true;
        format.color = color;
        format.font = "Arial";

        //tf.autoSize = TextFieldAutoSize.LEFT;
        tf.backgroundColor = 0xFFFFFF;
        tf.background = true;
        tf.borderColor = 0x777777;
        tf.border = true;
        tf.setTextFormat(format);
        tf.embedFonts = false;
    }
}

}