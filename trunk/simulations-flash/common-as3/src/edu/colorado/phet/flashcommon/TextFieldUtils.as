package edu.colorado.phet.flashcommon {
import flash.display.*;
import flash.events.*;
import flash.text.*;

public class TextFieldUtils {
    public function TextFieldUtils() {
    }

    public static function resizeText( txtField:TextField, alignment:String ):void {  //get an error when Object = textField
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