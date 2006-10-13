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

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.colorado.phet.common.view.graphics.Arrow;

import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.*;

/**
 * SeparationIndicatorArrow
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SeparationIndicatorArrow extends PNode {
    private PNode arrowheadNodeA;
    private PNode arrowheadNodeB;
    private Arrow arrowheadA;
    private Arrow arrowheadB;
    private double headHeight = 12;
    private double headWidth = 8;
    private PPath connectorLineNode = new PPath();
    private Line2D connectorLine = new Line2D.Double();

    /**
     * @param color
     */
    public SeparationIndicatorArrow( Color color ) {
        this.arrowheadA = new Arrow( new Point2D.Double( 0, headHeight ),
                                     new Point2D.Double(),
                                     headHeight,
                                     headWidth,
                                     1, 1, false );
        arrowheadNodeA = new PPath( arrowheadA.getShape() );
        arrowheadNodeA.setPaint( color );
        addChild( arrowheadNodeA );

        this.arrowheadB = new Arrow( new Point2D.Double( 0, -headHeight ),
                                     new Point2D.Double(),
                                     headHeight,
                                     headWidth,
                                     1 );
        arrowheadNodeB = new PPath( arrowheadB.getShape() );
        arrowheadNodeB.setPaint( color );
        addChild( arrowheadNodeB );

        connectorLineNode.setPaint( color );
        connectorLineNode.setStrokePaint( color );
        connectorLineNode.setStroke( new BasicStroke( 1 ) );
        addChild( connectorLineNode );

        setEndpoints( 0, 0, 0, 0 );
    }

    /**
     *
     */
    public void setEndpoints( double x1, double y1, double x2, double y2 ) {
        connectorLine.setLine( x1, y1 + headHeight,
                               x2, y2 - headHeight );
        connectorLineNode.setPathTo( connectorLine );

        arrowheadNodeA.setOffset( x1, y1 );
        arrowheadNodeB.setOffset( x2, y2 );
    }
}
