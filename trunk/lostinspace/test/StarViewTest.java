
import edu.colorado.games4education.lostinspace.model.*;

import java.awt.geom.Point2D;
import java.awt.*;
import java.util.List;

/**
 * Class: StarViewTest
 * Class: PACKAGE_NAME
 * User: Ron LeMaster
 * Date: Mar 18, 2004
 * Time: 7:34:40 AM
 */

public class StarViewTest {

    // Should print out 2 stars
    public void test1 () {
        System.out.println( "Test 1" );

        StarField starField = new StarField();
        NormalStar star = null;
        star = new NormalStar( Color.white, 100, new Point2D.Double( 20, 20 ), 0 );
        starField.addStar( star );
        star = new NormalStar( Color.white, 100, new Point2D.Double( 110, 100 ), 0 );
        starField.addStar( star );
        star = new NormalStar( Color.white, 100, new Point2D.Double( 150, 110 ), 0 );
        starField.addStar( star );

        StarView starView = new StarView( starField, Math.PI / 2 );
        starView.setPov( 100, 100, Math.PI * 3 / 4 );
        List visibleStars = starView.getVisibleStars();
        for( int i = 0; i < visibleStars.size(); i++ ) {
            Star s = (Star)visibleStars.get( i );
            System.out.println( "star: " + s );
        }
    }

    // Should print out 1 star
    public void test2 () {
        System.out.println( "Test 2" );

        StarField starField = new StarField();
        NormalStar star = null;
        star = new NormalStar( Color.white, 100, new Point2D.Double( 20, 20 ), 0 );
        starField.addStar( star );
        star = new NormalStar( Color.white, 100, new Point2D.Double( 110, 100 ), 0 );
        starField.addStar( star );
        star = new NormalStar( Color.white, 100, new Point2D.Double( 150, 110 ), 0 );
        starField.addStar( star );

        StarView starView = new StarView( starField, Math.PI / 2 );
        starView.setPov( 100, 100, 0 );
        List visibleStars = starView.getVisibleStars();
        for( int i = 0; i < visibleStars.size(); i++ ) {
            Star s = (Star)visibleStars.get( i );
            System.out.println( "star: " + s );
        }
    }

    // Tests the transform from polar to cartesian coordinates
    public void test3() {
        System.out.println( "Test 3" );

        Point2DPolar pc = null;
        Point2D.Double pov = null;
        Point2D.Double result = null;

        // Should print out 10:0
        pc = new Point2DPolar( 10, 0 );
        pov = new Point2D.Double( 0, 0 );
        result = pc.toPoint2D( pov );
        System.out.println( "--> " + result.getX() + ":" + result.getY() );

        // Should print out 7.07:7.07
        pc = new Point2DPolar( 10, Math.PI / 4 );
        pov = new Point2D.Double( 0, 0 );
        result = pc.toPoint2D( pov );
        System.out.println( "--> " + result.getX() + ":" + result.getY() );

        // Should print out 0:10
        pc = new Point2DPolar( 10, Math.PI / 2 );
        pov = new Point2D.Double( 0, 0 );
        result = pc.toPoint2D( pov );
        System.out.println( "--> " + result.getX() + ":" + result.getY() );

        // Should print out -7.07:-7.07
        pc = new Point2DPolar( 10, Math.PI * 5 / 4 );
        pov = new Point2D.Double( 0, 0 );
        result = pc.toPoint2D( pov );
        System.out.println( "--> " + result.getX() + ":" + result.getY() );
    }

    // Tests creating polar coordinates from cartesian
    public void test4() {
        System.out.println( "Test 4" );

        Point2D.Double cc = null;
        Point2D.Double po = null;
        Point2DPolar pc = null;

        // should print 5:0
        po = new Point2D.Double( 10, 10 );
        cc = new Point2D.Double( 15, 10 );
        pc = new Point2DPolar( cc, po );
        System.out.println( "--> " + pc.getR() + ":" + pc.getTheta() );

        // should print 10:pi/4
        po = new Point2D.Double( 10, 10 );
        cc = new Point2D.Double( 17.07, 17.07 );
        pc = new Point2DPolar( cc, po );
        System.out.println( "--> " + pc.getR() + ":" + pc.getTheta() );
    }

    // Tests getting the location of a star in a StarView's field of view
    public void test5() {
        System.out.println( "Test 5" );

        NormalStar star = null;
        StarField starField = new StarField();
        StarView starView = new StarView( starField, Math.PI / 2 );
        Point2D.Double p = null;

        // Should print 10:0
        star = new NormalStar(Color.white,  100, new Point2D.Double( 15, 0), 0 );
        starView.setPov( 10, 10, 0 );
        p = starView.getLocation( star );
        System.out.println( "-->" + p.getX() + ":" + p.getY() );

        // Should print 0:0
        star = new NormalStar( Color.white, 100, new Point2D.Double( 15, 10 ), 0 );
        starView.setPov( 10, 10, 0 );
        p = starView.getLocation( star );
        System.out.println( "-->" + p.getX() + ":" + p.getY() );

        // Should print -10:0
        star = new NormalStar( Color.white, 100, new Point2D.Double( 15, 20 ), 0 );
        starView.setPov( 10, 10, 0 );
        p = starView.getLocation( star );
        System.out.println( "-->" + p.getX() + ":" + p.getY() );
    }

    public static void main( String[] args ) {
        StarViewTest test = new StarViewTest();
        test.test1();
        test.test2();
        test.test3();
        test.test4();
        test.test5();
    }
}
