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
public class ControlPanelTest5 {

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

        SwingTimerClock clock = new SwingTimerClock( 10, 25, AbstractClock.FRAMES_PER_SECOND );
        Module module = new TestModule( "Test", clock );

        JComponent apparatusPanelContainer = new JPanel();
        JPanel contentPanel = new TestContentPanel( module.getApparatusPanel(), controlPanel);

        frame.setContentPane( contentPanel );

        frame.pack();
        frame.setVisible( true );

    }


    static class TestContentPanel extends JPanel {
        private GridBagConstraints appCtrlGbc= new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                                               GridBagConstraints.WEST,
                                                                               GridBagConstraints.BOTH,
                                                                               new Insets( 0, 0, 0, 0 ), 0, 0 );

        public TestContentPanel( JComponent apparatusPanelContainer, JComponent controlPanel ) {
            super( new GridBagLayout() );

            JPanel appPane = new JPanel( );
            appPane.add( apparatusPanelContainer );
            JComponent ctrlPane = new JScrollPane( controlPanel );
            JSplitPane appCtrlPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, appPane, ctrlPane );
            add( appCtrlPane, appCtrlGbc );
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
            add( scrollPane, controlsGbc );
        }
    }

    static class TestModule extends Module {
        protected TestModule( String name, AbstractClock clock ) {
            super( name, clock );
            ApparatusPanel ap = new ApparatusPanel();
//            ApparatusPanel2 ap = new ApparatusPanel2( clock );
            ap.setBackground( Color.white );
            ap.setDisplayBorder( true );
            ap.setPreferredSize( new Dimension( 200, 200 ) );
            ap.addGraphic( new PhetShapeGraphic( ap, new Rectangle( 100, 100, 50, 50 ), Color.red ) );
            ap.addGraphic( new PhetImageGraphic( ap, "images/Phet-logo-48x48.gif" ) );
            PhetTextGraphic textGraphic = new PhetTextGraphic( ap, new Font( "Lucida-sans", Font.BOLD, 30 ), name, Color.blue);
            textGraphic.setLocation( 50, 50 );
            ap.addGraphic( textGraphic );

            setApparatusPanel( ap );

            setModel( new BaseModel() );

            ControlPanel cp = new ControlPanel( this );
            JTextField textField = new JTextField( "012345678901234567890123456789" );

            cp.add( textField );

//            TestPanel tp = new TestPanel();
//            tp.add( textField );

//            setControlPanel( tp );
            setControlPanel( cp );
        }

        public boolean hasHelp() {
            return true;
        }
    }
}

