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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.DischargeLampsResources;

/**
 * SlowMotionControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SlowMotionCheckBox extends JCheckBox {

    public SlowMotionCheckBox( final Clock clock ) {
        super( new AbstractAction( DischargeLampsResources.getString( "Controls.SlowMotion" ) ) {
            public void actionPerformed( ActionEvent e ) {
                JCheckBox cb = (JCheckBox) e.getSource();
                if ( cb.isSelected() ) {
                    double dt = DischargeLampsConfig.DT / 5;
                    clock.setTimingStrategy( new TimingStrategy.Constant( dt ) );
                }
                else {
                    clock.setTimingStrategy( new TimingStrategy.Constant( DischargeLampsConfig.DT ) );
                }
            }
        } );
    }
}
