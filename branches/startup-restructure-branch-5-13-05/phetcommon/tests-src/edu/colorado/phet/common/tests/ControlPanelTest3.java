package edu.colorado.phet.common.tests;

/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.*;
import edu.colorado.phet.common.view.help.HelpPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * edu.colorado.phet.common.tests.ControlPanelTest
 * A test for working with the layout of the control panel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlPanelTest3 {

    /**
     * This test builds a normal JFrame with a ContentPanel in it, to eliminate the influences of PhetFrame,
     * ModuleManager, and PhetApplication.
     * <p/>
     * An apparatus panel and ClockControl panel can be added, too, or left out.
     * <p/>
     * Results: The ControlPanel resizes on the second resizing.
     *
     * NOTE!!!! It looks like the gridweights are a bit broken. A gridweight of 0 causes screwy behavior
     * with the JScrollPane. Things work right if it is a very small number, instead. If gridweightx=0, for
     * the controlPanelGbc in TestControlPanel, for example, this test shows odd behavior when you resize
     * vertically.
     *
     * @param args
     */
    public static void main( String[] args ) {

        JFrame frame = new JFrame( "test" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        JTextField textField = new JTextField( "012345678901234567890123456789" );
        JPanel jp = new JPanel();
        jp.add( textField );
        TestControlPanel controlPanel = new TestControlPanel( jp );

        JComponent apparatusPanelContainer = new JPanel();
        JPanel contentPanel = new TestContentPanel( apparatusPanelContainer, controlPanel);

        frame.setContentPane( contentPanel );

        frame.pack();
        frame.setVisible( true );
    }



    static class TestContentPanel extends JPanel {
        private GridBagConstraints apparatusPanelGbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                                               GridBagConstraints.WEST,
                                                                               GridBagConstraints.BOTH,
                                                                               new Insets( 0, 0, 0, 0 ), 0, 0 );
        // NOTE: if 0 is used for gridweightx, below, JScrollPane in the TestControlPanel that is
        // added using this GridPagConstraints acts whacky when you resize the frame vertically.
//        private GridBagConstraints controlPanelGbc = new GridBagConstraints(  1, 0, 1, 2, 0, 1,
//        private GridBagConstraints controlPanelGbc = new GridBagConstraints(  1, 0, 1, 1, 1, 1,
        private GridBagConstraints controlPanelGbc = new GridBagConstraints(  1, 0, 1, 2, 0.0001, 1,
                                                                             GridBagConstraints.NORTHEAST,
                                                                             GridBagConstraints.BOTH,
                                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );

        public TestContentPanel( JComponent apparatusPanelContainer, JComponent controlPanel ) {
            super( new GridBagLayout() );

            add( apparatusPanelContainer, apparatusPanelGbc );
            add( controlPanel, controlPanelGbc );
        }
    }


    static class TestControlPanel extends JPanel {
        public TestControlPanel( JPanel controlPane ) {
            super( new GridBagLayout() );

            this.setLayout( new GridBagLayout() );
            GridBagConstraints controlsGbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                                     GridBagConstraints.NORTH,
                                                                     GridBagConstraints.BOTH,
                                                                     new Insets( 0, 0, 0, 0 ), 0, 0 );

            // The panel where the simulation-specific controls go
            JScrollPane scrollPane = new JScrollPane( controlPane,
                                                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
            scrollPane.setBorder( null );
            add( controlPane, controlsGbc );
//            add( scrollPane, controlsGbc );
        }
    }
}

