// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.test;

import java.util.EventListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.test.TestPropertyT.TestModel.TestModelListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Comparsion of notification mechanisms.
 * Both approaches result in 5 calls to the model setter when a check box is clicked.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPropertyT extends JFrame {

    public static class TestModel {

        private final EventListenerList listeners;

        private final Property<Boolean> foo;
        private boolean bar;

        public TestModel( boolean foo, boolean bar ) {
            this.foo = new Property<Boolean>( foo );
            this.bar = bar;
            this.listeners = new EventListenerList();
        }

        public void addVisibleObserver( SimpleObserver o ) {
            foo.addObserver( o );
        }

        public void setFoo( boolean foo ) {
            System.out.println( "TestModel.setFoo " + foo );
            this.foo.setValue( foo );
        }

        public boolean isFoo() {
            return foo.getValue();
        }

        public void setBar( boolean bar ) {
            System.out.println( "TestModel.setBar " + bar );
            if ( bar != this.bar ) {
                this.bar = bar;
                fireBarChanged();
            }
        }

        public boolean isBar() {
            return bar;
        }

        public interface TestModelListener extends EventListener {
            public void barChanged();
        }

        public void addTestModelListener( TestModelListener listener ) {
            listeners.add( TestModelListener.class, listener );
        }

        private void fireBarChanged() {
            for ( TestModelListener listener : listeners.getListeners( TestModelListener.class ) ) {
                listener.barChanged();
            }
        }
    }

    public TestPropertyT() {

        final TestModel model = new TestModel( true /* visible */, true /* bar */ );

        final JCheckBox fooCheckBox = new JCheckBox( "foo" );
        final JCheckBox barCheckBox = new JCheckBox( "bar" );
        JPanel panel = new JPanel();
        panel.add( fooCheckBox );
        panel.add( barCheckBox );
        setContentPane( panel );
        pack();

        // wire up property that uses Property<T>
        fooCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setFoo( fooCheckBox.isSelected() );
            }
        } );
        model.addVisibleObserver( new SimpleObserver() {
            public void update() {
                fooCheckBox.setSelected( model.isFoo() );
            }
        });

        // wire up property that uses EventListener
        barCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
               model.setBar( barCheckBox.isSelected() );
            }
        } );
        model.addTestModelListener( new TestModelListener() {
            public void barChanged() {
                barCheckBox.setSelected( model.isBar() );
            }
        });
    }

    public static void main( String[] args ) {
        JFrame frame = new TestPropertyT();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }

}
