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
    private var tabWidth:Number;
    private var _tabHeight:Number;
    public function MiniTabBar( myModel2:Model2 ) {
        this.myModel2 = myModel2;
        this.tabWidth = 100;
        this._tabHeight = 25;
        trace("MiniTabBar Constructor creating Horizontal tab.");
        this.tabH = new MiniTab( this, 1, this.tabWidth,  this._tabHeight, 0xffff00, "Horizontal" ) ;
        trace("MiniTabBar Constructor creating Vertical tab.");
        this.tabV = new MiniTab( this, 2, this.tabWidth,  this._tabHeight, 0x44ff44, "Vertical" ) ;
        this.init();
    } //end constructor

    private function init():void{
        this.addChild( tabV );
        this.addChild( tabH );
        //this.tabV.x = 0.95*this.tabH.width;
    }

    public function get tabHeight():Number{
        return _tabHeight;
    }

    public function setPolarization( polarizationType:String ): void {
        trace( "MiniTabBar.setPolarization() called. ");
        var hTabDepth:int = this.getChildIndex( this.tabH );
        var vTabDepth:int = this.getChildIndex( this.tabV );
        if ( polarizationType == "H" ) {    //if horizontalTab selected
            this.myModel2.xModes = true;
            if( vTabDepth > hTabDepth ){
                this.swapChildren( this.tabH,  this.tabV );
            }
            
            //trace( "MiniTabBar.setPolarization called. polarizationType is "+ polarizationType);
        }
        else {   // if verticalTab selected
            this.myModel2.xModes =  false;
            if( hTabDepth > vTabDepth ){
                this.swapChildren( this.tabH,  this.tabV );
            }

            //trace( "MiniTabBar.setPolarization called. polarizationType is "+ polarizationType);
        }
    }//end setPolarization();
} //end class
} //end package
