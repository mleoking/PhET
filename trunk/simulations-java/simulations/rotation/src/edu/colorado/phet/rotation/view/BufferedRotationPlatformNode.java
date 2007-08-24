package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Author: Sam Reid
 * Aug 24, 2007, 3:32:33 AM
 */
public class BufferedRotationPlatformNode extends PNode {
    private RotationPlatformNode node;

    public BufferedRotationPlatformNode( RotationPlatform rotationPlatform ) {
        node = new RotationPlatformNode( rotationPlatform );
        BufferedImage image = getImage();
        System.out.println( "image.getWidth() = " + image.getWidth() + ", h=" + image.getHeight() );

        final PImage pimage = new PImage( image );
        pimage.translate( 0, -500 );
        addChild( pimage );
        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                pimage.setImage( getImage() );
            }
        } );
        timer.start();
    }

    private BufferedImage getImage() {
        return BufferedImageUtils.toBufferedImage( node.toImage( 500, 500, Color.white ) );
    }
}
