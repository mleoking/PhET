﻿package edu.colorado.phet.resonance {
import edu.colorado.phet.flashcommon.TextFieldUtils;

import flash.events.*;
import flash.text.*;

public class NiceButton {
    private var buttonBody: Object;
    private var myButtonWidth: Number;
    private var tFormat: TextFormat;
    private var buttonFunction: Function;

    public function NiceButton( buttonBody: Object, myButtonWidth: Number, buttonFunction: Function ) {
        this.buttonBody = buttonBody;
        this.myButtonWidth = myButtonWidth;
        this.buttonFunction = buttonFunction;
        this.tFormat = new TextFormat();
        this.tFormat.align = TextFormatAlign.CENTER;
        this.makeButton();
    }//end of constructor

    //can we modify this function so that buttonWidth resizes to account for label text length?
    public function setLabel( label: String ): void {
        buttonBody.label_txt.text = label;
        TextFieldUtils.resizeText( this.buttonBody.label_txt, TextFieldAutoSize.CENTER );
    }

//        public function changeLabel(label:String):void{
//            buttonBody.label_txt.text = label;
//        }

    public function getLabel(): String {
        return buttonBody.label_txt.text;
    }

    public function makeButton(): void {
        //trace("this.buttonBody = " , this.buttonBody);
        this.buttonBody.background.width = this.myButtonWidth;
        this.buttonBody.background.height = 30;
        this.buttonBody.label_txt.mouseEnabled = false;
        this.buttonBody.buttonMode = true;
        this.buttonBody.addEventListener( MouseEvent.MOUSE_DOWN, buttonBehave );
        this.buttonBody.addEventListener( MouseEvent.MOUSE_OVER, buttonBehave );
        this.buttonBody.addEventListener( MouseEvent.MOUSE_OUT, buttonBehave );
        this.buttonBody.addEventListener( MouseEvent.MOUSE_UP, buttonBehave );
        this.buttonBody.buttonMode = true;
        var localRef: Object = this;

        function buttonBehave( evt: MouseEvent ): void {

            if ( evt.type == "mouseDown" ) {
                localRef.buttonBody.x += 2;
                localRef.buttonBody.y += 2;
                //trace("evt.name:"+evt.type);
            } else if ( evt.type == "mouseOver" ) {
                localRef.tFormat.bold = true;
                localRef.buttonBody.label_txt.setTextFormat( localRef.tFormat );
                //trace("evt.name:"+evt.type);
            } else if ( evt.type == "mouseUp" ) {
                //trace("evt.name:"+evt.type);
                localRef.buttonBody.x -= 2;
                localRef.buttonBody.y -= 2;
                localRef.buttonFunction();
            } else if ( evt.type == "mouseOut" ) {
                localRef.tFormat.bold = false;
                localRef.buttonBody.label_txt.setTextFormat( localRef.tFormat );
                //trace("evt.name:"+evt.type);
            }
        }//end of buttonBehave

    }//end of makeButton

}//end of class
}//end of package