package edu.colorado.phet.flashcommon {
import flash.text.*;

public class TextFieldUtils {
    public function TextFieldUtils() {
    }

    //See http://www.adobe.com/support/flash/action_scripts/actionscript_dictionary/actionscript_dictionary729.html
    public static var CENTER = TextFieldAutoSize.CENTER;

    public static function resizeText(textField:TextField, alignment:String):void {  //get an error when Object = textField
        //trace("name: "+txtField.name + "   multiline: "+txtField.multiline + "   wordwrap: "+txtField.wordwrap);
        var textFormat:TextFormat = textField.getTextFormat();
        //trace(mTextField.text+" has alignment"+alignment);
        //trace(mTextField.text+" has textWidth "+mTextField.textWidth+" and field.width " + mTextField.width);
        //Check that string fits inside button and reduce font size if necessary
        textField.multiline = false;
        if (textField.textWidth + 2 >= textField.width) {
            trace("parent: " + textField.parent + "   name: " + textField.name + "  text resized ");
            var ratio:Number = 1.15 * textField.textWidth / textField.width;  //fudge factor of 1.15 to cover BOLDed text
            var initialHeight:Number = textField.height;
            trace(textField.text + " too long by factor of " + ratio + "   Initial height is " + textField.height + "   Initial y is " + textField.y);
            var oldSize:int = Number(textFormat.size); //TextFormat.size is type Object and must be cast to type Number
            var newSize:int = Math.round(oldSize / ratio);
            textFormat.size = newSize;
            textField.setTextFormat(textFormat);
            trace("New font size is " + textField.getTextFormat().size);
            textField.autoSize = alignment;  //resize bounding box
//            textField.y += (initialHeight - textField.height) / 2;  //keep text field vertically centered in button

            textField.autoSize = "none";  //make it possible to call this function multiple times
            //trace("New height is "+ mTextField.height+ "   Final y is " + mTextField.y);
            //trace(mTextField.text+" has field.width " + mTextField.width);
        }
    }
}
}