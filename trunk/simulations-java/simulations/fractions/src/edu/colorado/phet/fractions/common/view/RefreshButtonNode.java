// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.common.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.umd.cs.piccolo.PNode;

/**
 * Button that allows the user to "refresh" or reload the level.
 *
 * @author Sam Reid
 */
public class RefreshButtonNode extends PNode {

    //Color more like Noah P's yellow toggle buttons used elsewhere in the sim
    public static final Color BUTTON_COLOR = new Color( 253, 239, 8 );

    //Reshape so the button will have similar dimension to the other text buttons.
    public static BufferedImage copyWithPadding( BufferedImage image, int dw ) {
        return copyWithPadding( image, dw, 0 );
    }

    //Creates a copy of the buffered image but with padding on the left,top,right,bottom so that it will have a similar shape to other button icons
    public static BufferedImage copyWithPadding( BufferedImage image, int dw, int dh ) {
        BufferedImage copy = new BufferedImage( image.getWidth() + dw * 2, image.getHeight() + dh * 2, image.getType() );
        Graphics2D graphics2D = copy.createGraphics();
        graphics2D.drawImage( image, dw, dh, null );
        graphics2D.dispose();
        return copy;
    }

    //Create a RefreshButtonNode that has the specified callback listener when pressed (should refresh the scene by reloading new challenges).
    public RefreshButtonNode( final VoidFunction0 effect ) {
        final BufferedImage pad = copyWithPadding( BufferedImageUtils.multiScaleToWidth( Images.VIEW_REFRESH, 25 ), 12 );
        addChild( new HTMLImageButtonNode( pad ) {{
            setUserComponent( Components.refreshButton );
            setBackground( BUTTON_COLOR );
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    effect.apply();
                }
            } );
        }} );
    }
}