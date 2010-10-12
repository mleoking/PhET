package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.flexcommon.PhetTabNavigator;

public class BuoyancyTabNavigator extends PhetTabNavigator {
    public function BuoyancyTabNavigator() {
        super( new Array( new BuoyancyModule( "Intro", new BuoyancyContainer() )
                , new BuoyancyModule( "Buoyancy Playground", new BuoyancyContainer() )
                ) );
    }
}
}

import edu.colorado.phet.densityandbuoyancy.view.BuoyancyContainer;
import edu.colorado.phet.flexcommon.Module;

class BuoyancyModule extends Module {
    private var canvas: BuoyancyContainer;

    function BuoyancyModule( title: String, canvas: BuoyancyContainer ) {
        super( title, canvas );
        this.canvas = canvas;
    }

    override public function init(): void {
        super.init();
        canvas.onApplicationComplete();
    }

    override public function get running(): Boolean {
        return canvas.running;
    }

    override public function set running( r: Boolean ): void {
        canvas.running = r;
    }
}