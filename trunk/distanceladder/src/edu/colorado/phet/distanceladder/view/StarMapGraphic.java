/**
 * Class: StarMapGraphic
 * Class: edu.colorado.phet.distanceladder.view
 * User: Ron LeMaster
 * Date: Mar 17, 2004
 * Time: 8:52:52 PM
 */
package edu.colorado.phet.distanceladder.view;

import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;
import edu.colorado.phet.distanceladder.model.Star;
import edu.colorado.phet.distanceladder.model.StarField;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

// TODO: This should implement SimpleObserver and observe the StarField

public class StarMapGraphic extends CompositeInteractiveGraphic implements ImageObserver, SimpleObserver {

//    private BufferedImage mapImage;
    private AffineTransform mapTx = new AffineTransform();
    private Container container;
    private StarField starField;
    private HashMap starToGraphicMap = new HashMap();
    private static double defaultStarGraphicRadius = 5;
    private double starGraphicRadius = defaultStarGraphicRadius;
    private ArrayList removeList = new ArrayList();

    public StarMapGraphic( Container container, StarField starField ) {
        this.container = container;
        this.starField = starField;
        starField.addObserver( this );

//        ImageLoader imageLoader = new ImageLoader();
//        try {
//            mapImage = imageLoader.loadImage( "images/star-chart.gif" );
//        }
//        catch( IOException e ) {
//            e.printStackTrace();
//        }
    }

    public void setStarGraphicRadius( double starGraphicRadius ) {
        this.starGraphicRadius = starGraphicRadius;
    }

    public void paint( Graphics2D g ) {

        // TODO: the next two sections should be moved to an update() method
        // Add any new stars in the StarField
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
        g.setClip( starField.getBounds() );

        Iterator starIt = starToGraphicMap.keySet().iterator();
        while( starIt.hasNext() ) {
            Star star = (Star)starIt.next();
            StarGraphic sg = (StarGraphic)starToGraphicMap.get( star );
            sg.paint( g );
        }


        super.paint( g );


        // Restore the graphics transform
        g.setTransform( orgTx );
    }

    public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
        return false;
    }

    public void update() {
        final List stars = starField.getStars();
        for( int i = 0; i < stars.size(); i++ ) {
            Star star = (Star)stars.get( i );
            if( starToGraphicMap.get( star ) == null ) {
                StarGraphic sg = new StarGraphic( star, starGraphicRadius, star.getColor(), star.getLocation(), 1 );
                StarMapGraphic.this.addGraphic( sg );
                starToGraphicMap.put( star, sg );
            }
        }

        // Remove any stars that are no longer in the StarField
        Iterator starIt = starToGraphicMap.keySet().iterator();
        removeList.clear();
        while( starIt.hasNext() ) {
            Star star = (Star)starIt.next();
            if( !stars.contains( star ) ) {
                removeList.add( star );
            }
        }
        for( int i = 0; i < removeList.size(); i++ ) {
            Star star = (Star)removeList.get( i );
            starToGraphicMap.remove( star );
        }
        container.repaint();
    }
}
