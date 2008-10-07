/**
 * Class: SingleSourceModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 3, 2004
 */
package edu.colorado.phet.sound;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.sound.model.WaveMedium;
import edu.colorado.phet.sound.model.Wavefront;
import edu.colorado.phet.sound.view.SingleSourceApparatusPanel;
import edu.colorado.phet.sound.view.SoundControlPanel;
import edu.colorado.phet.sound.view.WaveMediumGraphic;

/**
 * A module with a single speaker source
 */
public abstract class SingleSourceModule extends SoundModule {
    
    private final SingleSourceApparatusPanel apparatusPanel;
    private final WaveMedium waveMedium;

    /**
     * @param application
     * @param name
     */
    protected SingleSourceModule( String name ) {
        super( name );
        apparatusPanel = new SingleSourceApparatusPanel( getSoundModel(), getClock() );
        setApparatusPanel( apparatusPanel );

        waveMedium = getSoundModel().getWaveMedium();
        WaveMediumGraphic waveMediumGraphic = new WaveMediumGraphic( waveMedium, apparatusPanel, this );
        apparatusPanel.addGraphic( waveMediumGraphic, 7 );
        Point2D.Double audioSource = new Point2D.Double( SoundConfig.s_wavefrontBaseX,
                                                         SoundConfig.s_wavefrontBaseY );
        waveMediumGraphic.initLayout( audioSource,
                                      SoundConfig.s_wavefrontHeight,
                                      SoundConfig.s_wavefrontRadius );
        this.setControlPanel( new SoundControlPanel( this ) );
    }

    protected void resetWaveMediumGraphic() {
        ArrayList wavefronts = waveMedium.getWavefronts();
        for( int i = 0; i < wavefronts.size(); i++ ) {
            Wavefront wavefront = (Wavefront)wavefronts.get( i );
            wavefront.clear();
        }
    }
}