package edu.colorado.phet.resonance {
import mx.core.UIComponent;

//import edu.colorado.phet.flashcommon.SimStrings;

public class Resonance extends UIComponent {
    var myModel: ShakerModel;
    var myMainView: MainView;
    var stageW: Number;
    var stageH: Number;

    public function Resonance() {
        //SimStrings.init(loaderInfo);
        myModel = new ShakerModel( 10 );  //argument is max number of resonators
        //stage width and height may need to be hard-coded
        this.stageW = 1024;
        this.stageH = 768;
        myMainView = new MainView( myModel, this.stageW, this.stageH );
        this.addChild( myMainView );
        //this.myMainView.initialize();
    }//end of constructor
}//end of class
}//end of package