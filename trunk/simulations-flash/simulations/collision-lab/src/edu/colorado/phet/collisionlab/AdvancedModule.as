package edu.colorado.phet.collisionlab {
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.view.AdvancedView;
import edu.colorado.phet.collisionlab.view.MainView;

import flash.display.DisplayObjectContainer;

public class AdvancedModule {
    var myModel: Model;
    var myMainView: MainView;

    public function AdvancedModule() {
        myModel = new Model();
        myMainView = new AdvancedView( myModel, 950, 700 );
    }

    public function attach( parent: DisplayObjectContainer ): void {
        parent.addChild( myMainView );
        myMainView.initialize();
    }
}
}