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
import edu.colorado.phet.piccolo.PiccoloPhetApplication;
import edu.colorado.phet.piccolo.help.MotionHelpBalloon;
import edu.colorado.phet.qm.modules.intensity.IntensityModule;
import edu.colorado.phet.qm.modules.mandel.MandelModule;
import edu.colorado.phet.qm.modules.single.SingleParticleModule;
import edu.colorado.phet.qm.persistence.PersistenceManager;
import edu.colorado.phet.qm.persistence.QWIState;
import edu.colorado.phet.qm.phetcommon.LucidaSansFont;

import javax.jnlp.UnavailableServiceException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:48:21 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class QWIApplication extends PiccoloPhetApplication {
    public static String TITLE = "Quantum Wave Interference";
    public static String DESCRIPTION = "Quantum Wave Interference";
    public static String VERSION = "1.00";
    private String[] args;
    private IntensityModule intensityModule;

    public QWIApplication( String[] args ) throws InvocationTargetException, InterruptedException {
        super( args, TITLE, DESCRIPTION, VERSION, createFrameSetup() );
        this.args = args;
        intensityModule = new IntensityModule( QWIApplication.this, createClock() );
        addModule( intensityModule );
        addModule( new SingleParticleModule( QWIApplication.this, createClock() ) );
        addModule( new MandelModule( QWIApplication.this, createClock() ) );
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
//                int width = 900;
                int width = 912;
//                int height = 704;
                int height = 732;
                Toolkit tk = Toolkit.getDefaultToolkit();
                Dimension d = tk.getScreenSize();
                int x = ( d.width - width ) / 2;
                int y = 0;
                frame.setLocation( x, y );
                frame.setSize( width, height );
            }
        };
    }

    public static void main( final String[] args ) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                try {
                    oldmain( args );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
                catch( InvocationTargetException e ) {
                    e.printStackTrace();
                }
            }
        } );
//        oldmain( args );
    }

    private static void oldmain( String[] args ) throws InterruptedException, InvocationTargetException {
        PhetLookAndFeel.setLookAndFeel();
        PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
        phetLookAndFeel.setFont( new LucidaSansFont( 13, false ) );
        phetLookAndFeel.apply();
        final QWIApplication QWIApplication = new QWIApplication( args );
        QWIApplication.startApplication();
        if( QWIApplication.intensityModule != null ) {
            addWiggleMe( QWIApplication );
        }
    }

    private static void addWiggleMe( final QWIApplication QWIApplication ) {
        final MotionHelpBalloon helpBalloon = new MotionHelpBalloon( QWIApplication.intensityModule.getSchrodingerPanel(), "Turn on the Laser" );
        helpBalloon.setTextColor( Color.white );
        helpBalloon.setShadowTextColor( Color.gray );
        helpBalloon.setShadowTextOffset( 1 );
        helpBalloon.setBalloonVisible( true );
        helpBalloon.setBalloonFillPaint( new Color( 128, 128, 128, 200 ) );

        helpBalloon.animateTo( QWIApplication.intensityModule.getSchrodingerPanel().getSchrodingerScreenNode().getGunGraphic().getOnGunGraphic(), QWIApplication.intensityModule.getSchrodingerPanel() );
        QWIApplication.intensityModule.getSchrodingerPanel().getSchrodingerScreenNode().addChild( helpBalloon );
        QWIApplication.intensityModule.getSchrodingerPanel().addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                helpBalloon.setVisible( false );
            }
        } );
    }

}
