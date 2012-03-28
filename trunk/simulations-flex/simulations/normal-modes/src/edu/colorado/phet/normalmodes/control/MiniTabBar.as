// Copyright 2002-2012, University of Colorado

/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 3/25/12
 * Time: 11:47 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.control {
import edu.colorado.phet.normalmodes.model.Model2;

import flash.display.Sprite;

public class MiniTabBar extends Sprite{
    private var myModel2:Model2;
    private var tabH: MiniTab;
    private var tabV: MiniTab;
    private var _tabWidth:Number;
    private var _tabHeight:Number;
    private var _folderWidth:Number;
    public function MiniTabBar( myModel2:Model2 ) {
        this.myModel2 = myModel2;
        this._tabWidth = 125;
        this._tabHeight = 25;
        this._folderWidth = 300;
        //trace("MiniTabBar Constructor creating Horizontal tab.");
        this.tabH = new MiniTab( this, 1, this._tabWidth,  this._tabHeight, this._folderWidth, 0xffff55, "Horizontal" ) ;
        //trace("MiniTabBar Constructor creating Vertical tab.");
        this.tabV = new MiniTab( this, 2, this._tabWidth,  this._tabHeight, this._folderWidth, 0x44ff44, "Vertical" ) ;
        this.init();
    } //end constructor

    private function init():void{
        this.addChild( tabV );
        this.addChild( tabH );
        //this.tabV.selected = true;  //start with vertical polarization
        //this.tabV.x = 0.95*this.tabH.width;
    }

    public function initializeMiniTabBarOnButtonArray():void{
        this.tabV.selected = true;  //start with vertical polarization
        this.tabH.selected = false;
        this.setPolarization( "V" );
    }

    public function get tabHeight():Number{
        return _tabHeight;
    }

    public function get tabWidth():Number{
        return _tabWidth;
    }

    public function get folderWidth():Number{
        return _folderWidth;
    }

    public function setPolarization( polarizationType:String ): void {
        //trace( "MiniTabBar.setPolarization() called. ");
        var hTabDepth:int = this.getChildIndex( this.tabH );
        var vTabDepth:int = this.getChildIndex( this.tabV );
        if ( polarizationType == "H" ) {    //if horizontalTab selected
            this.myModel2.xModes = true;
            this.tabH.selected = true;
            this.tabV.selected = false;
            if( vTabDepth > hTabDepth ){
                this.swapChildren( this.tabH,  this.tabV );
            }
            
            //trace( "MiniTabBar.setPolarization called. polarizationType is "+ polarizationType);
        }
        else {   // if verticalTab selected
            this.myModel2.xModes =  false;
            this.tabV.selected = true;
            this.tabH.selected = false;
            if( hTabDepth > vTabDepth ){
                this.swapChildren( this.tabH,  this.tabV );
            }

            //trace( "MiniTabBar.setPolarization called. polarizationType is "+ polarizationType);
        }
    }//end setPolarization();
} //end class
} //end package
