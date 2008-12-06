package edu.colorado.phet.testproject;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

class TestModule extends PiccoloModule {
    public TestModule( String s ) {
        super( s, new ConstantDtClock( 30, 1 ) );
        setSimulationPanel( new JButton() );
    }
}
