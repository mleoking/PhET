package edu.colorado.phet.collisionlab {
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.view.IntroView;
import edu.colorado.phet.collisionlab.view.MainView;

import flash.display.DisplayObjectContainer;

public class IntroModule extends CollisionLabModule {
    var myModel: Model;
    var myMainView: MainView;

    public function IntroModule() {
        myModel = new Model();
        myMainView = new IntroView( myModel, this, 950, 700 );
    }

    override public function resetAll(): void {
        myMainView.controlPanel.switchToOneDimension();
    }

    override public function attach( parent: DisplayObjectContainer ): void {
        parent.addChild( myMainView );
        myMainView.initialize();

        myMainView.controlPanel.switchToOneDimension();
    }
}
}