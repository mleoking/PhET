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
    public function MiniTabBar( myModel2:Model2 ) {
        this.myModel2 = myModel2;
        this.tabWidth = 100;
        this.tabH = new MiniTab( this, this.tabWidth,  20, 0xffff00, "Horizontal" ) ;
        this.tabV = new MiniTab( this, this.tabWidth,  20, 0x00ff00, "Vertical" ) ;
        this.init();
    } //end constructor

    private function init():void{
        this.addChild( tabV );
        this.addChild( tabH );
        this.tabV.x = 0.95*this.tabH.width;
    }

    public function setPolarization( polarizationType:String ): void {
        trace( "MiniTabBar.setPolarization() called. ");
        if ( polarizationType == "H" ) {
            this.myModel2.xModes = true;
            trace( "MiniTabBar.setPolarization called. polarizationType is "+ polarizationType);
        }
        else {
            this.myModel2.xModes =  false;
            trace( "MiniTabBar.setPolarization called. polarizationType is "+ polarizationType);
        }
    }//end setPolarization();
} //end class
} //end package
