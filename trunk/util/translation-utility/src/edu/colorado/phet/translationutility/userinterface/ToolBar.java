/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility.userinterface;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.translationutility.TUImages;
import edu.colorado.phet.translationutility.TUStrings;

/**
 * ToolBar is the tool bar in the main window.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ToolBar extends JPanel {

    /**
     * FindListener is the interface implemented by all listeners who 
     * want to be notified when the Next or Previous buttons are pushed.
     */
    public static interface ToolBarListener extends EventListener {
        public void handleTest();
        public void handleSubmit();
        public void handleSave();
        public void handleLoad();
        public void handleFind();
        public void handleHelp();
    }
    
    private EventListenerList _listenerList;
    
    /**
     * Constructor.
     */
    public ToolBar() {
        
        _listenerList = new EventListenerList();
        
        JButton testButton = new JButton( TUStrings.TEST_BUTTON, TUImages.TEST_ICON );
        testButton.setToolTipText( TUStrings.TOOLTIP_TEST );
        testButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireTest();
            }
        } );
        
        JButton submitButton = new JButton( TUStrings.SUBMIT_BUTTON, TUImages.SUBMIT_ICON );
        submitButton.setToolTipText( TUStrings.TOOLTIP_SUBMIT );
        submitButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireSubmit();
            }
        } );
        
        JButton saveButton = new JButton( TUStrings.SAVE_BUTTON, TUImages.SAVE_ICON );
        saveButton.setToolTipText( TUStrings.TOOLTIP_SAVE );
        saveButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireSave();
            }
        } );
       
        JButton loadButton = new JButton( TUStrings.LOAD_BUTTON, TUImages.LOAD_ICON );
        loadButton.setToolTipText( TUStrings.TOOLTIP_LOAD );
        loadButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireLoad();
            }
        } );
        
        JButton findButton = new JButton( TUStrings.FIND_BUTTON, TUImages.FIND_ICON );
        findButton.setToolTipText( TUStrings.TOOLTIP_FIND );
        findButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireFind();
            }
        } );
        
        JButton helpButton = new JButton( TUStrings.HELP_BUTTON, TUImages.HELP_ICON );
        helpButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireHelp();
            }
        } );
        
        JPanel buttonPanel = new JPanel( new GridLayout( 1, 8 ) );
        buttonPanel.add( testButton );
        buttonPanel.add( submitButton );
        buttonPanel.add( saveButton );
        buttonPanel.add( loadButton );
        buttonPanel.add( Box.createHorizontalStrut( 10 ) );
        buttonPanel.add( findButton );
        buttonPanel.add( helpButton );

        add( buttonPanel );
    }
    
    /**
     * Adds a ToolBarListener.
     * @param listener
     */
    public void addToolBarListener( ToolBarListener listener ) {
        _listenerList.add( ToolBarListener.class, listener );
    }
    
    /**
     * Removes a ToolBarListener.
     * @param listener
     */
    public void removeToolBarListener( ToolBarListener listener ) {
        _listenerList.remove( ToolBarListener.class, listener );
    }
    
    /*
     * Notifies all ToolBarListener that the Test button has been pressed.
     */
    private void fireTest() {
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == ToolBarListener.class ) {
                ((ToolBarListener) listeners[ i + 1 ] ).handleTest();
            }
        }
    }
    
    /*
     * Notifies all ToolBarListener that the Save button has been pressed.
     */
    private void fireSave() {
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == ToolBarListener.class ) {
                ((ToolBarListener) listeners[ i + 1 ] ).handleSave();
            }
        }
    }
    
    /*
     * Notifies all ToolBarListener that the Load button has been pressed.
     */
    private void fireLoad() {
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == ToolBarListener.class ) {
                ((ToolBarListener) listeners[ i + 1 ] ).handleLoad();
            }
        }
    }
    
    /*
     * Notifies all ToolBarListener that the Submit button has been pressed.
     */
    private void fireSubmit() {
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == ToolBarListener.class ) {
                ((ToolBarListener) listeners[ i + 1 ] ).handleSubmit();
            }
        }
    }
    
    /*
     * Notifies all ToolBarListener that the Find button has been pressed.
     */
    private void fireFind() {
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == ToolBarListener.class ) {
                ((ToolBarListener) listeners[ i + 1 ] ).handleFind();
            }
        }
    }
    
    /*
     * Notifies all ToolBarListener that the Help button has been pressed.
     */
    private void fireHelp() {
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == ToolBarListener.class ) {
                ((ToolBarListener) listeners[ i + 1 ] ).handleHelp();
            }
        }
    }
}
