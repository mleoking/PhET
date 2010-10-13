package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.flexcommon.PhetTabNavigator;

public class BuoyancyTabNavigator extends PhetTabNavigator {
    public function BuoyancyTabNavigator() {
        super( new Array( new BuoyancyModule( "Intro", new BuoyancyIntroContainer() )
                , new BuoyancyModule( "Buoyancy Playground", new BuoyancyPlaygroundContainer() )
                ) );
    }
}
}

import edu.colorado.phet.densityandbuoyancy.view.BuoyancyContainer;
import edu.colorado.phet.flexcommon.Module;

class BuoyancyModule extends Module {
    private var container: BuoyancyContainer;

    function BuoyancyModule( title: String, canvas: BuoyancyContainer ) {
        super( title, canvas );
        this.container = canvas;
    }

    override public function init(): void {
        super.init();
        container.init();
    }

    override public function get running(): Boolean {
        return container.running;
    }

    override public function set running( r: Boolean ): void {
        container.running = r;
    }
}