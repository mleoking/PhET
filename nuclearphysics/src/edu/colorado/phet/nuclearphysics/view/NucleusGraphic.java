/**
 * Class: NucleusGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.nuclearphysics.model.NuclearParticle;
import edu.colorado.phet.nuclearphysics.model.Nucleus;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashMap;

public class NucleusGraphic implements Graphic, SimpleObserver, ImageObserver {

    //
    // Static fields and methods
    //
    private static HashMap graphicToModelMap = new HashMap();
    private static NeutronGraphic neutronGraphic = new NeutronGraphic();
    private static ProtonGraphic protonGraphic = new ProtonGraphic();

    private static void register( NucleusGraphic nucleusGraphic, Nucleus nucleus ) {
        ArrayList al = (ArrayList)graphicToModelMap.get( nucleus );
        if( al == null ) {
            al = new ArrayList();
            graphicToModelMap.put( nucleus, al );
        }
        al.add( nucleusGraphic );
    }

    public static ArrayList getGraphicForNucleus( Nucleus nucleus ) {
        Object obj = graphicToModelMap.get( nucleus );
        return (ArrayList)obj;
    }

    public static void removeGraphicForNucleus( Nucleus nucleus ) {
        graphicToModelMap.remove( nucleus );
    }

    public static void removeAllGraphics() {
        graphicToModelMap.clear();
    }

    //
    // Instance fields and methods
    //
    private Point2D.Double position = new Point2D.Double();
    Nucleus nucleus;
    private Image img;
    private AffineTransform atx = new AffineTransform();

    public NucleusGraphic( Nucleus nucleus ) {
        nucleus.addObserver( this );
        register( this, nucleus );
        this.nucleus = nucleus;
        this.position.x = nucleus.getPosition().getX();
        this.position.y = nucleus.getPosition().getY();
        img = computeImage();
    }

    public void setTransform( AffineTransform atx ) {
        this.atx = atx;
    }

    protected Image computeImage() {
        BufferedImage bi = new BufferedImage( (int)( nucleus.getRadius() + NuclearParticle.RADIUS ) * 2,
                                              (int)( nucleus.getRadius() + NuclearParticle.RADIUS ) * 2,
                                              BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = (Graphics2D)bi.getGraphics();
        double dx = 0, dy = 0;
        int neutronIdx = 0, protonIdx = 0;
        boolean drawNeutron;
        for( int i = 0; i < nucleus.getNumNeutrons() + nucleus.getNumProtons(); i++ ) {
            drawNeutron = ( Math.random() > 0.5 ) ? true : false;
            dx = 2 * ( Math.random() - 0.5 ) * nucleus.getRadius();
            dy = 2 * ( Math.random() - 0.5 ) * Math.sqrt( nucleus.getRadius() * nucleus.getRadius() - dx * dx );
            if( drawNeutron && neutronIdx < nucleus.getNumNeutrons() ) {
                neutronIdx++;
                neutronGraphic.paint( g, nucleus.getRadius() + dx, nucleus.getRadius() + dy );
            }
            else if( !drawNeutron && protonIdx < nucleus.getNumProtons() ) {
                protonIdx++;
                protonGraphic.paint( g, nucleus.getRadius() + dx, nucleus.getRadius() + dy );
            }
        }
        g.dispose();
        return bi;
    }

    public void paint( Graphics2D g2, double x, double y ) {
        GraphicsState gs = new GraphicsState( g2 );
        g2.transform( atx );
        g2.drawImage( img,
                      (int)( x - nucleus.getRadius() - NuclearParticle.RADIUS ),
                      (int)( y - nucleus.getRadius() - NuclearParticle.RADIUS ),
                      this );
        gs.restoreGraphics();
    }

    public void paint( Graphics2D g2 ) {
        GraphicsState gs = new GraphicsState( g2 );
        g2.transform( atx );
        update();
        g2.drawImage( img, (int)position.getX(), (int)position.getY(), this );
        gs.restoreGraphics();
    }

    public void update() {
        this.position.x = nucleus.getPosition().getX() - nucleus.getRadius() - NuclearParticle.RADIUS;
        this.position.y = nucleus.getPosition().getY() - nucleus.getRadius() - NuclearParticle.RADIUS;
    }

    public Nucleus getNucleus() {
        return nucleus;
    }

    public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
        return false;
    }
}
