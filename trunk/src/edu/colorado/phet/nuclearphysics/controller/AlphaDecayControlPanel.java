/**
 * Class: AlphaDecayControlPanel
 * Package: edu.colorado.phet.nuclearphysics.controller
 * Author: Another Guy
 * Date: Mar 2, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.view.AlphaDecayModule;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class AlphaDecayControlPanel extends JPanel {
    private NuclearPhysicsModule module;

    public AlphaDecayControlPanel( final AlphaDecayModule module ) {
        this.module = module;

        JButton replayBtn = new JButton( "Replay" );
        replayBtn.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.run();
            }
        } );

        setLayout( new GridBagLayout() );
        int rowIdx = 0;
        try {
            GraphicsUtil.addGridBagComponent( this, replayBtn,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        Border titledBorder = BorderFactory.createTitledBorder( baseBorder, "Alpah Decay" );
        this.setBorder( titledBorder );

    }
}
