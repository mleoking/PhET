package edu.colorado.phet.rotation.graphs;

import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jan 1, 2007
 * Time: 10:07:45 PM
 * Copyright (c) Jan 1, 2007 by Sam Reid
 */

public class ZoomSuiteNode extends PNode {
    private ZoomControlNode verticalZoomControlNode;
    private ZoomControlNode horizontalZoomControlNode;

    public ZoomSuiteNode() {
        verticalZoomControlNode = new ZoomControlNode( ZoomControlNode.VERTICAL );
        horizontalZoomControlNode = new ZoomControlNode( ZoomControlNode.HORIZONTAL );

        addChild( verticalZoomControlNode );
        addChild( horizontalZoomControlNode );

        relayout();
    }

    public void addVerticalZoomListener( ZoomControlNode.ZoomListener zoomListener ) {
        verticalZoomControlNode.addZoomListener( zoomListener );
    }

    public void addHorizontalZoomListener( ZoomControlNode.ZoomListener zoomListener ) {
        horizontalZoomControlNode.addZoomListener( zoomListener );
    }

    private void relayout() {
        horizontalZoomControlNode.setOffset( 0, verticalZoomControlNode.getFullBounds().getMaxY() + 0 );
    }

    public void setVerticalZoomInEnabled( boolean enabled ) {
        verticalZoomControlNode.setZoomInEnabled( enabled );
    }

    public void setVerticalZoomOutEnabled( boolean enabled ) {
        verticalZoomControlNode.setZoomOutEnabled( enabled );
    }

    public void setHorizontalZoomInEnabled( boolean enabled ) {
        horizontalZoomControlNode.setZoomInEnabled( enabled );
    }

    public void setHorizontalZoomOutEnabled( boolean enabled ) {
        horizontalZoomControlNode.setZoomOutEnabled( enabled );
    }
}