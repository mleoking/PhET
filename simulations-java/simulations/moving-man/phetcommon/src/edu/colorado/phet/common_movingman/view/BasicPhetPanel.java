/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common_movingman.view;

import javax.swing.*;

/**
 * The content pane for the JFrame of a PhetApplication.
 *
 * @author ?
 * @version $Revision$
 * @deprecated use edu.colorado.phet.common.view.ContentPanel
 */
public class BasicPhetPanel extends ContentPanel {

    public BasicPhetPanel( JComponent apparatusPanelContainer, JComponent controlPanel, JComponent monitorPanel, JComponent appControl ) {
        super( apparatusPanelContainer, controlPanel, monitorPanel, appControl );
    }

}