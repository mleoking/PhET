/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.Image;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.greenhouse.model.Atom;
import edu.colorado.phet.greenhouse.model.AtomicBond;
import edu.colorado.phet.greenhouse.model.Molecule;
import edu.umd.cs.piccolo.PNode;


/**
 *
 * @author John Blanco
 */
public class MoleculeNode extends PNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private final PNode atomLayer;
    private final PNode bondLayer;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public MoleculeNode (Molecule molecule, ModelViewTransform2D mvt){
        bondLayer = new PNode();
        addChild(bondLayer);
        atomLayer = new PNode();
        addChild(atomLayer);
        
        for (Atom atom : molecule.getAtoms()){
            atomLayer.addChild( new AtomNode( atom, mvt ) );
        }
        
        for (AtomicBond atomicBond : molecule.getAtomicBonds()){
            bondLayer.addChild( new AtomicBondNode( atomicBond, mvt ) );
        }
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    /**
     * Retrieve an image representation of this node.  This was created in
     * order to support putting molecule images on control panels, but may
     * have other usages.
     */
    public BufferedImage getImage(){
        Image image = this.toImage();
        assert image instanceof BufferedImage;
        if (image instanceof BufferedImage){
            return (BufferedImage) this.toImage();
        }
        else{
            return null;
        }
    }

    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}
