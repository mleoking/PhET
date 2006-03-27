/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.DetectorSetControlPanel;
import edu.colorado.phet.waveinterference.view.IndexColorMap;
import edu.colorado.phet.waveinterference.view.IntensityReaderSet;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:04:03 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */
public class TestStripChartModule extends BasicWaveTestModule {
    private WaveModelGraphic waveModelGraphic;
    private IntensityReaderSet intensityReaderSet;

    public TestStripChartModule() {
        super( "Strip Chart" );

        waveModelGraphic = new WaveModelGraphic( getWaveModel(), 10, 10, new IndexColorMap( super.getLattice() ) );
        super.getPhetPCanvas().addScreenChild( waveModelGraphic );

        final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, waveModelGraphic.getCellDimensions().width );
        cellDim.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int dim = (int)cellDim.getValue();
                waveModelGraphic.setCellDimensions( dim, dim );
            }
        } );
        intensityReaderSet = new IntensityReaderSet();
        getPhetPCanvas().addScreenChild( intensityReaderSet );
        DetectorSetControlPanel detectorSetControlPanel = new DetectorSetControlPanel( intensityReaderSet, getPhetPCanvas(), getWaveModel(), waveModelGraphic.getLatticeScreenCoordinates() );

        getControlPanel().addControl( cellDim );
        getControlPanel().addControl( detectorSetControlPanel );
        detectorSetControlPanel.addIntensityReader();
    }


    protected void step() {
        super.step();
        waveModelGraphic.update();
        intensityReaderSet.update();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestStripChartModule() );
    }
}
