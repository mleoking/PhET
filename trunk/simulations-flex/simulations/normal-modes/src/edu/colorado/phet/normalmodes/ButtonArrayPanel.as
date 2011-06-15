/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 6/14/11
 * Time: 8:01 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes {
import flash.display.Sprite;

import mx.core.UIComponent;

public class ButtonArrayPanel extends UIComponent{

    private var myMainView: MainView;
    private var myModel2: Model2;
    private var container: Sprite;          //sprite container for array of buttons
    private var containerWidth:Number;      //width of container in pixels
    private var containerHeight:Number;     //height of array in pixels
    private var button_arr:Array;           //N x N array of pushbuttons
    private var topLeftX;Number;
    private var topLeftY:Number;
    private var nMax:int;

    public function ButtonArrayPanel(  myMainView: MainView, myModel2: Model2) {
        this.myMainView = myMainView;
        this.myModel2 = myModel2;
        this.nMax = this.myModel2.nMax;
        this.containerWidth = 100;
        this.container = new Sprite();
        var nbrMasses:int = this.myModel2.N;
        //button_arr is nMax+1 x nMax+1,  i = 0 row and j = 0 column are dummies
        //so that button_arr[i][j] corresponds to mode i, j.  Lowest mode is 1,1. Highest mode is nMax,nMax
        this.button_arr = new Array( nMax + 1 );
        for (var i:int = 0; i < nMax + 1; i++){
            this.button_arr[i] = new Array( nMax + 1 )
        }
        for(i = 1; i <= nMax; i++){
            for(var j:int = 1; j <= nMax; j++){
                this.button_arr[i][j] = new ModeButton( myModel2, i, j, this.containerWidth/nbrMasses );
                this.container.addChild(this.button_arr[i][j]);    //don't add i = 0 or j = 0, since these are dummies

            }
        }
        this.addChild( this.container );
        this.setNbrButtons();

    } //end constructor

    public function setNbrButtons( N:int ):void{

    }
} //end class
} //end package
