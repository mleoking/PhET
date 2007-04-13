/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * TestDynamicDialog demonstrates how to dynamically change 
 * the input area of a custom dialog.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestDynamicDialog extends JFrame implements ActionListener, ChangeListener {

    private JButton _openButton;
    private JSlider _slider;
    private DynamicDialog _dialog;
    
    public TestDynamicDialog() {
        super( "TestDynamicDialog" );
        
        _openButton = new JButton( "Open..." );
        _openButton.addActionListener( this );
        
        _slider = new JSlider();
        _slider.setMinimum( 0 );
        _slider.setMaximum( 10 );
        _slider.setMajorTickSpacing( 10 );
        _slider.setMinorTickSpacing( 1 );
        _slider.setSnapToTicks( true );
        _slider.setPaintLabels( true );
        _slider.setPaintTicks( true );
        _slider.addChangeListener( this );
        
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 20, 20, 20, 20 ) );
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
        panel.add( _openButton );
        panel.add( _slider );
        
        getContentPane().add( panel );
        pack();
    }
    
    public void actionPerformed( ActionEvent event ) {
        if ( event.getSource() == _openButton && _dialog == null ) {
            _dialog = new DynamicDialog( this, _slider.getValue() );
            _dialog.addWindowListener( new WindowAdapter() {
                public void windowClosing( WindowEvent we ) {
                    _dialog = null;
                }
                public void windowClosed( WindowEvent we ) {
                    _dialog = null;
                }
            } );
            _dialog.show();
        }
    }
    
    public void stateChanged( ChangeEvent event ) {
        if ( event.getSource() == _slider ) {
            if ( _dialog != null ) {
                _dialog.setCount( _slider.getValue() );
            }
        }
    }
    
    public static class DynamicDialog extends JDialog {
        
        JPanel _inputPanel;
        
        public DynamicDialog( JFrame owner, int count ) {
            super( owner, "Dynamic Dialog" );
            _inputPanel = new JPanel();
            _inputPanel.setLayout( new BoxLayout( _inputPanel, BoxLayout.Y_AXIS ) );
            
            JPanel actionPanel = new JPanel();
            JButton closeButton = new JButton( "Close" );
            closeButton.addActionListener( new ActionListener() { 
                public void actionPerformed( ActionEvent event ) {
                    dispose();
                }
            } );
            actionPanel.add( closeButton );
            
            JPanel parentPanel = new JPanel();
            parentPanel.setLayout( new BorderLayout() );
            parentPanel.add( _inputPanel, BorderLayout.CENTER );
            parentPanel.add( actionPanel, BorderLayout.SOUTH );
            getContentPane().add( parentPanel );
            
            setCount( count );
        }
        
        public void setCount( int count ) {
            System.out.println( "DynamicDialog.setCount " + count );
            _inputPanel.removeAll();
            for ( int i = 0; i < count; i++ ) {
                _inputPanel.add( new JLabel( "Item" + ( i + 1 ) ) );
            }
            pack();
        }
    }
    
    public static void main( String[] args ) {
        TestDynamicDialog frame = new TestDynamicDialog();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.show();
    }
}
