/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/view/AbstractSimPanel.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.1 $
 * Date modified : $Date: 2006/07/25 18:00:18 $
 */
package edu.colorado.phet.simlauncher.view;

import edu.colorado.phet.simlauncher.model.Catalog;
import edu.colorado.phet.simlauncher.model.SimContainer;

import javax.swing.*;
import java.awt.*;

/**
 * ISimPanel
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1 $
 */
public abstract class AbstractSimPanel extends JPanel implements Catalog.ChangeListener, SimContainer {
    public AbstractSimPanel( LayoutManager layout ) {
        super( layout );
    }

    protected abstract void updateSimTable();
}
