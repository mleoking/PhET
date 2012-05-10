// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;

public class KitCollection {
    private List<Kit> kits = new LinkedList<Kit>();

    private Property<Kit> currentKit;

    public KitCollection() {
    }

    public void addKit( final Kit kit ) {
        if ( currentKit == null ) {
            currentKit = new Property<Kit>( kit );
            kit.show();

            // handle kit visibility when this changes
            currentKit.addObserver( new ChangeObserver<Kit>() {
                public void update( Kit newValue, Kit oldValue ) {
                    newValue.show();
                    oldValue.hide();
                }
            } );
        }
        else {
            kit.hide();
        }
        kits.add( kit );
    }

    public List<Kit> getKits() {
        return kits;
    }

    public Kit getCurrentKit() {
        return currentKit.get();
    }

    public Property<Kit> getCurrentKitProperty() {
        return currentKit;
    }

    public int getCurrentKitIndex() {
        int index = kits.indexOf( currentKit.get() );
        if ( index < 0 ) {
            throw new RuntimeException( "Could not find current kit index" );
        }
        return index;
    }

    public boolean hasNextKit() {
        return getCurrentKitIndex() + 1 < kits.size();
    }

    public boolean hasPreviousKit() {
        return getCurrentKitIndex() - 1 >= 0;
    }

    public void goToNextKit() {
        if ( hasNextKit() ) {
            currentKit.set( kits.get( getCurrentKitIndex() + 1 ) );
        }
    }

    public void goToPreviousKit() {
        if ( hasPreviousKit() ) {
            currentKit.set( kits.get( getCurrentKitIndex() - 1 ) );
        }
    }

    public void resetAll() {
        for ( Kit kit : kits ) {
            kit.resetKit();
        }
        while ( hasPreviousKit() ) {
            goToPreviousKit();
        }
    }

}
