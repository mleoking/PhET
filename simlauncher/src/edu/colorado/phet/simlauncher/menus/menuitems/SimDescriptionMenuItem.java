/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.menus.menuitems;

import edu.colorado.phet.simlauncher.actions.DisplaySimDescriptionAction;
import edu.colorado.phet.simlauncher.model.SimContainer;

import javax.swing.*;

/**
 * SimLaunchMenuItem
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimDescriptionMenuItem extends JMenuItem {

    public SimDescriptionMenuItem( SimContainer simContainer ) {
        super( "Show description" );
        addActionListener( new DisplaySimDescriptionAction( simContainer, this ) );
    }
}
