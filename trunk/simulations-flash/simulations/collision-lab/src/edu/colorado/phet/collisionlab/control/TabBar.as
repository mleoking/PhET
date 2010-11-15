package edu.colorado.phet.collisionlab.control {
import flash.display.Sprite;

public class TabBar extends Sprite {
    private const tabHolder: Sprite = new Sprite();
    private var tabs: Array = new Array();
    public var selectedColor: int;
    public var disabledColor: int;
    private var listeners: Array = new Array();
    private var _selectedTab: Tab = null;

    public function TabBar( selectedColor: int, disabledColor: int ) {
        this.selectedColor = selectedColor;
        this.disabledColor = disabledColor;
        addChild( tabHolder );
    }

    public function addTab( tab: Tab ): void {
        var x: Number = tabHolder.width;
        tabHolder.addChild( tab );
        tab.x = x;
        tabs.push( tab );
        drawBackground();
    }

    public function drawBackground(): void {
        graphics.clear();
        //graphics.lineStyle( 1, 0x000000 );
        graphics.beginFill( disabledColor );
        graphics.drawRect( tabHolder.width, 0, 950, tabs[0].getCalculatedHeight() ); // TODO: improve height calculation, make width based on stage. null ref!
    }

    public function set selectedTab( tab: Tab ): void {
        for each( var otherTab: Tab in tabs ) {
            if ( tab !== otherTab ) {
                otherTab.selected = false;
            }
        }
        tab.selected = true;
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