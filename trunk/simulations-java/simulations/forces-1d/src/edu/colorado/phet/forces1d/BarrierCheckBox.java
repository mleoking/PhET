/*  */
package edu.colorado.phet.forces1d;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.forces1d.model.BoundaryCondition;

/**
 * User: Sam Reid
 * Date: Jan 25, 2005
 * Time: 3:18:39 AM
 */

public class BarrierCheckBox extends JCheckBox {
    public BarrierCheckBox( final Force1DApplication module ) {
        super( SimStrings.get( "BarrierCheckBox.barriers" ), true );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( !isSelected() ) {
                    module.getForceModel().setBoundsOpen();
                }
                else {
                    module.getForceModel().setBoundsWalled();
                }
            }
        } );
        module.getForceModel().addBoundaryConditionListener( new BoundaryCondition.Listener() {
            public void boundaryConditionOpen() {
                setSelected( false );
            }

            public void boundaryConditionWalls() {
                setSelected( true );
            }
        } );

    }
}
