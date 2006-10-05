package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.CCKImageSuite;
import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Battery;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 5, 2006
 * Time: 12:02:15 AM
 * Copyright (c) Oct 5, 2006 by Sam Reid
 */
public class BatteryNode extends ComponentImageNode {
    private Battery battery;
    private ICCKModule module;

    public BatteryNode( CCKModel model, Battery battery, Component component, ICCKModule module ) {
        super( model, battery, CCKImageSuite.getInstance().getLifelikeSuite().getBatteryImage(), component );
        this.battery = battery;
        this.module = module;
        update();
    }

    protected JPopupMenu createPopupMenu() {
        return new ComponentMenu.BatteryJMenu( battery, module ).getMenuComponent();
    }
}
