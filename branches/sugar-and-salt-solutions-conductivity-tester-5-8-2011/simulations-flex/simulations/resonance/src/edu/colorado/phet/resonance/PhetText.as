package edu.colorado.phet.resonance {
import flash.display.Sprite;

import flash.events.Event;
import flash.text.TextField;

import flash.text.TextFieldAutoSize;

import mx.core.IInvalidating;
import mx.core.UIComponent;

public class PhetText extends UIComponent {
    private var sprite: Sprite;
    private var _text: String;
    private var textField: TextField;
    
    public function PhetText( text: String) {
        super();

        sprite = new Sprite();
        textField = new TextField();
        sprite.addChild( textField );
        this.text = text;

        addChild(sprite);
    }

    public function get text():String {
        return _text
    }


    [PercentProxy("percentWidth")]
    [Inspectable(category="General")]
    [Bindable("widthChanged")]
    public override function get width(): Number {
        return textField.width;
    }


    [PercentProxy("percentHeight")]
    [Inspectable(category="General")]
    [Bindable("heightChanged")]
    public override function get height(): Number {
        return textField.height;
    }

    public function set text(text: String):void {
        _text = text;
        textField.autoSize = TextFieldAutoSize.LEFT;
        textField.text = " " + text + " ";

        trace( "th: " + textField.height );
        trace( "tw: " + textField.width );

        var p:IInvalidating = parent as IInvalidating;
            if (p)
            {
                p.invalidateSize();
                p.invalidateDisplayList();
            }
        dispatchEvent(new Event("widthChanged"));
        dispatchEvent(new Event("heightChanged"));

//        explicitHeight = textField.height;
//        explicitWidth = textField.width;
//        width = textField.height;
//        height = textField.width;
        invalidateSize();
         invalidateProperties();
            invalidateDisplayList();

        this.graphics.clear();
        this.graphics.beginFill( 0xFF0000, 0.5 );
        this.graphics.drawRect( 0, 0, this.width, this.height );
        this.graphics.endFill();
         this.graphics.beginFill( 0x00FF00, 0.5 );
        this.graphics.drawRect( textField.x, textField.y, textField.width, textField.height );
        this.graphics.endFill();

//        invalidateSize();
    }
}
}