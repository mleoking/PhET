/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

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
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for representations of molecules that depict concentration ratios (HA/A, H3O/OH)
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
abstract class MoleculesNode extends PComposite {

    private static final double BASE_CONCENTRATION = 1E-7;
    private static final int BASE_DOTS = 2;

    // if we have fewer than this number of dots, cheat them towards the center of the bounds
    private static final int COUNT_THRESHOLD_FOR_ADJUSTING_BOUNDS = 20;
    
    private final WeakAcid solution;
    private final PNode containerNode;
    private final MoleculeParentNode parentHA, parentA, parentH3O, parentOH;
    private final Random randomCoordinate;
    private final EventListenerList listeners;
    
    private int maxMolecules;
    private int countHA, countA, countH3O, countOH;
    
    public MoleculesNode( WeakAcid solution, PNode containerNode, int maxMolecules ) {
        super();
        setPickable( false );
        
        this.maxMolecules = maxMolecules;
        listeners = new EventListenerList();
        
        this.containerNode = containerNode;
        containerNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PROPERTY_FULL_BOUNDS ) ) {
                    deleteAllMolecules();
                    updateNumberOfMolecules();
                }
            }
        });
        
        this.solution = solution;
        solution.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                updateNumberOfMolecules();
            }
        });
        
        randomCoordinate = new Random();
        
        parentHA = new MoleculeParentNode();
        parentA = new MoleculeParentNode();
        parentH3O = new MoleculeParentNode();
        parentOH = new MoleculeParentNode();
        
        // rendering order will be modified later based on number of dots
        addChild( parentHA );
        addChild( parentA );
        addChild( parentH3O );
        addChild( parentOH );
    }
    
    protected WeakAcid getSolution() {
        return solution;
    }
    
    public void setMaxMolecules( int maxMolecules ) {
        if ( maxMolecules != this.maxMolecules ) {
            this.maxMolecules = maxMolecules;
            updateNumberOfMolecules();
            fireStateChanged();
        }
    }
    
    public int getMaxMolecules() {
        return maxMolecules;
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
    
    protected PNode getParentHA() {
        return parentHA;
    }
    
    protected PNode getParentA() {
        return parentA;
    }
    
    protected PNode getParentH3O() {
        return parentH3O;
    }
    
    protected PNode getParentOH() {
        return parentOH;
    }
    
    private void updateNumberOfMolecules() {
        countHA = getNumberOfDots( solution.getConcentrationHA(), maxMolecules );
        countA = getNumberOfDots( solution.getConcentrationA(), maxMolecules );
        countH3O = getNumberOfDots( solution.getConcentrationH3O(), maxMolecules );
        countOH = getNumberOfDots( solution.getConcentrationOH(), maxMolecules );
        updateNumberOfMoleculeNodes();
        sortMolecules();
    }
    
    protected abstract void updateNumberOfMoleculeNodes();
    
    protected void deleteAllMolecules() {
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            PNode node = getChild( i );
            if ( node instanceof MoleculeParentNode ) {
                node.removeAllChildren();
            }
        }
        countHA = countA = countH3O = countOH = 0;
    }
    
    /*
     * Gets the bounds within which a dot will be created.
     * This is typically the container bounds.
     * But if the number of dots is small, we shrink the container bounds so 
     * that dots stay away from the edges, making them easier to see.
     */
    protected PBounds getContainerBounds( int count ) {
        PBounds bounds = containerNode.getFullBoundsReference();
        if ( count < COUNT_THRESHOLD_FOR_ADJUSTING_BOUNDS ) {
            double margin = 10;
            bounds = new PBounds( bounds.getX() + margin, bounds.getY() + margin, bounds.getWidth() - ( 2 * margin ), bounds.getHeight() - ( 2 * margin ) );
        }
        return bounds;
    }

    // Gets a random point inside some bounds.
    protected void getRandomPoint( PBounds bounds, Point2D pOutput ) {
        double x = bounds.getX() + ( randomCoordinate.nextDouble() * bounds.getWidth() );
        double y = bounds.getY() + ( randomCoordinate.nextDouble() * bounds.getHeight() );
        pOutput.setLocation( x, y );
    }
    
    // Changes the rendering order from most to least number of molecules.
    private void sortMolecules() {
        PNode[] dotParents = new PNode[] { parentHA, parentA, parentH3O, parentOH };
        Arrays.sort( dotParents, new ChildrenCountComparator() );
        for ( int i = 0; i < dotParents.length; i++ ) {
            dotParents[i].moveToBack();
        }
    }
    
    // Number of molecules displayed is based on concentration.
    protected static int getNumberOfDots( double concentration, int maxMolecules ) {
        final double raiseFactor = MathUtil.log10( concentration / BASE_CONCENTRATION );
        final double baseFactor = Math.pow( ( maxMolecules / BASE_DOTS ), ( 1 / MathUtil.log10( 1 / BASE_CONCENTRATION ) ) );
        return (int)( BASE_DOTS * Math.pow( baseFactor, raiseFactor ) );
    }
    
    // marker class for parents of molecule nodes
    protected static class MoleculeParentNode extends PComposite {}
    
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
    
    protected void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        for ( ChangeListener listener : listeners.getListeners( ChangeListener.class ) ) {
            listener.stateChanged( event );
        }
    }
}
