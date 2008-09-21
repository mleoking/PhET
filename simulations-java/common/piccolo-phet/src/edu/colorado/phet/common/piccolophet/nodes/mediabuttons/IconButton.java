package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Draws an icon Shape over the button.
 */
public class IconButton extends AbstractMediaButton {
    private PPath iconNode;

    public IconButton( int buttonHeight ) {
        super( buttonHeight );
        iconNode = new PhetPPath( Color.BLACK, new BasicStroke( 1 ), Color.LIGHT_GRAY );
        addChild( iconNode );
    }

    protected void updateImage() {
        super.updateImage();
        iconNode.setPaint( isEnabled() ? Color.black : Color.gray );
    }

    public void setIconPath( Shape shape ) {
        iconNode.setPathTo( shape );
    }
}
