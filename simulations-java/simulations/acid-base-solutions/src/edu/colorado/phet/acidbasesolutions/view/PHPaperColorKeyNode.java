/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.acidbasesolutions.model.PHPaper;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Displays a color key for the pH paper.
 * pH values are mapped to colors in the visible spectrum.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHPaperColorKeyNode extends PhetPNode {
    
    public PHPaperColorKeyNode( final PHPaper paper ) {
        
        paper.addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            public void visibilityChanged() {
                setVisible( paper.isVisible() );
            }
        });
        
        //XXX placeholder node
        PPath pathNode = new PPath( new Rectangle2D.Double( 0, 0, 300, 100 ) );
        pathNode.setPaint( Color.RED );
        addChild( pathNode );
        
        setVisible( paper.isVisible() );
    }
}
