/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.InitialWavefunction;
import edu.colorado.phet.qm.view.SchrodingerPanel;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:48:21 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerModule extends Module {
    public SchrodingerPanel schrodingerPanel;
    public DiscreteModel discreteModel;

    /**
     * @param clock
     */
    public SchrodingerModule( AbstractClock clock ) {
        super( "Schrodinger Module", clock );

        setModel( new BaseModel() );

        discreteModel = new DiscreteModel( 100, 100 );
        addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                discreteModel.stepInTime( dt );
            }
        } );
//        discreteModel.addListener( new ScreenUpdate( discreteModel ) );
        schrodingerPanel = new SchrodingerPanel( this );
        setApparatusPanel( schrodingerPanel );

        SchrodingerControlPanel schrodingerControlPanel = new SchrodingerControlPanel( this );
        setControlPanel( schrodingerControlPanel );
    }

    public SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }

    public DiscreteModel getDiscreteModel() {
        return discreteModel;
    }

    public static void main( String[] args ) {
        AbstractClock clock = new SwingTimerClock( 1, 30 );
        PhetApplication phetApplication = new PhetApplication( args, "Schrodinger Equation", "Schrodinger Equation", "v0r0", clock, true, new FrameSetup.CenteredWithSize( 800, 800 ) );
        SchrodingerModule module = new SchrodingerModule( clock );
        phetApplication.setModules( new Module[]{module} );
        phetApplication.startApplication();
    }

    public void reset() {
        discreteModel.reset();
        schrodingerPanel.reset();
    }

    public void fireParticle( InitialWavefunction initialWavefunction ) {
        discreteModel.fireParticle( initialWavefunction );
        schrodingerPanel.updateGraphics();
    }

    public void setGridSpacing( final int nx, final int ny ) {
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
//                System.out.println( "SchrodingerModule.stepInTime" );
                discreteModel.setGridSpacing( nx, ny );
                getModel().removeModelElement( this );
//                System.out.println( "/SchrodingerModule.stepInTime" );
            }
        } );
//        discreteModel.setGridSpacing(nx,ny);
    }
}
