/**
 * Class: NucleusGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.Particle;

import java.awt.*;

public class NucleusGraphic implements Graphic {
    private Nucleus nucleus;
    private NeutronGraphic neutronGraphic;
    private ProtonGraphic protonGraphic;

    public NucleusGraphic( Nucleus nucleus ) {
        this.nucleus = nucleus;
        this.neutronGraphic = new NeutronGraphic();
        this.protonGraphic = new ProtonGraphic();
    }

    public void paint( Graphics2D g ) {
        double dx = 0, dy = 0;
        int neutronIdx = 0, protonIdx = 0;
        boolean drawNeutron;
        for( int i = 0; i < nucleus.getNumNeutrons() + nucleus.getNumProtons(); i++ ) {
            drawNeutron = ( Math.random() > 0.5 ) ? true : false;
            dx = ( Math.random() - 0.5 ) * Particle.RADIUS * ( i * 0.1 + 1 );
            dy = ( Math.random() - 0.5 ) * Particle.RADIUS * ( i * 0.1 + 1 );
            if( drawNeutron && neutronIdx < nucleus.getNumNeutrons() ) {
                neutronIdx++;
                neutronGraphic.paint( g,
                                      nucleus.getPosition().getX() + dx,
                                      nucleus.getPosition().getY() + dy );
            }
            else if( !drawNeutron && protonIdx < nucleus.getNumProtons() ) {
                protonIdx++;
                protonGraphic.paint( g,
                                     nucleus.getPosition().getX() + dx,
                                     nucleus.getPosition().getY() + dy );
            }
        }
    }
}
