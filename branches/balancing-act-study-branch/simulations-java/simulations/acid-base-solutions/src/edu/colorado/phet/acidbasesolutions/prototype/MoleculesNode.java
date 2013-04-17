// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.geom.Point2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.acidbasesolutions.prototype.IMoleculeLayeringStrategy.WeakAcidLayeringStrategy;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for representations of molecules that depict concentration ratios (HA/A, H3O/OH)
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
abstract class MoleculesNode extends PComposite {

    private final WeakAcid solution;
    private final MagnifyingGlass magnifyingGlass;
    private final MoleculeParentNode parentHA, parentA, parentH3O, parentOH, parentH2O;
    private final EventListenerList listeners;
    private final IMoleculeCountStrategy moleculeCountStrategy, h2oCountStrategy;
    private final IMoleculeLayeringStrategy layeringStrategy;
    
    private int maxMolecules, maxH2O;
    private float moleculeTransparency, h2oTransparency;
    private int countHA, countA, countH3O, countOH, countH2O;
    
    public MoleculesNode( WeakAcid solution, MagnifyingGlass magnifyingGlass, int maxMolecules, int maxH2O, float moleculeTransparency,
            IMoleculeCountStrategy moleculeCountStrategy, IMoleculeCountStrategy h2oCountStrategy, boolean showOH ) {
        super();
        setPickable( false );
        
        this.maxMolecules = maxMolecules;
        this.maxH2O = maxH2O;
        this.moleculeTransparency = this.h2oTransparency = moleculeTransparency;
        this.moleculeCountStrategy = moleculeCountStrategy;
        this.h2oCountStrategy = h2oCountStrategy;
        
        listeners = new EventListenerList();
        layeringStrategy = new WeakAcidLayeringStrategy();
        
        this.magnifyingGlass = magnifyingGlass;
        magnifyingGlass.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                deleteAllMolecules();
                updateNumberOfMolecules();
            }
        });
        
        this.solution = solution;
        solution.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                updateNumberOfMolecules();
                fireStateChanged();
            }
        });
        
        parentHA = new MoleculeParentNode();
        parentA = new MoleculeParentNode();
        parentH3O = new MoleculeParentNode();
        parentOH = new MoleculeParentNode();
        parentH2O = new MoleculeParentNode();
        
        // rendering order will be modified later based on number of dots
        addChild( parentH2O );
        addChild( parentHA );
        addChild( parentA );
        addChild( parentH3O );
        addChild( parentOH );
        
        // default state
        parentOH.setVisible( showOH );
        parentH2O.setVisible( MGPConstants.DEFAULT_SHOW_H2O );
    }
    
    public boolean isH2OVisible() {
        return parentH2O.getVisible();
    }
    
    public void setH2OVisible( boolean visible ) {
        if ( visible != isH2OVisible() ) {
            parentH2O.setVisible( visible );
            fireStateChanged();
        }
    }
    
    public int getMaxMolecules() {
        return maxMolecules;
    }
    
    public void setMaxMolecules( int maxMolecules ) {
        if ( maxMolecules != this.maxMolecules ) {
            this.maxMolecules = maxMolecules;
            updateNumberOfMolecules();
            fireStateChanged();
        }
    }
    
    public int getMaxH2O() {
        return maxH2O;
    }
    
    public void setMaxH2O( int maxH2O ) {
        if ( maxH2O != this.maxH2O ) {
            this.maxH2O = maxH2O;
            updateNumberOfMolecules();
            fireStateChanged();
        }
    }
    
    public float getMoleculeTransparency() {
        return moleculeTransparency;
    }
    
    public void setMoleculeTransparency( float moleculeTransparency ) {
        if ( moleculeTransparency != this.moleculeTransparency ) {
            this.moleculeTransparency = moleculeTransparency;
            // update the transparency of all existing non-H2O molecules
            for ( int i = 0; i < getChildrenCount(); i++ ) {
                PNode parent = getChild( i );
                if ( parent instanceof MoleculeParentNode && parent != getParentH2O() ) {
                    updateTransparency( parent, moleculeTransparency );
                }
            }
            fireStateChanged();
        }
    }
    
    public float getH2OTransparency() {
        return h2oTransparency;
    }
    
    public void setH2OTransparency( float h2oTransparency ) {
        if ( h2oTransparency != this.h2oTransparency ) {
            this.h2oTransparency = h2oTransparency;
            // update the transparency of all existing H2O molecules
            updateTransparency( getParentH2O(), h2oTransparency );
            fireStateChanged();
        }
    }
    
    // Updates the transparency of existing molecule nodes that are children of parent.
    protected abstract void updateTransparency( PNode parent, float transparency );
    
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
    
    public int getCountH2O() {
        return countH2O;
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
    
    protected PNode getParentH2O() {
        return parentH2O;
    }
    
    protected void updateNumberOfMolecules() {
        countHA = moleculeCountStrategy.getNumberOfMolecules( solution.getConcentrationHA(), maxMolecules );
        countA = moleculeCountStrategy.getNumberOfMolecules( solution.getConcentrationA(), maxMolecules );
        countH3O = moleculeCountStrategy.getNumberOfMolecules( solution.getConcentrationH3O(), maxMolecules );
        countOH = moleculeCountStrategy.getNumberOfMolecules( solution.getConcentrationOH(), maxMolecules );
        countH2O = h2oCountStrategy.getNumberOfMolecules( solution.getConcentrationH2O(), maxH2O );
        updateNumberOfMoleculeNodes();
        layeringStrategy.setRenderingOrder( parentHA, parentA, parentH3O, parentOH, parentH2O );
    }
    
    /*
     * Updates the number of molecule nodes to match the counts.
     * Subclasses should implement this, using their specific representations for the molecule nodes (eg, dots, images).
     */
    protected abstract void updateNumberOfMoleculeNodes();
    
    private void deleteAllMolecules() {
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            PNode node = getChild( i );
            if ( node instanceof MoleculeParentNode ) {
                node.removeAllChildren();
            }
        }
        countHA = countA = countH3O = countOH = countH2O = 0;
    }
    
    /* 
     * Gets a random point inside the magnifying glass.
     * The distance is *not* picked from a uniform distribution; to do so would cause points to cluster near the center.
     */
    protected Point2D getRandomPoint() {
        double radius = magnifyingGlass.getDiameter() / 2;
        double distance = radius * Math.sqrt( Math.random() ); 
        double angle = Math.random() * 2 * Math.PI;
        double x = distance * Math.cos( angle );
        double y = distance * Math.sin( angle );
        return new Point2D.Double( x, y );
    }
    
    // marker class for parents of molecule nodes
    protected static class MoleculeParentNode extends PComposite {}
    
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
