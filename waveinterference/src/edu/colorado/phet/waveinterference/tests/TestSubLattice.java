/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.waveinterference.model.SubLattice2D;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 13, 2006
 * Time: 11:23:11 PM
 * Copyright (c) Apr 13, 2006 by Sam Reid
 */

public class TestSubLattice extends Module {
    private WaveModel waveModel;
    private WaveModelGraphic waveModelGraphic;

    public TestSubLattice() {
        super( "Test Sub Lattice", new SwingClock( 30, 1 ) );
        PhetPCanvas panel = new PhetPCanvas();
        setSimulationPanel( panel );
        setModel( new BaseModel() );
        setControlPanel( new ControlPanel() );

        waveModel = new WaveModel( 50, 50 );
        waveModel.setSourceValue( 10, 10, 1 );
//        waveModelGraphic = new WaveModelGraphic( waveModel, 5,5 );
        waveModelGraphic = new WaveModelGraphic( new WaveModel( new SubLattice2D( waveModel.getLattice(), new Rectangle( 0, 0, 10, 10 ) ), 20, 20 ), 5, 5 );
        panel.addScreenChild( waveModelGraphic );
    }

    protected void handleClockTick( ClockEvent event ) {
        super.handleClockTick( event );
        waveModel.propagate();
        waveModelGraphic.update();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestSubLattice() );
    }
}
