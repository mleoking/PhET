package edu.colorado.phet.chargesandfields {

import flash.display.Sprite;

public class BackgroundSprite extends Sprite {
    private var myWidth : Number;
    private var myHeight : Number;

    public function BackgroundSprite(w : Number, h : Number) {
        myWidth = w;
        myHeight = h;

        drawBackground();
    }

    public function changeSize(w : Number, h : Number) : void {
        myWidth = w;
        myHeight = h;
        drawBackground();
    }

    public function drawBackground() : void {
        this.graphics.beginFill(0xFFFFFF);
        this.graphics.drawRect(0, 0, myWidth, myHeight);
        this.graphics.endFill();
    }

}
}
