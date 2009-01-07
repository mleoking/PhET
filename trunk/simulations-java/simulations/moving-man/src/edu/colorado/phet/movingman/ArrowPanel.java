/*  */
package edu.colorado.phet.movingman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

/**
 * User: Sam Reid
 * Date: May 18, 2005
 * Time: 10:15:19 PM
 */

public class ArrowPanel extends JPanel {
    public ArrowPanel( final IArrowPanelModule module ) {
        super( new FlowLayout() );
        setBorder( BorderFactory.createTitledBorder( SimStrings.get( "controls.vectors" ) ) );
        final JCheckBox velocity = new JCheckBox( SimStrings.get( "variables.velocity" ) );
        velocity.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setShowVelocityVector( velocity.isSelected() );
            }
        } );


        final JCheckBox acceleration = new JCheckBox( SimStrings.get( "variables.acceleration" ) );
        acceleration.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setShowAccelerationVector( acceleration.isSelected() );
            }
        } );
        add( velocity );
        add( acceleration );
        module.setShowAccelerationVector( false );
        module.setShowVelocityVector( false );
    }

    public static interface IArrowPanelModule {
        void setShowVelocityVector( boolean selected );

        void setShowAccelerationVector( boolean selected );
    }
}
