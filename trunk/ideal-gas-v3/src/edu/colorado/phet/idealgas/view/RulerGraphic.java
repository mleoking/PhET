/**
 * Class: RulerGraphic
 * Class: edu.colorado.phet.idealgas.view
 * User: Ron LeMaster
 * Date: Sep 16, 2004
 * Time: 9:24:51 PM
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class RulerGraphic extends DefaultInteractiveGraphic {
    private PhetImageGraphic rulerGraphic;

    public RulerGraphic( Component component ) {
        super( null );
        BufferedImage rulerImage = null;
        try {
            rulerImage = ImageLoader.loadBufferedImage( "images/meter-stick.gif" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        rulerGraphic = new PhetImageGraphic( component, rulerImage );
        setBoundedGraphic( rulerGraphic);

        this.addCursorHandBehavior();
        this.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                rulerGraphic.setPosition( (int)( rulerGraphic.getBounds().getMinX() + dx ),
                                          (int)( rulerGraphic.getBounds().getMinY() + dy ));
            }
        } );
    }
}
