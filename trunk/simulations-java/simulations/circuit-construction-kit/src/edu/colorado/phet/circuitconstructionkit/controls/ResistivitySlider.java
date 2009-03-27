package edu.colorado.phet.circuitconstructionkit.controls;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.model.ResistivityManager;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Jun 11, 2008
 * Time: 9:55:32 AM
 */
public class ResistivitySlider extends JPanel {
    private LinearValueControl control;

    public ResistivitySlider() {
        control = new LinearValueControl( ResistivityManager.DEFAULT_RESISTIVITY, 1, CCKResources.getString( "CCK3ControlPanel.WireResistivitySlider" ), "0.000000", "" );
        add( control );

        Font labelFont = new PhetFont( Font.PLAIN, 10 );
        JLabel lowLabel = new JLabel( CCKResources.getString( "CCK3ControlPanel.AlmostNoneLabel" ) );
        lowLabel.setFont( labelFont );
        JLabel highLabel = new JLabel( CCKResources.getString( "CCK3ControlPanel.LotsLabel" ) );
        highLabel.setFont( labelFont );

        control.addTickLabel( ResistivityManager.DEFAULT_RESISTIVITY, lowLabel );
        control.addTickLabel( 1, highLabel );
        control.setTextFieldVisible( false );
    }

    public double getValue() {
        return control.getValue();
    }

    public void addChangeListener( ChangeListener changeListener ) {
        control.addChangeListener( changeListener );
    }
}
