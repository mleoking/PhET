package edu.colorado.phet.rotation.tests.jfreechart;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jan 31, 2007
 * Time: 6:00:38 PM
 * Copyright (c) Jan 31, 2007 by Sam Reid
 */

public class CompareListeners {
    static class ObservableType1 {
        String name = "defaultName";
        SwingPropertyChangeSupport changeSupport = new SwingPropertyChangeSupport( this );
        private static final String PROPERTY_SIZE = "size";

        public void addListener( PropertyChangeListener propertyChangeListener ) {
            changeSupport.addPropertyChangeListener( PROPERTY_SIZE, propertyChangeListener );
        }

        public void setName( String name ) {
            String prevName = this.name;
            this.name = name;
            changeSupport.firePropertyChange( new PropertyChangeEvent( this, PROPERTY_SIZE, prevName, name ) );
        }
    }

    static class ObservableType2 {
        String name = "defaultName";
        private ArrayList listeners = new ArrayList();

        public static interface Listener {
            void nameChanged( ObservableType2 source, String oldName, String name );
        }

        public void addListener( Listener listener ) {
            listeners.add( listener );
        }

        public void setName( String name ) {
            String prevName = this.name;
            notifyListeners( prevName, name );
        }

        public void notifyListeners( String oldName, String newName ) {
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.nameChanged( this, oldName, name );
            }
        }
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {

                ObservableType1 observableType1 = new ObservableType1();
                observableType1.addListener( new PropertyChangeListener() {
                    public void propertyChange( PropertyChangeEvent evt ) {
                        String newName = (String)evt.getNewValue();
                        ObservableType1 source = (ObservableType1)evt.getSource();
                        System.out.println( "source = " + source + ", newName=" + newName + ", evt=" + evt );
                        new Throwable().printStackTrace();
                    }
                } );
                ObservableType2 observableType2 = new ObservableType2();
                observableType2.addListener( new ObservableType2.Listener() {
                    public void nameChanged( ObservableType2 source, String oldName, String name ) {
                        System.out.println( "CompareListeners.nameChanged, source=" + source + " oldName=" + oldName + ", newName=" + name );
                        new Throwable().printStackTrace();
                    }
                } );

                observableType1.setName( "larry" );
                observableType2.setName( "alice" );
            }
        } );
    }
}
