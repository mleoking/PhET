package edu.colorado.phet.distanceladder;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.framesetup.FrameCenterer;
import edu.colorado.phet.common.view.util.framesetup.MaxExtentFrameSetup;
import edu.colorado.phet.distanceladder.controller.CockpitModule;
import edu.colorado.phet.distanceladder.controller.StarMapModule;
import edu.colorado.phet.distanceladder.exercise.Exercise;
import edu.colorado.phet.distanceladder.exercise.Message;
import edu.colorado.phet.distanceladder.levels.Level5A;
import edu.colorado.phet.distanceladder.model.StarField;
import edu.colorado.phet.distanceladder.model.UniverseModel;

import javax.swing.*;
import java.awt.*;

/**
 * Class: edu.colorado.phet.distanceladder.CockpitModuleTest
 * Class: PACKAGE_NAME
 * User: Ron LeMaster
 * Date: Mar 18, 2004
 * Time: 9:09:55 AM
 */

public class DistanceLadderGameTest {
    private static ApplicationDescriptor appDesc;
    private Color[] colors = new Color[]{Color.green, Color.magenta, Color.orange,
                                         Color.white, Color.yellow};

    public void test1() {

        StarField starField = new StarField();
        SwingTimerClock clock = new SwingTimerClock( 10, 1000, true );

        UniverseModel model = new UniverseModel( starField, clock );
        model.getStarShip().setLocation( 0, 0 );

        CockpitModule cockpitModule = new CockpitModule( model );
        StarMapModule starMapModule = new StarMapModule( model );
        Module[] modules = new Module[]{cockpitModule, starMapModule};
        LostInSpaceApplication app = new LostInSpaceApplication( appDesc, modules, clock );
        app.startApplication( cockpitModule );

        doLevel( new Level5A( app.getApplicationView().getPhetFrame(), model ) );

        cockpitModule.activate( null );
    }

    private void displayMessage( Message message ) {
        message.display();
    }

    private void doLevel( Exercise level ) {
        while( !level.doIt() ) {
            JOptionPane.showMessageDialog( null, "Sorry, wrong answer.", "Results",
                                           JOptionPane.ERROR_MESSAGE );
        }
        JOptionPane.showMessageDialog( null, "Correct!", "Results",
                                       JOptionPane.INFORMATION_MESSAGE );
    }

    public static void main( String[] args ) {
        String desc = GraphicsUtil.formatMessage( "A game for learning how to\nmeasure interstellar distances." );
        appDesc = new ApplicationDescriptor( "Lost In Space",
                                             desc,
                                             "0.1",
                                             new MaxExtentFrameSetup( new FrameCenterer( 100, 100 ) ) );
        DistanceLadderGameTest test = new DistanceLadderGameTest();

        test.test1();
    }
}
