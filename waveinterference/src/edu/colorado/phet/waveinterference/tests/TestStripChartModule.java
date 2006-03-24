/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.IndexColorMap;
import edu.colorado.phet.waveinterference.view.IntensityReaderDecorator;
import edu.colorado.phet.waveinterference.view.SimpleLatticeGraphic;

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
public class TestStripChartModule extends BasicWaveTestModule {
    private SimpleLatticeGraphic simpleLatticeGraphic;
    private ArrayList intensityReaders = new ArrayList();

    public TestStripChartModule() {
        super( "Test Strip Chart" );

        simpleLatticeGraphic = new SimpleLatticeGraphic( super.getLattice(), 10, 10, new IndexColorMap( super.getLattice() ) );
        super.getPhetPCanvas().addScreenChild( simpleLatticeGraphic );

        BasicWaveTestControlPanel controlPanel = new BasicWaveTestControlPanel( this );
        final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, simpleLatticeGraphic.getCellDimensions().width );
        cellDim.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int dim = (int)cellDim.getValue();
                simpleLatticeGraphic.setCellDimensions( dim, dim );
            }
        } );

        JButton addDetector = new JButton( "Add Detector" );
        addDetector.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                addIntensityReader();
            }
        } );

        controlPanel.addControl( cellDim );
        controlPanel.addControl( addDetector );
        setControlPanel( controlPanel );
        addIntensityReader();
    }

    private void addIntensityReader() {
        final IntensityReaderDecorator intensityReader = new IntensityReaderDecorator( getPhetPCanvas(), getWaveModel(), simpleLatticeGraphic.getLatticeScreenCoordinates( getWaveModel() ) );
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
        simpleLatticeGraphic.update();
        for( int i = 0; i < intensityReaders.size(); i++ ) {
            IntensityReaderDecorator reader = (IntensityReaderDecorator)intensityReaders.get( i );
            reader.update();
        }
    }

    public static void main( String[] args ) {
        PhetApplication phetApplication = new PhetApplication( args, "Test Strip Chart", "", "" );
        phetApplication.addModule( new TestStripChartModule() );
        phetApplication.startApplication();
    }
}
