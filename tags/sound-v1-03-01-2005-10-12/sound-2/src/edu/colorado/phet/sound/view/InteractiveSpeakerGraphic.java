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
import edu.colorado.phet.sound.SoundConfig;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class InteractiveSpeakerGraphic extends DefaultInteractiveGraphic {
    private SpeakerGraphic speakerGraphic;
    private BufferedWaveMediumGraphic waveMediumGraphic;
    private ArrayList mouseReleaseListeners = new ArrayList();

    public InteractiveSpeakerGraphic( SpeakerGraphic speakerGraphic,
                                      BufferedWaveMediumGraphic waveMediumGraphic ) {
        super( speakerGraphic );
        this.speakerGraphic = speakerGraphic;
        this.waveMediumGraphic = waveMediumGraphic;
        this.addCursorHandBehavior();
        this.addTranslationBehavior( new SpeakerTranslator() );
    }

    interface MouseReleaseListener {
        void mouseReleased( InteractiveSpeakerGraphic interactiveGraphic );
    }

    public void addListener( MouseReleaseListener listener ) {
        mouseReleaseListeners.add( listener );
    }

    public void removeListener( MouseReleaseListener listener ) {
        mouseReleaseListeners.remove( listener );
    }

    public Point2D.Double getAudioSourceLocation() {
        return new Point2D.Double( waveMediumGraphic.getOrigin().getX(), waveMediumGraphic.getOrigin().getY() );
    }

    private void notifyMouseReleaseListeners() {
        for( int i = 0; i < mouseReleaseListeners.size(); i++ ) {
            MouseReleaseListener mouseReleaseListener = (MouseReleaseListener)mouseReleaseListeners.get( i );
            mouseReleaseListener.mouseReleased( this );
        }
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

    public void mouseReleased( MouseEvent e ) {
        super.mouseReleased( e );
        // Notify the listener that we have stopped dragging
        notifyMouseReleaseListeners();
    }

    private class SpeakerTranslator implements Translatable {
        public void translate( double dx, double dy ) {
            Point2D.Double p = speakerGraphic.getLocation();
            speakerGraphic.setLocation( (int)p.getX(), (int)( p.getY() + 2 * MathUtil.getSign( dy ) ) );
            waveMediumGraphic.setOrigin( new Point2D.Double( SoundConfig.s_wavefrontBaseX, speakerGraphic.getLocation().y ) );
        }
    }
}
