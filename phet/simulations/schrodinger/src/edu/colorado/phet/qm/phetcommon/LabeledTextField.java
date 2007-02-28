package edu.colorado.phet.qm.phetcommon;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Oct 25, 2006
 * Time: 7:57:19 PM
 * Copyright (c) Oct 25, 2006 by Sam Reid
 */

public class LabeledTextField extends JPanel {
    public JTextField jTextField;

    public LabeledTextField( String label, double minimumProbabilityForDetection ) {
        add( new JLabel( label ) );
        jTextField = new JTextField( "" + minimumProbabilityForDetection, 5 );
        jTextField.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyListeners();
            }
        } );
        add( jTextField );
        JButton applyButton = new JButton( "Apply" );
        applyButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyListeners();
            }
        } );
        add( applyButton );
    }

    public double getValue() {
        return Double.parseDouble( jTextField.getText() );
    }

    private ArrayList listeners = new ArrayList();

    public static interface Listener {
        void valueChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.valueChanged();
        }
    }
}
