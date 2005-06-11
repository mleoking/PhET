/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:48:21 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerModule extends Module {
    public SchrodingerApparatusPanel schrodingerApparatusPanel;
    public DiscreteModel discreteModel;

    /**
     * @param clock
     */
    public SchrodingerModule( AbstractClock clock ) {
        super( "Schrodinger Module", clock );
        schrodingerApparatusPanel = new SchrodingerApparatusPanel();
        setApparatusPanel( schrodingerApparatusPanel );
        setModel( new BaseModel() );
        SchrodingerControlPanel schrodingerControlPanel = new SchrodingerControlPanel( this );
        setControlPanel( schrodingerControlPanel );
        discreteModel = new DiscreteModel();
    }

    public SchrodingerApparatusPanel getSchrodingerApparatusPanel() {
        return schrodingerApparatusPanel;
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
        schrodingerApparatusPanel.reset();
    }
}
