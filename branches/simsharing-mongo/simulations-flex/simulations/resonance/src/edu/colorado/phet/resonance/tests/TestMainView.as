//MainView contains all views, acts as mediator, communication hub for views
package edu.colorado.phet.resonance.tests {
import mx.containers.Canvas;
import mx.controls.Button;

public class TestMainView extends Canvas {
    public function TestMainView( stageW: Number, stageH: Number ) {
        var button: Button = new Button();
        button.label = "Reset All";
        addChild( button );

        //this.myControlPanel.right = 10;    //does not work, "right" is a style property
        //this.myControlPanel.setStyle("right", 10);    //this works, but forces the control panel on the far right

        var myControlPanel: TestControlPanel = new TestControlPanel( this )
        myControlPanel.x = 0.8 * stageW; //- 3 * this.myControlPanel.width;
        myControlPanel.y = 0.1 * stageH;

        this.addChild( myControlPanel );
    }//end of constructor

}//end of class
}//end of package