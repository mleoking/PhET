package edu.colorado.phet.cck.piccolo_cck.lifelike;

import edu.colorado.phet.cck.CCKImageSuite;
import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Battery;
import edu.colorado.phet.cck.piccolo_cck.ComponentImageNode;
import edu.colorado.phet.cck.piccolo_cck.ComponentMenu;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Oct 5, 2006
 * Time: 12:02:15 AM
 * Copyright (c) Oct 5, 2006 by Sam Reid
 */
public class BatteryNode extends ComponentImageNode {
    private Battery battery;
    private ICCKModule module;

    public BatteryNode( CCKModel model, Battery battery, JComponent component, ICCKModule module ) {
        super( model, battery, CCKImageSuite.getInstance().getLifelikeSuite().getBatteryImage(), component, module );
        this.battery = battery;
        this.module = module;
        update();
    }

    protected JPopupMenu createPopupMenu() {
        return new ComponentMenu.BatteryJMenu( battery, module ).getMenuComponent();
    }
}
