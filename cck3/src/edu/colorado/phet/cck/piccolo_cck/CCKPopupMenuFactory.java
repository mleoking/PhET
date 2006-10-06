package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.components.*;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Oct 6, 2006
 * Time: 7:30:11 AM
 * Copyright (c) Oct 6, 2006 by Sam Reid
 */

public class CCKPopupMenuFactory {
    private ICCKModule module;

    public CCKPopupMenuFactory( ICCKModule module ) {
        this.module = module;
    }

    public JPopupMenu createPopupMenu( Branch branch ) {
        if( branch instanceof Switch ) {
            return new ComponentMenu.SwitchMenu( (Switch)branch, module ).getMenuComponent();
        }
        else if( branch instanceof SeriesAmmeter ) {
            return new ComponentMenu.SeriesAmmeterMenu( (SeriesAmmeter)branch, module ).getMenuComponent();
        }
        else if( branch instanceof ACVoltageSource ) {
            return new PiccoloACVoltageSourceMenu( (Battery)branch, module );
        }
        else {
            System.out.println( "No popup menu found for component: " + branch );
            return null;
        }
    }
}
