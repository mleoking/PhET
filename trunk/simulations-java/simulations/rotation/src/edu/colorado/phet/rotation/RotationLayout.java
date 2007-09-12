package edu.colorado.phet.rotation;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.umd.cs.piccolo.PNode;

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
    private PNode originNode;
    private RotationPlatform rotationPlatform;
    private static final double MIN_SCREEN_FRACTION_FOR_PLAY_AREA = 1.0 / 3.0;

    public RotationLayout( JComponent parent, PNode rotationPlayAreaNode, PNode rotationControlPanelNode, PNode timeSeriesGraphSetNode, PNode platformNode, PNode originNode, RotationPlatform rotationPlatform ) {
        this.parent = parent;
        this.rotationPlayAreaNode = rotationPlayAreaNode;
        this.rotationControlPanelNode = rotationControlPanelNode;
        this.timeSeriesGraphSetNode = timeSeriesGraphSetNode;
        this.platformNode = platformNode;
        this.originNode = originNode;
        this.rotationPlatform = rotationPlatform;
    }

    public void layout() {
        int padX = 10;
        int padY = 10;
        rotationControlPanelNode.setOffset( 0, getHeight() - rotationControlPanelNode.getFullBounds().getHeight() );
        double availWidth = rotationControlPanelNode.getFullBounds().getWidth();
        double availHeight = getHeight() - rotationControlPanelNode.getFullBounds().getHeight();

        availWidth = Math.max( Toolkit.getDefaultToolkit().getScreenSize().width * MIN_SCREEN_FRACTION_FOR_PLAY_AREA, availWidth );

//        rotationPlayAreaNode.setOffset( 200, 200 );
        rotationPlayAreaNode.setScale( 1.0 );

        //determine the radius in pixels of the rotation play area node
        availHeight -= padY * 2;
        availWidth -= padX * 2;

//        availWidth-=50;//for the origin node

        double sx = availWidth / ( platformNode.getFullBounds().getWidth() );
        double sy = availHeight / ( platformNode.getFullBounds().getHeight() );
        double scale = Math.min( sx, sy );
//        System.out.println( "sx = " + sx + ", sy=" + sy + ", scale=" + scale );
        if ( scale > 0 ) {
            rotationPlayAreaNode.scale( scale );
        }
        rotationPlayAreaNode.setOffset( scale * getRotationPlatform().getRadius(), scale * getRotationPlatform().getRadius() );

        double originNodeWidth = originNode.getGlobalFullBounds().getWidth();
        Rectangle2D bounds = new Rectangle2D.Double( getMaxXPlayAreaAndControlPanel() + padX + originNodeWidth, 0, getWidth() - getMaxXPlayAreaAndControlPanel() - padX - originNodeWidth, getHeight() );
//        System.out.println( "RSP::bounds = " + bounds );
        timeSeriesGraphSetNode.setBounds( bounds );
    }

    private RotationPlatform getRotationPlatform() {
        return rotationPlatform;
    }

    private double getMaxXPlayAreaAndControlPanel() {
        return Math.max( platformNode.getGlobalFullBounds().getMaxX(), platformNode.getGlobalFullBounds().getMaxX() );
    }

    private double getWidth() {
        return parent.getWidth();
    }

    private double getHeight() {
        return parent.getHeight();
    }
}
