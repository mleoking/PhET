/**
 * Class: StarMapGraphic
 * Class: edu.colorado.phet.distanceladder.view
 * User: Ron LeMaster
 * Date: Mar 17, 2004
 * Time: 8:52:52 PM
 */
package edu.colorado.phet.distanceladder.view;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;
import edu.colorado.phet.distanceladder.model.StarField;
import edu.colorado.phet.distanceladder.model.Star;
import edu.colorado.phet.distanceladder.Config;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class StarMapGraphic extends CompositeInteractiveGraphic implements ImageObserver {

    private BufferedImage mapImage;
    private AffineTransform mapTx = new AffineTransform();
    private Container container;
    private StarField starField;
    private HashMap starToGraphicMap = new HashMap();

    public StarMapGraphic( Container container, StarField starField ) {
        this.container = container;
        this.starField = starField;

        ImageLoader imageLoader = new ImageLoader();
        try {
            mapImage = imageLoader.loadImage( "images/star-chart.gif" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void paint( Graphics2D g ) {

        List stars = starField.getStars();
        for( int i = 0; i < stars.size(); i++ ) {
            Star star = (Star)stars.get( i );
            if( starToGraphicMap.get( star ) == null ) {
                StarGraphic sg = new StarGraphic( star, 5, star.getColor(), star.getLocation(), 1 );
                this.addGraphic( sg );
            }
        }
        Iterator starIt = starToGraphicMap.keySet().iterator();
        while( starIt.hasNext() ){
            Star star = (Star)starIt.next();
            if( !stars.contains( star ) ) {
                starToGraphicMap.remove( star );
            }
        }


        AffineTransform orgTx = g.getTransform();
        g.setColor( Color.black );

        Rectangle bounds = container.getBounds();
        double scaleX = bounds.getWidth() / starField.getBounds().getWidth();
        double scaleY = bounds.getHeight() / starField.getBounds().getHeight();
        double scale = Math.min( scaleX, scaleY );

        mapTx.setToIdentity();
        mapTx.translate( container.getWidth() / 2, container.getHeight() / 2 );
        mapTx.scale( scale, scale );
        g.transform( mapTx );
        g.fillRect( (int)-starField.getBounds().getWidth() / 2, (int)-starField.getBounds().getHeight() / 2,
                    (int)starField.getBounds().getWidth(), (int)starField.getBounds().getHeight() );
        super.paint( g );

        // Restore the graphics transform
        g.setTransform( orgTx );
    }

    public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
        return false;
    }
}
