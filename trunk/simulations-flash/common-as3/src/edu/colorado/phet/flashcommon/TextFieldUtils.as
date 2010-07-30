package edu.colorado.phet.flashcommon {
import flash.display.*;
import flash.events.*;
import flash.text.*;

import fl.controls.CheckBox;
import fl.controls.LabelButton;

public class TextFieldUtils {
    public function TextFieldUtils() {
    }
    
     // hook up the events on a TextField so that it appears like it is the label of the CheckBox.
    // handles mouse over, out, and clicks
    private function emulateButton( textField : TextField, checkBox : LabelButton ):void {
        textField.addEventListener( MouseEvent.ROLL_OVER, function( evt: Event):void { checkBox.setMouseState( "over" ); } );
        textField.addEventListener( MouseEvent.ROLL_OUT, function( evt: Event):void { checkBox.setMouseState( "up" ); } );
        textField.addEventListener( MouseEvent.MOUSE_DOWN, function( evt: Event):void { checkBox.setMouseState( "down" ); } );
        textField.addEventListener( MouseEvent.MOUSE_UP, function( evt: Event):void { checkBox.setMouseState( "up" ); } );
        textField.addEventListener( MouseEvent.CLICK, function( evt: Event):void { checkBox.dispatchEvent( evt ); } );
    }

    public static function resizeText( txtField:TextField, alignment:String ):void {  //get an error when Object = textField
        txtField.multiline = false;//made sure that internationalizable textfields do not have multiline, because it causes problems
        //trace("name: "+txtField.name + "   multiline: "+txtField.multiline + "   wordwrap: "+txtField.wordwrap);
        var mTextField:TextField = txtField;
        var mTextFormat:TextFormat = txtField.getTextFormat();
        var alignment:String = alignment;
        //trace(mTextField.text+" has alignment"+alignment);
        //trace(mTextField.text+" has textWidth "+mTextField.textWidth+" and field.width " + mTextField.width);
        //Check that string fits inside button and reduce font size if necessary

        if ( mTextField.textWidth + 2 >= mTextField.width ) {
            trace("parent: " + mTextField.parent + "   name: " + mTextField.name + "  text resized ");
            var ratio:Number = 1.15 * mTextField.textWidth / mTextField.width;  //fudge factor of 1.15 to cover BOLDed text
            var initialHeight:Number = mTextField.height;
            trace(mTextField.text + " too long by factor of " + ratio + "   Initial height is " + mTextField.height + "   Initial y is " + mTextField.y);
            var oldSize:int = Number(mTextFormat.size); //TextFormat.size is type Object and must be cast to type Number
            var newSize:int = Math.round(oldSize / ratio);
            mTextFormat.size = newSize;
            mTextField.setTextFormat(mTextFormat);
            trace("New font size is " + mTextField.getTextFormat().size);
            mTextField.autoSize = alignment;  //resize bounding box
            var finalHeight:Number = mTextField.height;
            mTextField.y += (initialHeight - finalHeight) / 2;  //keep text field vertically centered in button
            //trace("New height is "+ mTextField.height+ "   Final y is " + mTextField.y);
            //trace(mTextField.text+" has field.width " + mTextField.width);
        }
    }
}
}