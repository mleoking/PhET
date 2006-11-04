/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.molecularreactions.model.*;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;

/**
 * SimpleMoleculeIcon
 * <p>
 * An ImageIcon for a specified class of molecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeIcon extends ImageIcon {
    // The amount the second component of a composite molecule icon needs
    // to be offset in order for it to look like it's touching the other component
    private int contactOffset = -2;

    public MoleculeIcon( Class moleculeClass ) {
        PNode pNode = null;
        Image image = null;
        if( moleculeClass == MoleculeA.class ) {
            pNode = new IconGraphic( new MoleculeA() );
        }
        if( moleculeClass == MoleculeB.class ) {
            pNode = new IconGraphic( new MoleculeB() );
        }
        if( moleculeClass == MoleculeC.class ) {
            pNode = new IconGraphic( new MoleculeC() );
        }
        if( moleculeClass == MoleculeAB.class ) {
            PNode aNode = new IconGraphic( new MoleculeA() );
            PNode bNode = new IconGraphic( new MoleculeB() );
            pNode = new PNode();
            pNode.addChild( aNode );
            pNode.addChild( bNode );
            bNode.setOffset( aNode.getFullBounds().getWidth() + contactOffset, 0 );
        }
        if( moleculeClass == MoleculeBC.class ) {
            PNode bNode = new IconGraphic( new MoleculeB() );
            PNode cNode = new IconGraphic( new MoleculeC() );
            pNode = new PNode();
            pNode.addChild( bNode );
            pNode.addChild( cNode );
            cNode.setOffset( bNode.getFullBounds().getWidth() + contactOffset, 0 );
        }
        image = pNode.toImage();
        this.setImage( image );
    }


    private class IconGraphic extends AbstractSimpleMoleculeGraphic {

        public IconGraphic( SimpleMolecule molecule ) {
            super( molecule, true );
        }
    }
}
