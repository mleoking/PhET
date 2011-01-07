// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.translationutility.userinterface;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.translationutility.TUImages;
import edu.colorado.phet.translationutility.TUStrings;

/**
 * ToolBar is the tool bar in the main window.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class ToolBar extends JPanel {

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
    
    private EventListenerList listenerList;
    
    /**
     * Constructor.
     */
    public ToolBar() {
        
        listenerList = new EventListenerList();
        
        ArrayList<JButton> buttons = new ArrayList<JButton>();
        
        JButton testButton = new JButton( TUStrings.TEST_BUTTON, TUImages.TEST_ICON );
        buttons.add( testButton );
        testButton.setToolTipText( TUStrings.TOOLTIP_TEST );
        testButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireTest();
            }
        } );
        
        JButton submitButton = new JButton( TUStrings.SUBMIT_BUTTON, TUImages.SUBMIT_ICON );
        buttons.add( submitButton );
        submitButton.setToolTipText( TUStrings.TOOLTIP_SUBMIT );
        submitButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireSubmit();
            }
        } );
        
        JButton saveButton = new JButton( TUStrings.SAVE_BUTTON, TUImages.SAVE_ICON );
        buttons.add( saveButton );
        saveButton.setToolTipText( TUStrings.TOOLTIP_SAVE );
        saveButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireSave();
            }
        } );
       
        JButton loadButton = new JButton( TUStrings.LOAD_BUTTON, TUImages.LOAD_ICON );
        buttons.add( loadButton );
        loadButton.setToolTipText( TUStrings.TOOLTIP_LOAD );
        loadButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireLoad();
            }
        } );
        
        JButton findButton = new JButton( TUStrings.FIND_BUTTON, TUImages.FIND_ICON );
        buttons.add( findButton );
        findButton.setToolTipText( TUStrings.TOOLTIP_FIND );
        findButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireFind();
            }
        } );
        
        JButton helpButton = new JButton( TUStrings.HELP_BUTTON, TUImages.HELP_ICON );
        buttons.add( helpButton );
        helpButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireHelp();
            }
        } );
        
        // make all buttons have the same size
        Dimension maxButtonSize = new Dimension();
        for ( JButton button : buttons ) {
            int maxWidth = Math.max( maxButtonSize.width, button.getPreferredSize().width );
            int maxHeight = Math.max( maxButtonSize.height, button.getPreferredSize().height );
            maxButtonSize.setSize( maxWidth, maxHeight );
        }
        for ( JButton button : buttons ) {
            button.setPreferredSize( maxButtonSize );
        }
        
        // layout horizontally
        JPanel buttonPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( buttonPanel );
        layout.setInsets( new Insets( 2, 2, 2, 2 ) );
        buttonPanel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( testButton, row, column++ );
        layout.addComponent( submitButton, row, column++ );
        layout.addComponent( Box.createHorizontalStrut( 30 ), row, column++ );
        layout.addComponent( saveButton, row, column++ );
        layout.addComponent( loadButton, row, column++ );
        layout.addComponent( Box.createHorizontalStrut( 30 ), row, column++ );
        layout.addComponent( findButton, row, column++ );
        layout.addComponent( helpButton, row, column++ );

        add( buttonPanel );
    }
    
    /**
     * Adds a ToolBarListener.
     * @param listener
     */
    public void addToolBarListener( ToolBarListener listener ) {
        listenerList.add( ToolBarListener.class, listener );
    }
    
    /**
     * Removes a ToolBarListener.
     * @param listener
     */
    public void removeToolBarListener( ToolBarListener listener ) {
        listenerList.remove( ToolBarListener.class, listener );
    }
    
    /*
     * Notifies all ToolBarListener that the Test button has been pressed.
     */
    private void fireTest() {
        Object[] listeners = listenerList.getListenerList();
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
        Object[] listeners = listenerList.getListenerList();
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
        Object[] listeners = listenerList.getListenerList();
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
        Object[] listeners = listenerList.getListenerList();
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
        Object[] listeners = listenerList.getListenerList();
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
        Object[] listeners = listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == ToolBarListener.class ) {
                ((ToolBarListener) listeners[ i + 1 ] ).handleHelp();
            }
        }
    }
}
