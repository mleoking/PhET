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

    public NucleusGraphic( Nucleus nucleus ) {
        nucleus.addObserver( this );
        this.nucleus = nucleus;
        this.position.x = nucleus.getPosition().getX();
        this.position.y = nucleus.getPosition().getY();
        this.neutronGraphic = new NeutronGraphic();
        this.protonGraphic = new ProtonGraphic();
        img = computeImage();
    }

    private Image computeImage() {
        int numParticles = nucleus.getNumNeutrons() + nucleus.getNumProtons();
        double particleArea = ( Math.PI * NuclearParticle.RADIUS * NuclearParticle.RADIUS ) * numParticles;
        double nuclearRadius = Math.sqrt( particleArea / Math.PI ) / 3;
        BufferedImage bi = new BufferedImage( (int)nuclearRadius * 2, (int)nuclearRadius * 2, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = (Graphics2D)bi.getGraphics();
        double dx = 0, dy = 0;
        int neutronIdx = 0, protonIdx = 0;
        boolean drawNeutron;
        for( int i = 0; i < nucleus.getNumNeutrons() + nucleus.getNumProtons(); i++ ) {
            drawNeutron = ( Math.random() > 0.5 ) ? true : false;
//            dx = ( Math.random() - 0.5 ) * NuclearParticle.RADIUS * ( i * 0.1 + 1 );
//            dy = ( Math.random() - 0.5 ) * NuclearParticle.RADIUS * ( i * 0.1 + 1 );
            dx = 2 * ( Math.random() - 0.5 ) * nuclearRadius;
            dy = 2 * ( Math.random() - 0.5 ) * Math.sqrt( nuclearRadius * nuclearRadius - dx * dx );
            if( drawNeutron && neutronIdx < nucleus.getNumNeutrons() ) {
                neutronIdx++;
                neutronGraphic.paint( g,
                                      position.getX() + dx,
                                      position.getY() + dy );
            }
            else if( !drawNeutron && protonIdx < nucleus.getNumProtons() ) {
                protonIdx++;
                protonGraphic.paint( g,
                                     position.getX() + dx,
                                     position.getY() + dy );
            }
        }
        return bi;
    }

    public void paint( Graphics2D g ) {
        g.drawImage( img, (int)position.getX(), (int)position.getY(), this );
/*
        int numParticles = nucleus.getNumNeutrons() + nucleus.getNumProtons();
        double particleArea = ( Math.PI * NuclearParticle.RADIUS * NuclearParticle.RADIUS ) * numParticles;
        double nuclearRadius = Math.sqrt( particleArea / Math.PI ) / 3;
        double dx = 0, dy = 0;
        int neutronIdx = 0, protonIdx = 0;
        boolean drawNeutron;
        for( int i = 0; i < nucleus.getNumNeutrons() + nucleus.getNumProtons(); i++ ) {
            drawNeutron = ( Math.random() > 0.5 ) ? true : false;
//            dx = ( Math.random() - 0.5 ) * NuclearParticle.RADIUS * ( i * 0.1 + 1 );
//            dy = ( Math.random() - 0.5 ) * NuclearParticle.RADIUS * ( i * 0.1 + 1 );
            dx = 2 * ( Math.random() - 0.5 ) * nuclearRadius;
            dy = 2 * ( Math.random() - 0.5 ) * Math.sqrt( nuclearRadius * nuclearRadius - dx * dx );
            if( drawNeutron && neutronIdx < nucleus.getNumNeutrons() ) {
                neutronIdx++;
                neutronGraphic.paint( g,
                                      position.getX() + dx,
                                      position.getY() + dy );
            }
            else if( !drawNeutron && protonIdx < nucleus.getNumProtons() ) {
                protonIdx++;
                protonGraphic.paint( g,
                                     position.getX() + dx,
                                     position.getY() + dy );
            }
        }
*/
    }

    public void update() {
        this.position.x = nucleus.getPosition().getX();
        this.position.y = nucleus.getPosition().getY();
    }

    public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
