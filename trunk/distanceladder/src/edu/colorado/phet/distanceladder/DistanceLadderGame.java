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
import edu.colorado.phet.distanceladder.exercise.XmlExercise;
import edu.colorado.phet.distanceladder.levels.Level1;
import edu.colorado.phet.distanceladder.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 * Class: edu.colorado.phet.distanceladder.CockpitModuleTest
 * Class: PACKAGE_NAME
 * User: Ron LeMaster
 * Date: Mar 18, 2004
 * Time: 9:09:55 AM
 */

public class DistanceLadderGame {
    private static ApplicationDescriptor appDesc;
    private Color[] colors = new Color[]{Color.green, Color.magenta, Color.orange,
                                         Color.white, Color.yellow};

    public void test1() {

        StarField starField = new StarField();
        SwingTimerClock clock = new SwingTimerClock( 10, 1000, true );

        UniverseModel model = new UniverseModel( starField, clock );
        model.getStarShip().setLocation( 0, 0 );

        CockpitModule cockpitModule = new CockpitModule( model );
        Module starMapModule = new StarMapModule( model );
        Module[] modules = new Module[]{cockpitModule, starMapModule};
        LostInSpaceApplication app = new LostInSpaceApplication( appDesc, modules, clock );
        app.startApplication( cockpitModule );

        Star star = null;

        Random random = new Random();
        for( int i = 0; i < 200; i++ ) {
            double x = random.nextDouble() * Config.universeWidth - Config.universeWidth * 0.5;
            double y = random.nextDouble() * Config.universeWidth - Config.universeWidth * 0.5;
            int colorIdx = random.nextInt( colors.length );
            star = new NormalStar( colors[colorIdx], 50, new Point2D.Double( x, y ), random.nextDouble() * 500 - 250 );
            starField.addStar( star );
        }

        star = new NormalStar( Color.green, 1E6, new Point2D.Double( 100, 0 ), -45 );
        starField.addStar( star );
        star = new NormalStar( Color.magenta, 1E6, new Point2D.Double( 200, 0 ), -35 );
        starField.addStar( star );

        model.getStarShip().setPov( new PointOfView( 0, 0, 0 ) );

        starField.reset();
        while( !new Level1( app.getApplicationView().getPhetFrame() ).doIt() ) {
            //
        }

        Exercise ex2 = new Exercise( new XmlExercise( "exercises/vogon-question-1.xml" ), app.getApplicationView().getPhetFrame() );
        while( !ex2.doIt() ) {
            //
        }
        JOptionPane.showConfirmDialog( app.getApplicationView().getPhetFrame(), "???" );
        cockpitModule.activate( null );
    }


    public static void main( String[] args ) {
        String desc = GraphicsUtil.formatMessage( "A game for learning how to\nmeasure interstellar distances." );
        appDesc = new ApplicationDescriptor( "Lost In Space",
                                             desc,
                                             "0.1",
                                             new MaxExtentFrameSetup( new FrameCenterer( 100, 100 ) ) );
        DistanceLadderGame test = new DistanceLadderGame();

        test.test1();
    }
}
