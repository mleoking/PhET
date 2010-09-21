package edu.colorado.phet.rotation.view;

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
    private PClip playAreaClip;

    public RotationLayout( JComponent parent, PNode rotationPlayAreaNode, PNode rotationControlPanelNode, PNode timeSeriesGraphSetNode, PNode platformNode, PClip playAreaClip ) {
        this.parent = parent;
        this.rotationPlayAreaNode = rotationPlayAreaNode;
        this.rotationControlPanelNode = rotationControlPanelNode;
        this.timeSeriesGraphSetNode = timeSeriesGraphSetNode;
        this.platformNode = platformNode;
        this.playAreaClip = playAreaClip;
    }

    public void layout() {
        rotationControlPanelNode.setOffset( 0, getHeight() - rotationControlPanelNode.getFullBounds().getHeight() );
        double availWidth = getWidth() - ( 1024 - rotationControlPanelNode.getFullBounds().getWidth() );
        if ( availWidth < rotationControlPanelNode.getWidth() ) {
            availWidth = rotationControlPanelNode.getWidth();
        }

        //Share extra space equally between graphs and play area
        double extra = availWidth - rotationControlPanelNode.getFullBounds().getWidth();
        if ( extra > 0 ) {
            availWidth -= extra / 2;
        }
        double availHeight = getHeight() - rotationControlPanelNode.getFullBounds().getHeight();

        Dimension2D d = getPlayAreaScreenDim();
//        System.out.println( "d = " + d );

        double sx = availWidth / Math.abs( d.getHeight() );
        double sy = availHeight / Math.abs( d.getWidth() );
        double scale = Math.min( sx, sy );
//        System.out.println( "scale = " + scale + " limited by sx=" + ( scale == sx ) + ", lim by sy=" + ( scale == sy ) );
        if ( scale > 0 ) {
            rotationPlayAreaNode.scale( scale );
        }
        rotationPlayAreaNode.setOffset( availWidth / 2, availHeight / 2 );
        playAreaClip.setPathToRectangle( 0, 0, (float) availWidth, (float) availHeight );
        int padx = 3;
        timeSeriesGraphSetNode.setBounds( new Rectangle2D.Double( padx + availWidth, 0, getWidth() - availWidth - padx, getHeight() ) );
    }

    private Dimension2D getPlayAreaScreenDim() {
        double platformMaxWidth = RotationPlatform.MAX_RADIUS * 2.5;//double to get diameter, and add some for handle and padding
        Dimension2D d = new PDimension( platformMaxWidth, platformMaxWidth );
        rotationPlayAreaNode.setScale( 1.0 );
        platformNode.getParent().localToGlobal( d );
        return d;
    }

    private double getWidth() {
        return parent.getWidth();
    }

    private double getHeight() {
        return parent.getHeight();
    }
}
