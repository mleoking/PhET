/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.waveinterference.view.IndexColorMap;
import edu.colorado.phet.waveinterference.view.SimpleLatticeGraphic;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:06:44 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */
public class TestTopView extends BasicWaveTestModule {
    private SimpleLatticeGraphic simpleLatticeGraphic;
    private BasicWaveTestControlPanel controlPanel;

    protected TestTopView() {
        this( "Test Top View" );
    }

    protected TestTopView( String name ) {
        super( name );

        simpleLatticeGraphic = new SimpleLatticeGraphic( super.getLattice(), 10, 10, new IndexColorMap( super.getLattice() ) );
        super.getPhetPCanvas().addScreenChild( simpleLatticeGraphic );

        controlPanel = new BasicWaveTestControlPanel( this );
        final ModelSlider cellDim = new ModelSlider( "Cell Dimension", "pixels", 1, 50, simpleLatticeGraphic.getCellDimensions().width );
        cellDim.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int dim = (int)cellDim.getValue();
                simpleLatticeGraphic.setCellDimensions( dim, dim );
            }
        } );
        controlPanel.addControl( cellDim );
        setControlPanel( controlPanel );
    }

    public SimpleLatticeGraphic getSimpleLatticeGraphic() {
        return simpleLatticeGraphic;
    }

    protected void step() {
        super.step();
        simpleLatticeGraphic.update();
    }

    public static void main( String[] args ) {
        PhetApplication phetApplication = new PhetApplication( args, "Test Top View", "", "" );
        phetApplication.addModule( new TestTopView() );
        phetApplication.startApplication();
    }
}
