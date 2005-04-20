/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;

import javax.swing.*;

/**
 * ControlPanelTest
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlPanelTest {
    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        ControlPanel cp = new ControlPanel( new TestModule( "", null) );


        cp.add( new JButton( "ASDFASDF"));



        frame.getContentPane().add( cp );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }


    static class TestModule extends Module {
        protected TestModule( String name, AbstractClock clock ) {
            super( name, clock );
        }

        public boolean hasHelp() {
            return true;
        }
    }
}
