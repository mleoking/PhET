/**
 * Class: RulerGraphic
 * Class: edu.colorado.phet.idealgas.view
 * User: Ron LeMaster
 * Date: Sep 16, 2004
 * Time: 9:24:51 PM
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.idealgas.IdealGasConfig;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class RulerGraphic extends PhetGraphic {
    private PhetImageGraphic rulerGraphic;

    public RulerGraphic( Component component ) {
        super( component );
        BufferedImage rulerImage = null;
        try {
            rulerImage = ImageLoader.loadBufferedImage( IdealGasConfig.RULER_IMAGE_FILE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        rulerGraphic = new PhetImageGraphic( component, rulerImage );

        setCursorHand();
        addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent event ) {
                rulerGraphic.setLocation( (int)( rulerGraphic.getBounds().getMinX() + event.getDx() ),
                                          (int)( rulerGraphic.getBounds().getMinY() + event.getDy() ) );
                RulerGraphic.this.setLocation( rulerGraphic.getLocation() );
            }
        } );
    }

    protected Rectangle determineBounds() {
        return rulerGraphic.getBounds();
    }

    public void paint( Graphics2D g2 ) {
        GraphicsState gs = new GraphicsState( g2 );
        GraphicsUtil.setAlpha( g2, .9 );
        rulerGraphic.paint( g2 );
        gs.restoreGraphics();
    }
}
