/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.IndexColorMap;
import edu.colorado.phet.waveinterference.view.IntensityReaderDecorator;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:04:03 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */
public class TestStripChartModuleComponents extends BasicWaveTestModule {
    private WaveModelGraphic waveModelGraphic;
    private ArrayList intensityReaders = new ArrayList();

    public TestStripChartModuleComponents() {
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

        JButton addDetector = new JButton( "Add Detector" );
        addDetector.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                addIntensityReader();
            }
        } );

        getControlPanel().addControl( cellDim );
        getControlPanel().addControl( addDetector );
        addIntensityReader();
    }

    private void addIntensityReader() {
        final IntensityReaderDecorator intensityReader = new IntensityReaderDecorator( "Amplitude", getPhetPCanvas(), getWaveModel(), waveModelGraphic.getLatticeScreenCoordinates(), getClock() );
        intensityReader.addListener( new IntensityReaderDecorator.Listener() {
            public void deleted() {
                intensityReaders.remove( intensityReader );
                getPhetPCanvas().removeScreenChild( intensityReader );
            }
        } );
        intensityReader.setOffset( 300, 300 );
        getPhetPCanvas().addScreenChild( intensityReader );
        intensityReaders.add( intensityReader );
    }

    protected void step() {
        super.step();
        waveModelGraphic.update();
        for( int i = 0; i < intensityReaders.size(); i++ ) {
            IntensityReaderDecorator reader = (IntensityReaderDecorator)intensityReaders.get( i );
            reader.update();
        }
    }

    public static void main( String[] args ) {
        PhetApplication phetApplication = new PhetApplication( args, "Test Strip Chart", "", "" );
        phetApplication.addModule( new TestStripChartModuleComponents() );
        phetApplication.startApplication();
    }
}
