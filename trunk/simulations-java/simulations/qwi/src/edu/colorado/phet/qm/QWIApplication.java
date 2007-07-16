/*  */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.help.MotionHelpBalloon;
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
 *
 */

public class QWIApplication extends PiccoloPhetApplication {
//    public static String VERSION = "1.05";
    private IntensityModule intensityModule;
    public SingleParticleModule singleParticleModule;
    public MandelModule mandelModule;

    public QWIApplication( String[] args ) {
        super( args, QWIStrings.getString( "qwi.name" ), QWIStrings.getString( "qwi.description" ),
               getQWIVersion(), new QWIFrameSetup() );
//        super.setPhetLookAndFeel( new QWILookAndFeel());

        intensityModule = new IntensityModule( this, createClock() );
        addModule( intensityModule );

        singleParticleModule = new SingleParticleModule( this, createClock() );
        addModule( singleParticleModule );

        mandelModule = new MandelModule( this, createClock() );
        addModule( mandelModule );
        
        JMenuItem save = new JMenuItem( QWIStrings.getString( "menus.save" ) );
        save.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                QWIModule qwiModule = getActiveSchrodingerModule();
                new PersistenceManager( qwiModule.getSchrodingerPanel() ).save( new QWIState( qwiModule ) );
            }
        } );
        getPhetFrame().addFileMenuItem( save );

        JMenuItem load = new JMenuItem( QWIStrings.getString( "menus.load" ) );
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

    private static String getQWIVersion() {
        return PhetApplicationConfig.getVersion( "qwi").formatForTitleBar();
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

    public IntensityModule getIntensityModule() {
        return intensityModule;
    }

    public SingleParticleModule getSingleParticleModule() {
        return singleParticleModule;
    }

    public MandelModule getMandelModule() {
        return mandelModule;
    }

    private static void addWiggleMe( final QWIApplication QWIApplication ) {
        final MotionHelpBalloon helpBalloon = new MotionHelpBalloon( QWIApplication.intensityModule.getSchrodingerPanel(), QWIStrings.getString( "qwi.invitation" ) );
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
