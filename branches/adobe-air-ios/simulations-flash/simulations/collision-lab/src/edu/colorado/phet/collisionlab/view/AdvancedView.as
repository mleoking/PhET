//MainView contains all views, acts as mediator, communication hub for views
package edu.colorado.phet.collisionlab.view {
import edu.colorado.phet.collisionlab.AdvancedModule;
import edu.colorado.phet.collisionlab.control.AdvancedControlPanel;
import edu.colorado.phet.collisionlab.control.ControlPanel;
import edu.colorado.phet.collisionlab.model.Model;

public class AdvancedView extends MainView {
    public function AdvancedView( myModel: Model, module: AdvancedModule, stageW: Number, stageH: Number ) {
        super( myModel, module, stageW, stageH );
    }

    override public function createControlPanel( myModel: Model, myMainView: MainView ): ControlPanel {
        return new AdvancedControlPanel( myModel, myMainView );
    }
}
}