// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.acidbasesolutions.prototype.IMoleculeCountStrategy.ConcentrationMoleculeCountStrategy;
import edu.colorado.phet.acidbasesolutions.prototype.IMoleculeCountStrategy.ConstantMoleculeCountStrategy;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Represents concentration ratios (HA/A, H3O/OH) as a set of "dots".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class DotsNode extends MoleculesNode {
    
    private double dotDiameter;
    private Color colorHA, colorA, colorH3O, colorOH, colorH2O;
    
    public DotsNode( final WeakAcid solution, MagnifyingGlass magnifyingGlass, boolean showOH ) {
        super( solution, magnifyingGlass, MGPConstants.MAX_DOTS_RANGE.getDefault(), MGPConstants.MAX_H2O_DOTS_RANGE.getDefault(),
                (float) MGPConstants.DOT_TRANSPARENCY_RANGE.getDefault(), new ConcentrationMoleculeCountStrategy(), new ConstantMoleculeCountStrategy(), showOH );
        dotDiameter = MGPConstants.DOT_DIAMETER_RANGE.getDefault();
        colorHA = MGPConstants.COLOR_HA;
        colorA = MGPConstants.COLOR_A_MINUS;
        colorH3O = MGPConstants.COLOR_H3O_PLUS;
        colorOH = MGPConstants.COLOR_OH_MINUS;
        colorH2O = MGPConstants.COLOR_H2O;
        updateNumberOfMolecules(); // call this last
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
    
    public Color getColorHA() {
        return colorHA;
    }
    
    public void setColorHA( Color color ) {
        if ( !color.equals( colorHA ) ) {
            colorHA = color;
            updateDotColor( color, getParentHA() );
            fireStateChanged();
        }
    }
    
    public Color getColorA() {
        return colorA;
    }
    
    public void setColorA( Color color ) {
        if ( !color.equals( colorA ) ) {
            colorA = color;
            updateDotColor( color, getParentA() );
            fireStateChanged();
        }
    }
    
    public Color getColorH3O() {
        return colorH3O;
    }
    
    public void setColorH3O( Color color ) {
        if ( !color.equals( colorH3O ) ) {
            colorH3O = color;
            updateDotColor( color, getParentH3O() );
            fireStateChanged();
        }
    }
    
    public Color getColorOH() {
        return colorOH;
    }
    
    public void setColorOH( Color color ) {
        if ( !color.equals( colorOH ) ) {
            colorOH = color;
            updateDotColor( color, getParentOH() );
            fireStateChanged();
        }
    }
    
    public Color getColorH2O() {
        return colorH2O;
    }
    
    public void setColorH2O( Color color ) {
        if ( !color.equals( colorH2O ) ) {
            colorH2O = color;
            updateDotColor( color, getParentH2O() );
            fireStateChanged();
        }
    }
    
    // Updates the diameter of existing DotNodes that are children of parent.
    private static void updateDiameter( PNode parent, double diameter ) {
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode child = parent.getChild( i );
            if ( child instanceof DotNode ) {
                ( (DotNode) child ).setDiameter( diameter );
            }
        }
    }
    
    // Updates the transparency of existing DotNodes that are children of parent.
    protected void updateTransparency( PNode parent, float transparency ) {
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode child = parent.getChild( i );
            if ( child instanceof DotNode ) {
                ( (DotNode) child ).setTransparency( transparency );
            }
        }
    }
    
    // Updates the color of existing DotNodes that are children of parent.
    private static void updateDotColor( Color color, PNode parent ) {
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
        updateNumberOfMoleculeNodes( getParentHA(), getCountHA(), dotDiameter, getMoleculeTransparency(), colorHA );
        updateNumberOfMoleculeNodes( getParentA(), getCountA(), dotDiameter, getMoleculeTransparency(), colorA );
        updateNumberOfMoleculeNodes( getParentH3O(), getCountH3O(), dotDiameter, getMoleculeTransparency(), colorH3O );
        updateNumberOfMoleculeNodes( getParentOH(), getCountOH(), dotDiameter, getMoleculeTransparency(), colorOH );
        updateNumberOfMoleculeNodes( getParentH2O(), getCountH2O(), dotDiameter, getH2OTransparency(), colorH2O );
    }
    
    // Adjusts the number of dots, creates dots at random locations.
    private void updateNumberOfMoleculeNodes( PNode parent, int count, double diameter, float transparency, Color color ) {

        // remove nodes
        while ( count < parent.getChildrenCount() && count >= 0 ) {
            parent.removeChild( parent.getChildrenCount() - 1 );
        }

        // add nodes
        while ( count > parent.getChildrenCount() ) {
            DotNode node = new DotNode( diameter, color, transparency );
            Point2D p = getRandomPoint();
            double x = p.getX() - ( node.getFullBoundsReference().getWidth() / 2 );
            double y = p.getY() - ( node.getFullBoundsReference().getHeight() / 2 );
            node.setOffset( x, y );
            parent.addChild( node );
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
