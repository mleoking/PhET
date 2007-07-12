package edu.colorado.phet.rotation;

import edu.colorado.phet.rotation.view.RotationOriginNode;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;
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
    private double playAreaPadY = 50;
    private static final double MIN_SCREEN_FRACTION_FOR_PLAY_AREA = 1.0 / 3.0;

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

        double availWidth = rotationControlPanelNode.getFullBounds().getWidth();
        availWidth = Math.max( Toolkit.getDefaultToolkit().getScreenSize().width * MIN_SCREEN_FRACTION_FOR_PLAY_AREA, availWidth );
        double sx = availWidth / ( platformNode.getFullBounds().getWidth() + RotationOriginNode.AXIS_LENGTH );
//        double minimumPlayAreaWidthBasedOnScreenFraction = Toolkit.getDefaultToolkit().getScreenSize().width / 4.0;

        double availHeight = getHeight() - rotationControlPanelNode.getFullBounds().getHeight();
        double sy = availHeight / ( platformNode.getFullBounds().getHeight() + playAreaPadY );
        double scale = Math.min( sx, sy );
//        System.out.println( "sx = " + sx + ", sy=" + sy + ", scale=" + scale );
        if( scale > 0 ) {
            rotationPlayAreaNode.scale( scale );
        }


        Rectangle2D bounds = new Rectangle2D.Double( getMaxXPlayAreaAndControlPanel(), 0, getWidth() - getMaxXPlayAreaAndControlPanel(), getHeight() );
//        System.out.println( "RSP::bounds = " + bounds );
        timeSeriesGraphSetNode.setBounds( bounds );
    }

    private double getMaxXPlayAreaAndControlPanel() {
        return Math.max( rotationPlayAreaNode.getFullBounds().getMaxX(), rotationControlPanelNode.getFullBounds().getMaxX() );
    }

    private double getWidth() {
        return parent.getWidth();
    }

    private double getHeight() {
        return parent.getHeight();
    }
}
