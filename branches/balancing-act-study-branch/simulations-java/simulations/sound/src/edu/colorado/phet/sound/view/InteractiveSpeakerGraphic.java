// Copyright 2002-2011, University of Colorado

/**
 * Class: InteractiveSpeakerGraphic
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 31, 2004
 */
package edu.colorado.phet.sound.view;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EventListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.EventChannel;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.sound.SoundConfig;

public class InteractiveSpeakerGraphic extends CompositePhetGraphic {

    public interface InteractiveSpeakerChangeListener extends EventListener {
        void stateChanged(ChangeEvent e);
    }

    private SpeakerGraphic speakerGraphic;
    private BufferedWaveMediumGraphic waveMediumGraphic;
    private ArrayList mouseReleaseListeners = new ArrayList();
    private EventChannel changeEventChannel = new EventChannel( InteractiveSpeakerChangeListener.class );
    private InteractiveSpeakerChangeListener changeListenerProxy = (InteractiveSpeakerChangeListener)changeEventChannel.getListenerProxy();

    public InteractiveSpeakerGraphic( SpeakerGraphic speakerGraphic,
                                      BufferedWaveMediumGraphic waveMediumGraphic ) {
        this.speakerGraphic = speakerGraphic;
        addGraphic( speakerGraphic );
        this.waveMediumGraphic = waveMediumGraphic;
        this.setCursorHand();
        this.addTranslationListener( new SpeakerTranslator() );
    }

    public Point2D.Double getAudioSourceLocation() {
        return new Point2D.Double( waveMediumGraphic.getOrigin().getX(), waveMediumGraphic.getOrigin().getY() );
    }

    /**
     * Agent that moves the speaker and wave graphics if the the speaker is moved. Also notifies any change listeners
     */
    private class SpeakerTranslator implements TranslationListener {
        public void translationOccurred( TranslationEvent event ) {
            Point2D p = speakerGraphic.getLocation();
            speakerGraphic.setLocation( (int)p.getX(), (int)( p.getY() + 2 * MathUtil.getSign( event.getDy() ) ) );
            waveMediumGraphic.setOrigin( new Point2D.Double( SoundConfig.s_wavefrontBaseX, speakerGraphic.getLocation().y ) );
            changeListenerProxy.stateChanged( new ChangeEvent( InteractiveSpeakerGraphic.this ) );
        }
    }

    public void addChangeListener( InteractiveSpeakerChangeListener changeListener ) {
        changeEventChannel.addListener( changeListener );
    }

}
