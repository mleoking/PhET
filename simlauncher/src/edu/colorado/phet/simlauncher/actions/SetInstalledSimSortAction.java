/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.actions;

import edu.colorado.phet.simlauncher.Options;
import edu.colorado.phet.simlauncher.view.SimTable;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * SetInstalledSimSortAction
 * <p/>
 * An action that sets the sort type for the installed simulations
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SetInstalledSimSortAction extends AbstractAction {
    private SimTable.SimComparator sortType;

    public SetInstalledSimSortAction( SimTable.SimComparator sortType ) {
        this.sortType = sortType;
    }

    public void actionPerformed( ActionEvent e ) {
        Options.instance().setInstalledSimSortType( sortType );
    }
}
