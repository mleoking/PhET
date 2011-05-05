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
     * Convenience base class, supports connection to multiple properties via one method call.
     * This trades off type checking for the ability to listen to multiple properties,
     * possibly with heterogeneous types.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     */
    public abstract class MultiPropertyChangeListener implements PropertyChangeListener {

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
                public void propertyChanged( PropertyChangeEvent event ) {
                    System.out.println( "isHungry=" + isHungry.getValue() + " age=" + age.getValue() );
                }
            };
            listener.listenTo( isHungry, age );
            isHungry.setValue( false );
            age.setValue( 50 );
        }
    }
}
