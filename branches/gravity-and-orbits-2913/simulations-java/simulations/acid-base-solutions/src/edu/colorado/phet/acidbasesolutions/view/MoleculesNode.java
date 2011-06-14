// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.geom.Point2D;

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
/* package private */ class MoleculesNode extends PComposite {
    
    private static final int MAX_MOLECULES = 200; // max number of any one type of molecule
    private static final int MAX_H2O = 2000; // max number of H2O molecules
    private static final double MOLECULE_IMAGE_SCALE = 1;

    private final MagnifyingGlass magnifyingGlass;
    
    private final MoleculeImageParentNode parentReactant, parentProduct, parentH3O, parentOH, parentH2O;
    private final IMoleculeCountStrategy moleculeCountStrategy, h2oCountStrategy;
    
    // marker class for parents of molecule image nodes
    protected static class MoleculeImageParentNode extends PComposite {}
    
    public MoleculesNode( final MagnifyingGlass magnifyingGlass ) {
        super();
        setPickable( false );
        
        this.moleculeCountStrategy = new ConcentrationMoleculeCountStrategy();
        this.h2oCountStrategy = new ConstantMoleculeCountStrategy();
        
        this.magnifyingGlass = magnifyingGlass;
        magnifyingGlass.addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            @Override
            public void solutionChanged() {
                deleteAllMolecules();
                updateMolecules();
                updateMinoritySpeciesVisibility();
            }
            
            @Override
            public void strengthChanged() {
                updateMolecules();
            }
            
            @Override
            public void concentrationChanged() {
                updateMolecules();
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
        updateMolecules();
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
    
    /*
     * Updates the number of molecule nodes for all molecules.
     */
    private void updateMolecules() {
        
        AqueousSolution solution = magnifyingGlass.getSolution();
        
        // compute molecule counts
        int countReactant = moleculeCountStrategy.getNumberOfMolecules( solution.getSoluteConcentration(), MAX_MOLECULES );
        int countProduct = moleculeCountStrategy.getNumberOfMolecules( solution.getProductConcentration(), MAX_MOLECULES );
        int countH3O = moleculeCountStrategy.getNumberOfMolecules( solution.getH3OConcentration(), MAX_MOLECULES );
        int countOH = moleculeCountStrategy.getNumberOfMolecules( solution.getOHConcentration(), MAX_MOLECULES );
        
        // use a different strategy for H2O molecule counts
        int countH2O = h2oCountStrategy.getNumberOfMolecules( solution.getH2OConcentration(), MAX_H2O );
        
        // update number of molecule image nodes
        updateMoleculeNodes( parentReactant, solution.getSolute(), countReactant );
        updateMoleculeNodes( parentProduct, solution.getProduct(), countProduct );
        updateMoleculeNodes( parentH3O, solution.getH3OMolecule(), countH3O );
        updateMoleculeNodes( parentOH, solution.getOHMolecule(), countOH  );
        updateMoleculeNodes( parentH2O, solution.getWaterMolecule(), countH2O );
    }
    
    /*
     * Updates the number of molecule nodes for one type of molecule.
     */
    private void updateMoleculeNodes( PNode parent, Molecule molecule, int count ) {

        // remove nodes
        while ( count < parent.getChildrenCount() && count >= 0 ) {
            parent.removeChild( parent.getChildrenCount() - 1 );
        }

        // add nodes
        while ( count > parent.getChildrenCount() ) {
            // create node
            PImage node = new PImage( molecule.getImage() );
            node.setScale( MOLECULE_IMAGE_SCALE );
            parent.addChild( node );
            // move to a random location
            Point2D p = getRandomPoint();
            double x = p.getX() - ( node.getFullBoundsReference().getWidth() / 2 );
            double y = p.getY() - ( node.getFullBoundsReference().getHeight() / 2 );
            node.setOffset( x, y );
        }

        assert( count == parent.getChildrenCount() );
    }
    
    /*
     * Deletes all molecule image nodes, leaving other child nodes untouched.
     */
    private void deleteAllMolecules() {
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            PNode node = getChild( i );
            if ( node instanceof MoleculeImageParentNode ) {
                node.removeAllChildren();
            }
        }
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
