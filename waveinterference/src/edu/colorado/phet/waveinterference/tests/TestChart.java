/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

/**
 * User: Sam Reid
 * Date: Apr 12, 2006
 * Time: 8:00:15 PM
 * Copyright (c) Apr 12, 2006 by Sam Reid
 */

public class TestChart extends TestTopView {
    private ChartGraphic chartGraphic;

    public TestChart() {
        super( "Test Chart" );
        getWaveModelGraphic().setCellDimensions( 4, 4 );
        getWaveModelGraphic().setOffset( 100, 10 );
        chartGraphic = new ChartGraphic( "Displacement", getWaveModelGraphic().getLatticeScreenCoordinates(), getWaveModel() );
        getPhetPCanvas().getLayer().addChild( chartGraphic );
    }

    protected void step() {
        super.step();
        chartGraphic.updateChart();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestChart() );
    }
}
