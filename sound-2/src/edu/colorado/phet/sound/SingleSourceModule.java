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
import edu.colorado.phet.sound.view.SingleSourceApparatusPanel;
import edu.colorado.phet.sound.view.SoundControlPanel;
import edu.colorado.phet.sound.view.WaveMediumGraphicB;

import java.awt.geom.Point2D;

public abstract class SingleSourceModule extends SoundModule {

    protected SingleSourceModule( ApplicationModel appModel, String name ) {
        super( appModel, name );
        SingleSourceApparatusPanel apparatusPanel = new SingleSourceApparatusPanel( (SoundModel)getModel() );
        this.setApparatusPanel( apparatusPanel );

        final WaveMedium waveMedium = ((SoundModel)getModel()).getWaveMedium();
        WaveMediumGraphicB waveMediumGraphic = new WaveMediumGraphicB( waveMedium, getApparatusPanel(), this );
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

}