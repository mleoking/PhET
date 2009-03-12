package edu.colorado.phet.circuitconstructionkit.tests;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CircuitConstructionKitDCApplication;
import edu.colorado.phet.circuitconstructionkit.view.CCKPhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetFrameWorkaround;

/**
 * Sample usage
 * <applet code="edu.colorado.phet.circuitconstructionkit.tests.CCKApplet.class"
 * codebase="."
 * archive="circuit-construction-kit_all.jar"
 * width="600" height="600">
 * Your browser is completely ignoring the &lt;APPLET&gt; tag!
 * </applet>
 */

public class CCKApplet extends JApplet implements IProguardKeepClass {
    static PhetFrame frame;

    public void init() {
        super.init();

        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                CircuitConstructionKitDCApplication application = new CircuitConstructionKitDCApplication( config ) {
                    protected PhetFrame createPhetFrame() {
                        return new PhetFrameWorkaround( this ) {
                            public void setVisible( boolean b ) {
//                                super.setVisible( b );
                            }

                            public void show() {
//                                super.show();
                            }
                        };
                    }
                };
                PhetFrame frame = application.getPhetFrame();
                JComponent contentPane = (JComponent) frame.getContentPane();
                frame.setContentPane( new JLabel() );
                frame.dispose();
                frame.setVisible( false );
                setContentPane( contentPane );
                return application;
            }
        };

        PhetApplicationConfig appConfig = new PhetApplicationConfig( new String[0], "circuit-construction-kit", "circuit-construction-kit-ac" );
        appConfig.setLookAndFeel( new CCKPhetLookAndFeel() );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
