
package edu.colorado.phet.waveinterference.tests;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetTestApplication;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.waveinterference.view.IntensityReader;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDebug;

// for debugging #1404
public class TestDetectors extends Module {
    
    private static final boolean BUFFERED = true;  // bug occurs when BUFFERED == true
    
    private PhetPCanvas canvas;
    private Point2D nextDetectorLocation;

    public TestDetectors() {
        super( "Test Detectors", new SwingClock( 30, 1 ) );
        
        // play area
        canvas = ( BUFFERED ? new BufferedPhetPCanvas() : new PhetPCanvas() );
        
        setSimulationPanel( canvas );
        
        // control panel
        setControlPanel( new ControlPanel() );
        JButton addDetector = new JButton( "Add Detector" );
        addDetector.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                addDetector();
            }
        } );
        getControlPanel().addControl( addDetector );
        
        // add 1 detector
        nextDetectorLocation = new Point2D.Double( 300, 100 );
        addDetector();
    }

    private void addDetector() {
        PNode intensityReader = new DoNothingIntensityReader( getClock() );
        intensityReader.setOffset( nextDetectorLocation.getX(), nextDetectorLocation.getY() );
        canvas.addScreenChild( intensityReader );
        nextDetectorLocation.setLocation( nextDetectorLocation.getX() + 50, nextDetectorLocation.getY() + 50 );
    }
    
    private static class DoNothingIntensityReader extends IntensityReader {
        public DoNothingIntensityReader( IClock clock ) {
            super( "Do Nothing", null, null, clock );
        }
        // override so that we can pass null args to superclass constructor
        public void update() {}
    }

    public static void main( String[] args ) {
        PDebug.debugRegionManagement = true;
        PhetTestApplication phetApplication = new PhetTestApplication( args );
        phetApplication.addModule( new TestDetectors() );
        phetApplication.startApplication();
    }
}
