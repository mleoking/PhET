/**
 * Class: StarViewGraphic
 * Class: edu.colorado.games4education.lostinspace.view
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 10:17:23 PM
 */
package edu.colorado.games4education.lostinspace.view;

import edu.colorado.games4education.lostinspace.model.StarView;
import edu.colorado.games4education.lostinspace.model.Star;
import edu.colorado.games4education.lostinspace.Config;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class StarViewGraphic extends CompositeInteractiveGraphic {
    private Container container;
    private StarView starView;
    private Rectangle2D.Double background;
    private HashMap starToGraphicMap = new HashMap();

    public StarViewGraphic( Container container, StarView starView, Rectangle2D.Double bounds ) {
        this.container = container;
        this.starView = starView;
        this.background = bounds;
    }

    public void paint( Graphics2D g ) {
        g.setColor( Color.black );
        g.draw( background );
        super.paint( g );
    }

    public void update() {
        ArrayList visibleStars = starView.getVisibleStars();
        for( int i = 0; i < visibleStars.size(); i++ ) {
            Star visibleStar = (Star)visibleStars.get( i );
            StarGraphic starGraphic = (StarGraphic)starToGraphicMap.get( visibleStar );
            if( starGraphic != null ) {
                starGraphic = new StarGraphic( 10, Color.white, new Point2D.Double() );
                starToGraphicMap.put( visibleStar, starGraphic );
                this.addGraphic( starGraphic, Config.starLayer );
            }
            starGraphic.update( visibleStar );

            // Remove stars that aren't visible
            ArrayList removeList = new ArrayList();
            Iterator starIt = starToGraphicMap.keySet().iterator();
            while( starIt.hasNext() ) {
                Star star = (Star)starIt.next();
                if( !visibleStars.contains( star )) {
                    removeList.add( star );
                }
            }
            for( int j = 0; j < removeList.size(); j++ ) {
                Star star = (Star)removeList.get( j );
                starToGraphicMap.remove( star );
            }
        }
        container.repaint();
    }

}
