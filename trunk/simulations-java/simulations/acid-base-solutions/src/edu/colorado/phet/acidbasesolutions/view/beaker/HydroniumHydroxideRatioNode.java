/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.beaker;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.acidbasesolutions.ABSColors;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Depicts the ratio of H3O/OH as a set of particles.
 * In pH range 6 to 8, the relationship between number of particles and pH is log.
 * Outside of this range, we can't possibly draw that many particles, so we 
 * fake it using a linear relationship.
 * <p>
 * This class is highly parameterized, and its behavior is intended to be 
 * tweaked and tuned using developer controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HydroniumHydroxideRatioNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double ACID_PH_THRESHOLD = 6;
    private static final double BASE_PH_THRESHOLD = 8;
    
    private static final int DEFAULT_NUM_PARTICLES_AT_PH7 = 100;
    private static final int DEFAULT_NUM_PARTICLES_AT_PH15 = 3000;
    private static final int DEFAULT_MIN_MINORITY_PARTICLES = 5;
    private static final double DEFAULT_DIAMETER = 6;
    private static final int DEFAULT_MAJORITY_TRANSPARENCY = 140; // 0-255, transparent-opaque
    private static final int DEFAULT_MINORITY_TRANSPARENCY = 255; // 0-255, transparent-opaque
    private static final Color DEFAULT_H3O_COLOR = ABSColors.H3O_PLUS;
    private static final Color DEFAULT_OH_COLOR = ABSColors.OH_MINUS;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final AqueousSolution solution;
    
    private final PBounds containerBounds;
    private final PNode particlesParent;
    private final Random randomCoordinate;
    private HTMLNode numbersNode;
   
    private int numberOfParticlesAtPH7; // number of particles created when pH=7
    private int numberOfParticlesAtPH15; // number of particles created when pH=15
    private int minMinorityParticles; // min number of minority type of particle
    private double diameter; // diameter of all particles, view coordinates
    private int majorityTransparency; // transparency of majority type of particle, 0-255 
    private int minorityTransparency; // transparency of minority type of particle, 0-255
    private Color h3oColor, ohColor; // base colors, no alpha
    private int numberOfH3O, numberOfOH; // current number of each type of particle
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
   
    public HydroniumHydroxideRatioNode( AqueousSolution solution, PBounds containerBounds ) {
        super();
        
        // not interactive
        setPickable( false );
        setChildrenPickable( false );
        
        this.solution = solution;
        
        this.containerBounds = new PBounds( containerBounds );
        randomCoordinate = new Random();
        
        particlesParent = new PNode();
        addChild( particlesParent );
        
        // developer only, display particle counts in lower left of container
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            numbersNode = new HTMLNode( "?" );
            numbersNode.setFont( new PhetFont() );
            addChild( numbersNode );
            numbersNode.setOffset( containerBounds.getX() + 5, containerBounds.getMaxY() - numbersNode.getFullBoundsReference().getHeight() - 15 );
        }
        
        numberOfParticlesAtPH15 = DEFAULT_NUM_PARTICLES_AT_PH15;
        numberOfParticlesAtPH7 = DEFAULT_NUM_PARTICLES_AT_PH7;
        minMinorityParticles = DEFAULT_MIN_MINORITY_PARTICLES;
        diameter = DEFAULT_DIAMETER;
        majorityTransparency = DEFAULT_MAJORITY_TRANSPARENCY;
        minorityTransparency = DEFAULT_MINORITY_TRANSPARENCY;
        h3oColor = DEFAULT_H3O_COLOR;
        ohColor = DEFAULT_OH_COLOR;
        numberOfH3O = 0;
        numberOfOH = 0;
        
        update();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setNumberOfParticlesAtPH15( int num ) {
        if ( num != numberOfParticlesAtPH15 ) {
            numberOfParticlesAtPH15 = num;
            createParticles();
        }
    }
    
    public int getNumberOfParticlesAtPH15() {
        return numberOfParticlesAtPH15;
    }
    
    public int getNumberOfParticlesAtPH7() {
        return numberOfParticlesAtPH7;
    }
    
    public void setNumberOfParticlesAtPH7( int num ) {
        if ( num != numberOfParticlesAtPH7 ) {
            numberOfParticlesAtPH7 = num;
            createParticles();
        }
    }
    
    public void setMinMinorityParticles( int num ) {
        if ( num != minMinorityParticles ) {
            minMinorityParticles = num;
            createParticles();
        }
    }
    
    public int getMinMinorityParticles() {
        return minMinorityParticles;
    }
    
    public void setParticleDiameter( double diameter ) {
        if ( diameter != this.diameter ) {
            this.diameter = diameter;
            int count = particlesParent.getChildrenCount();
            for ( int i = 0; i < count; i++ ) {
                ( (ParticleNode) particlesParent.getChild( i ) ).setDiameter( diameter );
            }
        }
    }
    
    public double getParticleDiameter() {
        return diameter;
    }
    
    public void setMajorityTransparency( int transparency ) {
        if ( transparency != majorityTransparency ) {
            majorityTransparency = transparency;
            setH3OColor( h3oColor );
            setOHColor( ohColor );
        }
    }
    
    public int getMajorityTransparency() { 
        return majorityTransparency;
    }
    
    public void setMinorityTransparency( int transparency ) {
        if ( transparency != minorityTransparency ) {
            minorityTransparency = transparency;
            setH3OColor( h3oColor );
            setOHColor( ohColor );
        }
    }
    
    public int getMinorityTransparency() {
        return minorityTransparency;
    }
    
    public void setH3OColor( Color color ) {
        h3oColor = color;
        // compute color with alpha
        int alpha = ( numberOfH3O >= numberOfOH ? majorityTransparency : minorityTransparency );
        Color particleColor = ColorUtils.createColor( h3oColor, alpha );
        // update all H3O particle nodes
        int count = particlesParent.getChildrenCount();
        for ( int i = 0; i < count; i++ ) {
            PNode node = particlesParent.getChild( i );
            if ( node instanceof H3ONode ) {
                node.setPaint( particleColor );
            }
        }
    }
    
    public Color getH3OColor() {
        return h3oColor;
    }
    
    public void setOHColor( Color color ) {
        ohColor = color;
        // compute color with alpha
        int alpha = ( numberOfOH >= numberOfH3O ? majorityTransparency : minorityTransparency );
        Color particleColor = ColorUtils.createColor( ohColor, alpha );
        // update all OH particle nodes
        int count = particlesParent.getChildrenCount();
        for ( int i = 0; i < count; i++ ) {
            PNode node = particlesParent.getChild( i );
            if ( node instanceof OHNode ) {
                node.setPaint( particleColor );
            }
        }
    }
    
    public Color getOHColor() {
        return ohColor;
    }
    
    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------
    
    public void setVisible( boolean visible ) {
        if ( visible != getVisible() ) {
            super.setVisible( visible );
            if ( visible ) {
                update();
            }
            else {
                deleteAllParticles();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     */
    public void update() {
        if ( getVisible() ) {
            createParticles();
        }
    }
    
    /*
     * Updates the debugging display that shows the number of each particle type
     */
    private void updateNumbersNode( int h3o, int oh ) {
        if ( numbersNode != null ) {
            String html = HTMLUtils.toHTMLString( ABSSymbols.H3O_PLUS + "/" + ABSSymbols.OH_MINUS + "=" + h3o + "/" + oh );
            numbersNode.setHTML( html );
        }
    }
    
    //----------------------------------------------------------------------------
    // Particle creation
    //----------------------------------------------------------------------------
    
    /*
     * Deletes all particles.
     */
    private void deleteAllParticles() {
        particlesParent.removeAllChildren();
    }
    
    /*
     * Creates particle nodes based on the current pH value.
     * Particles are spread throughout the container without consideration of actual liquid volume.
     * This allows us to simply expose more particles (via LiquidNode) as the liquid's volume increases.
     */
    private void createParticles() {
        
        deleteAllParticles();
        
        final double pH = solution.getPH().getValue();

        if ( pH >= ACID_PH_THRESHOLD && pH <= BASE_PH_THRESHOLD ) {
            // # particles varies logarithmically in this range
            numberOfH3O = Math.max( minMinorityParticles, getNumberOfH3OParticles( pH ) );
            numberOfOH = Math.max( minMinorityParticles, getNumberOfOHParticles( pH ) );
        }
        else {
            // # particles varies linearly in this range
            final double pH15 = 15;
            // N is the number of particles to add for each 1 unit of pH above or below the thresholds
            final double N = ( numberOfParticlesAtPH15 - getNumberOfOHParticles( BASE_PH_THRESHOLD ) ) / ( pH15 - BASE_PH_THRESHOLD );
            if ( pH > BASE_PH_THRESHOLD ) {
                // strong base
                final double phDiff = pH - BASE_PH_THRESHOLD;
                numberOfH3O = (int) Math.max( minMinorityParticles, ( getNumberOfH3OParticles( BASE_PH_THRESHOLD ) - phDiff ) );
                numberOfOH = (int) ( getNumberOfOHParticles( BASE_PH_THRESHOLD ) + ( phDiff * N ) );
            }
            else {
                // strong acid
                final double phDiff = ACID_PH_THRESHOLD - pH;
                numberOfH3O = (int) ( getNumberOfH3OParticles( ACID_PH_THRESHOLD ) + ( phDiff * N ) );
                numberOfOH = (int) Math.max( minMinorityParticles, ( getNumberOfOHParticles( ACID_PH_THRESHOLD ) - phDiff ) );
            }
        }
        updateNumbersNode( numberOfH3O, numberOfOH );
        
        // create particles, minority species in foreground
        if ( numberOfH3O > numberOfOH ) {
            createH3ONodes( (int) numberOfH3O, majorityTransparency );
            createOHNodes( (int) numberOfOH, minorityTransparency );
        }
        else {
            createOHNodes( (int) numberOfOH, majorityTransparency );
            createH3ONodes( (int) numberOfH3O, minorityTransparency );
        }
    }
    
    private int getNumberOfH3OParticles( double pH ) {
        return (int) ( solution.getH3OConcentration( pH ) * ( numberOfParticlesAtPH7 / 2 ) / 1E-7 );
    }
    
    private int getNumberOfOHParticles( double pH ) {
        return (int) ( solution.getOHConcentration( pH ) * ( numberOfParticlesAtPH7 / 2 ) / 1E-7 );
    }
    
    /*
     * Creates H3O nodes at random locations.
     */
    private void createH3ONodes( int count, int alpha ) {
        Point2D pOffset = new Point2D.Double();
        Color color = ColorUtils.createColor( h3oColor, alpha );
        for ( int i = 0; i < count; i++ ) {
            getRandomPoint( containerBounds, pOffset );
            ParticleNode p = new H3ONode( diameter, color );
            p.setOffset( pOffset );
            particlesParent.addChild( p );
        }
    }

    /*
     * Creates OH nodes at random locations.
     */
    private void createOHNodes( int count, int alpha ) {
        Point2D pOffset = new Point2D.Double();
        Color color = ColorUtils.createColor( ohColor, alpha );
        for ( int i = 0; i < count; i++ ) {
            getRandomPoint( containerBounds, pOffset );
            ParticleNode p = new OHNode( diameter, color );
            p.setOffset( pOffset );
            particlesParent.addChild( p );
        }
    }

    /*
     * Gets a random point inside some bounds.
     */
    private void getRandomPoint( PBounds bounds, Point2D pOutput ) {
        double x = bounds.getX() + ( randomCoordinate.nextDouble() * bounds.getWidth() );
        double y = bounds.getY() + ( randomCoordinate.nextDouble() * bounds.getHeight() );
        pOutput.setLocation( x, y );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /* Base class for all particle nodes */
    private static abstract class ParticleNode extends PPath {

        private Ellipse2D _ellipse;

        public ParticleNode( double diameter, Color color ) {
            super();
            _ellipse = new Ellipse2D.Double();
            setPaint( color );
            setStroke( null );
            setDiameter( diameter );
        }

        public void setDiameter( double diameter ) {
            _ellipse.setFrame( -diameter / 2, -diameter / 2, diameter, diameter );
            setPathTo( _ellipse );
        }
    }

    /* H30 particle node, marker class */
    private static class H3ONode extends ParticleNode {
        public H3ONode( double diameter, Color color ) {
            super( diameter, color );
        }
    }

    /* OH particle node, marker class */
    private static class OHNode extends ParticleNode {
        public OHNode( double diameter, Color color ) {
            super( diameter, color );
        }
    }

}
