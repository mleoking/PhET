package com.pixelzoom.examples;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Gizmo is a JPanel that contains 2 lists and buttons for moving 
 * objects between the lists.  The list on the left is the "available" list,
 * the list on the right is the "chosen" list.  Between the 2 lists 
 * are 2 buttons, labeled to indicate the direction in which selected 
 * items will be moved.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Gizmo extends JPanel {

    // the list of available object
    private MutableList _availableList;
    // the list of chosen objects
    private MutableList _chosenList;

    /* The preferred way to use a dynamic list in an application is to bind 
     * the code that's updating the list to the ListModel, not to the JList itself. 
     * The JList encourages this practice because the mutable DefaultListModel API 
     * isn't exposed by JList. The advantage of keeping the JList model and the JList
     * (view) separate is that one can easily replace the view without disturbing 
     * the rest of the application. Occasionally it is more convenient to wire the 
     *  and view together. One simple way to do this is to make a subclass of 
     *  JList that hides the specifics of the DefaultListModel.
     */
    private static class MutableList extends JList {

        MutableList() {
            super( new DefaultListModel() );
        }

        DefaultListModel getContents() {
            return (DefaultListModel) getModel();
        }
    }   
    
    /**
     * Constructor.
     * @param availableObjects
     */
    public Gizmo( Object[] availableObjects ) {

        _availableList = new MutableList();
        _availableList.setPreferredSize( new Dimension( 200, 100 ) );
        for ( int i = 0; i < availableObjects.length; i++ ) {
            ((DefaultListModel)_availableList.getModel()).add( i, availableObjects[i] );
        }
        
        _chosenList = new MutableList();
        _chosenList.setPreferredSize( new Dimension( 200, 100 ) );

        // this probably needs tweaking...
        JScrollPane availableScrollPane = new JScrollPane( _availableList );
        availableScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
        availableScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        
        // this probably needs tweaking...
        JScrollPane chosenScrollPane = new JScrollPane( _chosenList );
        chosenScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
        chosenScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        
        JButton moveToChosenButton = new JButton( "-->" );
        moveToChosenButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                moveSelectedListItems( _availableList, _chosenList );
            }
        } );

        JButton moveToAvailableButton = new JButton( "<--" );
        moveToAvailableButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                moveSelectedListItems( _chosenList, _availableList );
            }
        } );
        
        Box buttonBox = new Box( BoxLayout.Y_AXIS );
        buttonBox.add( moveToChosenButton );
        buttonBox.add( moveToAvailableButton );
        
        setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
        add( availableScrollPane );
        add( buttonBox );
        add( chosenScrollPane );
    }
    
    /**
     * Gets the objects in the available list.
     * @return
     */
    public Object[] getAvailableObjects() {
        return _availableList.getContents().toArray();
    }
    
    /**
     * Gets the objects in the chosen list.
     * @return
     */
    public Object[] getChosenObjects() {
        return _chosenList.getContents().toArray();
    }
    
    /*
     * Moves items between two mutable lists.
     */
    private static void moveSelectedListItems( MutableList fromList, MutableList toList ) {
        Object[] objects = fromList.getSelectedValues();
        if ( objects != null ) {
            for ( int i = 0; i < objects.length; i++ ) {
                toList.getContents().addElement( objects[i] );
                fromList.getContents().removeElement( objects[i] );
            }
        }
    }
    
    /**
     * Test harness.
     * @param args
     */
    public static final void main( String[] args ) {
        
        Object[] availableObjects = new Object[ 10 ];
        for ( int i = 0; i < availableObjects.length; i++ ) {
            availableObjects[i] = new Integer( i );
        }
        
        Gizmo gizmo = new Gizmo( availableObjects );
        
        JFrame frame = new JFrame();
        frame.getContentPane().add( gizmo );
        frame.setSize( 400, 400 );
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.show();
    }
}
