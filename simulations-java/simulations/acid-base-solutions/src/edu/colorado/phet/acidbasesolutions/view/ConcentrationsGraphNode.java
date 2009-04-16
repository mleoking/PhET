package edu.colorado.phet.acidbasesolutions.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;


public class ConcentrationsGraphNode extends PhetPNode {

    public ConcentrationsGraphNode() {
        super();
        
        //XXX placeholder
        PPath path = new PPath();
        path.setPathTo( new Rectangle2D.Double( 0, 0, 400, 600 ) );
        path.setStroke( new BasicStroke( 2f ) );
        path.setStrokePaint( Color.BLACK );
        addChild( path );
    }
}
