// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.rotation.RotationStrings;
import edu.colorado.phet.rotation.model.AngleUnitModel;

/**
 * Author: Sam Reid
 * Jul 20, 2007, 10:42:26 AM
 */
public class AngleUnitsSelectionControl extends VerticalLayoutPanel {
    private JRadioButton degrees;
    private JRadioButton radians;
    private AngleUnitModel angleUnitModel;

    public AngleUnitsSelectionControl( final AngleUnitModel angleUnitModel ) {
        this.angleUnitModel = angleUnitModel;
        degrees = new JRadioButton( RotationStrings.getString( "units.degrees" ) );
        degrees.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                angleUnitModel.setRadians( false );
            }
        } );
        radians = new JRadioButton( RotationStrings.getString( "units.radians" ) );
        radians.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                angleUnitModel.setRadians( true );
            }
        } );

        add( degrees );
        add( radians );

        setBorder( new TitledBorder( RotationStrings.getString( "controls.angle.units" ) ) );
        angleUnitModel.addListener( new AngleUnitModel.Listener() {
            public void changed() {
                update();
            }
        } );
        update();
    }

    private void update() {
        degrees.setSelected( !angleUnitModel.isRadians() );
        radians.setSelected( angleUnitModel.isRadians() );
    }

}