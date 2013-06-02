/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/6/12
 * Time: 8:25 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.triglab.view {
import edu.colorado.phet.flashcommon.controls.NiceLabel;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.triglab.model.TrigModel;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;

import flash.display.Graphics;

import flash.display.Sprite;

public class GraphView extends Sprite{

    private var myMainView: MainView;
    private var myTrigModel: TrigModel;
//    private var nbrLines:int;           //nbr of field lines, passed in from the Field model
//    private var nbrPhotonsPerLine:int;  //nbr of photons per field line, passed in from the Field model
//    private var originX:Number;         //location of origin, in screen coordinates
//    private var originY:Number;
//    private var container:Sprite;       //container for field lines, so can put PAUSED sign underneath
//    private var pausedSign:NiceLabel;   //visible when sim is paused
//    private var paused_str:String;

    public function GraphView( myMainView: MainView,  myTrigModel: TrigModel ) {
        this.myMainView = myMainView;
        this.myTrigModel = myTrigModel;
//        this.nbrLines = this.myTrigModel.nbrLines;
//        this.nbrPhotonsPerLine = this.myTrigModel.nbrPhotonsPerLine;
//        this.originX = this.myMainView.stageW/2;     //place origin at center of stage
//        this.originY = this.myMainView.stageH/2;
        this.myTrigModel.registerView( this );
        this.initializeStrings();
        this.init();
        this.myTrigModel.updateViews();
    }

    private function initializeStrings():void{
        //this.paused_str = FlexSimStrings.get( "paused", "PAUSED" );
    }

    private function init():void{
//        this.container = new Sprite();
//        this.pausedSign = new NiceLabel( 40, paused_str );
//        this.pausedSign.setFontColor( 0xF5BA0A );
//        this.pausedSign.x = 0.01*this.myMainView.stageW; //this.myMainView.stageW/2 - pausedSign.width/2;
//        pausedSign.y = 0.01*myMainView.stageH;
//        this.addChild( pausedSign );
//        this.addChild( container );
//        this.pausedSign.visible = false;
    }

    public function update():void{

//        pausedSign.visible = myTrigModel.paused;
//        var g:Graphics = this.container.graphics;
//        g.clear();
//        g.lineStyle( 2, 0x0ffffff, 1 );
//        var fieldLine_arr:Array =  this.myTrigModel.fieldLine_arr;
//        //g.moveTo( originX + fieldLine_arr[i][0].xP, originY - fieldLine_arr[i][0].yP );
//        for( var i:int = 0; i < nbrLines; i++){
//            g.moveTo( originX + myTrigModel.xC,  originY - myTrigModel.yC );
//            //g.moveTo( originX + fieldLine_arr[i][0].xP, originY - fieldLine_arr[i][0].yP );
//            for( var j:int = 0; j < nbrPhotonsPerLine; j++ ) {
//                if( fieldLine_arr[i][j].emitted ) {     //Plot only if photon has been emitted by current motion choice
//
//                    g.lineTo( originX + fieldLine_arr[i][j].xP, originY - fieldLine_arr[i][j].yP );
//                    //g.drawRect( originX + fieldLine_arr[i][j].xP - 2, originY - fieldLine_arr[i][j].yP - 2, 4, 4 );
//                }
//            }
//        }
    }//end of update()
}//end of class
}//end of package
