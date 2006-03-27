/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.view.MeasurementControlPanel;
import edu.colorado.phet.waveinterference.view.MeasurementToolSet;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 11:13:47 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class TestMeasurementTools extends TestTopView {
    public TestMeasurementTools() {
        super( "Test Stopwatch Panel" );
        MeasurementToolSet measurementToolSet = new MeasurementToolSet( getPhetPCanvas(), getClock() );
        MeasurementControlPanel measurementControlPanel = new MeasurementControlPanel( measurementToolSet );
        getPhetPCanvas().addScreenChild( measurementToolSet );
        getControlPanel().addControl( measurementControlPanel );
    }

    public static void main( String[] args ) {
        ModuleApplication.startApplication( args, new TestMeasurementTools() );
    }
}
