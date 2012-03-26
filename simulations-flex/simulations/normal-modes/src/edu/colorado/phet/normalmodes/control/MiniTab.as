/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 3/25/12
 * Time: 10:01 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.control {
import edu.colorado.phet.normalmodes.NiceComponents.NiceLabel;
import edu.colorado.phet.normalmodes.control.MiniTabBar;
import edu.colorado.phet.normalmodes.util.TwoHeadedArrow;

import flash.display.Graphics;

import flash.display.Sprite;
import flash.events.MouseEvent;

import mx.containers.Canvas;

//2 miniTabs used for control of ButtonArrayPanel
public class MiniTab extends Sprite{
    private var myMiniTabBar: MiniTabBar;
    private var polarizationType:String;  //"H" for horiz polarization or "V" for vert polarization
    private var tabWidth:Number;   //width of tab in pixels
    private var tabHeight:Number;   //height of tab
    private var tabColor: uint;
    private var _selected:Boolean;
    private var tabLabel:NiceLabel;
    private var tabIcon:TwoHeadedArrow;

    public function MiniTab( myMiniTabBar:MiniTabBar, tabWidth:Number, tabHeight:Number, tabColor:uint, labelText:String ) {
        this.myMiniTabBar = myMiniTabBar;
        this.tabWidth = tabWidth;
        this.tabHeight = tabHeight;
        this.tabColor = tabColor;
        this.tabLabel = new NiceLabel( 12, " " + labelText );  //NiceLabel(fontSize, text)
        if( labelText = "Horizontal" ){
            this.polarizationType = "H";
        }else{
            this.polarizationType = "V";
        }
        this.tabIcon = new TwoHeadedArrow();
        this.drawTabShape();
        this.activateTab();
        this.addChild( tabLabel );
        this.setIcon();
        this.addChild( tabIcon );

    } //end constructor

    public function setIcon():void{
        this.tabIcon.width = 20;
        this.tabIcon.height = 10;
        this.tabIcon.x = this.tabLabel.width + 6;
        this.tabIcon.y = 0.5*this.tabLabel.height;
    } //end setIcon()

    private function drawTabShape(){
        var w:Number = this.tabWidth;
        var h:Number = this.tabHeight;
        var g:Graphics = this.graphics;
        g.clear();
        g.lineStyle( 1.5, 0x0000ff, 1 );
        g.beginFill( this.tabColor );
        g.moveTo( 0, 0 );
        g.lineTo( w,  0 );
        g.lineTo( w + 0.5*h, h );
        g.lineTo( 0, h );
        g.lineTo( 0, 0 );
        g.endFill();
    }//end drawTabShape

    private function activateTab(): void {
        //trace("this.buttonBody = " , this.buttonBody);
        this.buttonMode = true;
        this.mouseChildren = false;
        this.addEventListener( MouseEvent.MOUSE_DOWN, tabBehave );
        this.addEventListener( MouseEvent.MOUSE_OVER, tabBehave );
        this.addEventListener( MouseEvent.MOUSE_OUT, tabBehave );
        this.addEventListener( MouseEvent.MOUSE_UP, tabBehave );
        var localRef: Object = this;

        function tabBehave( evt: MouseEvent ): void {
            if ( evt.type == "mouseDown" ) {
                //localRef.myMiniTabBar.setPolarization( "V" );
                if( localRef.polarizationType == "V" ){
                   localRef.myMiniTabBar.setPolarization( "V" );
                }else{
                   localRef.myMiniTabBar.setPolarization( "H" );
                }
                //trace("evt.name:"+evt.type);
            } else if ( evt.type == "mouseOver" ) {
                localRef.tabLabel.setBold(true);
                //trace("evt.name:"+evt.type);
            } else if ( evt.type == "mouseUp" ) {
                //trace("evt.name:"+evt.type);

            } else if ( evt.type == "mouseOut" ) {
                localRef.tabLabel.setBold(false);
                //trace("evt.name:"+evt.type);
            }
        }//end of tabBehave
    }//end of activateTab

} //end class
} //end package
