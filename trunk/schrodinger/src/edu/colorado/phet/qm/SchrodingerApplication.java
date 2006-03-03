/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.TabbedModulePane;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.piccolo.help.HelpBalloon;
import edu.colorado.phet.qm.modules.intensity.IntensityModule;
import edu.colorado.phet.qm.modules.mandel.MandelModule;
import edu.colorado.phet.qm.modules.single.SingleParticleModule;
import edu.colorado.phet.qm.persistence.PersistenceManager;
import edu.colorado.phet.qm.persistence.QWIState;

import javax.jnlp.UnavailableServiceException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:48:21 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerApplication extends PhetApplication {
    public static String TITLE = "Quantum Wave Interference";
    public static String DESCRIPTION = "Quantum Wave Interference";
    public static String VERSION = "0.41";
    private String[] args;
    private IntensityModule intensityModule;

    public SchrodingerApplication( String[] args ) {
        super( args, TITLE, DESCRIPTION, VERSION, createFrameSetup() );
        this.args = args;

        intensityModule = new IntensityModule( this, createClock() );
        addModule( intensityModule );
        addModule( new SingleParticleModule( this, createClock() ) );
        addModule( new MandelModule( this, createClock() ) );

        JMenuItem save = new JMenuItem( "Save (detectors & barriers)" );
        save.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SchrodingerModule schrodingerModule = getActiveSchrodingerModule();
                new PersistenceManager( schrodingerModule.getSchrodingerPanel() ).save( new QWIState( schrodingerModule ) );
            }

        } );
        getPhetFrame().addFileMenuItem( save );

        JMenuItem load = new JMenuItem( "Load (detectors & barriers)" );
        load.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    SchrodingerModule schrodingerModule = getActiveSchrodingerModule();
                    QWIState state = (QWIState)new PersistenceManager( schrodingerModule.getSchrodingerPanel() ).load();
                    state.restore( schrodingerModule );
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                catch( UnavailableServiceException e1 ) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        } );
        getPhetFrame().addFileMenuItem( load );
        getPhetFrame().addFileMenuSeparator();
    }

    private SchrodingerModule getActiveSchrodingerModule() {
        return (SchrodingerModule)getActiveModule();
    }

    private static IClock createClock() {
        return new SwingClock( 30, 1 );
    }

    public String[] getArgs() {
        return args;
    }

    protected PhetFrame createPhetFrame( PhetApplication phetApplication ) {
        return new SchrodingerPhetFrame( phetApplication );
    }

    class SchrodingerPhetFrame extends PhetFrame {
        public SchrodingerPhetFrame( PhetApplication phetApplication ) {
            super( phetApplication );
        }

        protected Container createTabbedPane( PhetApplication application, Module[] modules ) {
            return new MyTabbedModulePane( application, modules );
        }
    }

    class MyTabbedModulePane extends TabbedModulePane {

        public MyTabbedModulePane( PhetApplication application, Module[] modules ) {
            super( application, modules );
        }
    }

    private static FrameSetup createFrameSetup() {
//        return new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 100, 100 ) );
//        return new FrameSetup.CenteredWithSize( 900, 680 );
        return new FrameSetup() {
            public void initialize( JFrame frame ) {
                int width = 900;
                int height = 704;
                Toolkit tk = Toolkit.getDefaultToolkit();
                Dimension d = tk.getScreenSize();
                int x = ( d.width - width ) / 2;
                int y = 0;
                frame.setLocation( x, y );
                frame.setSize( width, height );
            }
        };
    }

    public static void main( String[] args ) {
        PhetLookAndFeel.setLookAndFeel();
        new PhetLookAndFeel().apply();
        final SchrodingerApplication schrodingerApplication = new SchrodingerApplication( args );
        schrodingerApplication.startApplication();

//        addWiggleMe( schrodingerApplication );
    }

    private static void addWiggleMe( final SchrodingerApplication schrodingerApplication ) {
        final HelpBalloon helpBalloon = new HelpBalloon( schrodingerApplication.intensityModule.getSchrodingerPanel(), "Turn on the Laser", HelpBalloon.BOTTOM_CENTER, 100 );
        helpBalloon.pointAt( schrodingerApplication.intensityModule.getSchrodingerPanel().getSchrodingerScreenNode().getGunGraphic().getGunImageGraphic(), schrodingerApplication.intensityModule.getSchrodingerPanel() );

        schrodingerApplication.intensityModule.getSchrodingerPanel().getSchrodingerScreenNode().addChild( helpBalloon );
        helpBalloon.setVisible( true );
        helpBalloon.setEnabled( true );
        final Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                double frequency = 1.0 * 1.0 / 1000.0;
                helpBalloon.setArrowLength( 100 - 20 * Math.sin( System.currentTimeMillis() * frequency ) );
            }
        } );
        schrodingerApplication.intensityModule.getSchrodingerPanel().addMouseListener( new MouseListener() {
            public void mouseClicked( MouseEvent e ) {
            }

            public void mouseEntered( MouseEvent e ) {
            }

            public void mouseExited( MouseEvent e ) {
            }

            public void mousePressed( MouseEvent e ) {
                helpBalloon.setVisible( false );
                helpBalloon.setEnabled( false );
                schrodingerApplication.intensityModule.getSchrodingerPanel().getSchrodingerScreenNode().removeChild( helpBalloon );
                schrodingerApplication.intensityModule.getSchrodingerPanel().removeMouseListener( this );
            }

            public void mouseReleased( MouseEvent e ) {
            }
        } );

        timer.start();
        Timer t2 = new Timer( 5000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                timer.stop();
            }
        } );
        t2.setInitialDelay( 5000 );
        t2.start();
    }

}
