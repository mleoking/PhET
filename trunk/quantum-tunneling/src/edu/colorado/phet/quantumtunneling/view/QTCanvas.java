/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.Dimension;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;


/**
 * QTCanvas is a wrapper around PhetPCanvas.
 * PhetPCanvas is a bit experimental, so this is an attempt
 * to insulate ourselves from potential problems.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTCanvas extends PhetPCanvas {

    public QTCanvas( Dimension renderingSize ) {
        super( renderingSize );
    }
    
    public void addNode( int layer, PNode node ) {
        addWorldChild( layer, node );
    }
    
    public void addNode( PNode node ) {
        addWorldChild( node );
    }
}
