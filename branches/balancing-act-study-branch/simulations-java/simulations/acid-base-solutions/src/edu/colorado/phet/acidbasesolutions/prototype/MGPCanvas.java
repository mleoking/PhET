// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.geom.Dimension2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo canvas for the Magnifying Glass prototype.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MGPCanvas extends PhetPCanvas {
    
    private final PNode rootNode;
    private final MagnifyingGlassNode magnifyingGlassNode;
    private final BeakerNode beakerNode;
    private final PHMeterNode pHMeterNode;
    private final PNode reactionEquationNode;
    private final MoleculeCountsNode countsNode;

    public MGPCanvas( MGPModel model, boolean dev ) {
        super( MGPConstants.CANVAS_SIZE );
        setBackground( MGPConstants.CANVAS_COLOR );
        
        rootNode = new PNode();
        addWorldChild( rootNode );
        
        magnifyingGlassNode = new MagnifyingGlassNode( model.getMagnifyingGlass(), model.getSolution(), dev );
        magnifyingGlassNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( evt.getPropertyName().equals( PNode.PROPERTY_FULL_BOUNDS ) ) {
                    updateLayout();
                }
            }
        });
        
        beakerNode = new BeakerNode( model.getBeaker(), model.getSolution(), magnifyingGlassNode, dev );
        beakerNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( evt.getPropertyName().equals( PNode.PROPERTY_FULL_BOUNDS ) ) {
                    updateLayout();
                }
            }
        });
        
        pHMeterNode = new PHMeterNode( model.getSolution() );
        
        reactionEquationNode = new WeakAcidReactionEquationNode();
        reactionEquationNode.scale( 1.2 );
        
        countsNode = new MoleculeCountsNode( model.getSolution(), magnifyingGlassNode, dev );
        countsNode.setOffset( 20, 20 );
        countsNode.scale( 1.5 );
        
        // rendering order
        addChild( beakerNode );
        addChild( magnifyingGlassNode );
        addChild( pHMeterNode );
        addChild( reactionEquationNode );
        if ( dev ) {
            addChild( countsNode );
        }
    }
    
    public MagnifyingGlassNode getMagnifyingGlassNode() {
        return magnifyingGlassNode;
    }
    
    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }
    
    @Override
    public void updateLayout() {
        super.updateLayout();
        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() == 0 || worldSize.getHeight() == 0 ) {
            return;
        }
        double x = ( worldSize.getWidth() / 2 ) - ( reactionEquationNode.getFullBoundsReference().getWidth() / 2 );
        double y = worldSize.getHeight() - reactionEquationNode.getFullBoundsReference().getHeight() - 30;
        reactionEquationNode.setOffset( x, y );
        x = reactionEquationNode.getFullBoundsReference().getCenterX() - ( beakerNode.getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( beakerNode );
        y = reactionEquationNode.getFullBoundsReference().getMinY() - beakerNode.getFullBoundsReference().getHeight() - PNodeLayoutUtils.getOriginYOffset( beakerNode ) - 20;
        beakerNode.setOffset( x, y );
        magnifyingGlassNode.setOffset( x, y );
        x = beakerNode.getFullBoundsReference().getMinX() + 50;
        y = beakerNode.getFullBoundsReference().getMinY() - 20 ;
        pHMeterNode.setOffset( x, y );
    }
}
