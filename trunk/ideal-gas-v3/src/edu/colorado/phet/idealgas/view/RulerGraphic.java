/**
 * Class: RulerGraphic
 * Class: edu.colorado.phet.idealgas.view
 * User: Ron LeMaster
 * Date: Sep 16, 2004
 * Time: 9:24:51 PM
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.idealgas.IdealGasConfig;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class RulerGraphic extends PhetImageGraphic {
//public class RulerGraphic extends DefaultInteractiveGraphic {
    private PhetImageGraphic rulerGraphic;

    public RulerGraphic( Component component ) {
        super( null );
        BufferedImage rulerImage = null;
        try {
            rulerImage = ImageLoader.loadBufferedImage( IdealGasConfig.RULER_IMAGE_FILE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
//        rulerGraphic = new PhetImageGraphic( component, rulerImage );
        setImage( rulerImage);
//        setBoundedGraphic( rulerGraphic );

        setCursorHand();
//        this.addCursorHandBehavior();
        addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent event ) {
                rulerGraphic.setLocation( (int)( rulerGraphic.getBounds().getMinX() + event.getDx() ),
                                          (int)( rulerGraphic.getBounds().getMinY() + event.getDy() ) );
            }
        } );
    }
}
