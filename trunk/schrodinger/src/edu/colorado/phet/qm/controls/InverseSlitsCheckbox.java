/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.qm.davissongermer.QWIStrings;
import edu.colorado.phet.qm.view.QWIPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 11, 2005
 * Time: 9:19:53 PM
 * Copyright (c) Dec 11, 2005 by Sam Reid
 */

public class InverseSlitsCheckbox extends JCheckBox {
    private QWIPanel QWIPanel;

    public InverseSlitsCheckbox( final QWIPanel QWIPanel ) {
        super( QWIStrings.getString( "anti.slits" ) );
        this.QWIPanel = QWIPanel;
        setSelected( QWIPanel.isInverseSlits() );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                QWIPanel.setInverseSlits( isSelected() );
            }
        } );
        QWIPanel.addListener( new QWIPanel.Adapter() {
            public void inverseSlitsChanged() {
                setSelected( QWIPanel.isInverseSlits() );
            }
        } );
    }
}
