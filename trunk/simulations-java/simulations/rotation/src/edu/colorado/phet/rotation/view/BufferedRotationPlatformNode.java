package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Author: Sam Reid
 * Aug 24, 2007, 3:32:33 AM
 */
public class BufferedRotationPlatformNode extends PNode {
    public BufferedRotationPlatformNode( RotationPlatform rotationPlatform ) {
//        super(rotationPlatform );
        final RotationPlatformNode node = new RotationPlatformNode( rotationPlatform );
        BufferedImage image = BufferedImageUtils.toBufferedImage( node.toImage() );
        final PImage pimage = new PImage( image );
        addChild( pimage );
        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                pimage.setImage( BufferedImageUtils.toBufferedImage( node.toImage() ) );
            }
        } );
        timer.start();
    }
}
