package edu.colorado.phet.flashcommon.controls {

import flash.display.Sprite;
import flash.display.GradientType;
import flash.geom.Matrix;
import flash.display.SpreadMethod;

public class TabBar extends Sprite {
    private const tabHolder: Sprite = new Sprite();
    private var tabs: Array = new Array();
    public var selectedColor: int;
    public var disabledColor: int;
    public var backgroundColor: int;
    private var listeners: Array = new Array();
    private var _selectedTab: Tab = null;

    public function TabBar( selectedColor: int = 0xb4cdff, disabledColor: int = 0x647dff, backgroundColor: int = 0xf0f0f0 ) {
        this.selectedColor = selectedColor;
        this.disabledColor = disabledColor;
        this.backgroundColor = backgroundColor;
        addChild( tabHolder );
    }

    public function addTab( tab: Tab ): void {
        var offsetFromLeft: Number = 12;
        var tabPadding: Number = -8;
        var x: Number = tabHolder.width + offsetFromLeft + tabPadding * tabs.length;
        tabHolder.addChild( tab );
        tab.x = x;
        tab.y = 2;
        for each ( var otherTab:Tab in tabs ) {
            tabHolder.swapChildren( tab, otherTab ); // this actually will cause the tabs to be in inverted order on startup
        }
        tabs.push( tab );
        drawBackground();
    }

    public function drawBackground(): void {
        var dividingLine: Number = tabs[0].getCalculatedHeight() + 1;
        var separatorHeight: Number = 8;

        graphics.clear();
        //graphics.lineStyle( 1, 0x000000 );
        graphics.beginFill( backgroundColor );
        //graphics.drawRect( tabHolder.width, 0, 950, tabs[0].getCalculatedHeight() ); // TODO: improve height calculation, make width based on stage. null ref!
        graphics.drawRect( -2000, 0, 5000, dividingLine ); // TODO: improve height calculation, make width based on stage. null ref!
        graphics.endFill();
        //graphics.beginFill( selectedColor );
        var matrix:Matrix = new Matrix();
        matrix.createGradientBox( separatorHeight, separatorHeight, Math.PI / 2, 0, dividingLine );
//        graphics.beginGradientFill( GradientType.LINEAR, [selectedColor, Color.interpolateColor(selectedColor, 0x000000,0.25)], [1,1], [0x00, 0xFF], matrix, SpreadMethod.REPEAT);
        graphics.beginGradientFill( GradientType.LINEAR, [selectedColor, quarterColor(selectedColor,0x000000)], [1,1], [0x00, 0xFF], matrix, SpreadMethod.REPEAT);
//        graphics.drawRect( 0, 0, 500, 500 );
        graphics.drawRect( -2000, dividingLine, 5000, separatorHeight);
        graphics.endFill();
    }

    public function quarterColor( fromColor: int, toColor: int ): int {
        var red: int = 3*(fromColor >> 18) + (toColor >> 18);
        var green: int = 3*((fromColor & 0xFF00) >> 10) + ((toColor & 0xFF00) >> 10);
        var blue: int = 3*((fromColor & 0xFF) >> 2) + ((toColor & 0xFF) >> 2);
        return (red << 16) + (green << 8) + blue;
    }

    public function set selectedTab( tab: Tab ): void {
        for each( var otherTab: Tab in tabs ) {
            if ( tab !== otherTab ) {
                otherTab.selected = false;
            }
        }
        tab.selected = true;
        if( _selectedTab != null ) {
            tabHolder.swapChildren( tab, _selectedTab );
        }
        _selectedTab = tab;
        for each( var listener: Function in listeners ) {
            listener();
        }
    }

    public function get selectedTab(): Tab {
        return _selectedTab;
    }

    public function addListener( listener: Function ): void {
        listeners.push( listener );
    }
}
}