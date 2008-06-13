package edu.colorado.phet.phscale.control;

import java.awt.Insets;

import javax.swing.JButton;

import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.PHScaleStrings;


public class ResetAllButton extends JButton {
    public ResetAllButton() {
        super( PHScaleStrings.BUTTON_RESET_ALL  );
        setFont( PHScaleConstants.CONTROL_FONT );
        setMargin( new Insets( 10, 20, 10, 20 ) ); // top, left, bottom, right
    }
}
