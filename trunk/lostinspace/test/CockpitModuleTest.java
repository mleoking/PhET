
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ThreadedClock;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.games4education.lostinspace.model.UniverseModel;
import edu.colorado.games4education.lostinspace.model.StarField;
import edu.colorado.games4education.lostinspace.model.NormalStar;
import edu.colorado.games4education.lostinspace.model.StarView;
import edu.colorado.games4education.lostinspace.controller.CockpitModule;
import edu.colorado.games4education.lostinspace.controller.StarMapModule;
import edu.colorado.games4education.lostinspace.LostInSpaceApplication;

import java.awt.geom.Point2D;
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
        AbstractClock clock = new ThreadedClock( 10, 20, true );
        UniverseModel model = new UniverseModel( clock );
        CockpitModule cockpitModule = new CockpitModule( model );
        Module starMapModule = new StarMapModule( model );
        Module[] modules = new Module[]{cockpitModule, starMapModule};
        LostInSpaceApplication app = new LostInSpaceApplication( appDesc, modules, clock );
        app.startApplication( cockpitModule );

        StarField starField = cockpitModule.getStarField();
        NormalStar star = null;
        StarView starView = cockpitModule.getStarView();
        Point2D.Double p = null;

        star = new NormalStar( 100, new Point2D.Double( 15, 10), 0 );
        starField.addStar( star );
        starView.setPov( 10, 10, 0 );
        List l = starView.getVisibleStars();
        cockpitModule.update();

//        // Should print 0:0
//        star = new NormalStar( 100, new Point2D.Double( 15, 10 ), 0 );
//        starView.setPov( 10, 10, 0 );
//        p = starView.getLocation( star );
//        System.out.println( "-->" + p.getX() + ":" + p.getY() );
//
//        // Should print -10:0
//        star = new NormalStar( 100, new Point2D.Double( 15, 20 ), 0 );
//        starView.setPov( 10, 10, 0 );
//        p = starView.getLocation( star );
//        System.out.println( "-->" + p.getX() + ":" + p.getY() );
    }

    public static void main( String[] args ) {
        String desc = GraphicsUtil.formatMessage( "A game for learning how to/nmeasure interstellar distances." );
        appDesc = new ApplicationDescriptor( "Lost In Space",
                                                                           desc,
                                                                           "0.1" );
        CockpitModuleTest test = new CockpitModuleTest();

        test.test1();
    }
}
