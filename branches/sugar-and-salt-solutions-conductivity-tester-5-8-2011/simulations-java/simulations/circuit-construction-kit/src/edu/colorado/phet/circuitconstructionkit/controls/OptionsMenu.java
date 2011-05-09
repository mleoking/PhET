// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.controls;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Jul 11, 2006
 * Time: 1:05:36 AM
 */

public class OptionsMenu extends JMenu {
    public OptionsMenu(PhetApplication application, final CCKModule cck) {
        super(CCKResources.getString("OptionsMenu.Title"));
        setMnemonic(CCKResources.getString("OptionsMenu.TitleMnemonic").charAt(0));
        add(new BackgroundColorMenuItem(application, cck));
        add(new ToolboxColorMenuItem(application, cck));
    }
}
