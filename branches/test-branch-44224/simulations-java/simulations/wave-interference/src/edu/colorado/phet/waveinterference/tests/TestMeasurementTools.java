/*  */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.view.MeasurementControlPanel;
import edu.colorado.phet.waveinterference.view.MeasurementToolSet;
import edu.colorado.phet.waveinterference.ModuleApplication;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 11:13:47 PM
 */

public class TestMeasurementTools extends TestTopView {
    public TestMeasurementTools() {
        super( "Test Stopwatch Panel" );
        MeasurementToolSet measurementToolSet = new MeasurementToolSet( getPhetPCanvas(), getClock(), getLatticeScreenCoordinates(), "meters", 10, 10, "seconds", 1 );
        MeasurementControlPanel measurementControlPanel = new MeasurementControlPanel( measurementToolSet );
        getPhetPCanvas().addScreenChild( measurementToolSet );
        getControlPanel().addControl( measurementControlPanel );
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestMeasurementTools() );
    }
}
