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
	private var sliderKnob:Sprite;
	private var knobPosition:int; 		//0 at center; +1 for every magnify; -1 for every demagnify; 
	private var railLength:Number; 		//height of rail between magnify and demagnify buttons, in pixels
	private var nbrTics:int;			//number of tic marks on verical rail, should be odd number
	private var setMagnification:Function;
	
	public function ZoomControl( setMagnification:Function ){
		this.setMagnification = setMagnification;
		this.magnifyButton = new Sprite();
		this.demagnifyButton = new Sprite();
		this.rail = new Sprite();
		this.sliderKnob = new Sprite();
		this.knobPosition = 0;
		this.railLength = 140;
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
		var lineThickness1: Number = 4; 
		var lineThickness2: Number = 5;
		
		//draw tic marks on (invisible) veritical rail
		var gR:Graphics = this.rail.graphics;
		var tX:Number = 8;			//half-diameter of tic Mark
		gR.clear();
		gR.lineStyle( lineThickness1, blue, 1, true);
		var delPix:Number = this.railLength / (this.nbrTics - 1);   //vertical distance on rail between tic marks
		var maxTics:int = (this.nbrTics - 1)/2;   //maximum nbr of tic marks above or below zero tic
		for(var i:int = -maxTics + 1; i < maxTics; i++){
			gR.moveTo( -tX, i*delPix );
			gR.lineTo( +tX, i*delPix )
		}
		
		//draw slider knob
		var gK:Graphics = this.sliderKnob.graphics;
		var kH:Number = 12;		//knob Height
		var kW:Number = 30;		//knob width
		gK.clear();
		gK.lineStyle( lineThickness1, blue, 1, true );
		gK.beginFill( white );
		gK.drawRect( -kW/2, -kH/2, kW, kH );
		gK.endFill();
		
		//draw magnifying glass with plus sign in center;
		var gM:Graphics = this.magnifyButton.graphics;
		var rH:Number = this.railLength;  		//height of vertical rail, all lengths in pixels
		var mR:Number = 20;		//button radius
		var yPos:Number = -rH/2 - mR;		//y-position of center of button
		gM.clear();
		gM.lineStyle( lineThickness1, blue, 1, false );
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
		gD.lineStyle( lineThickness1, blue, 1, false );
		gD.beginFill( white, 1 );
		gD.drawCircle( 0, yPos, mR );
		gD.endFill();
		gD.lineStyle( lineThickness2, black, 1,true, LineScaleMode.NORMAL, CapsStyle.ROUND );
		lineRadius = 0.5*mR;
		gD.moveTo( -lineRadius, yPos );
		gD.lineTo( lineRadius, yPos );
	}//end of drawGraphics()
	
	private function makeFunctional():void{
		var delPix: Number = this.railLength / (this.nbrTics - 1);
		this.magnifyButton.buttonMode = true;
		this.demagnifyButton.buttonMode = true;
		this.sliderKnob.buttonMode = true;
		this.magnifyButton.addEventListener( MouseEvent.MOUSE_DOWN, buttonMagnify);
		this.demagnifyButton.addEventListener( MouseEvent.MOUSE_DOWN, buttonDemagnify);
		var thisObject:Object = this;
		var knobPosition:Number = this.knobPosition;
		var maxTics:int = (this.nbrTics - 1)/2;   //maximum nbr of tic marks above or below zero tic
		function buttonMagnify( evt:MouseEvent ):void{
			if( knobPosition < maxTics ){
				knobPosition += 1;
			}
			setKnobPosition();		
			thisObject.setMagnification( knobPosition );
		}
		function buttonDemagnify( evt:MouseEvent ):void{
			if( knobPosition > -maxTics ){
				knobPosition -= 1;
			}		
			setKnobPosition();
			thisObject.setMagnification( knobPosition );
		}
		
		function setKnobPosition():void{
			if( knobPosition <= maxTics && knobPosition >= -maxTics ){
				thisObject.sliderKnob.y = -delPix * knobPosition;
			}else if ( knobPosition > maxTics ) {
				thisObject.sliderKnob.y = -delPix * maxTics;
			}else if ( knobPosition < maxTics ) {
				thisObject.sliderKnob.y = +delPix * maxTics;
			}
		}
		
		this.makeKnobGrabbable(); 
	}//end makeFunctional()
	
	
	private function makeKnobGrabbable(): void {
        this.sliderKnob.addEventListener( MouseEvent.MOUSE_DOWN, grabKnob );
		var thisObject:Object = this;
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
			thisObject.knobPosition = -Math.round( knobY / delPix );
			thisObject.setMagnification( thisObject.knobPosition );
			//trace("ZoomControl.makeKnobGrabbable.moveKnob.  KnobPosition = "+ thisObject.knobPosition );
            evt.updateAfterEvent();
        }
    }//end makeKnobGrabbable()
	
}//end of class
}//end of package