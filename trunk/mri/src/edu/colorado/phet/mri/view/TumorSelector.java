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

import edu.colorado.phet.mri.model.Head;
import edu.colorado.phet.mri.model.Tumor;
import edu.colorado.phet.common.model.BaseModel;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * TumorSelector
 * <p>
 * Provides a button that adds a tumor to the head
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TumorSelector extends JPanel {

    public TumorSelector( final Head head, final BaseModel model ) {
        JButton tumorBtn = new JButton( "Add tumor");
        add( tumorBtn );
        tumorBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {

                Tumor tumor  = new Tumor( head.getBounds().getX() + head.getBounds().getWidth() / 2,
                                          head.getBounds().getY() + head.getBounds().getHeight() / 3,
                                          head.getBounds().getWidth() * 2/ 3,
                                          head.getBounds().getHeight() / 6);
                head.addTumor( tumor, model );
            }
        } );
    }
}
