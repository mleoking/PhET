/** Sam Reid*/
package edu.colorado.phet.cck3.grabbag;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.common.view.util.SimStrings;

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
        add( new GrabBagItem( getClass().getClassLoader().getResource( "dollarbill.gif" ), SimStrings.get( "DollarBill" ), Math.pow( 10, 9 ), 1 ) );
        add( new GrabBagItem( getClass().getClassLoader().getResource( "paperclip.gif" ), SimStrings.get( "PaperClip" ), CCK3Module.MIN_RESISTANCE, .7 ) );
        add( new GrabBagItem( getClass().getClassLoader().getResource( "penny.gif" ), SimStrings.get( "Penny" ), CCK3Module.MIN_RESISTANCE, .6 ) );
        add( new GrabBagItem( getClass().getClassLoader().getResource( "eraser.gif" ), SimStrings.get( "Eraser" ), Math.pow( 10, 9 ), .7 ) );
        add( new GrabBagItem( getClass().getClassLoader().getResource( "pencil.gif" ), SimStrings.get( "PencilLead" ), 3000, 3.5 ) );
        add( new GrabBagItem( getClass().getClassLoader().getResource( "hand.gif" ), SimStrings.get( "Hand" ), Math.pow( 10, 6 ), 1 ) );
        add( new GrabBagItem( getClass().getClassLoader().getResource( "dog.gif" ), SimStrings.get( "Dog" ), Math.pow( 10, 9 ), 2.5 ) );
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
