/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.mri.model.Head;
import edu.colorado.phet.mri.model.Tumor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * TumorSelector
 * <p/>
 * Provides a button that adds a tumor to the head
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TumorSelector extends JPanel {
    private static String unselectedStr = SimStrings.get( "ControlPanel.AddTumor" );
    private static String selectedStr = SimStrings.get( "ControlPanel.RemoveTumor" );

    private Tumor tumor;

    /**
     * @param head
     * @param model
     */
    public TumorSelector( final Head head, final BaseModel model ) {
        final JToggleButton tumorBtn = new JToggleButton( unselectedStr );
        add( tumorBtn );
        tumorBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( tumorBtn.isSelected() ) {
                    tumor = new Tumor( head.getBounds().getX() + head.getBounds().getWidth() * 0.5,
                                       head.getBounds().getY() + head.getBounds().getHeight() * 0.15,
                                       head.getBounds().getWidth() * 0.3,
                                       head.getBounds().getHeight() / 6 );
                    head.addTumor( tumor, model );
                    tumorBtn.setText( selectedStr );
                }
                else {
                    head.removeTumor( tumor, model );
                    tumorBtn.setText( unselectedStr );
                }
            }
        } );
    }
}
