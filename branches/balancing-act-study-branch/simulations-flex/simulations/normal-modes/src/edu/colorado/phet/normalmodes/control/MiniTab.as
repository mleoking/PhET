/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 3/25/12
 * Time: 10:01 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.control {
import edu.colorado.phet.flashcommon.controls.NiceLabel;
import edu.colorado.phet.normalmodes.util.TwoHeadedArrow;

import flash.display.Graphics;
import flash.display.Sprite;
import flash.events.MouseEvent;

/**
 * One of two miniTabs on miniTabBar, used for control of ButtonArrayPanel.
 * Each tab is the top of a rectangular folder which surrounds the button array.
 * The two miniTabs are for horiz and vert polarizations.
 * Registration point is top left corner of 1st tab.
 */
public class MiniTab extends Sprite {
    private var myMiniTabBar: MiniTabBar;
    private var polarizationType: String;    //"H" for horiz polarization or "V" for vert polarization
    private var tabNumber: int;              //1 for 1st tab on left, 2 for second tab, etc
    private var tabWidth: Number;            //width of tab in pixels
    private var tabHeight: Number;           //height of tab
    private var xStartPosition: Number;      //stage coordinate of left edge of tab in pixels
    private var tabColor: uint;
    private var _selected: Boolean;
    private var tabLabel: NiceLabel;
    private var tabIcon: TwoHeadedArrow;      //icon (= vert or horiz arrow) on the tab
    private var folderWidth: Number;
    private var folderHeight: Number;

    public function MiniTab( myMiniTabBar: MiniTabBar, tabNumber: int, tabWidth: Number, tabHeight: Number, folderWidth: Number, tabColor: uint, labelText: String ) {
        this.myMiniTabBar = myMiniTabBar;
        this.tabNumber = tabNumber;
        this.tabWidth = tabWidth;
        this.tabHeight = tabHeight;
        this.tabColor = tabColor;
        this.folderWidth = folderWidth;
        this.folderHeight = this.folderWidth;
        this.xStartPosition = (this.tabNumber - 1) * (this.tabWidth + 0.25 * this.tabHeight );
        this.tabLabel = new NiceLabel( 12, "   " + labelText );  //NiceLabel(fontSize, text), text is padded to push label to right
        if ( labelText == "Horizontal" ) {
            this.polarizationType = "H";
        }
        else {
            this.polarizationType = "V";
        }
        this.tabIcon = new TwoHeadedArrow();
        this.drawTabShape();
        this.activateTab();
        this.addChild( tabLabel );
        this.positionFields();
        this.addChild( tabIcon );
    } //end constructor

    public function set selected( tOrF: Boolean ): void {
        this._selected = tOrF;
        this.tabLabel.setBold( tOrF );
    }

    public function positionFields(): void {
        this.tabLabel.x = this.xStartPosition;
        this.tabIcon.width = 18;        //icon is the double-headed arrow
        this.tabIcon.height = 10;
        this.tabIcon.x = this.xStartPosition + this.tabLabel.width + 6;
        this.tabIcon.y = 0.5 * this.tabLabel.height;
        if ( this.tabNumber == 2 ) {     //tab 2 is the vertical polarization tab, so need vertical arrow icon
            this.tabIcon.setRegistrationPointAtCenter( true );
            this.tabIcon.rotation = 90;
            this.tabIcon.x += 5;    //have to tweek position so icon doesn't collide with border
            this.tabIcon.y += 2;
        }
    }

    private function drawTabShape(): void {
        var tW: Number = this.tabWidth;
        var tH: Number = this.tabHeight;
        var fW: Number = this.folderWidth;
        var fH: Number = this.folderHeight;
        var cS: Number = 15;  //cornerSize
        var g: Graphics = this.graphics;
        g.clear();
        g.lineStyle( 1.5, 0x0000ff, 1 );
        g.beginFill( this.tabColor );
        //Start at top left corner of tab, 1st tab is at 0,0
        var xStart: Number = this.xStartPosition;
        with ( g ) {
            moveTo( xStart, 0 );
            lineTo( xStart + tW, 0 );
            lineTo( xStart + tW + tH, tH );
            lineTo( fW - cS, tH );          //top right corner of folder
            curveTo( fW, tH, fW, tH + cS )
            lineTo( fW, tH + fH - cS );     //bottom right corner
            curveTo( fW, tH + fH, fW - cS, tH + fH );
            lineTo( 0 + cS, tH + fH );      //bottom left corner
            curveTo( 0, tH + fH, 0, tH + fH - cS );
            lineTo( 0, tH );                //top left corner
            lineTo( xStart, tH );
            lineTo( xStart, 0 )
        }
        g.endFill();
    }//end drawTabShape

    private function activateTab(): void {
        this.buttonMode = true;
        this.mouseChildren = false;
        this.addEventListener( MouseEvent.MOUSE_DOWN, tabBehave );
        this.addEventListener( MouseEvent.MOUSE_OVER, tabBehave );
        this.addEventListener( MouseEvent.MOUSE_OUT, tabBehave );
        this.addEventListener( MouseEvent.MOUSE_UP, tabBehave );
        var localRef: Object = this;

        function tabBehave( evt: MouseEvent ): void {
            if ( evt.type == "mouseDown" ) {
                if ( localRef.polarizationType == "V" ) {
                    localRef.myMiniTabBar.setPolarization( "V" );
                }
                else {
                    localRef.myMiniTabBar.setPolarization( "H" );
                }
            }
            else if ( evt.type == "mouseOver" ) {
                //nothing, for now
            }
            else if ( evt.type == "mouseUp" ) {
                //nothing, for now
            }
            else if ( evt.type == "mouseOut" ) {
                if ( !localRef._selected ) {
                    localRef.tabLabel.setBold( false );
                }
            }
        }//end of tabBehave
    }//end of activateTab


} //end class
} //end package
