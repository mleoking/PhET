
/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 6/14/11
 * Time: 8:01 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.control {
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.util.SpriteUIComponent;
import edu.colorado.phet.normalmodes.*;
import edu.colorado.phet.flashcommon.controls.NiceLabel;
import edu.colorado.phet.normalmodes.model.Model2;
import edu.colorado.phet.normalmodes.util.TwoHeadedArrow;
import edu.colorado.phet.normalmodes.view.MainView;

import flash.display.Graphics;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;
import flash.text.TextFormatAlign;

import mx.containers.Canvas;

import mx.core.UIComponent;

import org.aswing.JAccordion;

/**
 *View of a square array of buttons which control and display the spectrum of normal modes in 2D
 *There are two modes: vertical and horizontal, selectable with miniTabs.
 */

public class ButtonArrayPanel extends Canvas{

    private var myMainView: MainView;
    private var myModel2: Model2;
    private var container: Sprite;          //sprite container for array of buttons
    private var miniTabBar: MiniTabBar;     //bar with two tabs at top of array of buttons, for selecting between vert and horiz polarization
    private var maxContainerWidth:Number;   //max width of container in pixels
    private var padding:Number;             //vert and horiz gap between buttons in pixels
    //private var containerHeight:Number;     //height of array in pixels
    private var topLabel_txt: NiceLabel;           //Mode Spectrum label, shown above array
    private var bottomLabel_txt: NiceLabel;        //Mode Numbers label, shown below array
    private var tFormat: TextFormat;         //format for labels
    private var modeSpectrumDisplay_str: String; //text of top label
    private var modesNxNy_str: String;      //text of bottom label
    private var button_arr:Array;           //N x N array of push buttons, ModeButton objects
    //private var topLeftX:Number;
    //private var topLeftY:Number;
    private var nMax:int;                   //maximum number N in NxN array of buttons

    public function ButtonArrayPanel(  myMainView: MainView, myModel2: Model2) {
        percentWidth = 100;
        percentHeight = 100;
        this.myMainView = myMainView;
        this.myModel2 = myModel2;
        this.myModel2.registerView( this );
        this.nMax = this.myModel2.nMax;     //in 2D, the max number of mobile masses is nMax*nMax
        this.maxContainerWidth = 300;
        this.padding = 4;
        this.container = new Sprite();
        this.miniTabBar = new MiniTabBar( this.myModel2 );
        this.tFormat = new TextFormat();
        this.initializeStrings();
        this.topLabel_txt = new NiceLabel( 15, this.modeSpectrumDisplay_str );
        this.bottomLabel_txt = new NiceLabel( 15, this.modesNxNy_str );
        this.formatLabels();
        var nbrMasses:int = this.myModel2.N;
        //Button_arr is nMax+1 X nMax+1,  i = 0 row and j = 0 column are dummies,
        //so that button_arr[i][j] corresponds to mode i, j.  Lowest mode is 1,1. Highest mode is nMax,nMax
        this.button_arr = new Array( nMax + 1 );
        for (var i:int = 0; i < nMax + 1; i++){
            this.button_arr[i] = new Array( nMax + 1 )
        }
        //i, j order of addChild() important so that buttons look OK when pressed
        var buttonWidth:Number = ((this.maxContainerWidth - this.padding)/nbrMasses) - this.padding;
        for(i = nMax; i >= 1; i--){
            for(var j:int = nMax; j >= 1; j--){
                //nbrButtonsInRow*(buttonWidth + padding) + padding = maxContainerWidth  (need one extra padding on end of row)
                //buttonWidth = [(MaxContainerWidth - padding)/nbrButtonsInRow] - padding
                this.button_arr[i][j] = new ModeButton( myModel2, i, j, buttonWidth );
                this.container.addChild(this.button_arr[i][j]);    //don't add i = 0 or j = 0, since these are dummies
            }
        }
        this.addChild( new SpriteUIComponent( this.topLabel_txt ) );
        this.addChild( new SpriteUIComponent( this.miniTabBar ) );
        this.addChild( new SpriteUIComponent( this.container) );
        this.addChild( new SpriteUIComponent( this.bottomLabel_txt ) );
        this.positionChildren();
        this.setNbrButtons( );
    } //end constructor

    public function initializeButtonArray():void{
        this.miniTabBar.initializeMiniTabBarOnButtonArray();
    }

    public function initializeStrings():void{
        this.modeSpectrumDisplay_str = FlexSimStrings.get("modeSpectrumDisplay", "Mode Spectrum Display");
        this.modesNxNy_str = FlexSimStrings.get("modeNumbersXY", "Mode Numbers x, y");
    }

    private function formatLabels():void{
        this.tFormat.font = "Arial";
        this.tFormat.size = 16;
        this.tFormat.align = TextFormatAlign.LEFT;
        this.topLabel_txt.setTextFormat( this.tFormat );
        this.bottomLabel_txt.setTextFormat( this.tFormat );
    }

    private function positionChildren():void{
        this.topLabel_txt.y = 0;
        this.miniTabBar.y = 1.2*this.topLabel_txt.height;
    }

    /*Sets number of buttons = number of modes, sizes buttons, and resets all buttons to zero state*/
    public function setNbrButtons( ):void{
        var ySpacer:int = this.miniTabBar.y + this.miniTabBar.tabHeight;
        for(var i: int = 1; i <= this.nMax; i++ ){
            for( var j: int = 1; j <= this.nMax; j++ ){
                this.button_arr[i][j].visible = false;
            }
        }
        var N:int = this.myModel2.N;
        var size:Number = (this.maxContainerWidth - this.padding)/N - this.padding;
        var xOffset:Number;
        var yOffset:Number;

        for( i = 1; i <= N; i++ ){
            for( j = 1; j <= N; j++ ){
                //if..else to keep buttons and bottom label well-placed in folder regardless of number of buttons
                if( N == 1 || N == 2){
                    size = this.maxContainerWidth/4;
                    yOffset = this.padding + 0.5*this.maxContainerWidth/2 - (1*(size+this.padding)+this.padding)/2;
                }else if ( N == 3 ) {
                    size =  this.maxContainerWidth/5;
                    yOffset = this.padding + 0.8*this.maxContainerWidth/2 - (N*(size+this.padding)+this.padding)/2;
                } else if ( N == 4 ){
                    size =  this.maxContainerWidth/5;
                    yOffset = this.padding + this.maxContainerWidth/2 - (N*(size+this.padding)+this.padding)/2;
                } else if( N >= 5 ){
                    yOffset = this.padding + this.maxContainerWidth/2 - (N*(size+this.padding)+this.padding)/2;
                    //do nothing; size =  (this.maxContainerWidth /N);
                }
                xOffset = this.padding + this.maxContainerWidth/2 - (N*(size+this.padding)+this.padding)/2;
                this.button_arr[i][j].setSize( size );
                this.button_arr[i][j].visible = true;
                this.button_arr[i][j].changeBackgroundHeight( 0 );
                this.button_arr[i][j].pushedIn = false;
                this.button_arr[i][j].activatedH = false;
                this.button_arr[i][j].activatedV = false;
                this.button_arr[i][j].x = xOffset + ( j-1 )*(size + this.padding);
                this.button_arr[i][j].y = ySpacer +  yOffset +( i-1 )*(size + this.padding);   //
            }
        }
        //var borderWidth:Number = 5;
        this.bottomLabel_txt.x = this.maxContainerWidth/2 - bottomLabel_txt.width/2;  //xOffset;
        if( N == 4 ){
            yOffset -= 10; //Kludge to prevent bottomLabel from colliding with bottom edge of folder
        }
        this.bottomLabel_txt.y = ySpacer + yOffset + N*(size+this.padding)+this.padding;
    }//end setNbrButtons()

    /*Set color of buttons and background to indicate vert or horiz mode and set extent of colored region on button to indicate amplitude*/
    private function setButtonColors():void{
        //trace("ButtonArrayPanel.setButtonColors called.");
        var N:int = this.myModel2.N;
        var polarizationX:Boolean;    //true if polarization is horizontal
        if(this.myModel2.xModes){
            polarizationX = true;
        }else{
            polarizationX = false;
        }
        var springLength:Number = 1/(N + 1);
        var largeAmplitude:Number = 0.3*springLength;
        for(var i: int = 1; i <= N; i++ ){
            for( var j: int = 1; j <= N; j++ ){
                var amplitude:Number;  //amplitude of mode (i,j)
                var borderColor:uint;  //color of button trim, depends on polarization shown
                if(polarizationX){
                    amplitude = this.myModel2.getModeAmpliX( i, j );
                    borderColor = 0xff0000;
                }else{
                    amplitude = this.myModel2.getModeAmpliY( i, j );
                    borderColor = 0x0000ff;
                }
                var colorSize:int = Math.round( 16 * Math.min( 1, amplitude/largeAmplitude ));
                this.button_arr[i][j].changeBackgroundHeight( colorSize );
                this.button_arr[i][j].borderColor = borderColor;
            }
        }
    }//end setButtonColors();

    public function update():void{
        if( this.myModel2.nChanged || this.myModel2.modesZeroed ){
            this.setNbrButtons();
            this.setButtonColors();
            this.myModel2.modesZeroed = false;
        }
        if( this.myModel2.modesChanged ){
            this.setButtonColors();
            this.myModel2.modesChanged = false;
        }

    }//end update()

} //end class
} //end package
