package edu.colorado.phet.flexcommon {

import flash.events.Event;

import mx.containers.TabNavigator;
import mx.controls.Button;
import mx.events.IndexChangedEvent;

/**
 * Displays tabs for a flex application
 * TODO: Would be nice to have an application-level class that supports N tabs, where N>=1
 */
public class PhetTabNavigator extends TabNavigator {
    private var modules: Array;

    /**
     * Construction
     * @param modules components must be type Module
     */
    public function PhetTabNavigator( modules: Array ) {
        super();
        this.modules = modules;
        setStyle( "fontSize", 20 );
        setStyle( "paddingTop", 10 );
        setStyle( "backgroundColor", 0xb4cdff );

        percentWidth = 100;
        percentHeight = 100;
        for each ( var module: Module in modules ) {
            addChild( new Tab( module.title, module.component ) );
        }
        addEventListener( IndexChangedEvent.CHANGE, function( e: Event ): void {
            trace( "selected: " + selectedIndex );
            runModule( selectedIndex );
            updateTabColors();
        } );
    }

    override protected function updateDisplayList( unscaledWidth: Number, unscaledHeight: Number ): void {
        super.updateDisplayList( unscaledWidth, unscaledHeight );
        updateTabColors();
    }

    public function updateTabColors(): void {
        for ( var idx: int = 0; idx < modules.length; idx++ ) {
            var tab: Button = getTabAt( idx );
            tab.setStyle( "fillAlphas", [1.0, 1.0] );
            if ( idx == selectedIndex ) {
                tab.setStyle( "color", 0x000000 );
                tab.setStyle( "fillColors", [0xb4cdff, 0xb4cdff] );
                tab.setStyle( "backgroundColor", 0xb4cdff );
            }
            else {
                tab.setStyle( "color", 0x282828 );
                tab.setStyle( "fillColors", [0x647dff, 0x647dff] );
                tab.setStyle( "backgroundColor", 0x647dff );
            }
        }
    }

    //Todo: could rewrite this so that only the changed modules are messaged
    public function runModule( moduleIndex: Number ): void {
        for ( var i: Number = 0; i < modules.length; i++ ) {
            modules[i].running = moduleIndex == i;
        }
    }

    public function onApplicationComplete(): void {
        runModule( 0 );
    }
}
}

import mx.containers.VBox;
import mx.core.UIComponent;

class Tab extends VBox {

    public function Tab( title: String, content: UIComponent ) {
        super();
        percentWidth = 100;
        label = title;
        setStyle( "fontSize", 18 );
        addChild( content );
    }
}
