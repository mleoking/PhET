/**
 * Class: TwoSourceInterferenceControlPanel
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 13, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.PhetControlPanel;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.sound.SoundModule;

import javax.swing.*;
import java.awt.*;

public class TwoSourceInterferenceControlPanel extends SoundControlPanel {

    public TwoSourceInterferenceControlPanel( Module module ) {
        super( module );
        this.module = module;
        this.addPanel( new AudioControlPanel( (SoundModule)module ));
    }
}
