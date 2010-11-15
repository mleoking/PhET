package edu.colorado.phet.collisionlab {
import flash.display.DisplayObjectContainer;

public class AdvancedModule {
    var myModel: Model;
    var myMainView: MainView;

    public function AdvancedModule() {
        myModel = new Model();
        myMainView = new MainView( myModel, 950, 700 );
    }

    public function attach( parent: DisplayObjectContainer ): void {
        parent.addChild( myMainView );
        myMainView.initialize();
    }
}
}