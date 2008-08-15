package edu.colorado.phet.eatingandexercise.model;

import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.eatingandexercise.control.CaloricItem;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.CaloricFoodItem;

/**
 * Created by: Sam
 * Apr 23, 2008 at 1:39:44 PM
 */
public class CalorieSet {
    private ArrayList list = new ArrayList();
    private ArrayList listeners = new ArrayList();

    public CalorieSet() {
    }

    public CalorieSet( CaloricItem[] caloricItems ) {
        for ( int i = 0; i < caloricItems.length; i++ ) {
            addItem( caloricItems[i] );
        }
    }

    public void insertItem( int index, CaloricItem item ) {
        list.add( index, item );
        notifyItemAdded( item );
    }

    public void addItem( final CaloricItem item ) {
        list.add( item );
        notifyItemAdded( item );
        item.addListener( new CaloricItem.Listener() {//todo: remove memory leak

            public void caloriesChanged() {
                notifyItemChanged( item );
            }
        } );
    }

    private void notifyItemChanged( CaloricItem item ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).itemChanged( item );
        }
    }

    public double getTotalCalories() {
        double sum = 0;
        for ( int i = 0; i < list.size(); i++ ) {
            sum += ( (CaloricItem) list.get( i ) ).getCalories();
        }
        return sum;
    }

    public int size() {
        return list.size();
    }

    public CaloricItem getItem( int i ) {
        return (CaloricItem) list.get( i );
    }

    public void addAll( CaloricItem[] availableExercise ) {
        list.addAll( Arrays.asList( availableExercise ) );
    }

    public int getItemCount() {
        return size();
    }

    public void removeItem( CaloricItem item ) {
        list.remove( item );
        notifyItemRemoved( item );
    }

    private void notifyItemRemoved( CaloricItem item ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).itemRemoved( item );
        }
    }

    public void clear() {
        while ( getItemCount() > 0 ) {
            removeItem( getItem( 0 ) );
        }
    }

    public void removeAll( CaloricFoodItem[] baseDiets ) {
        for ( int i = 0; i < baseDiets.length; i++ ) {
            removeItem( baseDiets[i] );
        }
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public static interface Listener {
        void itemAdded( CaloricItem item );

        void itemRemoved( CaloricItem item );

        void itemChanged( CaloricItem item );
    }

    public static class Adapter implements Listener {

        public void itemAdded( CaloricItem item ) {
        }

        public void itemRemoved( CaloricItem item ) {
        }

        public void itemChanged( CaloricItem item ) {
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyItemAdded( CaloricItem item ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).itemAdded( item );
        }
    }

    public boolean contains( CaloricItem item ) {
        return list.contains( item );
    }
}
