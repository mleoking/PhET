package edu.colorado.phet.flashcommon.controls {
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
        textField.text = " " + title + " "; // cheap and scalable way to pad it
        textField.selectable = false;
        textField.x = 5;
        var textFormat: TextFormat = new TextFormat();
        textFormat.size = 16;
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
        var tabColor: int;
        if ( selected ) {
            tabColor = tabBar.selectedColor;
            textField.textColor = 0x000000;
        }
        else {
            tabColor = tabBar.disabledColor;
            textField.textColor = 0x282828;
        }
        //textField.background = true;

        graphics.clear();
        graphics.moveTo( 0, textField.height );
        if( selected ) {
            graphics.lineStyle( 1, 0x000000 );
        }
        graphics.beginFill( tabColor );
        graphics.lineTo( 0, 0 );
        graphics.lineTo( textField.width + textField.x, 0 );
        graphics.lineTo( textField.width + textField.x + 15, textField.height );
        graphics.lineStyle();
        graphics.endFill();
    }
}
}