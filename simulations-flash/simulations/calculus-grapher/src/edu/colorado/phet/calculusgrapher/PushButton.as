package edu.colorado.phet.calculusgrapher {

import flash.display.*;
import flash.events.*;
import flash.text.*;
import edu.colorado.phet.calculusgrapher.*;
import edu.colorado.phet.flashcommon.*;

public class PushButton {
    private var buttonBody:Object;
    private var bIcon:Sprite;
    public var myHighlight:Sprite;
    private var myButtonWidth:Number;
    public var key:String;
    private var rightOrLeft:String; //placement of label
    private var myLabel:TextField;
    private var labelFormat:TextFormat; // = new TextFormat();
    private var bFunction:Function;

    private var buttonFunction:Function;

    public static var TOOLTIP_LEFT : String = "left";
    public static var TOOLTIP_RIGHT : String = "right";

    public function PushButton( buttonBody:Object, bIcon:Sprite, key:String, rightOrLeft:String, bFunction:Function ) {
        this.buttonBody = buttonBody;
        this.myHighlight = this.buttonBody.highlight;
        this.bIcon = bIcon;
        //trace("this.bIcon.name: " + this.bIcon.name);
        this.myLabel = this.buttonBody.label_txt;
        myLabel.backgroundColor = 0xEEEEFF;
        this.bFunction = bFunction;
        this.key = key;
        this.rightOrLeft = rightOrLeft;
        this.labelFormat = new TextFormat();
        this.makePushButton();
    }//end of constructor

    public function makePushButton():void {
        this.buttonBody.buttonMode = true;
        this.buttonBody.addChild(this.bIcon);
        this.bIcon.mouseEnabled = false;
        this.myLabel.mouseEnabled = false;
        this.myLabel.text = " " + getLabelText() + " ";
        this.myLabel.visible = false;
        this.myHighlight.visible = false;
        this.myLabel.background = true;
        //this.myLabel.autoSize =
        if ( this.rightOrLeft == TOOLTIP_RIGHT ) {
            this.myLabel.autoSize = TextFieldAutoSize.LEFT;
            //this.labelFormat.align = TextFormatAlign.LEFT;  //not necessary when autosize is set
            this.myLabel.x = this.buttonBody.body_sp.width;
        }
        else if ( this.rightOrLeft == TOOLTIP_LEFT ) {
            this.myLabel.autoSize = TextFieldAutoSize.RIGHT;
            //this.labelFormat.align = TextFormatAlign.RIGHT;  //not necessary when autosize is set
            this.myLabel.x = -this.myLabel.width - 2;
        } else {
            throw new Error( "Unknown tooltip position. Should be TOOLTIP_LEFT or TOOLTIP_RIGHT" );
        }

        this.buttonBody.addEventListener(MouseEvent.MOUSE_DOWN, buttonBehave);
        this.buttonBody.addEventListener(MouseEvent.MOUSE_OVER, buttonBehave);
        this.buttonBody.addEventListener(MouseEvent.MOUSE_OUT, buttonBehave);
        this.buttonBody.addEventListener(MouseEvent.MOUSE_UP, buttonBehave);
    }

    public function getLabelText() : String {
        return SimStrings.get( key, capitalizeWord( key ) );
    }

    private function capitalizeWord( str : String ) : String {
        // TODO: move to flash-common?
        if( str == null ) {
            return null;
        }
        if( str.length == 0 ) {
            return new String( str );
        }
        return str.charAt( 0 ).toUpperCase() + str.substring( 1 );
    }

    private function buttonBehave( evt:MouseEvent ):void {
        if ( evt.type == "mouseDown" ) {
            this.buttonBody.x += 1;
            this.buttonBody.y += 1;
            this.bFunction(this.key);
            //this.myHighlight.visible = true;
            //trace("evt.name:"+evt.type);
        }
        else if ( evt.type == "mouseOver" ) {
            this.myLabel.visible = true;
            this.buttonBody.label_txt.setTextFormat(this.labelFormat);
            //trace("evt.name:"+evt.type);
        }
        else if ( evt.type == "mouseUp" ) {
                //trace("evt.name:"+evt.type);
                this.buttonBody.x -= 1;
                this.buttonBody.y -= 1;
                this.myHighlight.visible = true;
            }
            else if ( evt.type == "mouseOut" ) {
                    this.myLabel.visible = false;
                    this.buttonBody.label_txt.setTextFormat(this.labelFormat);
                    //trace("evt.name:"+evt.type);
                }
    }//end of buttonBehave

    public function highlightOn():void {
        this.myHighlight.visible = true;
    }
}//end of class
}//end of package