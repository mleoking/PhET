package edu.colorado.phet.fitness.model;

import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.fitness.control.CaloricItem;

/**
 * Created by: Sam
 * Apr 23, 2008 at 1:39:44 PM
 */
public class CalorieSet {
    private ArrayList list = new ArrayList();
    private ArrayList listeners = new ArrayList();

    public CalorieSet() {
    }

    public void addItem( CaloricItem item ) {
        list.add( item );
        notifyItemSetChanged();
    }

    public double getTotal() {
        double sum = 0;
        for ( int i = 0; i < list.size(); i++ ) {
            CaloricItem caloricItem = (CaloricItem) list.get( i );
            sum += caloricItem.getCalories();
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

    public static interface Listener {
        void itemSetChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyItemSetChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).itemSetChanged();
        }
    }
}
