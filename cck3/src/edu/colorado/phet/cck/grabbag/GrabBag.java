/** Sam Reid*/
package edu.colorado.phet.cck.grabbag;

import edu.colorado.phet.cck.model.CCKModel;
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
//        add( new GrabBagItem( getClass().getClassLoader().getResource( "dollarbill.gif" ), SimStrings.get( "GrabBag.DollarBill" ), Math.pow( 10, 9 ), 1 ) );
//        add( new GrabBagItem( getClass().getClassLoader().getResource( "paperclip.gif" ), SimStrings.get( "GrabBag.PaperClip" ), CCK3Module.MIN_RESISTANCE, .7 ) );
//        add( new GrabBagItem( getClass().getClassLoader().getResource( "penny.gif" ), SimStrings.get( "GrabBag.Penny" ), CCK3Module.MIN_RESISTANCE, .6 ) );
//        add( new GrabBagItem( getClass().getClassLoader().getResource( "eraser.gif" ), SimStrings.get( "GrabBag.Eraser" ), Math.pow( 10, 9 ), .7 ) );
//        add( new GrabBagItem( getClass().getClassLoader().getResource( "pencil.gif" ), SimStrings.get( "GrabBag.PencilLead" ), 3000, 3.5 ) );
//        add( new GrabBagItem( getClass().getClassLoader().getResource( "hand.gif" ), SimStrings.get( "GrabBag.Hand" ), Math.pow( 10, 6 ), 1 ) );
//        add( new GrabBagItem( getClass().getClassLoader().getResource( "dog.gif" ), SimStrings.get( "GrabBag.Dog" ), Math.pow( 10, 9 ), 2.5 ) );

        add( new GrabBagItem( "dollarbill.gif", SimStrings.get( "GrabBag.DollarBill" ), Math.pow( 10, 9 ), 1 ) );
        add( new GrabBagItem( "paperclip.gif", SimStrings.get( "GrabBag.PaperClip" ), CCKModel.MIN_RESISTANCE, .7 ) );
        add( new GrabBagItem( "penny.gif", SimStrings.get( "GrabBag.Penny" ), CCKModel.MIN_RESISTANCE, .6 ) );
        add( new GrabBagItem( "eraser.gif", SimStrings.get( "GrabBag.Eraser" ), Math.pow( 10, 9 ), .7 ) );
        add( new GrabBagItem( "pencil.gif", SimStrings.get( "GrabBag.PencilLead" ), 3000, 3.5 ) );
        add( new GrabBagItem( "hand.gif", SimStrings.get( "GrabBag.Hand" ), Math.pow( 10, 6 ), 1 ) );
        add( new GrabBagItem( "dog.gif", SimStrings.get( "GrabBag.Dog" ), Math.pow( 10, 9 ), 2.5 ) );
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
