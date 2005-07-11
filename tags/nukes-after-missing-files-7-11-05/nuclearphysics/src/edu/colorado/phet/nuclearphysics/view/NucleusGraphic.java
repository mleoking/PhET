/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.nuclearphysics.model.NuclearParticle;
import edu.colorado.phet.nuclearphysics.model.Nucleus;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * NucleusGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class NucleusGraphic extends PhetImageGraphic implements SimpleObserver {

    private NeutronGraphic neutronGraphic;
    private ProtonGraphic protonGraphic;
    private Nucleus nucleus;
    private BufferedImage img;
    private AffineTransform atx = new AffineTransform();

    /**
     * @param component
     * @param nucleus
     */
    public NucleusGraphic( Component component, Nucleus nucleus ) {
        super( component );
        nucleus.addObserver( this );

        neutronGraphic = new NeutronGraphic( component );
        protonGraphic = new ProtonGraphic( component );
        this.nucleus = nucleus;
        img = computeImage();
        setImage( img );

        setRegistrationPoint( img.getWidth() / 2,
                              img.getHeight() / 2 );
        update();
    }

//    public void setTransform( AffineTransform atx ) {
//        this.atx = atx;
//    }

    protected BufferedImage computeImage() {
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

//    public void paint( Graphics2D g2, double x, double y ) {
//        setLocation( (int)x, (int)y );
//        super.paint( g2 );
//    }

    public void update() {
        setLocation( (int)nucleus.getPosition().getX(), (int)nucleus.getPosition().getY() );
    }

    public Nucleus getNucleus() {
        return nucleus;
    }
}
