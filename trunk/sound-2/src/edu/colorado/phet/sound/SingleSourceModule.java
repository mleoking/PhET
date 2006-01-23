/**
 * Class: SingleSourceModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 3, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.WaveMedium;
import edu.colorado.phet.sound.model.Wavefront;
import edu.colorado.phet.sound.view.SingleSourceApparatusPanel;
import edu.colorado.phet.sound.view.SoundControlPanel;
import edu.colorado.phet.sound.view.WaveMediumGraphic;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * A module with a single speaker source
 */
public abstract class SingleSourceModule extends SoundModule {
    private WaveMedium waveMedium;

    /**
     * @param application
     * @param name
     */
    protected SingleSourceModule( SoundApplication application, String name ) {
        super( application, name );
        SingleSourceApparatusPanel apparatusPanel = new SingleSourceApparatusPanel( (SoundModel)getModel(), application.getClock() );
        this.setApparatusPanel( apparatusPanel );

        waveMedium = ( (SoundModel)getModel() ).getWaveMedium();
        WaveMediumGraphic waveMediumGraphic = new WaveMediumGraphic( waveMedium, getApparatusPanel(), this );
        this.addGraphic( waveMediumGraphic, 7 );
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