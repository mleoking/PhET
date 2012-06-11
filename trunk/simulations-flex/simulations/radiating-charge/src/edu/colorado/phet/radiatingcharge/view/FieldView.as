/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/6/12
 * Time: 8:25 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.radiatingcharge.view {
import edu.colorado.phet.radiatingcharge.model.FieldModel;

import flash.display.Graphics;

import flash.display.Sprite;

public class FieldView extends Sprite{

    private var myMainView: MainView;
    private var myFieldModel: FieldModel;
    private var nbrLines:int;           //nbr of field lines, passed in from the Field model
    private var nbrPhotonsPerLine:int;  //nbr of photons per field line, passed in from the Field model
    private var originX:Number;         //location of origin, in screen coordinates
    private var originY:Number;

    public function FieldView( myMainView: MainView,  myFieldModel: FieldModel ) {
        this.myMainView = myMainView;
        this.myFieldModel = myFieldModel;
        this.nbrLines = this.myFieldModel.nbrLines;
        this.nbrPhotonsPerLine = this.myFieldModel.nbrPhotonsPerLine;
        this.originX = this.myMainView.stageW/2;     //place origin at center of stage
        this.originY = this.myMainView.stageH/2;
        this.myFieldModel.registerView( this );
        this.myFieldModel.updateViews();
    }

    public function update():void{
        var g:Graphics = this.graphics;
        g.clear();
        g.lineStyle( 1, 0x000000, 1 );
        var fieldLine_arr:Array =  this.myFieldModel.fieldLine_arr;
        for( var i:int = 0; i < nbrLines; i++){
            //g.moveTo( fieldLine_arr[i][0].xP, fieldLine_arr[i][0].yP );
            //g.moveTo( originX + myFieldModel.xC,  originY - myFieldModel.yC );
            for( var j:int = 0; j < nbrPhotonsPerLine; j++ ) {
                //g.lineTo( originX + fieldLine_arr[i][j].xP, originY - fieldLine_arr[i][j].yP );
                g.drawRect( originX + fieldLine_arr[i][j].xP - 2, originY - fieldLine_arr[i][j].yP - 2, 4, 4 );
            }
        }
    }//end of update()
}//end of class
}//end of package
