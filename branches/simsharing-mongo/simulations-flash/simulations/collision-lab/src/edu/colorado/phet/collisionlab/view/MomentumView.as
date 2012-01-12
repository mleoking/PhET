package edu.colorado.phet.collisionlab.view {
import edu.colorado.phet.collisionlab.constants.CLConstants;
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.util.Util;
import edu.colorado.phet.flashcommon.SimStrings;
import edu.colorado.phet.flashcommon.TextFieldUtils;

import fl.controls.*;
import fl.events.*;

import flash.display.*;
import flash.events.*;
import flash.text.*;

public class MomentumView extends Sprite {
    var myModel: Model;
    var myMainView: MainView;	//mediator and container of views
    var canvas: Sprite;			//background on which everything is placed
    var border: Sprite;			//border
    var grid: Sprite;			//background grid
    var invisibleBorder: Sprite;	//grabbable border
    var canvas2: Sprite;			//contain graphics in front of grid
    var borderColor: uint;		//color of border 0xrrggbb
    var borderWidth: Number;
    var borderHeight: Number;
    var borderThickness: Number;	//border thickness in pixels
    var marquee: TextField;
    var stageH: Number;
    var stageW: Number;
    var pixelsPerSIMomentum: int;	//scale of view
    var momentum_arr: Array;		//array of momentum arrows in graphic
    var totMomentum: Arrow;
    var tipToTailDisplayOn: Boolean; //true if momentum arrows displayed tip-to-tail
    var tipToTail_cb: CheckBox;
    var scale_slider: Slider;	//zoom slider
    var plusSign: PlusSign;		//Library symbol, part of zoom slider
    var minusSign: MinusSign;	//Libary symbol, part of zoom slider

    //following 3 strings to be internationalized, see function initializeStrings() below
    var momenta_str: String;
    var tipToTail_str: String;
    var tot_str: String;

    private var zoomScale: Number = 50;

    public function MomentumView( myModel: Model, myMainView: MainView ) {
        this.myModel = myModel;
        this.myMainView = myMainView;
        this.myModel.registerView( this );
        this.canvas = new Sprite();
        this.invisibleBorder = new Sprite();
        this.grid = new Sprite();
        this.canvas2 = new Sprite();
        this.myMainView.addChild( this );
        this.addChild( this.canvas );
        this.canvas.addChild( this.invisibleBorder );
        this.canvas.addChild( this.grid );
        this.canvas.addChild( this.canvas2 );
        this.tipToTail_cb = new CheckBox();
        tipToTail_cb.width = 300;//allow i18ned text to go further
        this.scale_slider = new Slider();
        this.minusSign = new MinusSign();
        this.plusSign = new PlusSign();

        //this.canvas.addChild(this.border);
        this.initialize();

    }//end of constructor

    public function initialize(): void {
        this.borderWidth = 250;
        this.borderHeight = 250;
        this.borderThickness = 6;
        this.tipToTailDisplayOn = true;
        this.stageW = this.myMainView.stageW;
        this.stageH = this.myMainView.stageH;
        this.canvas.x = this.stageW - this.borderWidth - 15;
        this.canvas.y = this.stageH - this.borderHeight - 30;
        this.momentum_arr = new Array( this.myModel.nbrBalls );
        //trace("MomentumView.stageW: "+this.stageW);
        //trace("MomentumView.stageH: "+this.stageH);
        this.drawBorder();
        this.drawInvisibleBorder();
        this.initializeStrings();
        this.drawMarquee();
        this.setupCheckBox();
        this.setupSlider();
        this.drawGrid();
        this.drawArrows();
        Util.makePanelDraggableWithBorder( this, this.invisibleBorder );
        this.startUp();
        this.update();
    }

    public function initializeStrings(): void {
        this.momenta_str = SimStrings.get( "MomentumView.momenta", "Momenta" );
        this.tipToTail_str = SimStrings.get( "MomentumView.tipToTail", "tip-to-tail" );
        this.tot_str = SimStrings.get( "MomentumView.total", "tot" );
    }//end of initializeStrings()

    public function drawBorder(): void {
        var W: Number = this.borderWidth;
        var H: Number = this.borderHeight;
        var del: Number = this.borderThickness / 2;
        //trace("width: "+W+"    height: "+H);
        with ( this.canvas.graphics ) {
            clear();
            lineStyle( this.borderThickness, 0x0000ff );
            //            var x0: Number = 0;
            //            var y0: Number = 0;
            beginFill( 0xffffff );
            moveTo( -del, -del );
            lineTo( W + del, -del );
            lineTo( W + del, +H + del );
            lineTo( -del, +H + del );
            lineTo( -del, -del );
            endFill();
        }
    }//end of drawBorder();

    public function drawGrid(): void {
        var sliderVal: Number = this.scale_slider.value;
        var gridSpacing: Number = 10 + sliderVal * 100;		//somewhat arbitrary, adjust to taste
        var thickness: int = 2;
        var del: Number = this.borderThickness / 4;
        var W: Number = borderWidth;
        var H: Number = borderHeight;
        with ( this.grid.graphics ) {
            clear();
            lineStyle( thickness, 0xDDDDDD );  //GRAY
            //draw x-axis and y-axes
            moveTo( del, H / 2 );
            lineTo( W - del, H / 2 );
            moveTo( W / 2, del );
            lineTo( W / 2, H - del );
            //draw horizontal grid lines and vertical grid lines
            var maxN: int = Math.floor( H / (2 * gridSpacing) );  //n = number of lines above x-axis
            for ( var i: int = 1; i <= maxN; i++ ) {
                if ( !myModel.oneDMode ) {
                    // horizontal lines
                    moveTo( del, H / 2 - i * gridSpacing );
                    lineTo( W - del, H / 2 - i * gridSpacing );
                    moveTo( del, H / 2 + i * gridSpacing );
                    lineTo( W - del, H / 2 + i * gridSpacing );
                }
                // vertical lines
                moveTo( W / 2 + i * gridSpacing, del );
                lineTo( W / 2 + i * gridSpacing, H - del );
                moveTo( W / 2 - i * gridSpacing, del );
                lineTo( W / 2 - i * gridSpacing, H - del );
            }
        }//end with()
    }//end draw Grid

    public function drawInvisibleBorder(): void {
        var W: Number = this.borderWidth;
        var H: Number = this.borderHeight;
        var del: Number = this.borderThickness / 2;
        //trace("width: "+W+"    height: "+H);
        with ( this.invisibleBorder.graphics ) {
            clear();
            lineStyle( this.borderThickness, 0x000000, 0 );
            moveTo( -del, -del );
            lineTo( W + del, -del );
            lineTo( W + del, +H + del );
            lineTo( -del, +H );
            lineTo( -del, -del );
        }
    }//end of drawInvisibleBorder();

    public function drawMarquee(): void {
        this.marquee = new TextField();
        this.marquee.text = this.momenta_str; //"Momenta"; //
        this.marquee.selectable = false;
        this.marquee.autoSize = TextFieldAutoSize.LEFT;
        this.marquee.x = 10;
        var tFormat: TextFormat = new TextFormat();
        tFormat.font = "Arial";
        tFormat.bold = true;
        tFormat.italic = true;
        tFormat.color = 0xffaaaa;
        tFormat.size = 40;
        //textFormat must be applied to text, so set text property first then apply format
        this.marquee.setTextFormat( tFormat );
        TextFieldUtils.resizeText( this.marquee, TextFieldAutoSize.CENTER );
        this.canvas2.addChild( this.marquee );

    }//end of drawMarquee

    private function setupCheckBox(): void {
        this.tipToTail_cb.label = this.tipToTail_str; //"tip-to-tail"; //
        this.tipToTail_cb.selected = true;
        this.tipToTail_cb.textField.width = 0.7 * this.borderWidth;
        this.tipToTail_cb.y = this.borderHeight - this.tipToTail_cb.height;
        this.canvas2.addChild( this.tipToTail_cb );
        this.tipToTail_cb.addEventListener( MouseEvent.CLICK, tipToTailChangeListener );
    }//end of setupCheckBox

    private function setupSlider(): void {
        this.scale_slider.direction = SliderDirection.VERTICAL;
        this.scale_slider.minimum = 0;
        this.scale_slider.maximum = 1;
        this.scale_slider.snapInterval = 0.01;
        this.scale_slider.value = 0.2;
        this.scale_slider.liveDragging = true;
        this.canvas2.addChild( this.scale_slider );
        this.canvas2.addChild( this.minusSign );
        this.canvas2.addChild( this.plusSign );
        this.scale_slider.height = 0.6 * this.borderHeight;
        this.scale_slider.x = 0.93 * this.borderWidth;
        this.scale_slider.y = 0.5 * this.borderHeight - this.scale_slider.height;// - this.scale_slider.height/2;
        this.scale_slider.addEventListener( Event.CHANGE, sliderChangeListener );
        var signSize: int = 19;
        this.minusSign.width = this.minusSign.height = signSize;
        this.plusSign.width = this.plusSign.height = signSize;
        this.minusSign.x = this.plusSign.x = this.scale_slider.x - 1;
        this.minusSign.y = this.scale_slider.y + 2 * this.scale_slider.height + 4;
        this.plusSign.y = this.scale_slider.y - 0.8 * signSize;
        this.minusSign.buttonMode = true;
        this.minusSign.addEventListener( MouseEvent.MOUSE_DOWN, zoomOut );
        this.plusSign.buttonMode = true;
        this.plusSign.addEventListener( MouseEvent.MOUSE_DOWN, zoomIn );
    }

    private function zoomIn( evt: MouseEvent ): void {
        this.scale_slider.value += 0.1;
        var currentValue: Number = this.scale_slider.value;
        if ( currentValue > 1 ) {this.scale_slider.value = 1;}
        this.setScaleOfArrows( convertSliderToScale( scale_slider.value ) );
        this.drawGrid();
        //trace("pushed up, value = "+this.scale_slider.value);
    }

    private function zoomOut( evt: MouseEvent ): void {
        this.scale_slider.value -= 0.1;
        var currentValue: Number = this.scale_slider.value;
        if ( currentValue < 0 ) {this.scale_slider.value = 0;}
        this.setScaleOfArrows( convertSliderToScale( scale_slider.value ) );
        this.drawGrid();
        //trace("pushed down, value = "+this.scale_slider.value);
    }

    private function tipToTailChangeListener( evt: MouseEvent ): void {
        this.tipToTailDisplayOn = evt.target.selected;
        if ( this.tipToTailDisplayOn ) {this.arrangeArrowsTipToTail();}
        //trace("MomentumView.evt.target.selected: "+evt.target.selected);
    }

    private function sliderChangeListener( evt: SliderEvent ): void {
        this.setScaleOfArrows( convertSliderToScale( evt.target.value ) );
        this.drawGrid();
        //trace("MomentumView slider value = "+evt.target.value);
    }

    private function convertSliderToScale( sliderValue: Number ): Number {
        return 10 + sliderValue * 200;
    }

    private function getArrows():Array {
        var arrows: Array = new Array();
        for each ( var arrow: Arrow in momentum_arr ) {
            arrows.push( arrow );
        }
        arrows.push( totMomentum );
        return arrows;
    }

    private function setScaleOfArrows( scale: Number ): void {
        var oldScale: Number = zoomScale;
        zoomScale = scale;
        var maxN: int = CLConstants.MAX_BALLS;
        for each ( var arrow: Arrow in getArrows() ) {
            arrow.setScale( scale );

            // funky math to rescale the arrows, since they aren't otherwise connected. also, the center origin scaling does this
            arrow.x = (arrow.x - borderWidth / 2) * zoomScale / oldScale + borderWidth / 2;
            if( !myModel.oneDMode ) {
                arrow.y = (arrow.y - borderHeight / 2) * zoomScale / oldScale + borderHeight / 2;
            }
        }
        this.update();
    }//end setScaleOfArrows()

    //called once, at startUp
    private function drawArrows(): void {
        var maxN: int = CLConstants.MAX_BALLS;
        for ( var i: int = 0; i < maxN; i++ ) {
            this.momentum_arr[i] = new Arrow( i );
            this.momentum_arr[i].setShaftWidth( 8 );
            this.momentum_arr[i].x = this.borderWidth / 2;
            this.momentum_arr[i].y = this.borderHeight / 2;
            this.canvas.addChild( this.momentum_arr[i] );
            this.momentum_arr[i].setArrow( this.myModel.ball_arr[i].getMomentum() );
            this.momentum_arr[i].makeThisDraggable();
            this.momentum_arr[i].addEventListener( MouseEvent.MOUSE_DOWN, dragListener );
        }
        //trace("this.momentum_arr[0].parent"+this.momentum_arr[0].parent);

        this.totMomentum = new Arrow( maxN );  //index of total momentum is N = CollisionLabConstants.MAX_BALLS
        this.totMomentum.setText( tot_str );
        this.totMomentum.setColor( 0xff8800 );	//tot momentum arrow is orange
        this.totMomentum.setShaftWidth( 4 );
        this.totMomentum.x = this.borderWidth / 2;
        this.totMomentum.y = this.borderHeight / 2;
        this.canvas.addChild( this.totMomentum );
        this.totMomentum.setArrow( this.myModel.getTotalMomentum() );
        this.totMomentum.makeThisDraggable();
        this.totMomentum.addEventListener( MouseEvent.MOUSE_DOWN, dragListener );
    }

    private function arrangeArrowsTipToTail(): void {
        var N: int = this.myModel.nbrBalls;
        var oneDMode: Boolean = this.myModel.oneDMode;
        var arrowIM1: Arrow;
        var i: int;
        if ( oneDMode ) {
            var vertSpace: int = 20;
            var startHeight: Number = this.borderHeight / 2 - (N / 2) * vertSpace;
            this.momentum_arr[0].x = this.borderWidth / 2;
            this.momentum_arr[0].y = startHeight;
            for ( i = 1; i < N; i++ ) {
                arrowIM1 = this.momentum_arr[i - 1];  //IM1 = "i minus 1"
                this.momentum_arr[i].x = arrowIM1.x + arrowIM1.getTipX();
                this.momentum_arr[i].y = startHeight + i * vertSpace;
            }//end for
            this.totMomentum.x = this.borderWidth / 2;
            this.totMomentum.y = startHeight + N * vertSpace;
        }
        else {  //if 2DMode
            this.totMomentum.x = this.borderWidth / 2;
            this.totMomentum.y = this.borderHeight / 2;
            this.momentum_arr[0].x = this.borderWidth / 2;
            this.momentum_arr[0].y = this.borderHeight / 2;
            for ( i = 1; i < N; i++ ) {
                arrowIM1 = this.momentum_arr[i - 1];  //IM1 = "i minus 1"
                this.momentum_arr[i].x = arrowIM1.x + arrowIM1.getTipX();
                this.momentum_arr[i].y = arrowIM1.y + arrowIM1.getTipY();
            }//end for
        }
    }//end arangeArrowsTipToTail();


    //needed to set initial visibility of momentum arrows
    private function startUp(): void {
        var maxN: int = CLConstants.MAX_BALLS;
        var N: int = this.myModel.nbrBalls;
        var i: int;
        for ( i = 0; i < N; i++ ) {
            this.momentum_arr[i].visible = true;
        }
        for ( i = N; i < maxN; i++ ) {
            this.momentum_arr[i].visible = false
        }
    }

    public function dragListener( evt: MouseEvent ): void {
        this.tipToTailDisplayOn = false;
        this.tipToTail_cb.selected = false;
    }

    public function update(): void {
        var N: int = this.myModel.nbrBalls;
        var i: int;
        if ( this.myModel.nbrBallsChanged ) {
            var maxN: int = CLConstants.MAX_BALLS;
            for ( i = 0; i < N; i++ ) {
                this.momentum_arr[i].visible = true;
            }
            for ( i = N; i < maxN; i++ ) {
                this.momentum_arr[i].visible = false
            }
        }//end if()
        for ( i = 0; i < N; i++ ) {
            this.momentum_arr[i].setArrow( this.myModel.ball_arr[i].getMomentum() );
        }
        if ( this.myModel.resetting ) {
            this.arrangeArrowsTipToTail();
            this.tipToTail_cb.selected = true;
            this.tipToTailDisplayOn = true;
        }
        //position momentum arrows tip-to-tail
        if ( tipToTailDisplayOn ) {
            this.arrangeArrowsTipToTail();
        }//end if
        this.totMomentum.setArrow( this.myModel.getTotalMomentum() );
        //trace("momentum view update");
    }//end of update()

}//end of class
}//end of package