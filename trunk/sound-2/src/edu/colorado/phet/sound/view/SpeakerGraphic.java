/**
 * Class: SpeakerGraphic
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 11, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.sound.SoundConfig;
import edu.colorado.phet.sound.model.WaveMedium;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpeakerGraphic extends CompositePhetGraphic {

    public static int s_speakerConeOffsetX = 34;

    private PhetImageGraphic speakerFrame;
    private PhetImageGraphic speakerCone;
    private BufferedImage speakerFrameImg;
    private BufferedImage speakerConeImg;
    private Point2D.Double location = new Point2D.Double();

    public SpeakerGraphic( ApparatusPanel apparatusPanel, final WaveMedium waveMedium ) {

        super( apparatusPanel );

        // Set up the speaker
        try {
            speakerFrameImg = ImageLoader.loadBufferedImage( SoundConfig.SPEAKER_FRAME_IMAGE_FILE );
            speakerConeImg = ImageLoader.loadBufferedImage( SoundConfig.SPEAKER_CONE_IMAGE_FILE );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( "Image files not found" );
        }
        speakerFrame = new PhetImageGraphic( apparatusPanel, speakerFrameImg );
        speakerCone = new PhetImageGraphic( apparatusPanel, speakerConeImg );
        setLocation( SoundConfig.s_speakerBaseX, SoundConfig.s_wavefrontBaseY );
        this.addGraphic( speakerFrame );
        this.addGraphic( speakerCone );
        waveMedium.addObserver( new SimpleObserver() {
            private int s_maxSpeakerConeExcursion = 6;

            public void update() {
                int coneOffset = (int)( waveMedium.getAmplitudeAt( 0 ) / SoundConfig.s_maxAmplitude * s_maxSpeakerConeExcursion );
                speakerCone.setPosition( (int)location.getX() + s_speakerConeOffsetX + coneOffset,
                                         (int)location.getY() - speakerConeImg.getHeight( null ) / 2 );
            }
        } );
    }

    public void setLocation( int x, int y ) {
        this.location.setLocation( x, y );
        speakerFrame.setPosition( x, y - speakerFrameImg.getHeight( null ) / 2 );
        speakerCone.setPosition( x + s_speakerConeOffsetX, y - speakerConeImg.getHeight( null ) / 2 );
    }

    public Point2D.Double getLocation() {
        return location;
    }

    protected void syncBounds() {
        super.syncBounds();
    }

    public void setConePosition( int x ) {
        speakerCone.setPosition( (int)location.getX() + s_speakerConeOffsetX + x,
                                 (int)location.getY() - speakerConeImg.getHeight( null ) / 2 );
    }
}
