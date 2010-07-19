/*  */
package edu.colorado.phet.theramp.common;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 7, 2005
 * Time: 2:09:16 PM
 */


public class LayoutSet {

    private ArrayList items = new ArrayList();

    public void addSpacer( double size ) {
        addItem( new Spacer( size ) );
    }

    public void addItem( LayoutItem layoutItem ) {
        items.add( layoutItem );
    }

    public int getFixedSize() {
        int size = 0;
        for( int i = 0; i < items.size(); i++ ) {
            LayoutItem layoutItem = (LayoutItem)items.get( i );
            if( layoutItem instanceof FixedLayoutItem ) {
                FixedLayoutItem fixedLayoutItem = (FixedLayoutItem)layoutItem;
                size += fixedLayoutItem.getSize();
            }
        }
        return size;
    }

    public void hideVariableItems() {
        for( int i = 0; i < items.size(); i++ ) {
            LayoutItem layoutItem = (LayoutItem)items.get( i );
            if( layoutItem instanceof VariableLayoutItem ) {
                ( (VariableLayoutItem)layoutItem ).setVisible( false );
            }
        }
    }

    public double numVariableItems() {
        int num = 0;
        for( int i = 0; i < items.size(); i++ ) {
            LayoutItem layoutItem = (LayoutItem)items.get( i );
            if( layoutItem instanceof VariableLayoutItem ) {
                num++;
            }
        }
        return num;
    }

    public void layoutForVariableItemSize( double origValue, double size ) {
        double y = origValue;
        for( int i = 0; i < items.size(); i++ ) {
            LayoutItem layoutItem = (LayoutItem)items.get( i );
            layoutItem.setOffset( y );
            if( layoutItem instanceof VariableLayoutItem ) {
                ( (VariableLayoutItem)layoutItem ).setSize( size );
                y += size;
            }
            else if( layoutItem instanceof FixedLayoutItem ) {
                FixedLayoutItem item = (FixedLayoutItem)layoutItem;
                y += item.getSize();
            }
        }
    }


    public void layout( double origValue, double totalSpace ) {
        double availableRemainder = totalSpace - getFixedSize();
        if( availableRemainder <= 0 ) {
            hideVariableItems();
        }
        else {//share equally
            layoutForVariableItemSize( origValue, availableRemainder / numVariableItems() );
        }
    }

    public static interface LayoutItem {
        void setOffset( double offset );
    }

    public static abstract class FixedLayoutItem implements LayoutItem {
        private double size;

        public FixedLayoutItem( double size ) {
            this.size = size;
        }

        public double getSize() {
            return size;
        }
    }

    public static interface VariableLayoutItem extends LayoutItem {
        void setSize( double size );

        void setVisible( boolean b );
    }

    private static class Spacer extends FixedLayoutItem {
        public Spacer( double size ) {
            super( size );
        }

        public void setOffset( double offset ) {
        }
    }
}
