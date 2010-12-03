package edu.colorado.phet.flexcommon {

import mx.containers.TabNavigator;
import mx.events.IndexChangedEvent;

/**
 * Displays tabs for a flex application
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
        percentWidth = 100;
        percentHeight = 100;
        for each ( var module: Module in modules ) {
            addChild( new Tab( module.title, module.component ) );
        }
        addEventListener( IndexChangedEvent.CHANGE, function(): void {
            trace( "selected: " + selectedIndex );
            runModule( selectedIndex );
        } );
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
