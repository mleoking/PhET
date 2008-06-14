package edu.colorado.phet.circuitconstructionkit.piccolo.lifelike;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.view.CCKImageSuite;
import edu.colorado.phet.circuitconstructionkit.ICCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Switch;
import edu.colorado.phet.circuitconstructionkit.piccolo.RectangularComponentNode;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 2:33:35 PM
 */

public class SwitchBodyRectangleNode extends RectangularComponentNode {
    public SwitchBodyRectangleNode( CCKModel model, Switch s, JComponent component, ICCKModule module ) {
        super( model, s, CCKImageSuite.getInstance().getKnifeBoardImage().getWidth(), CCKImageSuite.getInstance().getKnifeBoardImage().getHeight(), component, module );
    }
}
