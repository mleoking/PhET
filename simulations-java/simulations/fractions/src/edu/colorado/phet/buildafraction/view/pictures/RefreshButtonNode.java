// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildafraction.view.pictures;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class RefreshButtonNode extends PNode {

    //Color more like NP's yellow toggle buttons used elsewhere in the sim
    public static final Color BUTTON_COLOR = new Color( 253, 239, 8 );

    //Reshape so the button will have similar dimension to the other text buttons.
    public static BufferedImage copyWithPadding( BufferedImage image, int dw ) {
        BufferedImage copy = new BufferedImage( image.getWidth() + dw * 2, image.getHeight(), image.getType() );
        Graphics2D graphics2D = copy.createGraphics();
        graphics2D.drawImage( image, dw, 0, null );
        graphics2D.dispose();
        return copy;
    }

    public RefreshButtonNode() {
        final BufferedImage pad = copyWithPadding( BufferedImageUtils.multiScaleToWidth( Images.VIEW_REFRESH, 25 ), 12 );
        addChild( new HTMLImageButtonNode( pad ) {{
            setBackground( BUTTON_COLOR );
        }} );
    }
}