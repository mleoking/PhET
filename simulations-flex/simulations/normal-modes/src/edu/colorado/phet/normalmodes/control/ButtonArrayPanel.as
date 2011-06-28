
/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 6/14/11
 * Time: 8:01 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.control {
import edu.colorado.phet.normalmodes.*;
import edu.colorado.phet.normalmodes.model.Model2;
import edu.colorado.phet.normalmodes.util.TwoHeadedArrow;
import edu.colorado.phet.normalmodes.view.MainView;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;
import flash.text.TextFormatAlign;

import mx.core.UIComponent;

import org.aswing.JAccordion;

public class ButtonArrayPanel extends UIComponent{

    private var myMainView: MainView;
    private var myModel2: Model2;
    private var container: Sprite;          //sprite container for array of buttons
    private var maxContainerWidth:Number;   //width of container in pixels
    private var containerHeight:Number;     //height of array in pixels
    private var label_txt: TextField;        //Label for array
    private var arrowGraphic: TwoHeadedArrow;//icon showing polarization of mode
    private var tFormat: TextFormat;         //format for label
    private var modesNxNy_str: String;      //text of Label
    private var button_arr:Array;           //N x N array of pushbuttons
    private var topLeftX;Number;
    private var topLeftY:Number;
    private var nMax:int;

    public function ButtonArrayPanel(  myMainView: MainView, myModel2: Model2) {
        this.myMainView = myMainView;
        this.myModel2 = myModel2;
        this.nMax = this.myModel2.nMax;
        this.maxContainerWidth = 250;
        this.container = new Sprite();
        this.label_txt = new TextField();
        this.arrowGraphic = new TwoHeadedArrow();
        this.arrowGraphic.scaleX = 0.5;
        this.arrowGraphic.scaleY = 0.5;
        this.arrowGraphic.setRegistrationPointAtCenter( true );
        this.tFormat = new TextFormat();
        this.initializeStrings();
        this.createLabel();
        var nbrMasses:int = this.myModel2.N;
        //button_arr is nMax+1 x nMax+1,  i = 0 row and j = 0 column are dummies
        //so that button_arr[i][j] corresponds to mode i, j.  Lowest mode is 1,1. Highest mode is nMax,nMax
        this.button_arr = new Array( nMax + 1 );
        for (var i:int = 0; i < nMax + 1; i++){
            this.button_arr[i] = new Array( nMax + 1 )
        }
        //i, j order of addChild() important so that buttons look OK when pressed
        for(i = nMax; i >= 1; i--){
            for(var j:int = nMax; j >= 1; j--){
                this.button_arr[i][j] = new ModeButton( myModel2, i, j, this.maxContainerWidth/nbrMasses );
                this.container.addChild(this.button_arr[i][j]);    //don't add i = 0 or j = 0, since these are dummies
            }
        }
        this.addChild( this.container );
        this.addChild( this.label_txt );
        this.addChild( this.arrowGraphic );
        this.setNbrButtons( );

    } //end constructor

    public function initializeStrings():void{
        this.modesNxNy_str = "Modes nx, ny";
    }

    private function createLabel():void{
        this.label_txt.text = this.modesNxNy_str;
        this.label_txt.autoSize = TextFieldAutoSize.LEFT;
        this.tFormat.font = "Arial";
        this.tFormat.size = 16;
        this.tFormat.align = TextFormatAlign.LEFT;
        this.label_txt.setTextFormat( this.tFormat );
        //this.label_txt.y = - this.label_txt.height;
    }

    public function setArrowVertical( tOrF:Boolean ):void{
       if( tOrF ){
           this.arrowGraphic.rotation = 90;
       }else{

           this.arrowGraphic.rotation = 0;
       }
    }

    //resets all buttons to zero state
    public function setNbrButtons( ):void{
        for(var i: int = 1; i <= this.nMax; i++ ){
            for( var j: int = 1; j <= this.nMax; j++ ){
                this.button_arr[i][j].visible = false;
            }
        }
        var N:int = this.myModel2.N;
        var size:Number = this.maxContainerWidth/N;
        var xOffset:Number;
        var yOffset:Number;
        for(var i: int = 1; i <= N; i++ ){
            for( var j: int = 1; j <= N; j++ ){
                if( N == 1 || N == 2){
                    size = this.maxContainerWidth/5;
                }else if ( N == 3 || N == 4 ) {
                    size =  this.maxContainerWidth/5;
                } else if( N >= 5 ){
                   size =  this.maxContainerWidth/N;
                }
                xOffset = this.maxContainerWidth/2 - N*size/2;
                yOffset = xOffset;
                this.button_arr[i][j].setSize( size );
                this.button_arr[i][j].visible = true;
                this.button_arr[i][j].drawButton( 0xffffff );
                this.button_arr[i][j].pushedIn = false;
                this.button_arr[i][j].activated = false;
                this.button_arr[i][j].x = xOffset + ( j-1 )*(size + 4);
                this.button_arr[i][j].y = yOffset + ( i-1 )*(size + 4);
            }
        }
        this.label_txt.x = xOffset;
        this.label_txt.y = yOffset - 1.3 * this.label_txt.height;
        this.arrowGraphic.x = label_txt.x + 1*label_txt.width + 0.7*arrowGraphic.width;
        this.arrowGraphic.y = label_txt.y + 1.0*this.arrowGraphic.height;
    }
} //end class
} //end package
