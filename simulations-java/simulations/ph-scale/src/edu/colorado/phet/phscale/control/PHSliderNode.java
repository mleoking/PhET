package edu.colorado.phet.phscale.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;


public class PHSliderNode extends PNode {
    
    public PHSliderNode( double width, double height ) {
        super();
        //XXX placeholder
        PPath pathNode = new PPath( new Rectangle2D.Double( 0, 0, width, height ) );
        pathNode.setStroke( new BasicStroke( 2f ) );
        pathNode.setStrokePaint( Color.BLACK );
        addChild( pathNode );
    }

}
