/** Sam Reid*/
package edu.colorado.phet.cck3.grabbag;

import edu.colorado.phet.cck3.CCK3Module;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 14, 2004
 * Time: 8:08:15 PM
 * Copyright (c) Sep 14, 2004 by Sam Reid
 */
public class GrabBag {
    ArrayList items = new ArrayList();

    public GrabBag() {
        /*
Dollar bill = infinite
Paper Clip = zero ohms
Penny = zero ohms
Eraser = infinite
Pencil Lead = 30,000 ohms
Hand = 1 Mega ohm
Dog = infinite
        */
        add( new GrabBagItem( getClass().getClassLoader().getResource( "dollarbill.gif" ), "Dollar Bill", Math.pow( 10, 9 ), 1 ) );
        add( new GrabBagItem( getClass().getClassLoader().getResource( "paperclip.gif" ), "Paper Clip", CCK3Module.MIN_RESISTANCE, .7 ) );
        add( new GrabBagItem( getClass().getClassLoader().getResource( "penny.gif" ), "Penny", CCK3Module.MIN_RESISTANCE, .6 ) );
        add( new GrabBagItem( getClass().getClassLoader().getResource( "eraser.gif" ), "Eraser", Math.pow( 10, 9 ), .7 ) );
        add( new GrabBagItem( getClass().getClassLoader().getResource( "pencil.gif" ), "Pencil Lead", 3000, 2 ) );
        add( new GrabBagItem( getClass().getClassLoader().getResource( "hand.gif" ), "Hand", Math.pow( 10, 6 ), 1 ) );
        add( new GrabBagItem( getClass().getClassLoader().getResource( "dog.gif" ), "Dog", Math.pow( 10, 9 ), 2.5 ) );
    }

    private void add( GrabBagItem grabBagItem ) {
        items.add( grabBagItem );
    }

    public int numItems() {
        return items.size();
    }

    public GrabBagItem itemAt( int i ) {
        return (GrabBagItem)items.get( i );
    }
}
