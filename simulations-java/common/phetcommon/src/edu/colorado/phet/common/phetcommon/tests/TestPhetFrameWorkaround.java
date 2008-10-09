package edu.colorado.phet.common.phetcommon.tests;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetFrameWorkaround;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

/**
 * This class is an attempt to demonstrate the problem for which PhetFrameWorkaround is a workaround.
 * This version works correctly (i.e. doesn't exhibit the problem) on Windows Vista on 2.6 GHz x 2 Athlon processors
 * <p/>
 * NOTE! Set USE_WORKAROUND to compare behavior with and without the workaround.
 */
public class TestPhetFrameWorkaround {

    // true = use a PhetFrameWorkaround
    // false = use a PhetFrame
    private static final boolean USE_WORKAROUND = true;

    public static void main( final String[] args ) {
        PhetApplicationConfig config=new PhetApplicationConfig( args, new PhetApplicationConfig.ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                final PhetApplication phetApplication = new PhetApplication( config ) {
                    protected PhetFrame createPhetFrame() {
                        if ( USE_WORKAROUND ) {
                            return new PhetFrameWorkaround( this );
                        }
                        else {
                            return new PhetFrame( this );
                        }
                    }
                };
                final TestModule module = new TestModule( "test", new SwingClock( 30, 1 ) );
                module.getClock().addClockListener( new ClockAdapter() {
                    public void clockTicked( ClockEvent clockEvent ) {
                        module.contentPane.invalidate();
                        module.contentPane.revalidate();
                        module.contentPane.repaint();
                        module.contentPane.setText( System.currentTimeMillis() + " button time!" );
                        module.contentPane.paintImmediately( 0, 0, module.contentPane.getWidth(), module.contentPane.getHeight() );
                    }
                } );
                phetApplication.addModule( module );
                phetApplication.startApplication();

                module.contentPane.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        final Dialog dialog = new Dialog( phetApplication.getPhetFrame(), false );
                        dialog.setSize( 400, 300 );
                        dialog.addWindowListener( new WindowAdapter() {
                            public void windowClosing( WindowEvent e ) {
                                dialog.dispose();
                            }
                        } );
                        Button comp = new Button( "dialog button" );

                        comp.setBackground( Color.green );
                        dialog.add( comp );
//                        dialog.pack();
                        dialog.setVisible( true );
                    }
                } );

                return phetApplication;
            }
        }, "phetcommon");
        config.setFrameSetup(  new FrameSetup.CenteredWithSize( 800, 600 ));
        config.launchSim();

    }

    static class TestModule extends Module {
        private JButton contentPane;

        public TestModule( String name, IClock clock ) {
            super( name, clock );
            contentPane = new JButton( "Simulation Panel Button" ) {
                protected void paintComponent( Graphics g ) {
                    try {
                        Thread.sleep( 300 );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                    super.paintComponent( g );
                }
            };
            setSimulationPanel( contentPane );
        }

    }
}


