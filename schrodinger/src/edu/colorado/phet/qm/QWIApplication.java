/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;
import edu.colorado.phet.piccolo.help.MotionHelpBalloon;
import edu.colorado.phet.qm.davissongermer.QWIStrings;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:48:21 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class QWIApplication extends PiccoloPhetApplication {
    public static String VERSION = "1.02.10";
    private IntensityModule intensityModule;

    public QWIApplication( String[] args ) {
        super( args, QWIStrings.getString( "quantum.wave.interference" ), QWIStrings.getString( "quantum.wave.interference" ),
               VERSION, new QWIFrameSetup() );
//        super.setPhetLookAndFeel( new QWILookAndFeel());

        intensityModule = new IntensityModule( QWIApplication.this, createClock() );
        addModule( intensityModule );
        addModule( new SingleParticleModule( QWIApplication.this, createClock() ) );
        addModule( new MandelModule( QWIApplication.this, createClock() ) );
        JMenuItem save = new JMenuItem( QWIStrings.getString( "save.detectors.barriers" ) );
        save.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                QWIModule qwiModule = getActiveSchrodingerModule();
                new PersistenceManager( qwiModule.getSchrodingerPanel() ).save( new QWIState( qwiModule ) );
            }
        } );
        getPhetFrame().addFileMenuItem( save );

        JMenuItem load = new JMenuItem( QWIStrings.getString( "load.detectors.barriers" ) );
        load.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    QWIModule qwiModule = getActiveSchrodingerModule();
                    QWIState state = (QWIState)new PersistenceManager( qwiModule.getSchrodingerPanel() ).load();
                    state.restore( qwiModule );
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

    private QWIModule getActiveSchrodingerModule() {
        return (QWIModule)getActiveModule();
    }

    private static IClock createClock() {
        return new SwingClock( 30, 1 );
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                QWIStrings.init( args );
                new QWIPhetLookAndFeel().initLookAndFeel();
                final QWIApplication QWIApplication = new QWIApplication( args );
                QWIApplication.startApplication();
                if( QWIApplication.intensityModule != null ) {
                    addWiggleMe( QWIApplication );
                }
                System.out.println( "UIManager.getLookAndFeel() = " + UIManager.getLookAndFeel() );
            }
        } );
    }

    private static void addWiggleMe( final QWIApplication QWIApplication ) {
        final MotionHelpBalloon helpBalloon = new MotionHelpBalloon( QWIApplication.intensityModule.getSchrodingerPanel(), QWIStrings.getString( "turn.on.the.laser" ) );
        helpBalloon.setTextColor( Color.white );
        helpBalloon.setShadowTextColor( Color.gray );
        helpBalloon.setShadowTextOffset( 1 );
        helpBalloon.setBalloonVisible( true );
        helpBalloon.setBalloonFillPaint( new Color( 128, 128, 128, 200 ) );

        QWIApplication.intensityModule.getSchrodingerPanel().getSchrodingerScreenNode().addChild( helpBalloon );
        helpBalloon.animateTo( QWIApplication.intensityModule.getSchrodingerPanel().getSchrodingerScreenNode().getGunGraphic() );

        QWIApplication.intensityModule.getSchrodingerPanel().addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                helpBalloon.setVisible( false );
            }
        } );
    }

}
