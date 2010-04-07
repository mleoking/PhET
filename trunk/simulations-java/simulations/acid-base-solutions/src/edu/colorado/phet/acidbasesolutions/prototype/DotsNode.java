/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Represents concentration ratios (HA/A, H3O/OH) as a set of "dots".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DotsNode extends MoleculesNode {
    
    private double dotDiameter = MGPConstants.DOT_DIAMETER_RANGE.getDefault();
    private float dotTransparency = (float) MGPConstants.DOT_TRANSPARENCY_RANGE.getDefault();
    
    public DotsNode( final WeakAcid solution, PNode containerNode ) {
        super( solution, containerNode, MGPConstants.MAX_DOTS_RANGE.getDefault() );
    }
    
    public double getDotDiameter() {
        return dotDiameter;
    }
    
    public void setDotDiameter( double dotDiameter ) {
        if ( dotDiameter != this.dotDiameter ) {
            this.dotDiameter = dotDiameter;
            for ( int i = 0; i < getChildrenCount(); i++ ) {
                PNode parent = getChild( i );
                if ( parent instanceof MoleculeParentNode ) {
                   updateDiameter( parent, dotDiameter );
                }
            }
            fireStateChanged();
        }
    }
    
    private static void updateDiameter( PNode parent, double diameter ) {
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode child = parent.getChild( i );
            if ( child instanceof DotNode ) {
                ( (DotNode) child ).setDiameter( diameter );
            }
        }
    }
    
    public float getDotTransparency() {
        return dotTransparency;
    }
    
    public void setDotTransparency( float dotTransparency ) {
        if ( dotTransparency != this.dotTransparency ) {
            this.dotTransparency = dotTransparency;
            for ( int i = 0; i < getChildrenCount(); i++ ) {
                PNode parent = getChild( i );
                if ( parent instanceof MoleculeParentNode ) {
                    updateTransparency( parent, dotTransparency );
                }
            }
            fireStateChanged();
        }
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
        setDotColor( color, getParentHA() );
    }
    
    public void setColorA( Color color ) {
        setDotColor( color, getParentA() );
    }
    
    public void setColorH3O( Color color ) {
        setDotColor( color, getParentH3O() );
    }
    
    public void setColorOH( Color color ) {
        setDotColor( color, getParentOH() );
    }
    
    private static void setDotColor( Color color, PNode parent ) {
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode child = parent.getChild( i );
            if ( child instanceof DotNode ) {
                ( (DotNode) child ).setPaint( color );
            }
        }
    }
    
    /*
     * Creates dots based on the current pH value.
     * Dots are spread at random location throughout the container.
     */
    protected void updateNumberOfMoleculeNodes() {
        updateNumberOfMoleculeNodes( getParentHA(), getCountHA(), MGPConstants.COLOR_HA );
        updateNumberOfMoleculeNodes( getParentA(), getCountA(), MGPConstants.COLOR_A_MINUS );
        updateNumberOfMoleculeNodes( getParentH3O(), getCountH3O(), MGPConstants.COLOR_H3O_PLUS );
        updateNumberOfMoleculeNodes( getParentOH(), getCountOH(), MGPConstants.COLOR_OH_MINUS );
    }
    
    // Adjusts the number of dots, creates dots at random locations.
    private void updateNumberOfMoleculeNodes( PNode parent, int count, Color color ) {

        // remove nodes
        while ( count < parent.getChildrenCount() && count >= 0 ) {
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
        
        assert( count == parent.getChildrenCount() );
    }
    
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
}
