/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.beaker;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import edu.colorado.phet.acidbasesolutions.ABSColors;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
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
 * Depicts the ratio of concentrations (HA/A, B/BH, MOH/M, H3O/OH) as a set of "dots".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RatioDotsNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double BASE_CONCENTRATION = 1E-7;
    private static final int BASE_DOTS = 2;
    private static final int MAX_DOTS = 3000;
    private static final double BASE_FACTOR = Math.pow( ( MAX_DOTS / BASE_DOTS ), ( 1 / MathUtil.log10( 1 / BASE_CONCENTRATION ) ) );

    private static final double DOT_DIAMETER = 6;
    private static final int DOT_TRANSPARENCY = 140; // 0-255, transparent-opaque
    
    private static final int COUNT_THRESHOLD_FOR_ADJUSTING_BOUNDS = 20;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final AqueousSolution solution;
    
    private final PBounds containerBounds;
    private final PNode parentReactant, parentProduct, parentH3O, parentOH;
    private final Random randomCoordinate;
    private DotCountNode soluteComponentsCountNode, hydroniumHydroxideCountsNode;
    private boolean soluteComponentsVisible, hydroniumHydroxideVisible;
    private boolean dirty;
    private final PNode[] dotParents;
    private boolean wasStrong;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
   
    public RatioDotsNode( AqueousSolution solution, PBounds containerBounds ) {
        super();
        
        soluteComponentsVisible = true;
        hydroniumHydroxideVisible = true;
        dirty = true;
        
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
        
        parentReactant = new PComposite();
        parentProduct = new PComposite();
        parentH3O = new PComposite();
        parentOH = new PComposite();
        
        // rendering order will be modified later based on number of dots
        addChild( parentReactant );
        addChild( parentProduct );
        addChild( parentH3O );
        addChild( parentOH );
        dotParents = new PNode[4];
        dotParents[0] = parentReactant;
        dotParents[1] = parentProduct;
        dotParents[2] = parentH3O;
        dotParents[3] = parentOH;
        
        // dot counts (developer)
        {
            soluteComponentsCountNode = new DotCountNode();
            addChild( soluteComponentsCountNode );

            hydroniumHydroxideCountsNode = new DotCountNode();
            addChild( hydroniumHydroxideCountsNode );

            // layout, lower left of container
            hydroniumHydroxideCountsNode.setOffset( containerBounds.getX() + 5, containerBounds.getMaxY() - hydroniumHydroxideCountsNode.getFullBoundsReference().getHeight() - 15 );
            soluteComponentsCountNode.setOffset( hydroniumHydroxideCountsNode.getXOffset(), hydroniumHydroxideCountsNode.getYOffset() - soluteComponentsCountNode.getFullBoundsReference().getHeight() - 15 );
        }
        
        wasStrong = !solution.getSolute().isStrong(); // force an update
        update();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setSoluteComponentsVisible( boolean visible ) {
        if ( visible != isSoluteComponentsVisible() ) {
            soluteComponentsVisible = visible;
            parentReactant.setVisible( visible );
            parentProduct.setVisible( visible );
            soluteComponentsCountNode.setVisible( visible && !solution.isPureWater() );
            if ( visible && dirty ) {
                update();
            }
        }
    }
    
    public boolean isSoluteComponentsVisible() {
        return soluteComponentsVisible;
    }
    
    public void setHydroniumHydroxideVisible( boolean visible ) {
        if ( visible != isHydroniumHydroxideVisible() ) {
            hydroniumHydroxideVisible = visible;
            parentH3O.setVisible( visible );
            parentOH.setVisible( visible );
            hydroniumHydroxideCountsNode.setVisible( visible );
            if ( visible && dirty ) {
                update();
            }
        }
    }
    
    public boolean isHydroniumHydroxideVisible() {
        return hydroniumHydroxideVisible;
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the view to match the model.
     */
    private void update() {
        if ( isSoluteComponentsVisible() || isHydroniumHydroxideVisible() ) {
            createDots();
            dirty = false;
        }
        else { 
            dirty = true;
        }
    }
    
    //----------------------------------------------------------------------------
    // Dot management
    //----------------------------------------------------------------------------
    
    /*
     * Deletes all dots.
     */
    private void deleteAllDots() {
        parentReactant.removeAllChildren();
        parentProduct.removeAllChildren();
        parentH3O.removeAllChildren();
        parentOH.removeAllChildren();
    }
    
    /*
     * Creates dots based on the current pH value.
     * Dots are spread at random location throughout the container.
     * While the solution remains in the strong strength region, dots remain static. 
     */
    private void createDots() {

        if ( !( wasStrong && solution.getSolute().isStrong() ) ) {

            wasStrong = solution.getSolute().isStrong();

            deleteAllDots();
            int dotsReactant = 0;
            int dotsProduct = 0;
            int dotsH3O = 0;
            int dotsOH = 0;

            if ( !solution.isPureWater() ) {

                dotsReactant = getNumberOfDots( solution.getReactantConcentration() );
                dotsProduct = getNumberOfDots( solution.getProductConcentration() );

                createNodes( dotsReactant, solution.getSolute().getColor(), parentReactant );
                createNodes( dotsProduct, solution.getSolute().getConjugateColor(), parentProduct );
            }

            dotsH3O = getNumberOfDots( solution.getH3OConcentration() );
            dotsOH = getNumberOfDots( solution.getOHConcentration() );
            createNodes( dotsH3O, ABSColors.H3O_PLUS, parentH3O );
            createNodes( dotsOH, ABSColors.OH_MINUS, parentOH );

            // Change rendering order from most to least dots.
            Arrays.sort( dotParents, new ChildrenCountComparator() );
            for ( int i = 0; i < dotParents.length; i++ ) {
                dotParents[i].moveToBack();
            }

            // counts display (developer)
            soluteComponentsCountNode.setVisible( !solution.isPureWater() && PhetApplication.getInstance().isDeveloperControlsEnabled() );
            if ( !solution.isPureWater() ) {
                Solute solute = solution.getSolute();
                soluteComponentsCountNode.setCounts( solute.getSymbol(), solute.getConjugateSymbol(), dotsReactant, dotsProduct );
            }
            hydroniumHydroxideCountsNode.setCounts( ABSSymbols.H3O_PLUS, ABSSymbols.OH_MINUS, dotsH3O, dotsOH );
        }
    }
    
    /*
     * Number of dots is based on concentration.
     */
    private int getNumberOfDots( double concentration ) {
        final double raiseFactor = MathUtil.log10( concentration / BASE_CONCENTRATION );
        return (int)( BASE_DOTS * Math.pow( BASE_FACTOR, raiseFactor ) );
    }
    
    /*
     * Creates nodes at random locations.
     */
    private void createNodes( int count, Color baseColor, PNode parent ) {
        if ( count > 0 ) {
            Point2D pOffset = new Point2D.Double();
            Color color = ColorUtils.createColor( baseColor, DOT_TRANSPARENCY );
            PBounds bounds = getContainerBounds( count );
            for ( int i = 0; i < count; i++ ) {
                getRandomPoint( bounds, pOffset );
                DotNode p = new DotNode( DOT_DIAMETER, color );
                p.setOffset( pOffset );
                parent.addChild( p );
            }
        }
    }
    
    /*
     * Gets the bounds within which a dot will be created.
     * This is typically the container bounds.
     * But if the number of dots is small, we shrink the container bounds so 
     * that dots stay away from the edges, making them easier to see.
     */
    private PBounds getContainerBounds( int count ) {
        PBounds bounds = containerBounds;
        if ( count < COUNT_THRESHOLD_FOR_ADJUSTING_BOUNDS ) {
            double margin = 10;
            bounds = new PBounds( bounds.getX() + margin, bounds.getY() + margin, bounds.getWidth() - ( 2 * margin ), bounds.getHeight() - ( 2 * margin ) );
        }
        return bounds;
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
    
    /* 
     * Dots.
     */
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
    
    /*
     * Developer display for the number of dots.
     */
    private static class DotCountNode extends HTMLNode {
        
        public DotCountNode() {
            super();
            setFont( new PhetFont( 16 ) );
            setCounts( "?", "?", 0, 0 );
            setVisible( PhetApplication.getInstance().isDeveloperControlsEnabled() );
        }
        
        public void setCounts( String symbol1, String symbol2, int count1, int count2 ) {
            setHTML( HTMLUtils.toHTMLString( symbol1 + "/" + symbol2 + " = " + count1 + "/" + count2 ) );
        }
        
        public void setVisible( boolean visible ) {
            if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                super.setVisible( visible );
            }
        }
    }
    
    /*
     * Sorts PNodes based on how many children they have (least to most).
     */
    private static class ChildrenCountComparator implements Comparator<PNode> {

        public int compare( final PNode node1, final PNode node2 ) {
            final int count1 = node1.getChildrenCount();
            final int count2 = node2.getChildrenCount();
            if ( count1 > count2 ) {
                return 1;
            }
            else if ( count1 < count2 ) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }
}
