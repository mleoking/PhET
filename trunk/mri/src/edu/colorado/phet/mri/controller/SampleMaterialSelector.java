/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.controller;

import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.model.SampleMaterial;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * SampleMaterialSelector
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SampleMaterialSelector extends JPanel {

    public SampleMaterialSelector( final MriModel model ) {
        final JComboBox selector = new JComboBox( SampleMaterial.INSTANCES );
        selector.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                model.setSampleMaterial( (SampleMaterial)selector.getSelectedItem() );
            }
        } );
//        selector.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                model.setSampleMaterial( (SampleMaterial)selector.getSelectedItem() );
//            }
//        } );
        add( selector );
    }
}
