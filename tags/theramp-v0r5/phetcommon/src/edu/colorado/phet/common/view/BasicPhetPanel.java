/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import javax.swing.JComponent;

/**
 * The content pane for the JFrame of a PhetApplication.
 * 
 * @deprecated use edu.colorado.phet.common.view.ContentPanel
 * @author ?
 * @version $Revision$
 */
public class BasicPhetPanel extends ContentPanel {

    public BasicPhetPanel( JComponent apparatusPanelContainer, JComponent controlPanel, JComponent monitorPanel, JComponent appControl ) {
        super( apparatusPanelContainer, controlPanel, monitorPanel, appControl );
    }

}