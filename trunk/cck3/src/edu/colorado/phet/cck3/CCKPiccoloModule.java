package edu.colorado.phet.cck3;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.SwingClock;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 2:47:24 AM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class CCKPiccoloModule extends Module {
    private String[] args;

    public CCKPiccoloModule( String[] args ) {
        super( "CCK-Piccolo", new SwingClock( 30, 1 ) );
        this.args = args;
        setModel( new BaseModel() );
        setSimulationPanel( new JLabel( "Simulation panel" ) );
    }
}
