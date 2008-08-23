/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.common_force1d.util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.*;

import edu.colorado.phet.forces1d.common_force1d.application.Module;
import edu.colorado.phet.forces1d.common_force1d.application.ModuleManager;
import edu.colorado.phet.forces1d.common_force1d.application.PhetApplication;
import edu.colorado.phet.forces1d.common_force1d.model.clock.AbstractClock;
import edu.colorado.phet.forces1d.common_force1d.model.clock.ClockTickEvent;
import edu.colorado.phet.forces1d.common_force1d.model.clock.ClockTickListener;
import edu.colorado.phet.forces1d.common_force1d.view.ApparatusPanel;
import edu.colorado.phet.forces1d.common_force1d.view.ApparatusPanel2;
import edu.colorado.phet.forces1d.common_force1d.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.forces1d.common_force1d.view.util.LineGrid;
import edu.colorado.phet.forces1d.common_force1d.view.util.MouseTracker;

/**
 * DebugMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DebugMenu extends JMenu {
    private HashMap appPanelsToGrids = new HashMap();
    private PhetApplication app;

    public DebugMenu( final PhetApplication app ) {
        super( "Debug" );
        this.app = app;
        setMnemonic( 'D' );

        this.add( new GridMenuItem() );
        this.add( new MouseTrackerMenuItem() );
        this.add( new FrameRateMenuItem() );

        OffscreenBufferMenuItem menuItem = new OffscreenBufferMenuItem( "OffscreenBuffer", true );
        OffscreenBufferMenuItem menuItem2 = new OffscreenBufferMenuItem( "Paint directly to screen.", false );
        OffscreenBufferDirtyItem menuItem4 = new OffscreenBufferDirtyItem();
        JRadioButtonMenuItem disjoint = new DisjointPaintMenuItem();

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( menuItem );
        buttonGroup.add( menuItem2 );
        buttonGroup.add( menuItem4 );
        buttonGroup.add( disjoint );

        this.addSeparator();
        this.add( menuItem );
        this.add( menuItem2 );
        this.add( menuItem4 );
        this.add( disjoint );
        this.addSeparator();


        this.add( new DebugMenu.ShortCircuitRectangles() );
    }

    private ApparatusPanel getApparatusPanel() {
        ApparatusPanel appPanel = app.getModuleManager().getActiveModule().getApparatusPanel();
        return appPanel;
    }

    //----------------------------------------------------------------
    // Menu Items
    //----------------------------------------------------------------

    /**
     * Displays/hides a 100 pixel-spaced grid
     */
    private class GridMenuItem extends JCheckBoxMenuItem {
        public GridMenuItem() {
            super( "Grid" );
            this.setMnemonic( 'G' );
            this.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ApparatusPanel appPanel = getApparatusPanel();
                    if ( appPanel instanceof ApparatusPanel2 ) {
                        ApparatusPanel2 appPanel2 = (ApparatusPanel2) appPanel;
                        if ( isSelected() ) {
                            LineGrid grid = new LineGrid( appPanel2, 100, 100, new Color( 0, 128, 0 ) );
                            appPanelsToGrids.put( appPanel2, grid );
                            appPanel2.addGraphic( grid );
                            appPanel.repaint();
                        }
                        else {
                            LineGrid grid = (LineGrid) appPanelsToGrids.get( appPanel2 );
                            appPanel2.removeGraphic( grid );
                            appPanelsToGrids.remove( appPanel2 );
                            appPanel2.repaint();
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog( app.getPhetFrame(), "<html>This option only applies to modules<br>that use ApparatusPanel2</html>" );
                    }
                }
            } );
        }

    }

    /**
     * Shows/hides a MouseTracker
     */
    private class MouseTrackerMenuItem extends JCheckBoxMenuItem {
        private HashMap appPanelToTracker = new HashMap();

        public MouseTrackerMenuItem() {
            super( "Mouse tracker " );
            this.setMnemonic( 'M' );
            this.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ApparatusPanel appPanel = getApparatusPanel();
                    if ( appPanel instanceof ApparatusPanel2 ) {
                        ApparatusPanel2 appPanel2 = (ApparatusPanel2) appPanel;
                        if ( isSelected() ) {
                            MouseTracker tracker = new MouseTracker( appPanel2 );
                            appPanel.addGraphic( tracker, Double.MAX_VALUE );
                            appPanelToTracker.put( appPanel2, tracker );
                        }
                        else {
                            MouseTracker tracker = (MouseTracker) appPanelToTracker.get( appPanel );
                            appPanel.removeGraphic( tracker );
                            appPanelToTracker.remove( appPanel );
                        }
                    }
                }
            } );
        }
    }

    /**
     * Shows/hides a window that reports the frame rate every second
     */
    private class FrameRateMenuItem extends JCheckBoxMenuItem {
        private JDialog frameRateDlg;

        public FrameRateMenuItem() {
            super( "Show frame rate" );
            this.setMnemonic( 'f' );
            frameRateDlg = new JDialog( app.getPhetFrame(), "Frame Rate", false );
            JTextArea textArea = new JTextArea( 10, 5 );
            frameRateDlg.getContentPane().add( new JScrollPane( textArea,
                                                                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER ) );
            frameRateDlg.setUndecorated( true );
            frameRateDlg.getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );
            frameRateDlg.pack();

            this.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    frameRateDlg.setVisible( isSelected() );
                }
            } );

            startRecording( app.getClock(), textArea );
        }

        void startRecording( AbstractClock clock, final JTextArea textArea ) {
            clock.addClockTickListener( new ClockTickListener() {
                int frameCnt = 0;
                long lastTickTime = System.currentTimeMillis();
                long averagingTime = 1000;

                public void clockTicked( ClockTickEvent event ) {
                    frameCnt++;
                    long currTime = System.currentTimeMillis();
                    if ( currTime - lastTickTime > averagingTime ) {
                        double rate = frameCnt * 1000 / ( currTime - lastTickTime );
                        lastTickTime = currTime;
                        frameCnt = 0;
                        textArea.append( "    " + Double.toString( rate ) + "\n" );
                    }
                }
            } );
        }
    }

    private class OffscreenBufferMenuItem extends JRadioButtonMenuItem {
        private boolean useOffscreenBuffer;

        public OffscreenBufferMenuItem( String name, final boolean useOffscreenBuffer ) {
            super( name );
            this.useOffscreenBuffer = useOffscreenBuffer;
            this.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    PhetApplication app = PhetApplication.instance();
                    ModuleManager mm = app.getModuleManager();
                    for ( int i = 0; i < mm.numModules(); i++ ) {
                        Module module = mm.moduleAt( i );
                        ApparatusPanel ap = module.getApparatusPanel();
                        if ( ap instanceof ApparatusPanel2 ) {
                            ApparatusPanel2 ap2 = (ApparatusPanel2) ap;
                            ap2.setUseOffscreenBuffer( useOffscreenBuffer );
                            ap2.revalidate();
                            ap2.repaint( ap2.getBounds() );
                        }
                    }
                }
            } );
        }
    }


    public class OffscreenBufferDirtyItem extends JRadioButtonMenuItem {
        public OffscreenBufferDirtyItem() {
            super( "OffscreenBuffer-(Dirty Regions Only)" );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    for ( int i = 0; i < PhetApplication.instance().getModuleManager().numModules(); i++ ) {
                        ApparatusPanel ap = PhetApplication.instance().getModuleManager().moduleAt( i ).getApparatusPanel();
                        if ( ap instanceof ApparatusPanel2 ) {
                            ( (ApparatusPanel2) ap ).setUseOffscreenBufferDirtyRegion();
                        }
                    }
                }
            } );
        }
    }

    public class DisjointPaintMenuItem extends JRadioButtonMenuItem {
        public DisjointPaintMenuItem() {
            super( "Paint Direct/Disjoint" );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    for ( int i = 0; i < PhetApplication.instance().getModuleManager().numModules(); i++ ) {
                        ApparatusPanel ap = PhetApplication.instance().getModuleManager().moduleAt( i ).getApparatusPanel();
                        if ( ap instanceof ApparatusPanel2 ) {
                            ( (ApparatusPanel2) ap ).setPaintStrategyDisjoint();
                        }
                    }
                }
            } );
        }
    }

    public class ShortCircuitRectangles extends JCheckBoxMenuItem {
        public ShortCircuitRectangles() {
            super( "Skip Rectangle Computation", PhetGraphic.SKIP_RECTANGLE_COMPUTATION );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    PhetGraphic.SKIP_RECTANGLE_COMPUTATION = isSelected();
                }
            } );
        }
    }


}
