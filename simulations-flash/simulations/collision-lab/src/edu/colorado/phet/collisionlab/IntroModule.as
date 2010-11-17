package edu.colorado.phet.collisionlab {
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.view.IntroView;
import edu.colorado.phet.collisionlab.view.MainView;

import flash.display.DisplayObjectContainer;

public class IntroModule {
    var myModel: Model;
    var myMainView: MainView;

    public function IntroModule() {
        myModel = new Model();
        myMainView = new IntroView( myModel, 950, 700 );
    }

    public function attach( parent: DisplayObjectContainer ): void {
        parent.addChild( myMainView );
        myMainView.initialize();
    }
}
}