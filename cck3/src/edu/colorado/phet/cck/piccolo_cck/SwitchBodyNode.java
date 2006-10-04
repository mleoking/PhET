package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.CCKImageSuite;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Switch;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 2:33:35 PM
 * Copyright (c) Sep 19, 2006 by Sam Reid
 */

public class SwitchBodyNode extends ComponentImageNode {
    public SwitchBodyNode( CCKModel model, Switch s, Component component ) {
        super( model, s, CCKImageSuite.getInstance().getKnifeBoardImage(), component );
    }

    protected JPopupMenu createPopupMenu() {
        return null;
    }
}
