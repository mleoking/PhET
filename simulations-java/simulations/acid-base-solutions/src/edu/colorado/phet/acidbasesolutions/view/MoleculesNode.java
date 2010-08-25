/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.*;
import edu.colorado.phet.acidbasesolutions.model.MagnifyingGlass.MagnifyingGlassChangeListener;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.acidbasesolutions.view.IMoleculeCountStrategy.ConcentrationMoleculeCountStrategy;
import edu.colorado.phet.acidbasesolutions.view.IMoleculeCountStrategy.ConstantMoleculeCountStrategy;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Collection of molecule images visible in the magnifying glass, used to represent concentration ratios.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MoleculesNode extends PComposite {

    private final MagnifyingGlass magnifyingGlass;
    
    private final MoleculeImageParentNode parentReactant, parentProduct, parentH3O, parentOH, parentH2O;
    private final IMoleculeCountStrategy moleculeCountStrategy, h2oCountStrategy;
    
    private int maxMolecules, maxH2O;
    private int countReactant, countProduct, countH3O, countOH, countH2O;
    private double imageScale;
    
    /*
     * Molecule image node.
     * Also servers as a marker class.
     */
    private static class MoleculeImageNode extends PImage {
        public MoleculeImageNode( Molecule molecule, double scale ) {
            super( molecule.getIcon() );
            setScale( scale );
        }
    }
    
    // marker class for parents of MoleculeImageNode
    protected static class MoleculeImageParentNode extends PComposite {}
    
    public MoleculesNode( final MagnifyingGlass magnifyingGlass ) {
        super();
        setPickable( false );
        
        this.maxMolecules = ABSConstants.MAGNIFYING_GLASS_MAX_MOLECULES;
        this.maxH2O = ABSConstants.MAGNIFYING_GLASS_MAX_H2O;
        this.moleculeCountStrategy = new ConcentrationMoleculeCountStrategy();
        this.h2oCountStrategy = new ConstantMoleculeCountStrategy();
        this.imageScale = ABSConstants.MAGNIFYING_GLASS_IMAGE_SCALE;
        
        this.magnifyingGlass = magnifyingGlass;
        magnifyingGlass.addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            @Override
            public void solutionChanged() {
                deleteAllMolecules();
                updateMoleculeCounts();
                updateMinoritySpeciesVisibility();
            }
            
            @Override
            public void strengthChanged() {
                updateMoleculeCounts();
            }
            
            @Override
            public void concentrationChanged() {
                updateMoleculeCounts();
            }
        });
        magnifyingGlass.addMagnifyingGlassListener( new MagnifyingGlassChangeListener() {
            public void waterVisibleChanged() {
                setWaterVisible( magnifyingGlass.isWaterVisible() );
            }
        });
        
        parentReactant = new MoleculeImageParentNode();
        parentProduct = new MoleculeImageParentNode();
        parentH3O = new MoleculeImageParentNode();
        parentOH = new MoleculeImageParentNode();
        parentH2O = new MoleculeImageParentNode();
        
        // rendering order (back-to-front) is: H2O reactant product H3O OH
        addChild( parentH2O );
        addChild( parentReactant );
        addChild( parentProduct );
        addChild( parentH3O );
        addChild( parentOH );
        
        // default state
        parentH2O.setVisible( magnifyingGlass.isWaterVisible() );
        updateMoleculeCounts();
        updateMinoritySpeciesVisibility();
    }
    
    private void setWaterVisible( boolean visible ) {
        if ( visible != isWaterVisible() ) {
            parentH2O.setVisible( visible );
        }
    }
    
    private boolean isWaterVisible() {
        return parentH2O.getVisible();
    }
    
    public int getMaxMolecules() {
        return maxMolecules;
    }
    
    public void setMaxMolecules( int maxMolecules ) {
        if ( maxMolecules != this.maxMolecules ) {
            this.maxMolecules = maxMolecules;
            updateMoleculeCounts();
        }
    }
    
    public int getMaxH2O() {
        return maxH2O;
    }
    
    public void setMaxH2O( int maxH2O ) {
        if ( maxH2O != this.maxH2O ) {
            this.maxH2O = maxH2O;
            updateMoleculeCounts();
        }
    }
    
    public double getImageScale() {
        return imageScale;
    }
    
    public void setImageScale( double imageScale ) {
        if ( imageScale != this.imageScale ) {
            this.imageScale = imageScale;
            for ( int i = 0; i < getChildrenCount(); i++ ) {
                PNode parent = getChild( i );
                if ( parent instanceof MoleculeImageParentNode ) {
                    updateScale( parent, imageScale );
                }
            }
        }
    }
    
    // Updates the scale of existing ImageNodes that are children of parent.
    private static void updateScale( PNode parent, double scale ) {
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode child = parent.getChild( i );
            if ( child instanceof MoleculeImageNode ) {
                ( (MoleculeImageNode) child ).setScale( scale );
            }
        }
    }
    
    /*
     * Our implementation (borrowed from advanced-acid-base-solutions) will always show
     * at least 1 of the minor species of molecules.  But in this sim, we never want to 
     * show the minor species.
     */
    private void updateMinoritySpeciesVisibility() {
        AqueousSolution solution = magnifyingGlass.getSolution();
        parentOH.setVisible( !( solution instanceof AcidSolution ) ); // hide OH- for acids
        parentH3O.setVisible( !( solution instanceof BaseSolution ) ); // hide H3O for bases
    }
    
    public int getCountReactant() {
        return countReactant;
    }
    
    public int getCountProduct() {
        return countProduct;
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
    
    protected PNode getParentReactant() {
        return parentReactant;
    }
    
    protected PNode getParentProduct() {
        return parentProduct;
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
    
    protected void updateMoleculeCounts() {
        AqueousSolution solution = magnifyingGlass.getSolution();
        countReactant = moleculeCountStrategy.getNumberOfMolecules( solution.getSoluteConcentration(), maxMolecules );
        countProduct = moleculeCountStrategy.getNumberOfMolecules( solution.getProductConcentration(), maxMolecules );
        countH3O = moleculeCountStrategy.getNumberOfMolecules( solution.getH3OConcentration(), maxMolecules );
        countOH = moleculeCountStrategy.getNumberOfMolecules( solution.getOHConcentration(), maxMolecules );
        countH2O = h2oCountStrategy.getNumberOfMolecules( solution.getH2OConcentration(), maxH2O );
        updateMoleculeNodes();
    }
    
    /*
     * Creates images based on molecule count strategies.
     * Images are distributed at random locations throughout the container.
     */
    protected void updateMoleculeNodes() {
        AqueousSolution solution = magnifyingGlass.getSolution();
        if ( !( solution instanceof PureWaterSolution ) ) {
            updateMoleculeNodes( getParentReactant(), getCountReactant(), getImageScale(), solution.getSolute() );
            updateMoleculeNodes( getParentProduct(), getCountProduct(), getImageScale(), solution.getProduct() );
        }
        updateMoleculeNodes( getParentH3O(), getCountH3O(), getImageScale(), solution.getH3OMolecule() );
        updateMoleculeNodes( getParentOH(), getCountOH(), getImageScale(), solution.getOHMolecule() );
        updateMoleculeNodes( getParentH2O(), getCountH2O(), getImageScale(), solution.getWaterMolecule() );
    }
    
    /*
     * Updates the number of molecule nodes for one type of molecule.
     */
    private void updateMoleculeNodes( PNode parent, int count, double scale, Molecule molecule ) {

        // remove nodes
        while ( count < parent.getChildrenCount() && count >= 0 ) {
            parent.removeChild( parent.getChildrenCount() - 1 );
        }

        // add nodes
        while ( count > parent.getChildrenCount() ) {
            MoleculeImageNode node = new MoleculeImageNode( molecule, scale );
            Point2D p = getRandomPoint();
            double x = p.getX() - ( node.getFullBoundsReference().getWidth() / 2 );
            double y = p.getY() - ( node.getFullBoundsReference().getHeight() / 2 );
            node.setOffset( x, y );
            parent.addChild( node );
        }

        assert( count == parent.getChildrenCount() );
    }
    
    private void deleteAllMolecules() {
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            PNode node = getChild( i );
            if ( node instanceof MoleculeImageParentNode ) {
                node.removeAllChildren();
            }
        }
        countReactant = countProduct = countH3O = countOH = countH2O = 0;
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
}
