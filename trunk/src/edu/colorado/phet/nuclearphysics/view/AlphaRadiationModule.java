/**
 * Class: AlphaRadiationModule
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 11:58:03 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.coreadditions.AlphaSetter;
import edu.colorado.phet.nuclearphysics.controller.AlphaDecayControlPanel;
import edu.colorado.phet.nuclearphysics.model.DecayListener;
import edu.colorado.phet.nuclearphysics.model.DecayProducts;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class AlphaRadiationModule extends OneNucleusModule implements DecayListener {

    //
    // Statics
    //
    private static int numAlphaParticles = 4;


    public AlphaRadiationModule( AbstractClock clock ) {
        super( "Alpha Radiation", clock );
        super.addControlPanelElement( new AlphaDecayControlPanel( this ) );

        getNucleus().addDecayListener( this );

        for( int i = 0; i < getNucleus().getAlphaParticles().length; i++ ) {
            addAlphaParticle( getNucleus().getAlphaParticles()[i] );
        }
        addRingGraphic();
    }

    private void addRingGraphic() {
        // Add a ring around the nucleus to show where its alpha decay radius is
        double radius = Math.abs( getNucleus().getPotentialProfile().getAlphaDecayX() );// + AlphaParticle.RADIUS;
        double x = getNucleus().getLocation().getX() - radius;
        double y = getNucleus().getLocation().getY() - radius;
        final Ellipse2D.Double alphaRing = new Ellipse2D.Double( x, y, radius * 2, radius * 2 );
        final Line2D.Double line1 = new Line2D.Double( x, -1000, x, 1000 );
        final Line2D.Double line2 = new Line2D.Double( x + radius * 2, -1000, x + radius * 2, 1000 );
        final Stroke ringStroke = new BasicStroke( 2f );
        final Stroke leaderLineStroke = new BasicStroke( 1f );
        Graphic ringGraphic = new Graphic() {
            public void paint( Graphics2D g ) {
                GraphicsUtil.setAntiAliasingOn( g );
                AlphaSetter.set( g, 0.3 );
                g.setColor( Color.blue );
                g.setStroke( ringStroke );
                g.draw( alphaRing );
                AlphaSetter.set( g, 1 );
            }
        };
        // Add leader lines from the ring up to the profile
        this.getPotentialProfilePanel().addOriginCenteredGraphic( ringGraphic );
        Graphic leaderLines = new Graphic() {
            public void paint( Graphics2D g ) {
                g.setColor( Color.black );
                g.setStroke( leaderLineStroke );
                AlphaSetter.set( g, 0.3 );
                g.draw( line1 );
                g.draw( line2 );
                AlphaSetter.set( g, 1 );
            }
        };
        this.getPotentialProfilePanel().addOriginCenteredGraphic( leaderLines );
    }


    public void alphaDecay( DecayProducts decayProducts ) {
        getPotentialProfilePanel().addDecayProduct( decayProducts.getN2() );
        getPhysicalPanel().addNucleus( decayProducts.getN2() );
        getModel().addModelElement( decayProducts.getN2() );
        Kaboom kaboom = new Kaboom( new Point2D.Double( 0, 0 ),
                                    25, 300, getPotentialProfilePanel() );
        getPotentialProfilePanel().addOriginCenteredGraphic( kaboom );
    }

    public void run() {
        super.run();
        getNucleus().addDecayListener( this );
        for( int i = 0; i < getNucleus().getAlphaParticles().length; i++ ) {
            addAlphaParticle( getNucleus().getAlphaParticles()[i] );
        }
        addRingGraphic();
    }
}
