
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ThreadedClock;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.games4education.lostinspace.model.*;
import edu.colorado.games4education.lostinspace.controller.CockpitModule;
import edu.colorado.games4education.lostinspace.controller.StarMapModule;
import edu.colorado.games4education.lostinspace.LostInSpaceApplication;
import edu.colorado.games4education.lostinspace.Config;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.*;
import java.util.List;

/**
 * Class: CockpitModuleTest
 * Class: PACKAGE_NAME
 * User: Ron LeMaster
 * Date: Mar 18, 2004
 * Time: 9:09:55 AM
 */

public class CockpitModuleTest {
    private static ApplicationDescriptor appDesc;

    public void test1() {
        System.out.println( "Test 1" );

//        AbstractClock clock = new ThreadedClock( 10, 20, true );
//        UniverseModel model = new UniverseModel( clock, starField );
//        CockpitModule cockpitModule = new CockpitModule( model );
//        Module starMapModule = new StarMapModule( model );
//        Module[] modules = new Module[]{cockpitModule, starMapModule};
//        LostInSpaceApplication app = new LostInSpaceApplication( appDesc, modules, clock );
//        app.startApplication( cockpitModule );

        StarField starField = new StarField( new Rectangle2D.Double( -Config.fixedStarDistance, - Config.fixedStarDistance,
                                                                     Config.fixedStarDistance, Config.fixedStarDistance) );

        AbstractClock clock = new ThreadedClock( 10, 20, true );
        UniverseModel model = new UniverseModel( starField, clock );
        CockpitModule cockpitModule = new CockpitModule( model );
        Module starMapModule = new StarMapModule( model );
        Module[] modules = new Module[]{cockpitModule, starMapModule};
        LostInSpaceApplication app = new LostInSpaceApplication( appDesc, modules, clock );
        app.startApplication( cockpitModule );

        Star star = null;
        StarView starView = cockpitModule.getStarView();
        Point2D.Double p = null;

        star = new NormalStar( Color.magenta, 100, new Point2D.Double( Config.fixedStarDistance * 1.2, 10 ), -50 );
        starField.addStar( star );
        star = new NormalStar( Color.green, 100, new Point2D.Double( 150, 10 ), -50 );
        starField.addStar( star );
        star = new NormalStar( Color.blue, 100, new Point2D.Double( 100, 10 ), 50 );
        starField.addStar( star );
        star = new NormalStar( Color.yellow, 100, new Point2D.Double( -100, -10 ), 50 );
        starField.addStar( star );
        star = new NormalStar( Color.white, 100, new Point2D.Double( 0, 0 ), 50 );
        starField.addStar( star );

        star = new FixedStar( Color.red, 100, 0, -80 );
        starField.addStar( star );
        star = new FixedStar( Color.red, 100, Math.PI / 2, -80 );
        starField.addStar( star );
        star = new FixedStar( Color.red, 100, Math.PI, -80 );
        starField.addStar( star );

        starView.setPov( 0, 0, 0 );

        cockpitModule.update();

    }

    public static void main( String[] args ) {
        String desc = GraphicsUtil.formatMessage( "A game for learning how to\nmeasure interstellar distances." );
        appDesc = new ApplicationDescriptor( "Lost In Space",
                                             desc,
                                             "0.1" );
        CockpitModuleTest test = new CockpitModuleTest();

        test.test1();
    }
}
