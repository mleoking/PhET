/**
 * Class: InteractiveSpeakerGraphic
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 31, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class InteractiveSpeakerGraphic extends DefaultInteractiveGraphic {
    private SpeakerGraphic speakerGraphic;
    private WaveMediumGraphicC waveMediumGraphic;

    public InteractiveSpeakerGraphic( SpeakerGraphic speakerGraphic,
                                      WaveMediumGraphicC waveMediumGraphic ) {
        super( speakerGraphic );
        this.speakerGraphic = speakerGraphic;
        this.waveMediumGraphic = waveMediumGraphic;
        this.addCursorHandBehavior();
        this.addTranslationBehavior( new SpeakerTranslator() );
    }

    public void mouseDragged( MouseEvent e ) {
        super.mouseDragged( e );
        try {
            Thread.sleep( 20 );
        }
        catch( InterruptedException e1 ) {
            e1.printStackTrace();
        }
    }

    private class SpeakerTranslator implements Translatable {
        public void translate( double dx, double dy ) {
            Point2D.Double p = speakerGraphic.getLocation();
            speakerGraphic.setLocation( (int)p.getX(), (int)( p.getY() + 1 * MathUtil.getSign( dy ) ) );
            waveMediumGraphic.setOrigin( speakerGraphic.getLocation() );
        }
    }
}
