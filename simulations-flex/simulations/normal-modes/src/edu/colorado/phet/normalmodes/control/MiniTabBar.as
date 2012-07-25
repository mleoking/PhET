/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created by IntelliJ IDEA.
 * User: Dubson
 * Date: 3/25/12
 * Time: 11:47 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.control {
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.normalmodes.model.Model2D;

import flash.display.Sprite;

/**
 * MiniTabBar holds 2 miniTabs at top of ButtonArrayPanel.
 * One miniTab for selecting horiz polarization, the other for vert polarization
 */
public class MiniTabBar extends Sprite {
    private var myModel2: Model2D;
    private var tabH: MiniTab;          //tab to select horizontal polarization
    private var tabV: MiniTab;          //tab to select vertical polarization
    private var _tabWidth: Number;
    private var _tabHeight: Number;
    private var _folderWidth: Number;   //tab is at top of folder-shaped graphic of this width
    private var horizontal_str: String;
    private var vertical_str: String;

    public function MiniTabBar( myModel2: Model2D ) {
        this.myModel2 = myModel2;
        this._tabWidth = 125;
        this._tabHeight = 25;
        this._folderWidth = 300;
        this.initializeStrings();
        this.tabH = new MiniTab( this, 1, this._tabWidth, this._tabHeight, this._folderWidth, 0xffff55, horizontal_str );
        this.tabV = new MiniTab( this, 2, this._tabWidth, this._tabHeight, this._folderWidth, 0x44ff44, vertical_str );
        this.init();
    } //end constructor

    private function initializeStrings(): void {
        horizontal_str = FlexSimStrings.get( "horizontal", "Horizontal" );
        vertical_str = FlexSimStrings.get( "vertical", "Vertical" );
    }

    private function init(): void {
        this.addChild( tabV );
        this.addChild( tabH );
    }

    public function initializeMiniTabBarOnButtonArray(): void {
        this.tabV.selected = true;  //start with vertical polarization selected
        this.tabH.selected = false;
        this.setPolarization( "V" );
    }

    public function get tabHeight(): Number {
        return _tabHeight;
    }

    public function setPolarization( polarizationType: String ): void {
        var hTabDepth: int = this.getChildIndex( this.tabH );
        var vTabDepth: int = this.getChildIndex( this.tabV );
        if ( polarizationType == "H" ) {    //if horizontal tab selected, bring that folder to top
            this.myModel2.xModes = true;
            this.tabH.selected = true;
            this.tabV.selected = false;
            if ( vTabDepth > hTabDepth ) {
                this.swapChildren( this.tabH, this.tabV );
            }
        }
        else {                              //if vertical tab selected
            this.myModel2.xModes = false;
            this.tabV.selected = true;
            this.tabH.selected = false;
            if ( hTabDepth > vTabDepth ) {
                this.swapChildren( this.tabH, this.tabV );
            }
        }
    }//end setPolarization();
} //end class
} //end package
