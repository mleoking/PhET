/*  */
package edu.colorado.phet.quantumwaveinterference;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.jnlp.UnavailableServiceException;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedModulePanePiccolo;
import edu.colorado.phet.common.piccolophet.help.MotionHelpBalloon;
import edu.colorado.phet.quantumwaveinterference.modules.intensity.IntensityModule;
import edu.colorado.phet.quantumwaveinterference.modules.mandel.MandelModule;
import edu.colorado.phet.quantumwaveinterference.modules.single.SingleParticleModule;
import edu.colorado.phet.quantumwaveinterference.persistence.PersistenceManager;
import edu.colorado.phet.quantumwaveinterference.persistence.QWIState;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:48:21 PM
 */

public class QuantumWaveInterferenceApplication extends PiccoloPhetApplication {
    //    public static String VERSION = "1.05";
    private IntensityModule intensityModule;
    private SingleParticleModule singleParticleModule;
    private MandelModule mandelModule;

    public QuantumWaveInterferenceApplication( PhetApplicationConfig config ) {
        super( config );
        setTabbedPaneType( new TabbedPaneType() {
            public ITabbedModulePane createTabbedPane() {
                return new TabbedModulePanePiccolo() {
                    //workaround for bug: "High Intensity" module tab renders as "High      " under Java 1.4
                    public void addTab( Module module ) {
                        super.addTab( "<html>" + module.getName() + "</html>", module.getModulePanel() );
                    }
                };
            }
        } );
//        super.setPhetLookAndFeel( new QWILookAndFeel());

        intensityModule = new IntensityModule( this, createClock() );
        addModule( intensityModule );

        singleParticleModule = new SingleParticleModule( this, createClock() );
        addModule( singleParticleModule );

        mandelModule = new MandelModule( this, createClock() );
        addModule( mandelModule );

        JMenuItem save = new JMenuItem( QWIResources.getString( "menus.save" ) );
        save.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                QWIModule qwiModule = getActiveSchrodingerModule();
                new PersistenceManager( qwiModule.getSchrodingerPanel() ).save( new QWIState( qwiModule ) );
            }
        } );
        getPhetFrame().addFileMenuItem( save );

        JMenuItem load = new JMenuItem( QWIResources.getString( "menus.load" ) );
        load.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    QWIModule qwiModule = getActiveSchrodingerModule();
                    QWIState state = (QWIState) new PersistenceManager( qwiModule.getSchrodingerPanel() ).load();
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
    
    public void startApplication() {
        super.startApplication();
        // add wiggle me after the app starts so that we see it move
        if ( intensityModule != null ) {
            addWiggleMe();
        }
    }

    private QWIModule getActiveSchrodingerModule() {
        return (QWIModule) getActiveModule();
    }

    private static IClock createClock() {
        return new SwingClock( 30, 1 );
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

    private void addWiggleMe() {
        QWIPanel schrodingerPanel = intensityModule.getSchrodingerPanel();
        
        final MotionHelpBalloon helpBalloon = new MotionHelpBalloon( schrodingerPanel, QWIResources.getString( "qwi.invitation" ) );
        helpBalloon.setTextColor( Color.white );
        helpBalloon.setShadowTextColor( Color.gray );
        helpBalloon.setShadowTextOffset( 1 );
        helpBalloon.setBalloonVisible( true );
        helpBalloon.setBalloonFillPaint( new Color( 128, 128, 128, 200 ) );

        schrodingerPanel.getSchrodingerScreenNode().addChild( helpBalloon );
        helpBalloon.animateTo( schrodingerPanel.getSchrodingerScreenNode().getGunGraphic() );

        schrodingerPanel.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                helpBalloon.setVisible( false );
            }
        } );
    }

    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new QuantumWaveInterferenceApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, appConstructor, QWIConstants.PROJECT_NAME, QWIConstants.FLAVOR_QUANTUM_WAVE_INTERFERENCE );
        appConfig.setLookAndFeel( new QWIPhetLookAndFeel() );
        appConfig.setFrameSetup( new QWIFrameSetup() );
        appConfig.launchSim();
    }
}
