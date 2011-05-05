// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.property4;

/**
 * This is the sole interface for property change notification.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface PropertyChangeListener<T> {

    public void propertyChanged( PropertyChangeEvent<T> event );

    /**
     * Adapter for developers who don't want to deal with PropertyChangeEvent.
     * Override one of the propertyChanged method.
     * If you override more than one, you'll get more than one notifications, so don't do that.
     *
     * @param <T>
     */
    public static class PropertyChangeAdapter<T> implements PropertyChangeListener<T> {

        public void propertyChanged( PropertyChangeEvent<T> event ) {
            propertyChanged();
            propertyChanged( event.value );
            propertyChanged( event.oldValue, event.value );
        }

        public void propertyChanged() {
        }

        public void propertyChanged( T value ) {
        }

        public void propertyChanged( T oldValue, T value ) {
        }
    }

    /**
     * Convenience base class, supports connection to multiple properties via one method call.
     * This trades off type checking for the ability to listen to multiple properties,
     * possibly with heterogeneous types.  And you won't be able to get any meaningful
     * information out of the PropertyChangeEvent.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     */
    public static abstract class MultiPropertyChangeListener extends PropertyChangeAdapter {

        public void propertyChanged( PropertyChangeEvent event ) {
        }

        /**
         * Listen to a variable number of properties.
         *
         * @param properties
         */
        public void listenTo( GettableProperty... properties ) {
            for ( GettableProperty property : properties ) {
                property.addListener( this );
            }
        }

        public static void main( String[] args ) {
            final SettableProperty<Boolean> isHungry = new SettableProperty<Boolean>( true );
            final SettableProperty<Integer> age = new SettableProperty<Integer>( 40 );
            MultiPropertyChangeListener listener = new MultiPropertyChangeListener() {
                // override the no-arg adapter method
                @Override public void propertyChanged() {
                    System.out.println( "isHungry=" + isHungry.getValue() + " age=" + age.getValue() );
                }
            };
            listener.listenTo( isHungry, age );
            isHungry.setValue( false );
            age.setValue( 50 );
        }
    }
}
