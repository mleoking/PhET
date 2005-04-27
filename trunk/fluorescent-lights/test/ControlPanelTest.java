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
import java.awt.*;

/**
 * ControlPanelTest
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlPanelTest {
    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        Container contentPane = frame.getContentPane();
        ControlPanel cp = new ControlPanel( new TestModule( "", null) );
        Component component = cp.add( new JButton( "ASDFASDF"));
        cp.add( new JTextField( "QWERQWERQWER") );
        frame.getContentPane().add( cp );

//        JPanel jp = new JPanel( new GridLayout( 2,1));
//        jp.setLayout( new GridBagLayout() );
//        GridBagConstraints gbc = new GridBagConstraints( 0,GridBagConstraints.RELATIVE,
//                                                         1,1,0,0,
//                                                         GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0,0,0,0),0,0);
//        JScrollPane sp = new JScrollPane( jp );
//        jp.add( component, gbc );
//        jp.add( new JTextField( "QWERQWERQWER"), gbc);
//        contentPane.add( sp );


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
