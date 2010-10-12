package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.common.PhetTabNavigator;

public class BuoyancyTabNavigator extends PhetTabNavigator {
    public function BuoyancyTabNavigator() {
        super( new Array( new BuoyancyModule( "Intro", new BuoyancyCanvas() )
                , new BuoyancyModule( "Buoyancy Playground", new BuoyancyCanvas() )
                ) );
    }
}
}

import edu.colorado.phet.densityandbuoyancy.common.Module;
import edu.colorado.phet.densityandbuoyancy.view.BuoyancyCanvas;

class BuoyancyModule extends Module {
    private var canvas: BuoyancyCanvas;

    function BuoyancyModule( title: String, canvas: BuoyancyCanvas ) {
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