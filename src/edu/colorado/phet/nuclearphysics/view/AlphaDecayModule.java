/**
 * Class: AlphaDecayModule
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 11:58:03 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.controller.AlphaDecayControlPanel;
import edu.colorado.phet.nuclearphysics.controller.ProfiledNucleusModule;
import edu.colorado.phet.nuclearphysics.model.*;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class AlphaDecayModule extends ProfiledNucleusModule implements DecayListener {

    private AlphaDecayPhysicalPanel physicalPanel;
    private Ellipse2D.Double alphaRing;
    private Line2D.Double leaderLine1;
    private Line2D.Double leaderLine2;

    public AlphaDecayModule( AbstractClock clock ) {
        super( "Alpha Radiation", clock );

        getApparatusPanel().setLayout( new GridLayout( 2, 1 ) );
        physicalPanel = new AlphaDecayPhysicalPanel();
        super.setPhysicalPanel( physicalPanel );
        getApparatusPanel().remove( 0 );
        getApparatusPanel().add( physicalPanel, 0 );

        setNucleus( new Uranium235( new Point2D.Double( 0, 0 ), getModel() ) );
        setUraniumNucleus( getNucleus() );

        super.addControlPanelElement( new AlphaDecayControlPanel( this ) );

        getNucleus().addDecayListener( this );

        for( int i = 0; i < getNucleus().getAlphaParticles().length; i++ ) {
            addAlphaParticle( getNucleus().getAlphaParticles()[i], getNucleus() );
        }
        addRingGraphic();
    }

    protected void addAlphaParticle( AlphaParticle alphaParticle, Nucleus nucleus ) {
        this.getModel().addModelElement( alphaParticle );
        physicalPanel.addAlphaParticle( alphaParticle );
        getPotentialProfilePanel().addAlphaParticle( alphaParticle, nucleus );
    }

    private void addRingGraphic() {
        // Add a ring around the nucleus to show where its alpha decay radius is
        setRingAttributes();
//        double radius = Math.abs( getNucleus().getPotentialProfile().getAlphaDecayX() );// + AlphaParticle.RADIUS;
//        double x = getNucleus().getLocation().getX() - radius;
//        double y = getNucleus().getLocation().getY() - radius;
//        alphaRing = new Ellipse2D.Double( x, y, radius * 2, radius * 2 );
        final Stroke ringStroke = new BasicStroke( 2f );
        Graphic ringGraphic = new Graphic() {
            public void paint( Graphics2D g ) {
                GraphicsUtil.setAntiAliasingOn( g );
                GraphicsUtil.setAlpha( g, 0.3 );
                g.setColor( Color.blue );
                g.setStroke( ringStroke );
                g.draw( alphaRing );
                GraphicsUtil.setAlpha( g, 1 );
            }
        };
        this.getPhysicalPanel().addOriginCenteredGraphic( ringGraphic );

        // Add leader lines from the ring up to the profile
        final Stroke leaderLineStroke = new BasicStroke( 1f );
        Graphic leaderLines = new Graphic() {
            public void paint( Graphics2D g ) {
                g.setColor( Color.black );
                g.setStroke( leaderLineStroke );
                GraphicsUtil.setAlpha( g, 0.3 );
                g.draw( leaderLine1 );
                g.draw( leaderLine2 );
                GraphicsUtil.setAlpha( g, 1 );
            }
        };
        this.getPhysicalPanel().addOriginCenteredGraphic( leaderLines );
    }

    private void setRingAttributes() {
        double radius = Math.abs( getNucleus().getPotentialProfile().getAlphaDecayX() );
        double x = getNucleus().getLocation().getX() - radius;
        double y = getNucleus().getLocation().getY() - radius;
        alphaRing = new Ellipse2D.Double( x, y, radius * 2, radius * 2 );
        leaderLine1 = new Line2D.Double( x, -1000, x, 1000 );
        leaderLine2 = new Line2D.Double( x + radius * 2, -1000, x + radius * 2, 1000 );
    }


    public void alphaDecay( DecayProducts decayProducts ) {
        getPotentialProfilePanel().addDecayProduct( decayProducts.getN2() );

        // Make the nucleus shake
//        Thread shaker = new Thread( new NucleusShaker() );
//        shaker.run();

        getPhysicalPanel().addNucleus( decayProducts.getN2() );
        getModel().addModelElement( decayProducts.getN2() );

        setRingAttributes();
    }

    public void run() {
        super.run();
        getNucleus().addDecayListener( this );
        for( int i = 0; i < getNucleus().getAlphaParticles().length; i++ ) {
            addAlphaParticle( getNucleus().getAlphaParticles()[i], getNucleus() );
        }
        addRingGraphic();
    }

    //
    // Inner classes
    //
    private class NucleusShaker implements Runnable {

        public void run() {
            for( int i = 0; i < 20; i++ ) {
                ArrayList graphicList = NucleusGraphic.getGraphicForNucleus( getNucleus() );
                for( int j = 0; j < graphicList.size(); j++ ) {
                    Graphic graphic = (Graphic)graphicList.get( j );
                    getPhysicalPanel().removeGraphic( graphic );
                }
                getPhysicalPanel().addNucleus( getNucleus() );
                getPhysicalPanel().repaint();
                try {
                    Thread.sleep( 200 );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }
}
