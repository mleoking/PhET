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

public class TwoSourceInterferenceControlPanel extends PhetControlPanel {

    public TwoSourceInterferenceControlPanel( Module module ) {
        super( module );
        this.module = module;
        setControlPane( new ControlPanel( (SoundModule)module ) );
    }

    private static class ControlPanel extends JPanel {
        int rowIdx = 0;
        ControlPanel( SoundModule module ) {
            this.setLayout( new GridBagLayout() );
            try {
                GraphicsUtil.addGridBagComponent( this, new AudioControlPanel( (SoundModule)module ),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.NONE, GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }
        }
    }
}
