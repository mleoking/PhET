package edu.colorado.phet.collisionlab.control {
import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

public class Tab extends Sprite {
    private var textField: TextField;
    private var _selected: Boolean = false;
    private var tabBar: TabBar;

    public function Tab( title: String, tabBar: TabBar ) {
        this.tabBar = tabBar;
        textField = new TextField();
        textField.autoSize = TextFieldAutoSize.LEFT;
        textField.text = " " + title + " ";
        textField.selectable = false;
        var textFormat: TextFormat = new TextFormat();
        textFormat.size = 20;
        textFormat.font = "Arial";
        textFormat.bold = true;
        textField.setTextFormat( textFormat );
        addChild( textField );
        update();
        buttonMode = true;
        mouseChildren = false;
        mouseEnabled = true;
        useHandCursor = true;
        var tab: Tab = this;
        addEventListener( MouseEvent.CLICK, function(): void {
            tabBar.selectedTab = tab;
        } );
    }

    public function getCalculatedHeight(): Number {
        return textField.height;
    }

    public function get selected(): Boolean {
        return _selected;
    }

    public function set selected( value: Boolean ): void {
        if ( value != _selected ) {
            _selected = value;
            update();
        }
    }

    public function update(): void {
        //graphics.clear();
        //graphics.lineStyle( 1, 0x000000 );
        //graphics.moveTo( textField.width, 0 );
        //graphics.lineTo( textField.width, textField.height );
        if ( selected ) {
            textField.backgroundColor = tabBar.selectedColor;
        }
        else {
            textField.backgroundColor = tabBar.disabledColor;
        }
        textField.background = true;
    }
}
}