package edu.colorado.phet.rotation;

import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.geom.Rectangle2D;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:40:39 AM
 */
public class RotationLayout {
    private JComponent parent;

    private PNode rotationPlayAreaNode;
    private PNode rotationControlPanelNode;
    private PNode timeSeriesGraphSetNode;
    private PNode platformNode;

    public RotationLayout( JComponent parent, PNode rotationPlayAreaNode, PNode rotationControlPanelNode, PNode timeSeriesGraphSetNode, PNode platformNode ) {
        this.parent = parent;
        this.rotationPlayAreaNode = rotationPlayAreaNode;
        this.rotationControlPanelNode = rotationControlPanelNode;
        this.timeSeriesGraphSetNode = timeSeriesGraphSetNode;
        this.platformNode = platformNode;
    }

    public void layout() {
        rotationPlayAreaNode.setOffset( 0, 0 );
        rotationPlayAreaNode.setScale( 1.0 );
        rotationControlPanelNode.setOffset( 0, getHeight() - rotationControlPanelNode.getFullBounds().getHeight() );
        double maxX = Math.max( rotationPlayAreaNode.getFullBounds().getMaxX(), rotationControlPanelNode.getFullBounds().getMaxX() );

        double width = rotationControlPanelNode.getFullBounds().getWidth();
        double sx = width / platformNode.getFullBounds().getWidth();

        double height = getHeight() - rotationControlPanelNode.getFullBounds().getHeight();
        double sy = height / platformNode.getFullBounds().getHeight();
        double scale = Math.min( sx, sy );
//        System.out.println( "sx = " + sx + ", sy=" + sy + ", scale=" + scale );
        if( scale > 0 ) {
            rotationPlayAreaNode.scale( scale );
        }

        Rectangle2D bounds = new Rectangle2D.Double( maxX, 0, getWidth() - maxX, getHeight() );
//        System.out.println( "RSP::bounds = " + bounds );
        timeSeriesGraphSetNode.setBounds( bounds );
    }

    private double getWidth() {
        return parent.getWidth();
    }

    private double getHeight() {
        return parent.getHeight();
    }
}
