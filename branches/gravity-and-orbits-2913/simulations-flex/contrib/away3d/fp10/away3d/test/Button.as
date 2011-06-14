package away3d.test
{
    import flash.display.*;

    /**
    * Simple rounded rectangle button
    */ 
    public class Button extends SimpleButton
    {
        public var selected:Boolean = false;

        public function Button(text:String, pwidth:int = 80, pheight:int = 20)
        {
            upState = new ButtonState(pwidth, pheight, text, 0x000000);
            overState = new ButtonState(pwidth, pheight,text, 0x666666);
            downState = new ButtonState(pwidth, pheight, text, 0xFFFFFF);
            hitTestState = new ButtonState(pwidth, pheight);
        }
    }
}

import flash.text.*;
import flash.display.*;

import away3d.test.*;

class ButtonState extends Sprite
{
    public function ButtonState(pwidth:int, pheight:int, text:String = null, color:int = 0)
    {
        addChild(new Panel(0, 0, pwidth, pheight));
        if (text)
        {
            var label:TextField = new TextField();
            label.autoSize = TextFieldAutoSize.LEFT;
            label.x = 5;
            label.y = 0;
            label.defaultTextFormat = new TextFormat("Arial", 14, color);
            label.text = text;
            addChild(label);
        }
    }
}
