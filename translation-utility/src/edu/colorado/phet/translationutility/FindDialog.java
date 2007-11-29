/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.EventListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;


public class FindDialog extends JDialog {
    
    private static final String FIND_LABEL = TUResources.getString( "label.find" );
    private static final String NEXT_LABEL = TUResources.getString( "button.next" );
    private static final String PREVIOUS_LABEL = TUResources.getString( "button.previous" );
    private static final String CLOSE_LABEL = TUResources.getString( "button.close" );
    
    private static final Icon NEXT_ICON = TUResources.getIcon( "nextArrow.png" );
    private static final Icon PREVIOUS_ICON = TUResources.getIcon( "previousArrow.png" );
    
    private JTextField _textField;
    private JButton _nextButton;
    private JButton _previousButton;
    private EventListenerList _listenerList;
    
    public static interface FindListener extends EventListener {
        public void findNext( String text );
        public void findPrevious( String text );
    }

    public FindDialog( Frame owner, String defaultText ) {
        super( owner );
        setTitle( TUResources.getString( "title.findDialog" ) );
        setModal( false );
        setResizable( false );
        
        _listenerList = new EventListenerList();
        
        JPanel inputPanel = new JPanel();
        {
            JLabel findLabel = new JLabel( FIND_LABEL );

            _textField = new JTextField( defaultText );
            _textField.setColumns( 30 );
            _textField.setEditable( true );
            _textField.addKeyListener( new KeyAdapter() {
                public void keyReleased( KeyEvent e ) {
                    updateButtons();
                }
            } );
            
            inputPanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
            inputPanel.add( findLabel );
            inputPanel.add( _textField );
        }
        
        JPanel buttonPanel = new JPanel();
        {
            _nextButton = new JButton( NEXT_LABEL, NEXT_ICON );
            _nextButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    fireNext();
                }
            } );

            _previousButton = new JButton( PREVIOUS_LABEL, PREVIOUS_ICON );
            _previousButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    firePrevious();
                }
            } );
            
            JButton closeButton = new JButton( CLOSE_LABEL );
            closeButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    dispose();
                }
            } );
            
            JPanel innerPanel = new JPanel( new GridLayout( 1, 7 ) );
            innerPanel.add( _nextButton );
            innerPanel.add( _previousButton );
            innerPanel.add( Box.createHorizontalStrut( 20 ) );
            innerPanel.add( closeButton );
            buttonPanel.add( innerPanel );
        }
        
        JPanel bottomPanel = new JPanel( new BorderLayout() );
        bottomPanel.add( new JSeparator(), BorderLayout.NORTH );
        bottomPanel.add( buttonPanel, BorderLayout.CENTER );

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );
        mainPanel.setBorder( new EmptyBorder( 10, 10, 0, 10 ) );
        mainPanel.add( inputPanel, BorderLayout.CENTER );
        mainPanel.add( bottomPanel, BorderLayout.SOUTH );
        
        setContentPane( mainPanel );
        pack();
        
        updateButtons();
    }
    
    public String getText() {
        return _textField.getText();
    }
    
    private void updateButtons() {
        String s = _textField.getText();
        boolean enabled = ( s != null && s.length() != 0 );
        _nextButton.setEnabled( enabled );
        _previousButton.setEnabled( enabled );
    }
    
    public void addFindListener( FindListener listener ) {
        _listenerList.add( FindListener.class, listener );
    }
    
    public void removeFindListener( FindListener listener ) {
        _listenerList.remove( FindListener.class, listener );
    }
    
    private void fireNext() {
        String text = getText();
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == FindListener.class ) {
                ((FindListener) listeners[ i + 1 ] ).findNext( text );
            }
        }
    }
    
    private void firePrevious() {
        String text = getText();
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == FindListener.class ) {
                ((FindListener) listeners[ i + 1 ] ).findPrevious( text );
            }
        }
    }
}
