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
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.io.IOException;

/**
 * edu.colorado.phet.common.tests.ControlPanelTest
 * A test for working with the layout of the control panel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlPanelTest6 {
    public static void main( String[] args ) {

        JFrame frame = new JFrame( "test" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingTimerClock clock = new SwingTimerClock( 10, 25, AbstractClock.FRAMES_PER_SECOND );
        TestModule module = new TestModule( "A", clock );
        TestModule moduleB = new TestModule( "B", clock );
        TestModule moduleC = new TestModule( "C", clock );
        JPanel jp = new JPanel( new GridBagLayout() );


        // Create a TabbedApparatusPanelContainer and a ContentPanel
        Module[] modules = new Module[]{module, moduleB, moduleC};
        TestTabbedPane tabbedPane = new TestTabbedPane( modules );
        TestContentPanel contentPanel = new TestContentPanel( tabbedPane, module.getControlPanel(), null, null );
        tabbedPane.setContentPanel( contentPanel );
        frame.setContentPane( contentPanel );

        frame.pack();
        frame.setVisible( true );
    }

    static class TestTabbedPane extends JTabbedPane {
        int idx = -1;
        private Module[] modules;
        private TestContentPanel contentPanel;
//        private ContentPanel contentPanel;

        public TestTabbedPane( Module[] modules ) {
            this.modules = modules;
            for( int i = 0; i < modules.length; i++ ) {
                addTab( modules[i].getName(), modules[i].getApparatusPanel() );
            }
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    if( contentPanel == null ) {
                        throw new RuntimeException( "contentPanel not initialized" );
                    }
                    int idx = getSelectedIndex();
                    contentPanel.setControlPanel( TestTabbedPane.this.modules[idx].getControlPanel() );
                }
            } );
        }

        public void setContentPanel( TestContentPanel contentPanel ) {
//        public void setContentPanel( ContentPanel contentPanel ) {
            this.contentPanel = contentPanel;
            contentPanel.setControlPanel( TestTabbedPane.this.modules[0].getControlPanel() );
        }
    }


    static class TestContentPanel extends JPanel {
        private JComponent controlPanel;

        public TestContentPanel( JComponent tabbedPane, JComponent controlPanel, JComponent obj, JComponent appCtrlPanel ) {
            setLayout( new BorderLayout() );
            add( tabbedPane, BorderLayout.CENTER );
            add( controlPanel, BorderLayout.EAST );
        }

        public void setControlPanel( JComponent panel ) {
            if( controlPanel != null ) {
                remove( controlPanel );
            }
            controlPanel = panel;
            add( panel, BorderLayout.EAST );
        }

        public void setApparatusPanelContainer( JComponent panel ) {
            add( panel, BorderLayout.CENTER );
        }
    }


    static class TestModule extends Module {
        protected TestModule( String name, AbstractClock clock ) {
            super( name, clock );
            ApparatusPanel ap = new ApparatusPanel();
//            ApparatusPanel2 ap = new AP2( clock, name );
//            ApparatusPanel2 ap = new ApparatusPanel2( clock );
            ap.setBackground( Color.white );
            ap.setDisplayBorder( true );
            ap.setPreferredSize( new Dimension( 200, 200 ) );
            ap.addGraphic( new PhetShapeGraphic( ap, new Rectangle( 100, 100, 50, 50 ), Color.red ) );
            ap.addGraphic( new PhetImageGraphic( ap, "images/Phet-logo-48x48.gif" ) );
            PhetTextGraphic textGraphic = new PhetTextGraphic( ap, new Font( "Lucida-sans", Font.BOLD, 30 ), name, Color.blue );
            textGraphic.setLocation( 50, 50 );
            ap.addGraphic( textGraphic );

            setApparatusPanel( ap );

            setModel( new BaseModel() );


            JTextField textField = new JTextField( "012345678901234567890123456789" );
            JScrollPane scrollPane = new JScrollPane( textField );

            // Depending on which of the following we use, the resizing problem does or does not occur
            if( true ) {
                ControlPanel cp = new ControlPanel( this );
                cp.add( scrollPane );
                setControlPanel( cp );
            }
            else {
                JPanel testCp = new JPanel();
                testCp.add( scrollPane );
                setControlPanel( testCp );
            }
        }

        public boolean hasHelp() {
            return true;
        }
    }
}
