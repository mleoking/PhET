package edu.colorado.phet.circuitconstructionkit.grabbag;

import java.util.ArrayList;

import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;

/**
 * User: Sam Reid
 * Date: Sep 14, 2004
 * Time: 8:08:15 PM
 */
public class GrabBag {
    ArrayList items = new ArrayList();
    private static final double SCALE = 1.3;

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

        /*
        Pencil lead resistivity=
         5.351E-3 ohm-m
         http://panda.unm.edu/regener/lab/161L/Resistance.pdf
        for A=4E-6m^2, L=1cm, this gives resistance R=135 Ohms

        http://www.ndt-ed.org/GeneralResources/MaterialProperties/ET/ET_matlprop_Misc_Matls.htm
         7.837E-06 ohm-m
        for A=4E-6m^2, L=1cm, this gives resistance R=0.2 Ohms

        we have been using R=3000 Ohms, although my notes say at one time we considered 30,000 Ohms
            */


        add( new GrabBagItem( "dollarbill.gif", CCKResources.getString( "GrabBag.DollarBill" ), Math.pow( 10, 9 ), 1 * SCALE ) );
        add( new GrabBagItem( "paperclip.gif", CCKResources.getString( "GrabBag.PaperClip" ), CCKModel.MIN_RESISTANCE, .7 * SCALE ) );
        add( new GrabBagItem( "penny.gif", CCKResources.getString( "GrabBag.Penny" ), CCKModel.MIN_RESISTANCE, .6 * SCALE ) );
        add( new GrabBagItem( "eraser.gif", CCKResources.getString( "GrabBag.Eraser" ), Math.pow( 10, 9 ), .7 * SCALE ) );
        add( new GrabBagItem( "pencil.gif", CCKResources.getString( "GrabBag.PencilLead" ), 300, 3.5 * SCALE ) );
        add( new GrabBagItem( "hand.gif", CCKResources.getString( "GrabBag.Hand" ), Math.pow( 10, 6 ), 1 * SCALE ) );
        add( new GrabBagItem( "dog.gif", CCKResources.getString( "GrabBag.Dog" ), Math.pow( 10, 9 ), 2.5 * SCALE ) );
    }

    private void add( GrabBagItem grabBagItem ) {
        items.add( grabBagItem );
    }

    public int numItems() {
        return items.size();
    }

    public GrabBagItem itemAt( int i ) {
        return (GrabBagItem) items.get( i );
    }
}
