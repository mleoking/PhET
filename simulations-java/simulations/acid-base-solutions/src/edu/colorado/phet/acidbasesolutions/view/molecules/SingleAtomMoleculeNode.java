/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.molecules;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for all molecules that have a single atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class SingleAtomMoleculeNode extends AbstractMoleculeNode {

    public SingleAtomMoleculeNode( double diameter, Color color, Color hiliteColor ) {
        
        // attributes
        Stroke stroke = new BasicStroke( 0.5f );
        Color strokeColor = color.darker();
        
        // atoms
        SphericalNode atom = new SphericalNode( diameter, createPaint( diameter, color, hiliteColor ), stroke, strokeColor, false ); 
        
        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( atom );
        
        // layout
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
