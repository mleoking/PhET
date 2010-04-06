/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Depicts the ratio of concentrations (HA/A, H3O/OH) as a set of "dots".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DotsNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double BASE_CONCENTRATION = 1E-7;
    private static final int BASE_DOTS = 2;

    // if we have fewer than this number of dots, cheat them towards the center of the bounds
    private static final int COUNT_THRESHOLD_FOR_ADJUSTING_BOUNDS = 20;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final WeakAcid solution;
    private final PNode containerNode;
    private final PNode parentHA, parentA, parentH3O, parentOH;
    private final Random randomCoordinate;
    private final EventListenerList listeners;
    
    private int maxDots = MGPConstants.MAX_DOTS_RANGE.getDefault();
    private double dotDiameter = MGPConstants.DOT_DIAMETER_RANGE.getDefault();
    private float dotTransparency = (float) MGPConstants.DOT_TRANSPARENCY_RANGE.getDefault();
    private int countHA, countA, countH3O, countOH;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
   
    public DotsNode( final WeakAcid solution, PNode containerNode ) {
        super();
        
        listeners = new EventListenerList();
        
        // not interactive
        setPickable( false );
        setChildrenPickable( false );
        
        this.containerNode = containerNode;
        containerNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PROPERTY_FULL_BOUNDS ) ) {
                    deleteAllDots();
                    updateNumberOfDots();
                }
            }
        });
        
        this.solution = solution;
        solution.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                updateNumberOfDots();
            }
        });
        
        randomCoordinate = new Random();
        
        parentHA = new PComposite();
        parentA = new PComposite();
        parentH3O = new PComposite();
        parentOH = new PComposite();
        
        // rendering order will be modified later based on number of dots
        addChild( parentHA );
        addChild( parentA );
        addChild( parentH3O );
        addChild( parentOH );
        
        updateNumberOfDots();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setMaxDots( int maxDots ) {
        if ( maxDots != this.maxDots ) {
            this.maxDots = maxDots;
            deleteAllDots();
            updateNumberOfDots();
            fireStateChanged();
        }
    }
    
    public int getMaxDots() {
        return maxDots;
    }
    
    public void setDotDiameter( double dotDiameter ) {
        if ( dotDiameter != this.dotDiameter ) {
            this.dotDiameter = dotDiameter;
            updateDiameter();
            fireStateChanged();
        }
    }
    
    public double getDotDiameter() {
        return dotDiameter;
    }
    
    public void setDotTransparency( float dotTransparency ) {
        if ( dotTransparency != this.dotTransparency ) {
            this.dotTransparency = dotTransparency;
            updateTransparency();
            fireStateChanged();
        }
    }
    
    public float getDotTransparency() {
        return dotTransparency;
    }
    
    public int getCountHA() {
        return countHA;
    }
    
    public int getCountA() {
        return countA;
    }
    
    public int getCountH3O() {
        return countH3O;
    }
    
    public int getCountOH() {
        return countOH;
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Creates dots based on the current pH value.
     * Dots are spread at random location throughout the container.
     */
    private void updateNumberOfDots() {

        countHA = getNumberOfDots( solution.getConcentrationHA() );
        countA = getNumberOfDots( solution.getConcentrationA() );
        countH3O = getNumberOfDots( solution.getConcentrationH3O() );
        countOH = getNumberOfDots( solution.getConcentrationOH() );
        
        adjustNodeCount( countHA, MGPConstants.COLOR_HA, parentHA );
        adjustNodeCount( countA, MGPConstants.COLOR_A_MINUS, parentA );
        adjustNodeCount( countH3O, MGPConstants.COLOR_H3O_PLUS, parentH3O );
        adjustNodeCount( countOH, MGPConstants.COLOR_OH_MINUS, parentOH );

        sortDots();
    }
    
    // Deletes all dots.
    private void deleteAllDots() {
        parentHA.removeAllChildren();
        parentA.removeAllChildren();
        parentH3O.removeAllChildren();
        parentOH.removeAllChildren();
        countHA = countA = countH3O = countOH = 0;
    }
    
    // Changes the rendering order from most to least dots.
    private void sortDots() {
        PNode[] dotParents = new PNode[] { parentHA, parentA, parentH3O, parentOH };
        Arrays.sort( dotParents, new ChildrenCountComparator() );
        for ( int i = 0; i < dotParents.length; i++ ) {
            dotParents[i].moveToBack();
        }
    }
    
    // Number of dots is based on concentration.
    private int getNumberOfDots( double concentration ) {
        final double raiseFactor = MathUtil.log10( concentration / BASE_CONCENTRATION );
        final double baseFactor = Math.pow( ( maxDots / BASE_DOTS ), ( 1 / MathUtil.log10( 1 / BASE_CONCENTRATION ) ) );
        return (int)( BASE_DOTS * Math.pow( baseFactor, raiseFactor ) );
    }
    
    // Adjusts the number of dots, creates dots at random locations.
    private void adjustNodeCount( int count, Color color, PNode parent ) {

        // remove nodes
        while ( count < parent.getChildrenCount() && count > 0 ) {
            parent.removeChild( parent.getChildrenCount() - 1 );
        }

        // add nodes
        Point2D pOffset = new Point2D.Double();
        PBounds bounds = getContainerBounds( count );
        while ( count > parent.getChildrenCount() ) {
            getRandomPoint( bounds, pOffset );
            DotNode p = new DotNode( dotDiameter, color, dotTransparency );
            p.setOffset( pOffset );
            parent.addChild( p );
        }
    }
    
    /*
     * Gets the bounds within which a dot will be created.
     * This is typically the container bounds.
     * But if the number of dots is small, we shrink the container bounds so 
     * that dots stay away from the edges, making them easier to see.
     */
    private PBounds getContainerBounds( int count ) {
        PBounds bounds = containerNode.getFullBoundsReference();
        if ( count < COUNT_THRESHOLD_FOR_ADJUSTING_BOUNDS ) {
            double margin = 10;
            bounds = new PBounds( bounds.getX() + margin, bounds.getY() + margin, bounds.getWidth() - ( 2 * margin ), bounds.getHeight() - ( 2 * margin ) );
        }
        return bounds;
    }

    // Gets a random point inside some bounds.
    private void getRandomPoint( PBounds bounds, Point2D pOutput ) {
        double x = bounds.getX() + ( randomCoordinate.nextDouble() * bounds.getWidth() );
        double y = bounds.getY() + ( randomCoordinate.nextDouble() * bounds.getHeight() );
        pOutput.setLocation( x, y );
    }
    
    private void updateDiameter() {
        updateDiameter( parentHA, dotDiameter );
        updateDiameter( parentA, dotDiameter );
        updateDiameter( parentH3O, dotDiameter );
        updateDiameter( parentOH, dotDiameter );
    }
    
    private static void updateDiameter( PNode parent, double diameter ) {
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode child = parent.getChild( i );
            if ( child instanceof DotNode ) {
                ( (DotNode) child ).setDiameter( diameter );
            }
        }
    }
    
    private void updateTransparency() {
        updateTransparency( parentHA, dotTransparency );
        updateTransparency( parentA, dotTransparency );
        updateTransparency( parentH3O, dotTransparency );
        updateTransparency( parentOH, dotTransparency );
    }
    
    private static void updateTransparency( PNode parent, float transparency ) {
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode child = parent.getChild( i );
            if ( child instanceof DotNode ) {
                ( (DotNode) child ).setTransparency( transparency );
            }
        }
    }
    
    public void setColorHA( Color color ) {
        setDotColor( color, parentHA );
    }
    
    public void setColorA( Color color ) {
        setDotColor( color, parentA );
    }
    
    public void setColorH3O( Color color ) {
        setDotColor( color, parentH3O );
    }
    
    public void setColorOH( Color color ) {
        setDotColor( color, parentOH );
    }
    
    private void setDotColor( Color color, PNode parent ) {
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode child = parent.getChild( i );
            if ( child instanceof DotNode ) {
                ( (DotNode) child ).setPaint( color );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    // Dots
    private static class DotNode extends PPath {

        private Ellipse2D ellipse;

        public DotNode( double diameter, Color color, float transparency ) {
            super();
            ellipse = new Ellipse2D.Double();
            setTransparency( transparency );
            setPaint( color );
            setStroke( null );
            setDiameter( diameter );
        }

        public void setDiameter( double diameter ) {
            ellipse.setFrame( -diameter / 2, -diameter / 2, diameter, diameter ); // origin at geometric center
            setPathTo( ellipse );
        }
    }
    
    // Sorts PNodes based on how many children they have (least to most).
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
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( ChangeListener.class, listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( ChangeListener.class, listener );
    }
    
    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        for ( ChangeListener listener : listeners.getListeners( ChangeListener.class ) ) {
            listener.stateChanged( event );
        }
    }
}
