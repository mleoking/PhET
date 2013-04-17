// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.translationutility.userinterface;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.EventListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.translationutility.TUImages;
import edu.colorado.phet.translationutility.TUStrings;

/**
 * FindDialog is a dialog that lets you specify a string to find.
 * When the Next or Previous buttons are pressed, all FindListeners are notified.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class FindDialog extends JDialog {
    
    private final JTextField textField;
    private final JButton nextButton;
    private final JButton previousButton;
    private final EventListenerList listenerList;
    
    /**
     * FindListener is the interface implemented by all listeners who 
     * want to be notified when the Next or Previous buttons are pushed.
     */
    public static interface FindListener extends EventListener {
        public void findNext( String text );
        public void findPrevious( String text );
    }

    /**
     * Constructor.
     * 
     * @param owner
     * @param defaultText
     * @param textFieldFont
     */
    public FindDialog( Frame owner, String defaultText, Font textFieldFont ) {
        super( owner );
        
        setTitle( TUStrings.FIND_TITLE );
        setModal( false );
        setResizable( false );
        
        listenerList = new EventListenerList();
        
        // create the panel where the user inputs information
        JPanel inputPanel = new JPanel();
        {
            JLabel findLabel = new JLabel( TUStrings.FIND_LABEL );

            textField = new JTextField( defaultText );
            textField.setFont( textFieldFont );
            textField.setColumns( 30 );
            textField.setEditable( true );
            textField.addKeyListener( new KeyAdapter() {
                public void keyReleased( KeyEvent e ) {
                    updateButtons();
                    // pressing enter in the textfield is the same as pressing the Next button
                    if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                        fireNext();
                    }
                }
            } );
            
            inputPanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
            inputPanel.add( findLabel );
            inputPanel.add( textField );
        }
        
        // create the panel that contains action buttons
        JPanel buttonPanel = new JPanel();
        {
            nextButton = new JButton( TUStrings.NEXT_BUTTON, TUImages.NEXT_ICON );
            nextButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    fireNext();
                }
            } );

            previousButton = new JButton( TUStrings.PREVIOUS_BUTTON, TUImages.PREVIOUS_ICON );
            previousButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    firePrevious();
                }
            } );
            
            JButton closeButton = new JButton( TUStrings.CLOSE_BUTTON );
            closeButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    dispose();
                }
            } );
            
            JPanel innerPanel = new JPanel( new GridLayout( 1, 7 ) );
            innerPanel.add( nextButton );
            innerPanel.add( previousButton );
            innerPanel.add( Box.createHorizontalStrut( 20 ) );
            innerPanel.add( closeButton );
            buttonPanel.add( innerPanel );
        }
        
        // layout
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
        
        // set the initial state of the buttons
        updateButtons();
    }
    
    /**
     * Gets the text that has been entered in the text field.
     * @return String
     */
    public String getText() {
        return textField.getText();
    }
    
    /*
     * Updates the state of the Next and Previous buttons.
     * These buttons are disabled if the text field is empty.
     */
    private void updateButtons() {
        String s = textField.getText();
        boolean enabled = ( s != null && s.length() != 0 );
        nextButton.setEnabled( enabled );
        previousButton.setEnabled( enabled );
    }
    
    /**
     * Adds a FindListener.
     * @param listener
     */
    public void addFindListener( FindListener listener ) {
        listenerList.add( FindListener.class, listener );
    }
    
    /**
     * Removes a FindListener.
     * @param listener
     */
    public void removeFindListener( FindListener listener ) {
        listenerList.remove( FindListener.class, listener );
    }
    
    /*
     * Notifies all FindListeners that the Next button has been pressed.
     */
    private void fireNext() {
        String text = getText();
        if ( text != null && text.length() > 0 ) {
            Object[] listeners = listenerList.getListenerList();
            for ( int i = 0; i < listeners.length; i += 2 ) {
                if ( listeners[i] == FindListener.class ) {
                    ( (FindListener) listeners[i + 1] ).findNext( text );
                }
            }
        }
    }
    
    /*
     * Notifies all FindListeners that the Previous button has been pressed.
     */
    private void firePrevious() {
        String text = getText();
        if ( text != null && text.length() > 0 ) {
            Object[] listeners = listenerList.getListenerList();
            for ( int i = 0; i < listeners.length; i += 2 ) {
                if ( listeners[i] == FindListener.class ) {
                    ( (FindListener) listeners[i + 1] ).findPrevious( text );
                }
            }
        }
    }
}
