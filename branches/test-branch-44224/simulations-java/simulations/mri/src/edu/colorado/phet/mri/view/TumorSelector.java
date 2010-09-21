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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.view.MultiStateButton;
import edu.colorado.phet.mri.MriResources;
import edu.colorado.phet.mri.model.Head;
import edu.colorado.phet.mri.model.Tumor;

/**
 * TumorSelector
 * <p/>
 * Provides a button that adds a tumor to the head
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TumorSelector extends JPanel {
    private static String unselectedStr = MriResources.getString( "ControlPanel.AddTumor" );
    private static String selectedStr = MriResources.getString( "ControlPanel.RemoveTumor" );

    private Tumor tumor;

    /**
     * @param head
     * @param model
     */
    public TumorSelector( final Head head, final BaseModel model ) {
        final MultiStateButton tumorBtn = new MultiStateButton();
        tumorBtn.addMode( unselectedStr, unselectedStr, null );
        tumorBtn.addMode( selectedStr, selectedStr, null );
        add( tumorBtn );
        tumorBtn.addActionListener( unselectedStr, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                tumor = new Tumor( head.getBounds().getX() + head.getBounds().getWidth() * 0.5,
                                   head.getBounds().getY() + head.getBounds().getHeight() * 0.15,
                                   head.getBounds().getWidth() * 0.3,
                                   head.getBounds().getHeight() / 6 );
                head.addTumor( tumor, model );
                tumorBtn.setText( selectedStr );
            }
        } );
        tumorBtn.addActionListener( selectedStr, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                head.removeTumor( tumor, model );
                tumorBtn.setText( unselectedStr );
            }
        } );
    }
}
