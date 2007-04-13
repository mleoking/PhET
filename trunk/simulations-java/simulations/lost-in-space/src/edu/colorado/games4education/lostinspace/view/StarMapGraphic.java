/**
 * Class: StarMapGraphic
 * Class: edu.colorado.games4education.lostinspace.view
 * User: Ron LeMaster
 * Date: Mar 17, 2004
 * Time: 8:52:52 PM
 */
package edu.colorado.games4education.lostinspace.view;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;
import edu.colorado.games4education.lostinspace.model.StarField;
import edu.colorado.games4education.lostinspace.model.Star;
import edu.colorado.games4education.lostinspace.Config;

import java.awt.*;
import java.awt.geom.AffineTransform;
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
                StarGraphic sg = new StarGraphic( 10, star.getColor(), star.getLocation() );
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


        g.setColor( Color.black );
        g.fillRect( 0, 0, container.getWidth(), container.getHeight() );

        double rx = ( 0.8 * starField.getBounds().getWidth() ) / container.getWidth();
        double ry = ( 0.8 * starField.getBounds().getHeight() ) / container.getHeight();
        mapTx.setToIdentity();
        double r = Math.min( rx, ry );
        mapTx.translate( container.getWidth() / 2, container.getHeight() / 2 );
        mapTx.scale( r, r );
        AffineTransform orgTx = g.getTransform();
        g.transform( mapTx );
        super.paint( g );
        g.setTransform( orgTx );

//        g.drawImage( mapImage, mapTx, this );
    }

    public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
        return false;
    }
}
