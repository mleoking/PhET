/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PNode;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 28, 2005
 * Time: 12:30:23 PM
 * Copyright (c) Jul 28, 2005 by Sam Reid
 */

public class TargetedWiggleMe extends PNode {
    private ConnectorGraphic connectorGraphic;
    private WiggleMe wiggleMe;
    private PNode target;

    public TargetedWiggleMe( String message, int x, int y, PNode target ) {
        this.target = target;
        wiggleMe = new WiggleMe( message, x, y );

        connectorGraphic = new ArrowConnectorGraphic( wiggleMe, target );
        connectorGraphic.setStroke( new BasicStroke( 2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 2 ) );
        connectorGraphic.setPaint( new GradientPaint( 0, 0, Color.red, 1000, 0, Color.blue, false ) );
        connectorGraphic.setPickable( false );
        connectorGraphic.setChildrenPickable( false );
        addChild( connectorGraphic );
        addChild( wiggleMe );
        setPickable( false );
        setChildrenPickable( false );
    }
}
