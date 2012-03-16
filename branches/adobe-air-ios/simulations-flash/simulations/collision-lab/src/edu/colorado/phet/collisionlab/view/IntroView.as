//MainView contains all views, acts as mediator, communication hub for views
package edu.colorado.phet.collisionlab.view {
import edu.colorado.phet.collisionlab.IntroModule;
import edu.colorado.phet.collisionlab.control.ControlPanel;
import edu.colorado.phet.collisionlab.control.IntroControlPanel;
import edu.colorado.phet.collisionlab.model.Model;

public class IntroView extends MainView {
    public function IntroView( myModel: Model, module: IntroModule, stageW: Number, stageH: Number ) {
        super( myModel, module, stageW, stageH );
    }

    override public function createControlPanel( myModel: Model, myMainView: MainView ): ControlPanel {
        return new IntroControlPanel( myModel, myMainView );
    }
}
}