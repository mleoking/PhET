/**
 * Class: SingleSourceModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 3, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.sound.model.SineWaveFunction;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.Wavefront;
import edu.colorado.phet.sound.model.WavefrontOscillator;
import edu.colorado.phet.sound.view.SingleSourceApparatusPanel;
import edu.colorado.phet.sound.view.SoundControlPanel;

import java.awt.*;

public abstract class SingleSourceModule extends SoundModule {

    protected SingleSourceModule( ApplicationModel appModel, String name ) {
        super( name );

        this.setModel( new SoundModel( appModel.getClock() ) );
        SingleSourceApparatusPanel apparatusPanel = new SingleSourceApparatusPanel( (SoundModel)getModel() );
        this.setApparatusPanel( apparatusPanel );
        initModel();
        initControlPanel();
    }

    private void initControlPanel() {
        this.setControlPanel( new SoundControlPanel( this ) );
    }

}