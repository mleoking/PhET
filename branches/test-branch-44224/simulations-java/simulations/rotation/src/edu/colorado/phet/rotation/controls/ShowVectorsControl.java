package edu.colorado.phet.rotation.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.rotation.RotationStrings;
import edu.colorado.phet.rotation.view.RotationColorScheme;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 10:10:46 PM
 */

public class ShowVectorsControl extends VerticalLayoutPanel {
    private VectorViewModel vectorViewModel;
//    private GridBagConstraints gridBagConstraints;

    public ShowVectorsControl( final VectorViewModel vectorViewModel ) {
        this.vectorViewModel = vectorViewModel;

//        gridBagConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        final JCheckBox velocityCheckBox = new JCheckBox( "<html>"+RotationStrings.getString( "show" ) + "<br>" + RotationStrings.getString( "variable.velocity" ) + " " + RotationStrings.getString( "vector" )+"</html>", vectorViewModel.isVelocityVisible() );
        velocityCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                vectorViewModel.setVelocityVisible( velocityCheckBox.isSelected() );
            }
        } );

        final JCheckBox accelerationCheckBox = new JCheckBox( "<html>"+RotationStrings.getString( "show" ) + "<br>" + RotationStrings.getString( "variable.acceleration" ) + " " + RotationStrings.getString( "vector" )+"</html>", vectorViewModel.isAccelerationVisible() );
        accelerationCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                vectorViewModel.setAccelerationVisible( accelerationCheckBox.isSelected() );
            }
        } );

        vectorViewModel.addListener( new VectorViewModel.Listener() {
            public void visibilityChanged() {
                velocityCheckBox.setSelected( vectorViewModel.isVelocityVisible() );
                accelerationCheckBox.setSelected( vectorViewModel.isAccelerationVisible() );
            }
        } );

//        JLabel showVectorLabel = new JLabel( RotationStrings.getString( "controls.show.vectors" ) );
//        showVectorLabel.setFont( RotationLookAndFeel.getControlPanelTitleFont() );
//        velocityCheckBox.setFont( RotationLookAndFeel.getCheckBoxFont() );
        velocityCheckBox.setForeground( RotationColorScheme.VELOCITY_COLOR );
        accelerationCheckBox.setForeground( RotationColorScheme.ACCELERATION_COLOR );

//        accelerationCheckBox.setFont( RotationLookAndFeel.getCheckBoxFont() );


//        add( showVectorLabel, gridBagConstraints );
        add( velocityCheckBox );
        add( accelerationCheckBox );
    }

//    public void add( JComponent compononent ) {
//        add( compononent, gridBagConstraints );
//    }
}
