/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/6/12
 * Time: 8:25 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.radiatingcharge.view {
import edu.colorado.phet.flashcommon.controls.NiceLabel;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.radiatingcharge.model.FieldModel;
import edu.colorado.phet.radiatingcharge.util.SpriteUIComponent;

import flash.display.Graphics;

import flash.display.Sprite;

public class FieldView extends Sprite{

    private var myMainView: MainView;
    private var myFieldModel: FieldModel;
    private var nbrLines:int;           //nbr of field lines, passed in from the Field model
    private var nbrPhotonsPerLine:int;  //nbr of photons per field line, passed in from the Field model
    private var originX:Number;         //location of origin, in screen coordinates
    private var originY:Number;
    private var container:Sprite;       //container for field lines, so can put PAUSED sign underneath
    private var pausedSign:NiceLabel;   //visible when sim is paused
    private var paused_str:String;

    public function FieldView( myMainView: MainView,  myFieldModel: FieldModel ) {
        this.myMainView = myMainView;
        this.myFieldModel = myFieldModel;
        this.nbrLines = this.myFieldModel.nbrLines;
        this.nbrPhotonsPerLine = this.myFieldModel.nbrPhotonsPerLine;
        this.originX = this.myMainView.stageW/2;     //place origin at center of stage
        this.originY = this.myMainView.stageH/2;
        this.myFieldModel.registerView( this );
        this.initializeStrings();
        this.init();
        this.myFieldModel.updateViews();
    }

    private function initializeStrings():void{
        this.paused_str = FlexSimStrings.get( "paused", "PAUSED" );
    }

    private function init():void{
        this.container = new Sprite();
        this.pausedSign = new NiceLabel( 80, paused_str );
        this.pausedSign.setFontColor( 0xF5BA0A );
        this.pausedSign.x = this.myMainView.stageW/2 - pausedSign.width/2;
        pausedSign.y = 0.1*myMainView.stageH;
        this.addChild( pausedSign );
        this.addChild( container );
        this.pausedSign.visible = false;
    }

    public function update():void{
//        trace("FieldView.update() paused = "+myFieldModel.paused);
//        if( myFieldModel.paused ){
//            pausedSign.visible = false;
//        }else{
//            pausedSign.visible = true;
//        }
        pausedSign.visible = myFieldModel.paused;
        var g:Graphics = this.container.graphics;
        g.clear();
        g.lineStyle( 2, 0x0000ff, 1 );
        var fieldLine_arr:Array =  this.myFieldModel.fieldLine_arr;
        //g.moveTo( originX + fieldLine_arr[i][0].xP, originY - fieldLine_arr[i][0].yP );
        for( var i:int = 0; i < nbrLines; i++){
            g.moveTo( originX + myFieldModel.xC,  originY - myFieldModel.yC );
            //g.moveTo( originX + fieldLine_arr[i][0].xP, originY - fieldLine_arr[i][0].yP );
            for( var j:int = 0; j < nbrPhotonsPerLine; j++ ) {
                if( fieldLine_arr[i][j].emitted ) {
                    g.lineTo( originX + fieldLine_arr[i][j].xP, originY - fieldLine_arr[i][j].yP );
                    //g.drawRect( originX + fieldLine_arr[i][j].xP - 2, originY - fieldLine_arr[i][j].yP - 2, 4, 4 );
                }
            }
        }
    }//end of update()
}//end of class
}//end of package
