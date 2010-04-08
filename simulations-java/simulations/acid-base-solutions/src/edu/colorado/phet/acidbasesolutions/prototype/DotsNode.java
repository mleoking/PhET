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
    private Color colorHA, colorA, colorH3O, colorOH, colorH2O;
    
    public DotsNode( final WeakAcid solution, PNode containerNode ) {
        super( solution, containerNode, MGPConstants.MAX_DOTS_RANGE.getDefault(), MGPConstants.MAX_H2O_DOTS_RANGE.getDefault(), (float) MGPConstants.DOT_TRANSPARENCY_RANGE.getDefault() );
        colorHA = MGPConstants.COLOR_HA;
        colorA = MGPConstants.COLOR_A_MINUS;
        colorH3O = MGPConstants.COLOR_H3O_PLUS;
        colorOH = MGPConstants.COLOR_OH_MINUS;
        colorH2O = MGPConstants.COLOR_H2O;
        updateNumberOfMoleculeNodes();
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
    
    protected void updateTransparency( PNode parent, float transparency ) {
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode child = parent.getChild( i );
            if ( child instanceof DotNode ) {
                ( (DotNode) child ).setTransparency( transparency );
            }
        }
    }
    
    public Color getColorHA() {
        return colorHA;
    }
    
    public void setColorHA( Color color ) {
        colorHA = color;
        setDotColor( color, getParentHA() );
    }
    
    public Color getColorA() {
        return colorA;
    }
    
    public void setColorA( Color color ) {
        colorA = color;
        setDotColor( color, getParentA() );
    }
    
    public Color getColorH3O() {
        return colorH3O;
    }
    
    public void setColorH3O( Color color ) {
        colorH3O = color;
        setDotColor( color, getParentH3O() );
    }
    
    public Color getColorOH() {
        return colorOH;
    }
    
    public void setColorOH( Color color ) {
        colorOH = color;
        setDotColor( color, getParentOH() );
    }
    
    public Color getColorH2O() {
        return colorH2O;
    }
    
    public void setColorH2O( Color color ) {
        colorH2O = color;
        setDotColor( color, getParentH2O() );
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
        updateNumberOfMoleculeNodes( getParentHA(), getCountHA(), dotDiameter, getTransparency(), colorHA );
        updateNumberOfMoleculeNodes( getParentA(), getCountA(), dotDiameter, getTransparency(), colorA );
        updateNumberOfMoleculeNodes( getParentH3O(), getCountH3O(), dotDiameter, getTransparency(), colorH3O );
        updateNumberOfMoleculeNodes( getParentOH(), getCountOH(), dotDiameter, getTransparency(), colorOH );
        updateNumberOfMoleculeNodes( getParentH2O(), getCountH2O(), dotDiameter, getH2OTransparency(), colorH2O );
    }
    
    // Adjusts the number of dots, creates dots at random locations.
    private void updateNumberOfMoleculeNodes( PNode parent, int count, double diameter, float transparency, Color color ) {

        // remove nodes
        while ( count < parent.getChildrenCount() && count >= 0 ) {
            parent.removeChild( parent.getChildrenCount() - 1 );
        }

        // add nodes
        Point2D pOffset = new Point2D.Double();
        PBounds bounds = getContainerBounds( count );
        while ( count > parent.getChildrenCount() ) {
            getRandomPoint( bounds, pOffset );
            DotNode p = new DotNode( diameter, color, transparency );
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
