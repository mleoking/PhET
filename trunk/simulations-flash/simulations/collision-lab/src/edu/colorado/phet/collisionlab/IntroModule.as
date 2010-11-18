package edu.colorado.phet.collisionlab {
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.view.IntroView;
import edu.colorado.phet.collisionlab.view.MainView;

import flash.display.DisplayObjectContainer;

public class IntroModule extends CollisionLabModule {
    var myMainView: MainView;

    public function IntroModule() {
        myMainView = new IntroView( myModel, this, 950, 700 );
    }

    override public function resetAll(): void {
        myMainView.controlPanel.switchToOneDimension();
        myMainView.controlPanel.setBorderExists( false );
        myMainView.controlPanel.showCM( false );
        myMainView.controlPanel.sub_showCM_cb.selected = false;
    }

    override public function attach( parent: DisplayObjectContainer ): void {
        parent.addChild( myMainView );
        myMainView.initialize();

        myMainView.controlPanel.switchToOneDimension();
        myMainView.controlPanel.setBorderExists( false );
        myMainView.controlPanel.showCM( false );
        myMainView.controlPanel.sub_showCM_cb.selected = false; // for some reason, it has to be here instead of other places
    }

    override public function allowAddRemoveBalls(): Boolean {
        return false;
    }

    override public function createModel(): Model {
        return new Model( true );
    }
}
}