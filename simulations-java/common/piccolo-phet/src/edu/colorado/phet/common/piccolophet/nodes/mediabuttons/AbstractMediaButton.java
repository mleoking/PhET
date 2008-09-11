/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;


public class AbstractMediaButton extends PNode {
    
    PImage buttonImageNode;
    
    public AbstractMediaButton( int buttonHeight ) {
        
//        BufferedImage image = PhetCommonResources.getInstance().getImage( "clock/Play24.gif" );
//        image = BufferedImageUtils.multiScaleToHeight( image, buttonHeight );
        BufferedImage image = new BufferedImage(buttonHeight, buttonHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setPaint( Color.ORANGE );
        g.fillRect( 0, 0, buttonHeight, buttonHeight );
        buttonImageNode = new PImage( image );
        
        addChild( buttonImageNode );
    }
    
    public PDimension getButtonDimension() {
        return new PDimension(buttonImageNode.getFullBounds().width, buttonImageNode.getFullBounds().height);
    }
}
