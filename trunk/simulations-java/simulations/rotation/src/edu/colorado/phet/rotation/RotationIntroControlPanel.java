package edu.colorado.phet.rotation;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.rotation.controls.ResetButton;
import edu.colorado.phet.rotation.controls.RulerButton;
import edu.colorado.phet.rotation.controls.ShowVectorsControl;
import edu.colorado.phet.rotation.graphs.AbstractRotationGraphSet;
import edu.colorado.phet.rotation.view.RotationSimPlayAreaNode;

/**
 * Created by: Sam
 * Dec 1, 2007 at 7:39:48 AM
 */
public class RotationIntroControlPanel extends VerticalLayoutPanel {

    public RotationIntroControlPanel( final RotationIntroModule introModule, RotationSimPlayAreaNode playAreaNode ) {
        add( new ResetButton( introModule ) );
//        checkBoxPanel.add( new RulerButton( rulerNode ) );
        add( new ShowVectorsControl( introModule.getVectorViewModel() ) );

        RulerButton rulerButton = new RulerButton( playAreaNode.getRulerNode() );
        add( rulerButton );
        add( createAngleSlider( introModule ) );
        add( createVelocitySlider( introModule ) );
    }

    public static double radiansToDegrees( double rad ) {
        return rad * 360 / 2 / Math.PI;
    }

    public static double degreesToRadians( double d ) {
        return d * Math.PI * 2 / 360;
    }

    private LinearValueControl createAngleSlider( final RotationIntroModule introModule ) {
        final LinearValueControl linearSlider = new LinearValueControl( radiansToDegrees( -Math.PI * 2 * 2 ), radiansToDegrees( Math.PI * 2 * 2 ), 0.0, RotationStrings.getString( "variable.angle" ), "0.00", RotationStrings.getString( "units.degrees" ) ) {
            protected boolean isValueInRange( double value ) {
                return true;
            }
        };
        linearSlider.getTextField().setColumns( "1000.00".length() );
        linearSlider.setSignifyOutOfBounds( false );
        final ChangeListener listener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                introModule.getRotationModel().getRotationPlatform().setPositionDriven();
                introModule.getRotationModel().getRotationPlatform().setPosition( degreesToRadians( linearSlider.getValue() ) );
            }
        };
        linearSlider.addChangeListener( listener );

        introModule.getRotationModel().getRotationPlatform().getPositionVariable().addListener( new IVariable.Listener() {
            public void valueChanged() {
                //set a value to the slider without sending notification, otherwise interpolated values for omega will cause problems
                linearSlider.removeChangeListener( listener );
                linearSlider.setValue( radiansToDegrees( introModule.getRotationModel().getRotationPlatform().getPositionVariable().getValue() ) );
                linearSlider.addChangeListener( listener );
            }
        } );
        return linearSlider;
    }

    private LinearValueControl createVelocitySlider( final RotationIntroModule introModule ) {
        final LinearValueControl linearSlider = new LinearValueControl( radiansToDegrees( AbstractRotationGraphSet.MIN_ANG_VEL ), radiansToDegrees( AbstractRotationGraphSet.MAX_ANG_VEL ), 0.0, RotationStrings.getString( "variable.angular.velocity" ), "0.00", RotationStrings.getString( "units.degrees" ) + "/" + RotationStrings.getString( "units.s" ) ) {
            protected boolean isValueInRange( double value ) {
                return true;
            }
        };
        linearSlider.getTextField().setColumns( "1000.00".length() );
        linearSlider.setSignifyOutOfBounds( false );
        final ChangeListener listener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                introModule.getRotationModel().getRotationPlatform().setVelocityDriven();
                introModule.getRotationModel().getRotationPlatform().setVelocity( degreesToRadians( linearSlider.getValue() ) );
            }
        };
        linearSlider.addChangeListener( listener );

        introModule.getRotationModel().getRotationPlatform().getVelocityVariable().addListener( new IVariable.Listener() {
            public void valueChanged() {
                //set a value to the slider without sending notification, otherwise interpolated values for omega will cause problems
                linearSlider.removeChangeListener( listener );
                linearSlider.setValue( radiansToDegrees( introModule.getRotationModel().getRotationPlatform().getVelocityVariable().getValue() ) );
                linearSlider.addChangeListener( listener );
            }
        } );
        return linearSlider;
    }
}
