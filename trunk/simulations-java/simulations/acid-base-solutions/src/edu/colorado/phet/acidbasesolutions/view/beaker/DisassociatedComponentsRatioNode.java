/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.beaker;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Depicts the ratio of the disassociated components (HA/A, B/BH, MOH/M) as a set of "dots".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DisassociatedComponentsRatioNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double BASE_CONCENTRATION = 1E-6;
    private static final int BASE_DOTS = 2;
    private static final int MAX_DOTS = 3000;
    private static final double BASE_FACTOR = Math.pow( ( MAX_DOTS / BASE_DOTS ), ( 1 / MathUtil.log10( 1 / BASE_CONCENTRATION ) ) );

    private static final double DOT_DIAMETER = 6;
    
    // transparency of dots0-255, transparent-opaque
    private static final int MAJORITY_TRANSPARENCY = 140; // majority species
    private static final int MINORITY_TRANSPARENCY = 140; // minority species
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final AqueousSolution solution;
    
    private final PBounds containerBounds;
    private final PNode particlesParent;
    private final Random randomCoordinate;
    private HTMLNode numbersNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
   
    public DisassociatedComponentsRatioNode( AqueousSolution solution, PBounds containerBounds ) {
        super();
        
        // not interactive
        setPickable( false );
        setChildrenPickable( false );
        
        this.solution = solution;
        solution.addSolutionListener( new SolutionListener() {

            public void concentrationChanged() {
                update();
            }

            public void soluteChanged() {
                update();
            }

            public void strengthChanged() {
                update();
            }
        });
        
        this.containerBounds = new PBounds( containerBounds );
        randomCoordinate = new Random();
        
        particlesParent = new PNode();
        addChild( particlesParent );
        
        // developer only, display particle counts in lower left of container
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            numbersNode = new HTMLNode( "?" );
            numbersNode.setFont( new PhetFont( 16 ) );
            addChild( numbersNode );
            numbersNode.setOffset( containerBounds.getX() + 5, containerBounds.getMaxY() - numbersNode.getFullBoundsReference().getHeight() - 15 );
        }
        
        update();
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
    
    /*
     * Updates the view to match the model.
     */
    private void update() {
        if ( getVisible() ) {
            createParticles();
        }
    }
    
    /*
     * Updates the debugging display that shows the number of each particle type
     */
    private void updateNumbersNode( int numReactantDots, int numProductDots ) {
        if ( numbersNode != null ) {
            Solute solute = solution.getSolute();
            String html = HTMLUtils.toHTMLString( solute.getSymbol() + "/" + solute.getConjugateSymbol() + " = " + numReactantDots + "/" + numProductDots );
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
        int numberOfReactantDots = 0;
        int numberOfProductDots = 0;
        
        if ( !solution.isPureWater() ) {

            numberOfReactantDots = getNumberOfDots( solution.getReactantConcentration() );
            numberOfProductDots = getNumberOfDots( solution.getProductConcentration() );
            
            // create particles, minority species in foreground
            if ( numberOfReactantDots > numberOfProductDots ) {
                createReactantNodes( (int) numberOfReactantDots, MAJORITY_TRANSPARENCY );
                createProductNodes( (int) numberOfProductDots, MINORITY_TRANSPARENCY );
            }
            else {
                createProductNodes( (int) numberOfProductDots, MAJORITY_TRANSPARENCY );
                createReactantNodes( (int) numberOfReactantDots, MINORITY_TRANSPARENCY );
            }
        }
        
        // developer display
        updateNumbersNode( numberOfReactantDots, numberOfProductDots );
    }
    
    private int getNumberOfDots( double concentration ) {
        final double raiseFactor = MathUtil.log10( concentration / BASE_CONCENTRATION );
        return (int)( BASE_DOTS * Math.pow( BASE_FACTOR, raiseFactor ) );
    }
    
    /*
     * Creates reactant nodes at random locations.
     */
    private void createReactantNodes( int count, int alpha ) {
        Point2D pOffset = new Point2D.Double();
        Color color = ColorUtils.createColor( solution.getSolute().getColor(), alpha );
        for ( int i = 0; i < count; i++ ) {
            getRandomPoint( containerBounds, pOffset );
            DotNode p = new DotNode( DOT_DIAMETER, color );
            p.setOffset( pOffset );
            particlesParent.addChild( p );
        }
    }

    /*
     * Creates product nodes at random locations.
     */
    private void createProductNodes( int count, int alpha ) {
        Point2D pOffset = new Point2D.Double();
        Color color = ColorUtils.createColor( solution.getSolute().getConjugateColor(), alpha );
        for ( int i = 0; i < count; i++ ) {
            getRandomPoint( containerBounds, pOffset );
            DotNode p = new DotNode( DOT_DIAMETER, color );
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
    
    /* Base class for all dot nodes */
    private static class DotNode extends PPath {

        private Ellipse2D _ellipse;

        public DotNode( double diameter, Color color ) {
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
}
