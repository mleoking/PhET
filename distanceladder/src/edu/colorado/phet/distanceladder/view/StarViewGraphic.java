/**
 * Class: StarViewGraphic
 * Class: edu.colorado.phet.distanceladder.view
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 10:17:23 PM
 */
package edu.colorado.phet.distanceladder.view;

import edu.colorado.phet.distanceladder.Config;
import edu.colorado.phet.distanceladder.model.Star;
import edu.colorado.phet.distanceladder.model.StarView;
import edu.colorado.phet.distanceladder.model.Point2DPolar;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;
import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This graphic contains the stars as seen from the cockpit
 */
public class StarViewGraphic extends CompositeInteractiveGraphic implements SimpleObserver {

    private static double minStarRadius = 2;

    private Container container;
    private StarView starView;
    private Rectangle2D.Double background;
    private AffineTransform starViewTx = new AffineTransform();
    private HashMap starToGraphicMap = new HashMap();

    public StarViewGraphic( Container container, StarView starView ) {
        this.container = container;
        this.starView = starView;
        this.background = starView.getBounds();
        this.starViewTx.setToIdentity();
        this.starViewTx.translate( background.getMinX() + background.getWidth() / 2, background.getMinY() + background.getHeight() / 2 );
//        this.starViewTx.translate( bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2 );
    }

    public void paint( Graphics2D g ) {

        AffineTransform orgTx = g.getTransform();
        Rectangle bounds = container.getBounds();
        double scaleX = bounds.getWidth() / background.getWidth();
        AffineTransform atx = AffineTransform.getScaleInstance( scaleX, scaleX );
        g.transform( atx );

        g.setColor( Color.black );
        g.fillRect( 0, 0, (int)background.getWidth(), (int)background.getHeight() );
        g.transform( starViewTx );

        super.paint( g );

        g.setTransform( orgTx );
    }

    public void update() {
        ArrayList visibleStars = starView.getVisibleStars();
        StarGraphic starGraphic = null;
        for( int i = 0; i < visibleStars.size(); i++ ) {
            Star visibleStar = (Star)visibleStars.get( i );
            starGraphic = (StarGraphic)starToGraphicMap.get( visibleStar );
            double d = starView.getPov().distanceSq( visibleStar.getLocation() );
            // Set the radius of the star based on how close it is to the POV
            double radius = minStarRadius;
//            double radius = Math.max( 40000 / d, minStarRadius );
//            double radius = Math.min( 15, Math.max( 40000 / d, 2 ));
            double brightness = Math.min( 1 , 0.25 *  Config.universeWidth / visibleStar.getLocation().distance( starView.getPov() ));
//            double brightness = visibleStar.getLuminance() * ( 1 / visibleStar.getLocation().distanceSq( starView.getPov() ) );
            if( starGraphic == null ) {
                starGraphic = new StarGraphic( visibleStar, radius, visibleStar.getColor(), new Point2D.Double(), brightness );
                starToGraphicMap.put( visibleStar, starGraphic );
                this.addGraphic( starGraphic, 1 / d );
            }

            starGraphic.update( starView.getApparentLocation( visibleStar ), radius, brightness );
        }

        // Remove stars that aren't visible
        ArrayList removeList = new ArrayList();
        Iterator starIt = starToGraphicMap.keySet().iterator();
        while( starIt.hasNext() ) {
            Star star = (Star)starIt.next();
            if( !visibleStars.contains( star ) ) {
                removeList.add( star );
                StarGraphic sg = (StarGraphic)starToGraphicMap.get( star );
                this.remove( sg );
            }
        }
        for( int i = 0; i < removeList.size(); i++ ) {
            Star star = (Star)removeList.get( i );
            starToGraphicMap.remove( star );
        }
        container.repaint();
    }

}
