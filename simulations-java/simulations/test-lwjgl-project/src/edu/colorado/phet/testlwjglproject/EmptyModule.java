// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject;

import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

public class EmptyModule extends Module {
    public EmptyModule( String name ) {
        super( name, new ConstantDtClock() );
        setSimulationPanel( new JPanel() );
    }
}
