/**
 * Class: SingleSourceModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 3, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.ApplicationModel;
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
    private WaveMediumGraphic waveMediumGraphic;

    /**
     * @param appModel
     * @param name
     */
    protected SingleSourceModule( ApplicationModel appModel, String name ) {
        super( appModel, name );
        SingleSourceApparatusPanel apparatusPanel = new SingleSourceApparatusPanel( (SoundModel)getModel(), appModel.getClock() );
        this.setApparatusPanel( apparatusPanel );

        /*final WaveMedium*/ waveMedium = ( (SoundModel)getModel() ).getWaveMedium();
        WaveMediumGraphic waveMediumGraphic = new WaveMediumGraphic( waveMedium, getApparatusPanel(), this );
        this.addGraphic( waveMediumGraphic, 7 );
        Point2D.Double audioSource = new Point2D.Double( SoundConfig.s_wavefrontBaseX,
                                                         SoundConfig.s_wavefrontBaseY );
        waveMediumGraphic.initLayout( audioSource,
                                      SoundConfig.s_wavefrontHeight,
                                      SoundConfig.s_wavefrontRadius );
        initControlPanel();
    }

    private void initControlPanel() {
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