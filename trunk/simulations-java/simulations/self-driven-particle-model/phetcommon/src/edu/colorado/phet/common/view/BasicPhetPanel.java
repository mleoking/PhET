/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/view/BasicPhetPanel.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.view;

import javax.swing.JComponent;

/**
 * The content pane for the JFrame of a PhetApplication.
 * 
 * @deprecated use edu.colorado.phet.common.view.ContentPanel
 * @author ?
 * @version $Revision: 1.1.1.1 $
 */
public class BasicPhetPanel extends ContentPanel {

    public BasicPhetPanel( JComponent apparatusPanelContainer, JComponent controlPanel, JComponent monitorPanel, JComponent appControl ) {
        super( apparatusPanelContainer, controlPanel, monitorPanel, appControl );
    }

}