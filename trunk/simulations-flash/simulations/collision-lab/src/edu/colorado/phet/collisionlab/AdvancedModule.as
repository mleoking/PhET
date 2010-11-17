package edu.colorado.phet.collisionlab {
import edu.colorado.phet.collisionlab.view.AdvancedView;
import edu.colorado.phet.collisionlab.view.MainView;

import flash.display.DisplayObjectContainer;

public class AdvancedModule extends CollisionLabModule {
    var myMainView: MainView;

    public function AdvancedModule() {
        myMainView = new AdvancedView( myModel, this, 950, 700 );
    }

    override public function resetAll(): void {

    }

    override public function attach( parent: DisplayObjectContainer ): void {
        parent.addChild( myMainView );
        myMainView.initialize();
    }
}
}