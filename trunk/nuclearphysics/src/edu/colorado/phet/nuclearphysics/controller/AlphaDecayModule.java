/**
 * Class: AlphaDecayModule
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 11:58:03 AM
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.Config;
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.AlphaDecayPhysicalPanel;
import edu.colorado.phet.nuclearphysics.view.Kaboom;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class AlphaDecayModule extends ProfiledNucleusModule implements DecayListener {

    private AlphaDecayPhysicalPanel physicalPanel;
    private Ellipse2D.Double alphaRing;
    private Line2D.Double leaderLine1;
    private Line2D.Double leaderLine2;
    private AlphaDecayControlPanel alphaDecayControlPanel;
    private Graphic ringGraphic;
    private Graphic leaderLines;
    private double ringLevel = Config.backgroundGraphicLevel;
    private double leaderLineLevel = 0;

    float miterLimit = 10f;
    float[] dashPattern = {10f};
    float dashPhase = 5f;
    final Stroke leaderLineStroke = new BasicStroke( 1f, BasicStroke.CAP_BUTT,
                                                     BasicStroke.JOIN_MITER, miterLimit, dashPattern, dashPhase );

    public AlphaDecayModule( AbstractClock clock ) {
        super( "Alpha Radiation", clock );

        // DEBUG ONLY!!!
        //        clock.setDt( clock.getDt() / 10 );

        getApparatusPanel().setLayout( new GridLayout( 2, 1 ) );
        physicalPanel = new AlphaDecayPhysicalPanel();
        super.setPhysicalPanel( physicalPanel );
        getApparatusPanel().remove( 0 );
        getApparatusPanel().add( physicalPanel, 0 );
        alphaDecayControlPanel = new AlphaDecayControlPanel( this );
        super.addControlPanelElement( alphaDecayControlPanel );
    }

    public void start() {
        // todo: combine these calls
        Uranium235 nucleus = new Uranium235( new Point2D.Double( 0, 0 ), getModel() );
        setNucleus( nucleus );
        setUraniumNucleus( nucleus );

        // Very ugly, but it works for now
        //        ( (Uranium235Graphic)( (ArrayList)NucleusGraphic.getGraphicForNucleus( nucleus ) ).get( 0 ) ).setDisplayLabel( false );
        nucleus.addDecayListener( this );

        // Add the nucleus' jumping alpha particles to the model and the panels
        for( int i = 0; i < nucleus.getAlphaParticles().length; i++ ) {
            addAlphaParticle( nucleus.getAlphaParticles()[i], getNucleus() );
        }
        addRingGraphic( getNucleus() );
        alphaDecayControlPanel.startTimer();
    }

    public void stop() {
        Nucleus nucleus = getNucleus();
        getModel().removeModelElement( nucleus );

        ( (NuclearPhysicsModel)getModel() ).removeNuclearPartilces();

        getPotentialProfilePanel().removeAllAlphaParticles();
        getPotentialProfilePanel().removeAllPotentialProfiles();
        getPotentialProfilePanel().removeGraphic( leaderLines );

        getPhysicalPanel().clear();
        getPhysicalPanel().removeAllGraphics();
        getPhysicalPanel().removeGraphic( ringGraphic );
        getPhysicalPanel().removeGraphic( leaderLines );

        //        NucleusGraphic.removeAllGraphics();

        alphaDecayControlPanel.stopTimer();
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        this.start();
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        this.stop();
    }

    protected void addAlphaParticle( AlphaParticle alphaParticle, Nucleus nucleus ) {
        // The following line is inserted to try to solve a bug Wendy found, that show's up
        // only on her computer. If she hits the "Reset" button before alpha decay has happened,
        // the new alpha particles appear first at (0,0) on the profile panel, rather than at
        // the bottom of the potential well.
        alphaParticle.stepInTime( 0 );
        this.getModel().addModelElement( alphaParticle );
        physicalPanel.addAlphaParticle( alphaParticle );
        getPotentialProfilePanel().addAlphaParticle( alphaParticle, nucleus );
    }

    private void addRingGraphic( Nucleus nucleus ) {
        // Add a ring around the nucleus to show where its alpha decay radius is
        setRingAttributes( nucleus );
        final Stroke ringStroke = new BasicStroke( 2f );
        ringGraphic = new Graphic() {
            public void paint( Graphics2D g ) {
                if( alphaRing != null ) {
                    GraphicsUtil.setAntiAliasingOn( g );
                    GraphicsUtil.setAlpha( g, 0.4 );
                    g.setColor( Color.blue );
                    g.setStroke( ringStroke );
                    g.draw( alphaRing );
                    GraphicsUtil.setAlpha( g, 1 );
                }
            }
        };
        this.getPhysicalPanel().addOriginCenteredGraphic( ringGraphic, ringLevel );

        // Add leader lines from the ring up to the profile
        leaderLines = new Graphic() {
            public void paint( Graphics2D g ) {
                if( leaderLine1 != null && leaderLine2 != null ) {
                    g.setColor( Color.black );
                    g.setStroke( leaderLineStroke );
                    GraphicsUtil.setAlpha( g, 0.4 );
                    g.draw( leaderLine1 );
                    g.draw( leaderLine2 );
                    GraphicsUtil.setAlpha( g, 1 );
                }
            }
        };
        this.getPhysicalPanel().addOriginCenteredGraphic( leaderLines, leaderLineLevel );
        this.getPotentialProfilePanel().addOriginCenteredGraphic( leaderLines );
    }

    private void setRingAttributes( Nucleus nucleus ) {
        if( nucleus.getPotentialProfile().getAlphaDecayX() < 0 ) {
            double radius = Math.abs( nucleus.getPotentialProfile().getAlphaDecayX() );
            double x = getNucleus().getPosition().getX() - radius;
            double y = getNucleus().getPosition().getY() - radius;
            alphaRing = new Ellipse2D.Double( x, y, radius * 2, radius * 2 );
            leaderLine1 = new Line2D.Double( x, -1000, x, 1000 );
            leaderLine2 = new Line2D.Double( x + radius * 2, -1000, x + radius * 2, 1000 );
        }
        else {
            alphaRing = null;
            leaderLine1 = null;
            leaderLine2 = null;
        }
    }

    public void alphaDecay( AlphaDecayProducts decayProducts ) {

        alphaDecayControlPanel.stopTimer();

        //Remove old nucleus
        getModel().removeModelElement( decayProducts.getParent() );

        //        java.util.List graphics = (java.util.List)NucleusGraphic.getGraphicForNucleus( decayProducts.getParent() );
        //        for( int i = 0; i < graphics.size(); i++ ) {
        //            NucleusGraphic ng = (NucleusGraphic)graphics.get( i );
        //            getPotentialProfilePanel().removeGraphic( ng );
        //            this.getPhysicalPanel().removeGraphic( ng );
        //        }
        getPotentialProfilePanel().removePotentialProfile( decayProducts.getParent().getPotentialProfile() );

        // Bind the alpha particles to the daughter nucleus
        Uranium235 u235 = (Uranium235)decayProducts.getParent();
        AlphaParticle[] alphaParticles = u235.getAlphaParticles();
        for( int i = 0; i < alphaParticles.length; i++ ) {
            AlphaParticle alphaParticle = alphaParticles[i];
            alphaParticle.setNucleus( decayProducts.getDaughter() );
        }

        // Add the daughter nucleus
        getPhysicalPanel().addNucleus( decayProducts.getDaughter() );
        //        NucleusGraphic daughterGraphic = new NucleusGraphic( decayProducts.getDaughter() );
        //        getPhysicalPanel().addGraphic( daughterGraphic );
        getModel().addModelElement( decayProducts.getDaughter() );
        getPotentialProfilePanel().addPotentialProfile( decayProducts.getDaughter() );

        // Set the size of the alpha decay threshold ring and the positions of the
        // leader lines
        setRingAttributes( decayProducts.getDaughter() );

        // Make a bang!
        Kaboom kaboom = new Kaboom( new Point2D.Double(), 25, 300, getPhysicalPanel() );
        getPhysicalPanel().addGraphic( kaboom );
    }
}

