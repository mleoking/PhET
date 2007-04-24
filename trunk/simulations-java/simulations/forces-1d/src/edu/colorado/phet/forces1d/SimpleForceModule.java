//
//package edu.colorado.phet.forces1d;
//
//import edu.colorado.phet.common.model.clock.AbstractClock;
//
//import java.io.IOException;
//
///**
// * User: Sam Reid
// * Date: Nov 12, 2004
// * Time: 10:06:43 PM
// *
// */
//public class SimpleForceModule extends Force1DModule {
//    private SimpleControlPanel forceControlPanel;
//
//    public SimpleForceModule( AbstractClock clock,PhetLo ) throws IOException {
//        super( clock, "Forces in 1-D" );
//
//        forceControlPanel = new SimpleControlPanel( this );
//        setControlPanel( forceControlPanel );
//        setFrictionEnabled( true );
//    }
//
//    protected void updateGraphics() {
//        forcePanel.updateGraphics();
//        forceControlPanel.updateGraphics();
//    }
//
//    public void reset() {
//        super.reset();
//        forceControlPanel.reset();
//    }
//
//    public void updateControlPanelGraphics() {
//        forceControlPanel.updateGraphics();
//    }
//
//    public void handleControlPanelInputs() {
//        forceControlPanel.handleUserInput();
//    }
//
//}
