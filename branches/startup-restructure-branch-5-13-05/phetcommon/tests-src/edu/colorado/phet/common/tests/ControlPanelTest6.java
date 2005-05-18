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

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.net.URL;

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

        new TestControlPanel();

        // Create a TabbedApparatusPanelContainer and a ContentPanel
        Module[] modules = new Module[]{module, moduleB, moduleC};
        TestTabbedPane tabbedPane = new TestTabbedPane( modules );
        ITestContentPanel contentPanel = new TestContentPanel2( tabbedPane, module.getControlPanel(), null, null );
        tabbedPane.setContentPanel( contentPanel );
        frame.setContentPane( contentPanel );

        frame.pack();
        frame.setVisible( true );
    }

    static class TestTabbedPane extends JTabbedPane {
        int idx = -1;
        private Module[] modules;
        private ITestContentPanel contentPanel;
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
                    final int idx = getSelectedIndex();
                    contentPanel.setControlPanel( TestTabbedPane.this.modules[idx].getControlPanel() );
                }
            } );
        }

        public void setContentPanel( ITestContentPanel contentPanel ) {
//        public void setContentPanel( ContentPanel contentPanel ) {
            this.contentPanel = contentPanel;
            contentPanel.setControlPanel( TestTabbedPane.this.modules[0].getControlPanel() );
        }
    }


    static class TestContentPanel2 extends ITestContentPanel {
        private JComponent apparatusPanel;
        private JComponent controlPanel;
        private JComponent monitorPanel;
        private GridBagConstraints apparatusPanelGbc = new GridBagConstraints( 0, 1, 1, 1, 1, 1,
                                                                               GridBagConstraints.WEST,
                                                                               GridBagConstraints.BOTH,
                                                                               new Insets( 0, 0, 0, 0 ), 0, 0 );
        private GridBagConstraints controlPanelGbc = new GridBagConstraints( 1, 1, 1, 1, 0, 0,
                                                                             GridBagConstraints.NORTH,
                                                                             GridBagConstraints.NONE,
                                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );
        private GridBagConstraints clockControlPanelGbc = new GridBagConstraints( 0, 2, 1, 1, 1, 0,
                                                                                  GridBagConstraints.SOUTH,
                                                                                  GridBagConstraints.HORIZONTAL,
                                                                                  new Insets( 0, 0, 0, 0 ), 0, 0 );
        private GridBagConstraints monitorPanelGbc = new GridBagConstraints( 0, 1, 1, 1, 0, 0,
                                                                             GridBagConstraints.PAGE_END,
                                                                             GridBagConstraints.NONE,
                                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );

        public TestContentPanel2( JComponent apparatusPanelContainer, JComponent controlPanel,
                             JComponent monitorPanel, JComponent appControl ) {
//            super(apparatusPanelContainer, controlPanel, monitorPanel, appControl );
            setLayout( new GridBagLayout() );
            add( apparatusPanelContainer, apparatusPanelGbc );
//            setApparatusPanelContainer( apparatusPanelContainer );
            setMonitorPanel( monitorPanel );
        }

        private void setPanel( JComponent component, GridBagConstraints gridBagConstraints ) {
            if( component != null ) {
                add( component, gridBagConstraints );
            }
            revalidate();
            repaint();
        }

        public void setControlPanel( JComponent panel ) {
            if( panel != null ) {
//            appCtrlPane.setRightComponent( panel );
            }
            if( controlPanel != null ) {
                remove( controlPanel );
            }
            controlPanel = panel;
            setPanel( panel, controlPanelGbc );
//        add( panel, BorderLayout.EAST );
        }

        public void setMonitorPanel( JComponent panel ) {
            if( monitorPanel != null ) {
                remove( monitorPanel );
            }
            monitorPanel = panel;
            setPanel( panel, monitorPanelGbc );
        }

        public void setApparatusPanelContainer( JComponent panel ) {
            if( apparatusPanel != null ) {
                remove( apparatusPanel );
            }
            apparatusPanel = panel;
//        add( panel, BorderLayout.CENTER );
            setPanel( panel, apparatusPanelGbc );
        }

    }



    static class TestContentPanel extends ITestContentPanel {
        private JComponent controlPanel;

        private GridBagConstraints apparatusPanelGbc = new GridBagConstraints( 0, 1, 1, 1, 1, 1,
                                                                               GridBagConstraints.WEST,
                                                                               GridBagConstraints.BOTH,
                                                                               new Insets( 0, 0, 0, 0 ), 0, 0 );
        private GridBagConstraints controlPanelGbc = new GridBagConstraints( 1, 1, 1, 1, 0, 0,
                                                                             GridBagConstraints.NORTH,
                                                                             GridBagConstraints.NONE,
                                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );

        public TestContentPanel( JComponent tabbedPane, JComponent controlPanel, JComponent obj, JComponent appCtrlPanel ) {
            setLayout( new GridBagLayout() );
            add( tabbedPane, apparatusPanelGbc );
            add( controlPanel, controlPanelGbc );
        }

        public void setControlPanel( JComponent panel ) {
            if( controlPanel != null ) {
                remove( controlPanel );
            }
            controlPanel = panel;
            add( controlPanel, controlPanelGbc );
            revalidate();
            repaint();
        }
    }


    static class TestControlPanel extends JPanel {
        private GridBagConstraints controlsGbc;
        private JScrollPane scrollPane;
        private JPanel controlsPane;
        private GridBagConstraints controlsInternalGbc;

        public TestControlPanel() {
            this.setLayout( new GridBagLayout() );
            GridBagConstraints logoGbc = new GridBagConstraints( 0, 0, 1, 1, 0, 0,
                                                                 GridBagConstraints.NORTH,
                                                                 GridBagConstraints.NONE,
                                                                 new Insets( 0, 0, 0, 0 ), 0, 0 );
            controlsGbc = new GridBagConstraints( 0, 1, 1, 1, 0, 1,
                                                  GridBagConstraints.NORTH,
                                                  GridBagConstraints.BOTH,
//                                                         GridBagConstraints.NONE,
                                                  new Insets( 0, 0, 0, 0 ), 0, 0 );

            // Add the logo
            URL resource = getClass().getClassLoader().getResource( "images/Phet-Flatirons-logo-3-small.gif" );
            JComponent titleLabel = ( new JLabel( new ImageIcon( resource ) ) );
            add( titleLabel, logoGbc );

            controlsPane = new JPanel( new GridBagLayout() );
            controlsInternalGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                                                          GridBagConstraints.NORTH,
                                                          GridBagConstraints.NONE,
                                                          new Insets( 0, 0, 0, 0 ), 0, 0 );

            scrollPane = new JScrollPane( controlsPane );
            add( scrollPane, controlsGbc );

            System.out.println( "this.getSize( ) = " + this.getSize() );
            getLayout().layoutContainer( this );
            GridBagLayout gbl = ((GridBagLayout)getLayout());
            System.out.println( "gbl.minimumLayoutSize( ) = " + gbl.minimumLayoutSize( this ) );
            System.out.println( "gbl.maximumLayoutSize( ) = " + gbl.maximumLayoutSize( this ) );
            setSize( gbl.minimumLayoutSize( this ) );
            setSize( new Dimension( 500, 300) );
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    System.out.println( "this.getSize( ) = " + getSize() );

                }
            } );
//            add( titleLabel, BorderLayout.NORTH );
        }

        public Component add( Component comp ) {
            controlsPane.add( comp, controlsInternalGbc );
            return comp;
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
            JTextField nameField = new JTextField( name );

            // Depending on which of the following we use, the resizing problem does or does not occur
            if( true ) {
                ControlPanel cp = new ControlPanel( this );
                cp.add( textField );
                cp.add( nameField );
                setControlPanel( cp );
            }
            else {
                TestControlPanel testCp = new TestControlPanel();
                testCp.add( textField );
                testCp.add( nameField );
//                testCp.add( scrollPane );
                setControlPanel( testCp );
            }
        }

        public boolean hasHelp() {
            return true;
        }
    }
}
