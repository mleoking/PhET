/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.control;

import edu.colorado.phet.common.model.clock.Clock;
import edu.colorado.phet.common.model.clock.TimingStrategy;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * SlowMotionControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SlowMotionCheckBox extends JCheckBox {

    public SlowMotionCheckBox( final Clock clock ) {
        super( new AbstractAction( SimStrings.get( "Controls.SlowMotion" ) ) {
            public void actionPerformed( ActionEvent e ) {
                JCheckBox cb = (JCheckBox)e.getSource();
                if( cb.isSelected() ) {
                    double dt = DischargeLampsConfig.DT / 5;
                    clock.setTimingStrategy( new TimingStrategy.Constant( dt ));
                }
                else {
                    clock.setTimingStrategy( new TimingStrategy.Constant( DischargeLampsConfig.DT ));
                }
            }
        } );
    }
}
