/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view.icons;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.colorado.phet.common.view.graphics.Arrow;

import java.awt.geom.Point2D;
import java.awt.*;

/**
 * ReactionArrowNode
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ReactionArrowNode extends PNode {


    public ReactionArrowNode( Paint arrowColor ) {
        Arrow arrowC = new Arrow( new Point2D.Double( 0, 0 ),
                                 new Point2D.Double( 30, 0 ),
                                 12, 8, 1 );
        Arrow arrowA = new Arrow( new Point2D.Double( 30, 0 ),
                                 new Point2D.Double( 0, 0 ),
                                 12, 8, 1 );
        PNode arrowNode = new PNode( );
        PPath arrowANode = new PPath( arrowA.getShape() );
        arrowANode.setPaint( arrowColor );
        arrowANode.setStrokePaint( arrowColor);
        arrowNode.addChild( arrowANode );
        PPath arrowCNode = new PPath( arrowC.getShape() );
        arrowCNode.setPaint( arrowColor );
        arrowCNode.setStrokePaint( arrowColor);
        arrowNode.addChild( arrowCNode );
        arrowANode.setOffset( 0, arrowANode.getFullBounds().getHeight() / 2 );
        arrowCNode.setOffset( arrowANode.getWidth(), arrowANode.getFullBounds().getHeight() / 2 );

        addChild( arrowANode );
        addChild( arrowCNode );        
    }
}
