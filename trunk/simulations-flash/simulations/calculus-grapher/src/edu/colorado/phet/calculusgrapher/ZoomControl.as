package edu.colorado.phet.calculusgrapher {
import flash.display.*;
import flash.events.*;
import flash.geom.Point;
//import flash.text.*;
import edu.colorado.phet.calculusgrapher.*;
//import edu.colorado.phet.flashcommon.*;

public class ZoomControl extends Sprite{
	
	public var magnifyButton:Sprite;
	public var demagnifyButton:Sprite;
	private var rail:Sprite;
	public var sliderKnob:Sprite;
	private var railLength:Number; 		//height of rail in pixels
	private var nbrTics:int;			//number of tic marks on rail, should be odd number
	
	public function ZoomControl(){
		this.magnifyButton = new Sprite();
		this.demagnifyButton = new Sprite();
		this.rail = new Sprite();
		this.sliderKnob = new Sprite();
		this.railLength = 70;
		this.nbrTics = 9;
		this.drawGraphics();
		this.makeFunctional();
		this.addChild( this.rail );
		this.addChild( this.magnifyButton );
		this.addChild( this.demagnifyButton );
		this.addChild( this.sliderKnob );
	}//end of constructor
	
	private function drawGraphics():void{
		var black:Number = 0x000000;
		var white:Number = 0xffffff;
		var blue:Number = 0x0000ff;
		var lineThickness1: Number = 1; 
		var lineThickness2: Number = 2;
		
		//draw vertical rail
		var gR:Graphics = this.rail.graphics;
		var rH:Number = this.railLength;  		//height of vertical rail, all lengths in pixels
		//var rW:Number = 6;		//width of rail
		gR.clear();
		gR.lineStyle( lineThickness2, blue, 1, true);
		gR.moveTo( 0, -rH/2 );
		gR.lineTo( 0, rH/2 );
		//gR.beginFill( white );
		//gR.drawRect( -rW/2, -rH/2, rW, rH );
		//gR.endFill();
		
		//draw slider knob
		var gK:Graphics = this.sliderKnob.graphics;
		var kH:Number = 6;		//knob Height
		var kW:Number = 15;		//knob width
		gK.clear();
		gK.lineStyle( lineThickness1, blue, 1, true );
		gK.beginFill( white );
		gK.drawRect( -kW/2, -kH/2, kW, kH );
		gK.endFill();
		
		//draw magnifying glass with plus sign in center;
		var gM:Graphics = this.magnifyButton.graphics;
		var mR:Number = 10;		//button radius
		var yPos:Number = -rH/2 - mR;		//y-position of center of button
		gM.clear();
		gM.lineStyle( lineThickness1, blue, 1, true );
		gM.beginFill( white, 1 );
		gM.drawCircle( 0, yPos, mR );
		gM.endFill();
		gM.lineStyle( lineThickness2, black, 1,true, LineScaleMode.NORMAL, CapsStyle.ROUND );
		var lineRadius:Number = 0.5*mR;
		gM.moveTo( 0, yPos - lineRadius );
		gM.lineTo( 0, yPos + lineRadius );
		gM.moveTo( -lineRadius, yPos );
		gM.lineTo( lineRadius, yPos );
		
		//draw demagnifying glass with minus sign in center;
		var gD:Graphics = this.demagnifyButton.graphics;
		//var mR:Number = 10;		//button radius
		yPos = rH/2 + mR;		//y-position of center of button
		gD.clear();
		gD.lineStyle( lineThickness1, blue, 1, true );
		gD.beginFill( white, 1 );
		gD.drawCircle( 0, yPos, mR );
		gD.endFill();
		gD.lineStyle( lineThickness2, black, 1,true, LineScaleMode.NORMAL, CapsStyle.ROUND );
		lineRadius = 0.5*mR;
		gD.moveTo( -lineRadius, yPos );
		gD.lineTo( lineRadius, yPos );
	}//end of drawGraphics()
	
	private function makeFunctional():void{
		this.magnifyButton.buttonMode = true;
		this.demagnifyButton.buttonMode = true;
		this.sliderKnob.buttonMode = true;
		this.makeKnobGrabbable(); 
	}//end makeFunctional()
	
	private function makeKnobGrabbable(): void {
        this.sliderKnob.addEventListener( MouseEvent.MOUSE_DOWN, grabKnob );
        var thisRail: Object = this.rail;
        var thisKnob: Object = this.sliderKnob;
        var delPix: Number = this.railLength / (this.nbrTics - 1);

        function grabKnob( evt: MouseEvent ): void {
            stage.addEventListener( MouseEvent.MOUSE_UP, releaseKnob );
            stage.addEventListener( MouseEvent.MOUSE_MOVE, moveKnob );
        }

        function releaseKnob( evt: MouseEvent ): void {
            stage.removeEventListener( MouseEvent.MOUSE_UP, releaseKnob );
            stage.removeEventListener( MouseEvent.MOUSE_MOVE, moveKnob );
        }

        function moveKnob( evt: MouseEvent ): void {
            //trace("HorizSlider.mouseY = "+mouseY);
            var knobY: Number;
            var maxY: int = thisKnob.parent.railLength/2;
			var minY: int = -thisKnob.parent.railLength/2;
            if ( mouseY < minY ) {
                knobY = minY;
            } else if ( mouseY > maxY ) {
                knobY = maxY;
            } else {
                knobY = mouseY;
            }

            //set knob to nearest detented position
            thisKnob.y = delPix * Math.round( knobY / delPix );
            evt.updateAfterEvent();
        }
    }//end makeKnobGrabbable()
	
}//end of class
}//end of package