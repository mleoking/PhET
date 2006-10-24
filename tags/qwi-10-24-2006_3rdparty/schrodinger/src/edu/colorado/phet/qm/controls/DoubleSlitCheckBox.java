/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.qm.davissongermer.QWIStrings;
import edu.colorado.phet.qm.model.QWIModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 11, 2005
 * Time: 8:17:28 AM
 * Copyright (c) Jul 11, 2005 by Sam Reid
 */

public class DoubleSlitCheckBox extends JCheckBox {
    private QWIModel QWIModel;

    public DoubleSlitCheckBox( final QWIModel QWIModel ) {
        super( QWIStrings.getString( "double.slit" ), QWIModel.isDoubleSlitEnabled() );
        setFont( new Font( "Lucida Sans", Font.BOLD, 22 ) );

        this.QWIModel = QWIModel;

        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( isSelected() ) {
                    getDiscreteModel().setDoubleSlitEnabled( true );
                }
                else {
                    getDiscreteModel().setDoubleSlitEnabled( false );
                }
            }
        } );
        QWIModel.addListener( new QWIModel.Adapter() {
            public void doubleSlitVisibilityChanged() {
                setSelected( QWIModel.isDoubleSlitEnabled() );
            }
        } );
    }

    public void setFont( Font font ) {
        super.setFont( font );
    }

    private QWIModel getDiscreteModel() {
        return QWIModel;
    }
}
