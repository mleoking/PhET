package edu.colorado.phet.cck.piccolo_cck.lifelike;

import edu.colorado.phet.cck.CCKImageSuite;
import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Switch;
import edu.colorado.phet.cck.piccolo_cck.ComponentImageNode;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 2:33:35 PM
 * Copyright (c) Sep 19, 2006 by Sam Reid
 */

public class SwitchBodyImageNode extends ComponentImageNode {
    public SwitchBodyImageNode( CCKModel model, Switch s, JComponent component, ICCKModule module ) {
        super( model, s, CCKImageSuite.getInstance().getKnifeBoardImage(), component, module );
    }
}
