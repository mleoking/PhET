
/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 6/14/11
 * Time: 8:01 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.control {
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.normalmodes.*;
import edu.colorado.phet.normalmodes.NiceComponents.NiceLabel;
import edu.colorado.phet.normalmodes.model.Model2;
import edu.colorado.phet.normalmodes.util.SpriteUIComponent;
import edu.colorado.phet.normalmodes.util.SpriteUIComponent;
import edu.colorado.phet.normalmodes.util.TwoHeadedArrow;
import edu.colorado.phet.normalmodes.view.MainView;

import flash.display.Sprite;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;
import flash.text.TextFormatAlign;

import mx.containers.Canvas;

import mx.core.UIComponent;

import org.aswing.JAccordion;

//array of button which control and display spectrum of modes
//two display modes: vertitcal and horizontal
public class ButtonArrayPanel extends Canvas{

    private var myMainView: MainView;
    private var myModel2: Model2;
    private var container: Sprite;          //sprite container for array of buttons
    private var myPolarizationPanel: PolarizationPanel;
    private var maxContainerWidth:Number;   //width of container in pixels
    private var containerHeight:Number;     //height of array in pixels
    //private var color_arr:Array;            //array of possible button colors
    private var label_txt: NiceLabel;        //Label for array
    private var arrowGraphic: TwoHeadedArrow;//icon showing polarization of mode
    //private var verticalPolarization:Boolean;
    private var tFormat: TextFormat;         //format for label
    private var modesNxNy_str: String;      //text of Label
    private var button_arr:Array;           //N x N array of pushbuttons
    private var topLeftX;Number;
    private var topLeftY:Number;
    private var nMax:int;

    public function ButtonArrayPanel(  myMainView: MainView, myModel2: Model2) {
        percentWidth = 100;
        percentHeight = 100;
        this.myMainView = myMainView;
        this.myModel2 = myModel2;
        this.myModel2.registerView( this );
        this.nMax = this.myModel2.nMax;     //in 2D, number of mobile masses is nMax*nMax
        this.maxContainerWidth = 250;
        //this.color_arr = new Array();
        //this.makeColorArray();
        //this.verticalPolarization = true;
        this.container = new Sprite();
        this.myPolarizationPanel = new PolarizationPanel( myMainView, myModel2 );

        this.arrowGraphic = new TwoHeadedArrow();
        this.arrowGraphic.scaleX = 0.5;
        this.arrowGraphic.scaleY = 0.5;
        this.arrowGraphic.setRegistrationPointAtCenter( true );
        this.arrowGraphic.rotation = 90;
        this.tFormat = new TextFormat();
        this.initializeStrings();
        this.label_txt = new NiceLabel( 15, this.modesNxNy_str );
        this.formatLabel();
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
        this.addChild( new SpriteUIComponent(this.label_txt) );
        //this.addChild( this.myPolarizationPanel );
        this.addChild( new SpriteUIComponent( this.container) );
        this.addChild( this.myPolarizationPanel );
        //this.myPolarizationPanel.setStyle( "horizontalAlign", "right");
        //this.myPolarizationPanel.setStyle( "right" , 20 );
        this.myPolarizationPanel.x = 80;
        this.myPolarizationPanel.y = 0;
        //this.addChild( new SpriteUIComponent( this.arrowGraphic ) );
        this.setNbrButtons( );

    } //end constructor

    public function initializeStrings():void{
        this.modesNxNy_str = FlexSimStrings.get("modeNumbersXY", "Mode Numbers x, y");
    }

    private function formatLabel():void{
        //this.label_txt.text = this.modesNxNy_str;
       // this.label_txt.autoSize = TextFieldAutoSize.LEFT;
        this.tFormat.font = "Arial";
        this.tFormat.size = 16;
        this.tFormat.align = TextFormatAlign.LEFT;
        this.label_txt.setTextFormat( this.tFormat );
        //this.label_txt.y = - this.label_txt.height;
    }

    /*
    public function showVerticalPolarization( tOrF:Boolean ):void{
       if( tOrF ){
           this.arrowGraphic.rotation = 90;
           this.arrowGraphic.x = label_txt.x + 1*label_txt.width + 6;
           this.verticalPolarization = true;
           this.setButtonColors();
       }else{
           this.arrowGraphic.rotation = 0;
           this.arrowGraphic.x = label_txt.x + 1*label_txt.width + 13;
           this.verticalPolarization = false;
           this.setButtonColors();
       }
    } */

    //resets all buttons to zero state
    public function setNbrButtons( ):void{
        var ySpacer:int = 55;
        for(var i: int = 1; i <= this.nMax; i++ ){
            for( var j: int = 1; j <= this.nMax; j++ ){
                this.button_arr[i][j].visible = false;
            }
        }
        var N:int = this.myModel2.N;
        //trace("ButtonArrayPanel.setNbrButtons called. N = " + N);
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
                yOffset = 0; // xOffset;
                this.button_arr[i][j].setSize( size );
                this.button_arr[i][j].visible = true;
                //this.button_arr[i][j].changeColor( 0xffffff );
                this.button_arr[i][j].changeBackgroundHeight( 0 );
                this.button_arr[i][j].pushedIn = false;
                this.button_arr[i][j].activatedH = false;
                this.button_arr[i][j].activatedV = false;
                this.button_arr[i][j].x = xOffset + ( j-1 )*(size + 4);
                this.button_arr[i][j].y = ySpacer + yOffset + ( i-1 )*(size + 4);
            }
        }
        this.label_txt.x = 0;//0.5*container.width - label_txt.width/2;  //xOffset;
        this.label_txt.y = ySpacer - 1.1*label_txt.height; //yOffset - 1.3 * label_txt.height;
        this.myPolarizationPanel.x = label_txt.width + 15;

        //trace( "ButtonArrayPanel.myPolarizationPanel.height = " + myPolarizationPanel.height );
        //this.myPolarizationPanel.y = 0;//- myPolarizationPanel.height;
        /*
        if(this.verticalPolarization){
           this.arrowGraphic.x = label_txt.x + 1*label_txt.width + 3;
        }else{
           this.arrowGraphic.x = label_txt.x + 1*label_txt.width + 10;
        }
        this.arrowGraphic.y = label_txt.y + 0.5*label_txt.height;
        */
    }//end setNbrButtons()

    //color buttons to indicate amplitude of mode
    public function setButtonColors():void{
        var N:int = this.myModel2.N;
        var polarizationX:Boolean;    //true if polarization is horizontal
        if(this.myModel2.xModes){
            polarizationX = true;
        }else{
            polarizationX = false;
        }
        var springLength:Number = 1/(N + 1);
        var largeAmplitude = 0.3*springLength;
        for(var i: int = 1; i <= N; i++ ){
            for( var j: int = 1; j <= N; j++ ){
                //var Xamplitude = this.myModel2.getModeAmpliX( i, j );
                //var Yamplitude = this.myModel2.getModeAmpliY( i, j );
                var amplitude:Number;  //amplitude of mode (i,j)
                if(polarizationX){
                    amplitude = this.myModel2.getModeAmpliX( i, j );
                }else{
                    amplitude = this.myModel2.getModeAmpliY( i, j );
                }
                //var colorX:int = Math.round( 16 * Math.min( 1, Xamplitude/largeAmplitude ));
                //var colorY:int = Math.round( 16 * Math.min( 1, Yamplitude/largeAmplitude ));
                var colorSize:int = Math.round( 16 * Math.min( 1, amplitude/largeAmplitude ));
                //trace("ButtonArrayPanel.setButtonColors() called. i ="+i+"  j="+j+"  colorX="+colorX+"  colorY="+colorY);
                this.button_arr[i][j].changeBackgroundHeight( colorSize );
                //trace("amplitude=" + amplitude + "  polarizationX="+polarizationX + "   i=" + i + "  j=" + j + "   colorSize=" + colorSize);
                /*if( this.myModel2.xModes ){
                    //this.button_arr[i][j].setLabel( colorX.toString());
                    this.button_arr[i][j].changeBackgroundHeight( colorX );
                    //this.button_arr[i][j].changeColor( this.color_arr[ colorX ]);
                }else if( !this.myModel2.xModes ) {
                    //this.button_arr[i][j].setLabel( colorY.toString());
                    this.button_arr[i][j].changeBackgroundHeight( colorY );
                    //this.button_arr[i][j].changeColor( this.color_arr[ colorY ]);
                }*/
            }
        }
    }//end setButtonColors();

    public function update():void{
        if( this.myModel2.nChanged || this.myModel2.modesZeroed ){
            this.setNbrButtons();
            this.setButtonColors();
            //this.myModel2.nChanged = false;
            this.myModel2.modesZeroed = false;
        }
        if( this.myModel2.modesChanged ){
            //trace("ButtonArrayPanel.update() called.");
            this.setButtonColors();
            this.myModel2.modesChanged = false;
        }

    }//end update()

} //end class
} //end package
