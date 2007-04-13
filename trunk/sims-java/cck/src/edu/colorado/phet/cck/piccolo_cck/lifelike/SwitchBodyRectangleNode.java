package edu.colorado.phet.cck.piccolo_cck.lifelike;

import edu.colorado.phet.cck.CCKImageSuite;
import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Switch;
import edu.colorado.phet.cck.piccolo_cck.RectangularComponentNode;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 2:33:35 PM
 * Copyright (c) Sep 19, 2006 by Sam Reid
 */

public class SwitchBodyRectangleNode extends RectangularComponentNode {
    public SwitchBodyRectangleNode( CCKModel model, Switch s, JComponent component, ICCKModule module ) {
        super( model, s, CCKImageSuite.getInstance().getKnifeBoardImage().getWidth(), CCKImageSuite.getInstance().getKnifeBoardImage().getHeight(), component, module );
    }
}
