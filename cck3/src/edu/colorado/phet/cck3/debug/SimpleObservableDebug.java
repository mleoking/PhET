/** Sam Reid*/
package edu.colorado.phet.cck3.debug;

import edu.colorado.phet.common_cck.util.SimpleObservable;
import edu.colorado.phet.common_cck.util.SimpleObserver;

/**
 * User: Sam Reid
 * Date: Sep 27, 2004
 * Time: 11:07:19 AM
 * Copyright (c) Sep 27, 2004 by Sam Reid
 */
public class SimpleObservableDebug extends SimpleObservable {
//    private static ArrayList instances = new ArrayList();

    public SimpleObservableDebug() {
//        instances.add( this );
    }

    public void debug() {
//        if( numObservers() > 30 ) {
//            System.out.println( getClass().getName() + " num observers=" + numObservers() );
//            System.out.println( "toString() = " + super.toString() );
//            new Exception( "Too many observers." ).printStackTrace();
//        }
        if( numObservers() > 200 ) {
//            System.out.println( "true = " + true );
            System.out.println( "Debug> " + getClass().getName() + " now has " + numObservers() + " observers." );
        }
    }

    public int numObservers() {
        return super.numObservers();
    }

    public void addObserver( SimpleObserver so ) {
        super.addObserver( so );
        debug();
    }

    public void notifyObservers() {
        super.notifyObservers();
        debug();
    }

    public void removeObserver( SimpleObserver obs ) {
        super.removeObserver( obs );
        debug();
    }

    public void removeAllObservers() {
        super.removeAllObservers();
        debug();
    }

//    public static SimpleObservableDebug[] instances() {
//        System.gc();
//        return (SimpleObservableDebug[])instances.toArray( new SimpleObservableDebug[0] );
//    }

    public SimpleObserver[] getObservers() {
        return super.getObservers();
    }
}
