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

public class NucleusGraphic implements Graphic, SimpleObserver, ImageObserver {
    private Point2D.Double position = new Point2D.Double();
    private Nucleus nucleus;
    private NeutronGraphic neutronGraphic;
    private ProtonGraphic protonGraphic;
    private Image img;
    private double nuclearRadius;

    public NucleusGraphic( Nucleus nucleus ) {

        // Register the graphic to the model element
//        modelElementToGraphicMap.put( nucleus, this );

        nucleus.addObserver( this );
        this.nucleus = nucleus;
        this.position.x = nucleus.getLocation().getX();
        this.position.y = nucleus.getLocation().getY();
        this.neutronGraphic = new NeutronGraphic();
        this.protonGraphic = new ProtonGraphic();
        img = computeImage();
    }

    private Image computeImage() {
        int numParticles = nucleus.getNumNeutrons() + nucleus.getNumProtons();
        double particleArea = ( Math.PI * NuclearParticle.RADIUS * NuclearParticle.RADIUS ) * numParticles;
        nuclearRadius = Math.sqrt( particleArea / Math.PI ) / 2;
        BufferedImage bi = new BufferedImage( (int)( nuclearRadius + NuclearParticle.RADIUS ) * 2,
                                              (int)( nuclearRadius + NuclearParticle.RADIUS ) * 2,
                                              BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = (Graphics2D)bi.getGraphics();
        double dx = 0, dy = 0;
        int neutronIdx = 0, protonIdx = 0;
        boolean drawNeutron;
        for( int i = 0; i < nucleus.getNumNeutrons() + nucleus.getNumProtons(); i++ ) {
            drawNeutron = ( Math.random() > 0.5 ) ? true : false;
            dx = 2 * ( Math.random() - 0.5 ) * nuclearRadius;
            dy = 2 * ( Math.random() - 0.5 ) * Math.sqrt( nuclearRadius * nuclearRadius - dx * dx );
            if( drawNeutron && neutronIdx < nucleus.getNumNeutrons() ) {
                neutronIdx++;
                neutronGraphic.paint( g,
                                      nuclearRadius + dx,
                                      nuclearRadius + dy );
            }
            else if( !drawNeutron && protonIdx < nucleus.getNumProtons() ) {
                protonIdx++;
                protonGraphic.paint( g,
                                     nuclearRadius + dx,
                                     nuclearRadius + dy );
            }
        }
        return bi;
    }

    public void paint( Graphics graphics, int x, int y ) {
        Graphics2D g2 = (Graphics2D)graphics;
        g2.drawImage( img, x - (int)nuclearRadius,
                      y - (int)nuclearRadius,
                      this );
    }

    public void paintPotentialRendering( Graphics graphics, int x, int y ) {
        Graphics2D g2 = (Graphics2D)graphics;
        double xStat = nucleus.getStatisticalLocationOffset().getX();
        double yStat = nucleus.getStatisticalLocationOffset().getY();
        double d = Math.sqrt( xStat * xStat + yStat * yStat ) * ( xStat > 0 ? 1 : -1 );
        g2.drawImage( img, x - (int)nuclearRadius + (int)d, y, this );
    }

    public void paint( Graphics2D g ) {
        g.drawImage( img, (int)position.getX(), (int)position.getY(), this );
    }

    public void update() {
        this.position.x = nucleus.getLocation().getX();
        this.position.y = nucleus.getLocation().getY();
//        this.position.x = nucleus.getLocation().getX() + nucleus.getStatisticalLocationOffset().getX();
//        this.position.y = nucleus.getLocation().getY() + nucleus.getStatisticalLocationOffset().getY();
    }

    public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


    //
    // Statics
    //
//    private static HashMap modelElementToGraphicMap = new HashMap();
//
//    public static Graphic getGraphic( Nucleus nucleus ) {
//        return (Graphic)modelElementToGraphicMap.get( nucleus );
//    }
}
