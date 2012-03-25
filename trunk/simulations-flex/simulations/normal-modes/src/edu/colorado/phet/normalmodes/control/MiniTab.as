/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 3/25/12
 * Time: 10:01 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.control {
import edu.colorado.phet.normalmodes.NiceComponents.NiceLabel;
import edu.colorado.phet.normalmodes.util.TwoHeadedArrow;

import flash.display.Graphics;

import flash.display.Sprite;

import mx.containers.Canvas;

//2 miniTabs used for control of ButtonArrayPanel
public class MiniTab extends Sprite{
    private var tabWidth:Number;   //width of tab in pixels
    private var tabHeight:Number;   //height of tab
    private var tabColor: uint;
    private var tabLabel:NiceLabel;
    private var tabIcon:TwoHeadedArrow;

    public function MiniTab( tabWidth:Number, tabHeight:Number, tabColor:uint, labelText:String ) {
        this.tabWidth = tabWidth;
        this.tabHeight = tabHeight;
        this.tabColor = tabColor;
        this.tabLabel = new NiceLabel( 12, " " + labelText );  //NiceLabel(fontSize, text)
        this.tabIcon = new TwoHeadedArrow();
        this.drawTabShape();
        this.addChild( tabLabel );
        this.setIcon();
        this.addChild( tabIcon );

    } //end constructor

    public function setIcon():void{
        this.tabIcon.width = 20;
        this.tabIcon.height = 10;
        this.tabIcon.x = this.tabLabel.width + 3;
        this.tabIcon.y = 0.5*this.tabLabel.height;
    } //end setIcon()

    private function drawTabShape(){
        var w:Number = this.tabWidth;
        var h:Number = this.tabHeight;
        var g:Graphics = this.graphics;
        g.clear();
        g.lineStyle( 2, 0x000000, 1 );
        g.beginFill( this.tabColor );
        g.moveTo( 0, 0 );
        g.lineTo( w,  0 );
        g.lineTo( w + 0.5*h, h );
        g.lineTo( 0, h );
        g.lineTo( 0, 0 );
        g.endFill();
    }//end drawTabShape
} //end class
} //end package
