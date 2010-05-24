package edu.colorado.phet.rotator {

import flash.display.*;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.system.Security;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

/**
 * Rotates through simulation animations. Parameters passed in through FlashVars. Video encoded as FLV and accessed
 * through NetStream.
 */
public class Rotator extends MovieClip {

    /**
     * Width of rotator and previews
     */
    public static var PREVIEW_WIDTH : Number = 300;

    /**
     * Height of previews
     */
    public static var PREVIEW_HEIGHT : Number = 200;

    public static var FRAMES_BETWEEN_SWITCH : Number = 7 * 30; // * 30 since 30 frames a second

    /**
     * A fixed number of previews.
     */
    private var previews : Array = new Array();

    /**
     * The number of previews
     */
    private var quantity : Number;

    /**
     * The index into the preview array that represents the preview currently in focus. The animation will slide until
     * this preview takes up the entire rotator.
     */
    private var idx : Number = 0;

    /**
     * The index into the preview array for the preview that has the focus at load time
     */
    private var startIdx : Number = 0;

    /**
     * Holds all of the previews as children in the scene graph. loaderHolder is a child of the rotator.
     */
    private var previewHolder : MovieClip = new MovieClip();

    /**
     * The current offset (in pixels) of the desired slide position from the current slide position.
     */
    private var offset : Number = 0;

    /**
     * Count-down to when sliding to the next preview is triggered
     */
    private var timer : Number = FRAMES_BETWEEN_SWITCH;

    /**
     * Sliding-model velocity so we can do smooth animation
     */
    private var velocity : Number = 0;

    /**
     * Debugging display text box
     */
    private var debug : TextField = new TextField();

    public function Rotator() {
        // make sure we can grab FLVs from these locations
        Security.allowDomain("192.168.1.64", "phetsims.colorado.edu", "phet.colorado.edu");

        debug.text = "";

        // in AS3 we grab FlashVars from this
        var li : LoaderInfo = this.root.loaderInfo;

        // if starting index is non-zero, set it up
        if ( li.parameters.startIndex != null && li.parameters.startIndex != undefined ) {
            startIdx = Number(li.parameters.startIndex);
            idx = startIdx;
        }

        // set up all of the preview instances and variables
        if ( !li.parameters.quantity ) {
            // TODO: remove after dev
            quantity = 2;
            previews.push(Preview.createPreview("Masses & Springs", "/en/simulation/mass-spring-lab", "mass-spring-lab"));
            previews.push(Preview.createPreview("Circuit Construction Kit (DC Only)", "/en/simulation/circuit-construction-kit-dc", "circuit-construction-kit-dc"));
            previewHolder.addChild(previews[0]);
            previewHolder.addChild(previews[1]);
            previews[1].visible = false;
            startIdx = 1;
            idx = 1;
        }
        else {
            quantity = Number(li.parameters.quantity);
            for ( var i : Number = 1; i <= quantity; i++ ) {
                // pull parameters from flashvars for each preview
                var title : String = li.parameters["title" + String(i)];
                var url : String = li.parameters["url" + String(i)];
                var sim : String = li.parameters["sim" + String(i)];
                var preview:Preview = Preview.createPreview(title, url, sim);
                previews.push(preview);
                previewHolder.addChild(preview);
                preview.visible = i == 1;
            }
        }

        // position all of the previews
        reposition();

        // hide the starting preview so we can fade in later
        previews[startIdx].alpha = 0;

        // trigger the loading of the 1st preview and upon completion load the 2nd.
        startLoad();

        addChild(previewHolder);

        // always show the hand over this area
        this.useHandCursor = true;
        this.buttonMode = true;

        // whether to position the next/previous on the right or left
        var farRight : Boolean = true;

        // next button

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
        nextHolder.x = farRight ? PREVIEW_WIDTH - nextText.width - 1 : PREVIEW_WIDTH / 2;
        nextHolder.y = PREVIEW_HEIGHT;
        addChild(nextHolder);

        // previous button

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
        prevHolder.x = farRight ? PREVIEW_WIDTH - prevText.width - 1 - nextText.width : PREVIEW_WIDTH / 2 - prevText.width;
        prevHolder.y = PREVIEW_HEIGHT;
        addChild(prevHolder);

        // event handling

        nextHolder.addEventListener(MouseEvent.CLICK, function( evt:Event ) {
            next(true);
        });

        prevHolder.addEventListener(MouseEvent.CLICK, function( evt:Event ) {
            previous(true);
        });

        //addChild(debug);

        this.addEventListener(Event.ENTER_FRAME, function( evt:Event ) {
            // do all of this every frame

            if ( !previews[startIdx].isLoaded() ) {
                return;
            }

            // fade in the first preview once it is loaded
            if ( previews[startIdx].alpha < 1 ) {
                previews[startIdx].alpha += 0.1;
            }

            // if enough time has passed, start sliding to the next preview
            timer--;
            if ( timer == 0 ) {
                next();
            }

            // if we are at our destination, don't bother recomputing velocity and position for the sliding.
            if ( offset == 0 ) {
                return;
            }

            // do some math that is mostly an over-damped oscillator but near the target is weighted with something else

            var c0 : Number = 0.2;
            var c1 : Number = 0.015;
            var a : Number = -c0 * velocity - c1 * offset;
            var bounce : Number = velocity + 0.5 * a;
            velocity += a;

            var minv : Number = 5;
            var slide : Number = 50;
            if ( Math.abs(offset) < slide ) {
                // how much weight to give to the stabilizer
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

            // position the previews, and handle visibility
            reposition();
        });

    }

    /**
     * Move all of the previews into place, and enable / disable them depending on whether they are in our range of view
     */
    private function reposition() : void {
        var totalWidth : Number = PREVIEW_WIDTH * quantity;

        for ( var i : Number = 0; i < quantity; i++ ) {
            var x : Number = ((i - idx) * PREVIEW_WIDTH + offset) % totalWidth;
            if ( x < 0 ) { x += totalWidth; }
            if ( totalWidth - x < PREVIEW_WIDTH ) {
                x -= totalWidth;
            }
            if ( x < PREVIEW_WIDTH ) {
                previews[i].x = x;
                previews[i].enable();
            }
            else {
                // just in case, move it far away. sometimes flash player uses less processor for these things
                previews[i].x = -5000;
                previews[i].disable();
            }
        }
    }

    /**
     * Reset the time left before a slide is triggered
     */
    private function resetTimer() : void {
        timer = FRAMES_BETWEEN_SWITCH;
    }

    /**
     * Slide one to the right
     * @param force Whether to force movement if the next preview is not ready to be viewed
     */
    private function next( force : Boolean = false ) : void {
        resetTimer();
        if ( !force && !nextPreview().isLoaded() ) {
            // sanity check to try triggering the load
            triggerLoad(nextPreview());
            return;
        }
        idx = nextIdx(idx);
        offset += PREVIEW_WIDTH;

        // start loading the next preview
        triggerLoad(previews[nextIdx(idx)]);
    }

    /**
     * Slide one to the left
     * @param force Whether to force movement if the next preview is not ready to be viewed
     */
    private function previous( force : Boolean = false ) : void {
        resetTimer();
        if ( !force && !prevPreview().isLoaded() ) {
            // sanity check to try triggering the load
            triggerLoad(prevPreview());
            return;
        }
        idx = prevIdx(idx);
        offset -= PREVIEW_WIDTH;

        // if going backwards, might have to load the current one
        triggerLoad(previews[idx]);

        // start loading the next one if they keep going back
        triggerLoad(previews[prevIdx(idx)]);
    }

    private function nextIdx( i : Number ) : Number { return (i + 1) < quantity ? i + 1 : 0;}

    private function prevIdx( i : Number ) : Number { return (i - 1) >= 0 ? i - 1 : quantity - 1; }

    private function nextPreview() : Preview { return previews[nextIdx(idx)]; }

    private function prevPreview() : Preview { return previews[prevIdx(idx)]; }

    /**
     * Start loading the first preview, and upon completion of that then start loading the 2nd preview. Do no more,
     * because if the user navigates away quickly then they won't have downloaded everything off of our server. This
     * should help conserve bandwidth
     */
    private function startLoad() : void {
        triggerLoad(previews[startIdx]);
        previews[startIdx].addEventListener(Preview.LOADED, function loadEvt( evt : Event ) : void {
            if ( previews[nextIdx(startIdx)] != null ) {
                triggerLoad(previews[nextIdx(startIdx)]);
            }
        });
    }

    /**
     * If a preview hasn't attempted to load yet, start trying to load it
     * @param preview The preview to load
     */
    private function triggerLoad( preview : Preview ) : void {
        if ( !preview.isStarted() ) {
            preview.load();
        }
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