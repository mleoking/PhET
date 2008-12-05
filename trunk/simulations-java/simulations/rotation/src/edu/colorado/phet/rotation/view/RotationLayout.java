package edu.colorado.phet.rotation.view;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PClip;

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
    private double minFraction;
    private PClip playAreaClip;
//    private static final double DEFAULT_MIN_SCREEN_FRACTION_FOR_PLAY_AREA = 1.0 / 3.0;

    public RotationLayout( JComponent parent, PNode rotationPlayAreaNode, PNode rotationControlPanelNode, PNode timeSeriesGraphSetNode, PNode platformNode, PNode originNode, RotationPlatform rotationPlatform, double minFraction, PClip playAreaClip ) {
        this.parent = parent;
        this.rotationPlayAreaNode = rotationPlayAreaNode;
        this.rotationControlPanelNode = rotationControlPanelNode;
        this.timeSeriesGraphSetNode = timeSeriesGraphSetNode;
        this.platformNode = platformNode;
        this.originNode = originNode;
        this.rotationPlatform = rotationPlatform;
        this.minFraction = minFraction;
        this.playAreaClip = playAreaClip;
    }

    public void layout() {
        rotationControlPanelNode.setOffset( 0, getHeight() - rotationControlPanelNode.getFullBounds().getHeight() );
        double availWidth = Math.max( rotationControlPanelNode.getFullBounds().getWidth(), Toolkit.getDefaultToolkit().getScreenSize().width * minFraction );
        double availHeight = getHeight() - rotationControlPanelNode.getFullBounds().getHeight();

        double platformMaxRadius = RotationPlatform.MAX_RADIUS * 2;
        Dimension2D d = new PDimension( platformMaxRadius, platformMaxRadius );
        rotationPlayAreaNode.setScale( 1.0 );
        platformNode.getParent().localToGlobal( d );
//        System.out.println( "d = " + d );

        rotationPlayAreaNode.setScale( 1.0 );

        double sx = availWidth / Math.abs( d.getHeight() );
        double sy = availHeight / Math.abs( d.getWidth() );
        double scale = Math.min( sx, sy );
//        System.out.println( "scale = " + scale + " limited by sx=" + ( scale == sx ) + ", lim by sy=" + ( scale == sy ) );
        if ( scale > 0 ) {
            rotationPlayAreaNode.scale( scale * 0.825 );
        }
        rotationPlayAreaNode.setOffset( scale * getRotationPlatform().getRadius(), scale * getRotationPlatform().getRadius() );
        playAreaClip.setPathToRectangle( 0, 0, (float) availWidth, (float) availHeight );
        int padx = 3;
        timeSeriesGraphSetNode.setBounds( new Rectangle2D.Double( padx + getMaxXPlayAreaAndControlPanel(), 0, getWidth() - getMaxXPlayAreaAndControlPanel() - padx, getHeight() ) );
    }

    private RotationPlatform getRotationPlatform() {
        return rotationPlatform;
    }

    private double getMaxXPlayAreaAndControlPanel() {
        return Math.max( playAreaClip.getGlobalFullBounds().getMaxX(), rotationControlPanelNode.getGlobalFullBounds().getMaxX() );
    }

    private double getWidth() {
        return parent.getWidth();
    }

    private double getHeight() {
        return parent.getHeight();
    }
}
