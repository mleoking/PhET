package edu.colorado.phet.resonance {

import flash.display.*;
import flash.events.*;
import flash.geom.*;
import flash.text.*;

public class MassSpringView extends Sprite {

    private var model: MassSpringModel;	//model of single mass-spring system
    private var spring: Sprite;			//view of spring
    private var L0InPix: Number;		//length of relaxed spring in pixels
    private var mass: Sprite;			//view of mass
    //private var massShadow: Sprite;     //shadow of mass
    //private var xOff:Number;            //x-offset of shadow
    //private var yOff:Number;            //y-offset of shadow
    private var label_txt: TextField;	//label on mass: 1, 2, 3, ...
    private var labelColor:Number;      //default is white; yellow is mass is selected
    public var tFormat1: TextFormat;
    private var pixPerMeter: Number;	//scale: number of pixels in 1 meter
    private var stageW: int;
    private var stageH: int;
    private var orientation: Number;	//-1 if mass on top of spring, +1 if mass hanging from bottom of spring

    public function MassSpringView( model: MassSpringModel ) {
        this.model = model;
        this.model.registerView( this );
        this.initialize();
    }//end of constructor

    public function initialize(): void {
        this.orientation = -1;
        this.stageW = Util.STAGEW;
        this.stageH = Util.STAGEH;
        this.pixPerMeter = 800;
        this.L0InPix = this.model.getL0() * this.pixPerMeter;
        //this.xOff = -10;
        //this.yOff = 25;
        this.spring = new Sprite();
        this.mass = new Sprite();
        //this.massShadow = new Sprite();
        this.labelColor = 0xffffff;  //default label color is white
        this.label_txt = new TextField();	//static label
        //this.drawMassShadow();
        this.drawSpring();
        this.drawMass();
        //this.setLabel(this.model.getRNbr().toString());
        //this.addChild(this.massShadow);
        this.addChild( this.spring );
        this.addChild( this.mass );
        this.mass.addChild( this.label_txt );
        this.makeMassGrabbable();
        //this.spring.x = 0;//stageW/2;
        //this.spring.y = 0;//stageH/2;
        //trace("MassSpringView.initialize() called. stageW = "+this.stageW);
    }//end of initialize()


    public function drawSpring(): void {
        var N: int = 7; //number of turns in spring
        var D0inPix: Number = this.orientation * this.L0InPix;  //D for displacement
        var r: Number = 10; //radius of spring coil in pixels
        var h: Number = 0.8 * D0inPix / N;  //height of a single coil
        //trace("D0inPix = "+D0inPix);
        var k: Number = this.model.getK();
        var lineWidth: Number = 0.3 * Math.pow( k, 1 / 2 );
        //trace("MassSpringView.drawSpring. lineWidth = " + lineWidth);
        var g: Graphics = this.spring.graphics;
        g.clear();

        var x0: Number = 0; //this.stageW/2;
        var y0: Number = 0; //this.stageH/2;

        //draw shadow of spring
//        var shadowColor: Number = 0xdddd77;  //background is 0xffff99
//        g.lineStyle( lineWidth, shadowColor, 1, false, LineScaleMode.NONE, CapsStyle.NONE, JointStyle.BEVEL );
//        var offset: Number = lineWidth / 2;
//        g.moveTo( xOff + x0, yOff + y0 );
//        g.lineTo( xOff + x0, yOff + y0 + 0.1 * D0inPix );
//        for ( var i: int = 0; i < N; i++ ) {
//            g.lineTo( xOff + x0 + r, yOff + y0 + 0.1 * D0inPix + i * h + h / 4 );
//            g.lineTo( xOff + x0 - r, yOff + y0 + 0.1 * D0inPix + i * h + (3 / 4) * h );
//            g.lineTo( xOff + x0, yOff + y0 + 0.1 * D0inPix + i * h + h );
//        }
//        g.lineTo( xOff + x0, yOff + y0 + D0inPix );

        //draw spring coils
        g.lineStyle( 1.5 * lineWidth, 0xff0000, 1, true, LineScaleMode.NONE, CapsStyle.ROUND, JointStyle.ROUND );
        g.moveTo( x0, y0 );
        g.lineTo( x0, y0 + 0.1 * D0inPix );//0.1*D0inPix);
        g.lineStyle( 1.3 * lineWidth, 0xff0000, 1, true, LineScaleMode.NONE, CapsStyle.NONE, JointStyle.BEVEL );
        for ( var i: int = 0; i < N; i++ ) {
            g.lineTo( x0 + r, y0 + 0.1 * D0inPix + i * h + h / 4 );
            g.lineTo( x0 - r, y0 + 0.1 * D0inPix + i * h + (3 / 4) * h );
            g.lineTo( x0, y0 + 0.1 * D0inPix + i * h + h );
        }
        g.lineStyle( 1.3 * lineWidth, 0xff0000, 1, false, LineScaleMode.NONE, CapsStyle.ROUND, JointStyle.ROUND );
        g.lineTo( x0, y0 + D0inPix );

        //draw highlight on topside of every other spring coil
        g.lineStyle( 2, 0xffdeeee, Math.max( 1, lineWidth / 4 ), true, LineScaleMode.NONE, CapsStyle.NONE, JointStyle.BEVEL );
        var offset: Number = -lineWidth / 2;
        g.moveTo( x0, offset + y0 + 0.1 * D0inPix );
        for ( var i: int = 0; i < N; i ++ ) {
            g.moveTo( x0 + r, offset + y0 + 0.1 * D0inPix + i * h + h / 4 );
            g.lineTo( x0 - r, offset + y0 + 0.1 * D0inPix + i * h + (3 / 4) * h );
            g.moveTo( x0, offset + y0 + 0.1 * D0inPix + i * h + h );
        }
        //draw shadow on underside of every other spring coil
        g.lineStyle( 2, Math.max( 1, 0xaa0000 ), lineWidth / 4, true, LineScaleMode.NONE, CapsStyle.NONE, JointStyle.BEVEL );
        offset = +lineWidth / 2;
        g.moveTo( x0, offset + y0 + 0.1 * D0inPix );
        for ( var i: int = 0; i < N; i ++ ) {
            g.moveTo( x0 + r, offset + y0 + 0.1 * D0inPix + i * h + h / 4 );
            g.lineTo( x0 - r, offset + y0 + 0.1 * D0inPix + i * h + (3 / 4) * h );
            g.moveTo( x0, offset + y0 + 0.1 * D0inPix + i * h + h );
        }

    }//end of drawSpring()

    public function drawMass(): void {
        var lineWidth = 4;
        var gradMatrix = new Matrix();   //for creating highlights and shadow on mass
        //var L0inPix:Number = this.pixPerMeter * this.model.getL0();
        var mass: Number = this.model.getM();  //(width)^3 ~ mass
        var massW: Number = Math.pow( mass, 1 / 3 ) * 40;
        gradMatrix.createGradientBox( 1.0 * massW, 1.0 * massW, -Math.PI / 4, massW/2, 0 );
        var g: Graphics = this.mass.graphics;
        g.clear();

        var x0: Number = 0;
        var y0: Number = 0;

        g.lineStyle( lineWidth, 0x0000ff, 1, true );
        g.lineGradientStyle( GradientType.LINEAR, [0x0000aa, 0x8888ff], [1,1], [118,138], gradMatrix );
        g.beginFill( 0x5555ff );
        if ( this.orientation == -1 ) { y0 = -massW; }
        g.drawRoundRect( x0 - massW / 2, y0, massW, massW, 0.3 * massW );
        g.endFill();
        this.makeLabel();
        var D0inPix: Number = this.orientation * this.L0InPix;  //D for displacement
        //this.mass.y = D0inPix;
    }//end of drawMass()

//    public function drawMassShadow():void{
//        var lineWidth = 3;
//        var shadowColor: Number = 0xdddd77;  //background is 0xffff99
//        var mass: Number = this.model.getM();  //(width)^3 ~ mass
//        var massW: Number = Math.pow( mass, 1 / 3 ) * 40
//        var g: Graphics = this.massShadow.graphics;
//        g.clear();
//        var x0: Number = 0 + xOff;
//        var y0: Number = 0 + yOff;
//        g.lineStyle( lineWidth, shadowColor, 1, true );
//        g.beginFill( shadowColor );
//        if ( this.orientation == -1 ) { y0 = -massW + yOff; }
//        g.drawRoundRect( x0 - massW / 2, y0, massW, massW, 0.3 * massW );
//        g.endFill();
//
//    }

    private function makeLabel(): void {
        this.label_txt.selectable = false;
        //this.label_txt.border = true;
        this.label_txt.autoSize = TextFieldAutoSize.CENTER;
        this.label_txt.text = this.model.getRNbr().toString();
        this.tFormat1 = new TextFormat();	//format of label
        this.tFormat1.font = "Arial";
        this.tFormat1.color = this.labelColor;
        this.tFormat1.bold  = true;
        this.tFormat1.size = 22;
        this.label_txt.setTextFormat( this.tFormat1 );
        this.label_txt.x = -0.5 * this.label_txt.width;
        var mass: Number = this.model.getM();  //(width)^3 ~ mass
        var massH: Number = Math.pow( mass, 1 / 3 ) * 40;
        this.label_txt.y = -0.5 * massH - 0.5 * this.label_txt.height;
        //trace("MSView.makeLabel. RNbr = "+this.model.getRNbr().toString()+"    this.mass.height = "+this.mass.height);
    }

    public function setLabel( txt: String ): void {
        this.label_txt.text = txt;
        this.label_txt.x = -0.5 * this.label_txt.width;
        this.label_txt.setTextFormat( tFormat1 );
    }

    public function setLabelColor(color:Number):void{
       this.labelColor = color;
       this.tFormat1.color = color;
       this.label_txt.setTextFormat( tFormat1 );
    }

    public function getModel(): MassSpringModel {
        return this.model;
    }

    private function makeMassGrabbable(): void { //mass can be moved vertically only
        var target = this.mass;
        var thisObject: Object = this;
        //var L0inPix:Number = this.orientation*this.pixPerMeter * this.model.getL0();
        var D0inPix: Number = this.orientation * this.L0InPix;
        target.buttonMode = true;
        target.mouseChildren = false;
        target.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        var clickOffset: Point;

        function startTargetDrag( evt: MouseEvent ): void {
            //set selected resonator's label color
            //thisObject.setLabelColor(0xffff00);
            var indx: int = thisObject.model.getRNbr();
            //Geezz! There has gotta be a better way for next line.
            thisObject.model.shakerModel.view.myMainView.myControlPanel.setResonatorIndex( indx );
            //mass not grabbable if sim paused or if shaker is on
            if ( !thisObject.model.shakerModel.paused && !thisObject.model.shakerModel.getRunning() ) {
                //problem with localX, localY if sprite is rotated.
                //trace("MassSpringView.makeMassGrabbable. indx = "+ indx);
                clickOffset = new Point( evt.localX, evt.localY );
                //trace("evt.target.y: "+evt.target.y);
                thisObject.model.stopMotion();
                stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
                stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
                //thisObject.spring.scaleY *= 1.5;
            }
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            //trace("stop dragging");
            clickOffset = null;
            stage.removeEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
            thisObject.model.startMotion();
        }

        function dragTarget( evt: MouseEvent ): void {
            //target.y = mouseY - clickOffset.y;
            var newYInPix = mouseY - clickOffset.y;
            var massYInMeters = thisObject.orientation * newYInPix / thisObject.pixPerMeter;
            //var massYInMeters = target.y/thisObject.pixPerMeter;
            thisObject.model.setY( massYInMeters );
            //trace("massYinMeters" + massYinMeters);
            //trace("evt.localY = "+evt.localY);
            //trace("mouseY = "+mouseY);
            //trace("thisObject.mass.y = " + thisObject.mass.y);
            evt.updateAfterEvent();
        }//end of dragTarget()
    }//end of makeMassGrabballe()


    public function update(): void {
        //update position of mass
        this.mass.y = this.orientation * this.model.getY() * this.pixPerMeter;
        //this.massShadow.y = this.mass.y;
        this.spring.y = this.orientation * this.model.getY0() * this.pixPerMeter;
        //update length of spring
        var sprLengthInPix: Number = (this.mass.y - this.spring.y);

        if ( this.orientation * sprLengthInPix > 0 ) {
            this.spring.scaleY = this.orientation * sprLengthInPix / this.L0InPix;
            this.spring.rotation = 0;
        }
        else {
            this.spring.scaleY = Math.abs( this.orientation * sprLengthInPix ) / this.L0InPix; //0;
            this.spring.rotation = 180;     //Problem: highlights on spring on wrong side now
        }
        this.spring.scaleX = Math.min( 1.5, 1 / this.spring.scaleY );

    }//end update()

}///end of class

}//end of package