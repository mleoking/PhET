/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 10/12/11
 * Time: 7:19 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.control {
import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.MouseEvent;

public class ShowHideButton extends Sprite {
    private var showButton: Sprite;
    private var hideButton: Sprite;
    private var containerShown: Boolean;  //true if container is shown, and hideButton is shown
    //container = the view or control object which contains this ShowHide button
    //container must have show() and hide() functions which are called by clicking on showHideButton
    private var container: Object;

    public function ShowHideButton( container: Object ) {
        this.container = container;
        this.containerShown = true;
        this.showButton = new Sprite();
        this.hideButton = new Sprite();
        this.drawButtons();
        this.addChild( showButton );
        this.addChild( hideButton );
        this.makeButtonsClickable();
    }

    private function drawButtons(): void {
        //draw show button
        var R: Number = 8;  //half edge-length (radius) of button in pixels
        var gS: Graphics = this.showButton.graphics;
        gS.clear();
        gS.lineStyle( 1.2, 0x225522, 1, false );
        gS.moveTo( 0, 0 );
        gS.beginFill( 0x228822 );
        gS.drawRoundRect( -R, -R, 2 * R, 2 * R, 5 );
        gS.endFill();
        //white plus symbol
        gS.lineStyle( 1.5, 0xffffff, 1, true );
        var f: Number = 0.5;  //fraction of button width = size of plus or minus symbol
        gS.moveTo( -f * R, 0 );
        gS.lineTo( f * R, 0 );
        gS.moveTo( 0, -f * R );
        gS.lineTo( 0, f * R );

        //draw hide button
        var gH: Graphics = this.hideButton.graphics;
        gH.clear();
        gH.lineStyle( 1.2, 0x771111, 1.5, false );
        //gH.moveTo( 0, 0 );
        gH.beginFill( 0xEE3333 );
        gH.drawRoundRect( -R, -R, 2 * R, 2 * R, 5 );
        gH.endFill();
        //white minus symbol
        gH.lineStyle( 1.5, 0xffffff, 1, true );
        gH.moveTo( -f * R, 0 );
        gH.lineTo( f * R, 0 );
    }//end drawButtons();

    private function makeButtonsClickable(): void {
        showButton.buttonMode = true;
        hideButton.buttonMode = true;
        this.showButton.addEventListener( MouseEvent.MOUSE_DOWN, clickButton );
        this.hideButton.addEventListener( MouseEvent.MOUSE_DOWN, clickButton );
        var thisObject: Object = this;

        function clickButton(): void {
            if ( thisObject.containerShown ) {
                thisObject.container.hide();
                thisObject.showButton.visible = true;
                thisObject.hideButton.visible = false;
                thisObject.containerShown = false;
            }
            else {
                thisObject.container.show();
                thisObject.showButton.visible = false;
                thisObject.hideButton.visible = true;
                thisObject.containerShown = true;
            }
        }
    }//end of makeButtonsClickable()
} //end of class
} //end of package
