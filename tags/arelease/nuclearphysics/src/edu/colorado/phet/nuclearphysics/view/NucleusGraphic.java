/**
 * Class: NucleusGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.nuclearphysics.model.NuclearParticle;
import edu.colorado.phet.nuclearphysics.model.Nucleus;

import java.awt.*;
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

    //
    // Instance fields and methods
    //
    private Point2D.Double position = new Point2D.Double();
    Nucleus nucleus;
    private NeutronGraphic neutronGraphic;
    private ProtonGraphic protonGraphic;
    private Image img;

    public NucleusGraphic( Nucleus nucleus ) {
        nucleus.addObserver( this );
        register( this, nucleus );
        this.nucleus = nucleus;
        this.position.x = nucleus.getLocation().getX();
        this.position.y = nucleus.getLocation().getY();
        this.neutronGraphic = new NeutronGraphic();
        this.protonGraphic = new ProtonGraphic();
        img = computeImage();
    }

    private Image computeImage() {
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
        return bi;
    }

    public void paint( Graphics graphics, int x, int y ) {
        Graphics2D g2 = (Graphics2D)graphics;
        g2.drawImage( img,
                      x - (int)( nucleus.getRadius() - NuclearParticle.RADIUS ),
                      y - (int)( nucleus.getRadius() - NuclearParticle.RADIUS ),
                      this );
    }

    public void paint( Graphics2D g ) {
        g.drawImage( img, (int)position.getX(), (int)position.getY(), this );
    }

    public void update() {
        this.position.x = nucleus.getLocation().getX() - nucleus.getRadius() - NuclearParticle.RADIUS;
        this.position.y = nucleus.getLocation().getY() - nucleus.getRadius() - NuclearParticle.RADIUS;
    }

    public Nucleus getNucleus() {
        return nucleus;
    }

    public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
        return false;
    }
}
