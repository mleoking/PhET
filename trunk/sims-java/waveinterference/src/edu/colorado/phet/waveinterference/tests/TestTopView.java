/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.IndexColorMap;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:06:44 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */
public class TestTopView extends BasicWaveTestModule {
    private WaveModelGraphic waveModelGraphic;

    protected TestTopView() {
        this( "Top View" );
    }

    protected TestTopView( String name ) {
        super( name );

        waveModelGraphic = new WaveModelGraphic( getWaveModel(), 10, 10, new IndexColorMap( super.getLattice() ) );
        super.getPhetPCanvas().addScreenChild( waveModelGraphic );

        final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, waveModelGraphic.getCellDimensions().width );
        cellDim.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int dim = (int)cellDim.getValue();
                waveModelGraphic.setCellDimensions( dim, dim );
            }
        } );
        getControlPanel().addControl( cellDim );
    }

    public WaveModelGraphic getWaveModelGraphic() {
        return waveModelGraphic;
    }

    protected void step() {
        super.step();
        waveModelGraphic.update();
    }

    protected LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return getWaveModelGraphic().getLatticeScreenCoordinates();
    }

    public static void main( String[] args ) {
        PhetApplication phetApplication = new PhetApplication( args, "Test Top View", "", "" );
        phetApplication.addModule( new TestTopView() );
        phetApplication.startApplication();
    }
}
