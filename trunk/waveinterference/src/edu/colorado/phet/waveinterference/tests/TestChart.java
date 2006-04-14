/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.view.CrossSectionGraphic;
import edu.colorado.phet.waveinterference.view.IndexColorMap;
import edu.colorado.phet.waveinterference.view.MutableColor;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 12, 2006
 * Time: 8:00:15 PM
 * Copyright (c) Apr 12, 2006 by Sam Reid
 */

public class TestChart extends TestTopView {
    private WaveChartGraphic waveChartGraphic;

    public TestChart() {
        super( "Test Chart" );
        getWaveModelGraphic().setCellDimensions( 4, 4 );
        getWaveModelGraphic().setOffset( 100, 10 );

        final MutableColor waterColor = new MutableColor( new Color( 130, 185, 255 ) );
        getWaveModelGraphic().setColorMap( new IndexColorMap( getLattice(), waterColor ) );
        waveChartGraphic = new WaveChartGraphic( "Displacement", getWaveModelGraphic().getLatticeScreenCoordinates(), getWaveModel(), waterColor );
        getPhetPCanvas().getLayer().addChild( waveChartGraphic );

        final CrossSectionGraphic crossSectionGraphic = new CrossSectionGraphic( getWaveModel(), getLatticeScreenCoordinates() );
        getPhetPCanvas().addScreenChild( crossSectionGraphic );

        MutableColorChooser mutableColorChooser = new MutableColorChooser( waterColor );
        getControlPanel().addControlFullWidth( mutableColorChooser );
    }

    protected void step() {
        super.step();
        waveChartGraphic.updateChart();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestChart() );
    }
}
