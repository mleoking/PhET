/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility.userinterface;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.translationutility.TUResources;

/**
 * ToolBar is the tool bar in the main window.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ToolBar extends JPanel {

    private static final String SAVE_BUTTON_LABEL = TUResources.getString( "button.save" );
    private static final String LOAD_BUTTON_LABEL = TUResources.getString( "button.load" );
    private static final String TEST_BUTTON_LABEL = TUResources.getString( "button.test" );
    private static final String SUBMIT_BUTTON_LABEL = TUResources.getString( "button.submit" );
    private static final String FIND_BUTTON_LABEL = TUResources.getString( "button.find" );
    private static final String HELP_BUTTON_LABEL = TUResources.getString( "button.help" );
    
    /**
     * FindListener is the interface implemented by all listeners who 
     * want to be notified when the Next or Previous buttons are pushed.
     */
    public static interface ToolBarListener extends EventListener {
        public void handleTest();
        public void handleSave();
        public void handleLoad();
        public void handleSubmit();
        public void handleFind();
        public void handleHelp();
    }
    
    private EventListenerList _listenerList;
    
    /**
     * Constructor.
     */
    public ToolBar() {
        
        _listenerList = new EventListenerList();
        
        Icon testIcon = TUResources.getIcon( "testButton.png" );
        JButton testButton = new JButton( TEST_BUTTON_LABEL, testIcon );
        testButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireTest();
            }
        } );
        
        Icon saveIcon = TUResources.getIcon( "saveButton.png" );
        JButton saveButton = new JButton( SAVE_BUTTON_LABEL, saveIcon );
        saveButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireSave();
            }
        } );
       
        Icon loadIcon = TUResources.getIcon( "loadButton.png" );
        JButton loadButton = new JButton( LOAD_BUTTON_LABEL, loadIcon );
        loadButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireLoad();
            }
        } );
        
        Icon submitIcon = TUResources.getIcon( "submitButton.png" );
        JButton submitButton = new JButton( SUBMIT_BUTTON_LABEL, submitIcon );
        submitButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireSubmit();
            }
        } );
        
        Icon findIcon = TUResources.getIcon( "findButton.png" );
        JButton findButton = new JButton( FIND_BUTTON_LABEL, findIcon );
        findButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireFind();
            }
        } );
        
        Icon helpIcon = TUResources.getIcon( "helpButton.png" );
        JButton helpButton = new JButton( HELP_BUTTON_LABEL, helpIcon );
        helpButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireHelp();
            }
        } );
        
        JPanel buttonPanel = new JPanel( new GridLayout( 1, 9 ) );
        buttonPanel.add( testButton );
        buttonPanel.add( saveButton );
        buttonPanel.add( loadButton );
        buttonPanel.add( submitButton );
        buttonPanel.add( Box.createHorizontalStrut( 20 ) );
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
