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

import edu.colorado.phet.mri.util.ControlBorderFactory;
import edu.colorado.phet.mri.view.TumorSelector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * HeadOnOffControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HeadControl extends JPanel {

    public HeadControl( final HeadModule module ) {
        super( new GridBagLayout() );
        setBorder( ControlBorderFactory.createBorder( "Head" ) );
        TumorSelector tumorSelector = new TumorSelector( module.getHead(), module.getModel() );
        final JCheckBox showHideCB = new JCheckBox( "Show" );
        showHideCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getHeadGraphic().setVisible( showHideCB.isSelected() );
            }
        } );
        showHideCB.setSelected( true );

        GridBagConstraints gbc = new GridBagConstraints( GridBagConstraints.RELATIVE, 0,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        add( showHideCB, gbc );
        add( tumorSelector, gbc );
    }
}
