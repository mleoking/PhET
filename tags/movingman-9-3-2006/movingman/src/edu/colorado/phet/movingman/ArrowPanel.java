/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman;

import edu.colorado.phet.common.view.components.HorizontalLayoutPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: May 18, 2005
 * Time: 10:15:19 PM
 * Copyright (c) May 18, 2005 by Sam Reid
 */

public class ArrowPanel extends HorizontalLayoutPanel {
    public ArrowPanel( final MovingManModule module ) {
        setBorder( BorderFactory.createTitledBorder( "Vectors" ) );
        final JCheckBox velocity = new JCheckBox( "Velocity" );
        velocity.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setShowVelocityVector( velocity.isSelected() );
            }
        } );


        final JCheckBox acceleration = new JCheckBox( "Acceleration" );
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
}
