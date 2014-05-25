/**
 * Created by Dubson on 5/25/2014.
 * View and Controller. "Drawer" from which user can grab light sources and optical components.
 */
package edu.colorado.phet.opticslab.control {
import edu.colorado.phet.opticslab.view.*;
import edu.colorado.phet.opticslab.model.OpticsModel;

import flash.display.Graphics;

import flash.display.Sprite;

public class ComponentDrawer extends Sprite {
    private var myMainView: MainView;
    private var myOpticsModel: OpticsModel;
    private var sourceCompartment: Sprite; //compartment containing light sources
    private var maskCompartment: Sprite;   //compartment containing aperature masks
    private var lensCompartment: Sprite;   //contains lenses
    private var mirrorCompartment: Sprite; //contains mirrors
    private var stageW: Number;     //width of main stage, read from MainView
    private var stageH: Number;     //height of main stage, read from MainView

    public function ComponentDrawer( mainView: MainView, opticsModel: OpticsModel ) {
        myMainView = mainView;
        myOpticsModel = opticsModel;
        this.stageW = myMainView.stageW;
        this.stageH = myMainView.stageH;
        //no need to register with Model since this is a controller.
        init();
    }//end constructor

    private function init():void{

    }//end init()

    //Draw box representing drawer full of components
    private function drawGraphics():void{
        var g:Graphics = this.graphics;
        var lineColor = 0xff0000;
        var fillColor = 0x00ff00;
        //draw outer box
        with( g ){
            clear();
            lineStyle( 3, lineColor );
            beginFill( fillColor, 1 );
            moveTo( 0, 0 );
            endFill();
        }
    }//end drawGraphics()

}//end class
}//end package
