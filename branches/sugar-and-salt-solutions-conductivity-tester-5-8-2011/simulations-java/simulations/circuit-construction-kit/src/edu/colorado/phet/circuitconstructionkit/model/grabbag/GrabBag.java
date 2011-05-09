// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.grabbag;

import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 14, 2004
 * Time: 8:08:15 PM
 */
public class GrabBag {
    ArrayList items = new ArrayList();
    private static final double SCALE = 1.3;

    public GrabBag() {
        add(new GrabBagItem("dollarbill.gif", CCKResources.getString("GrabBag.DollarBill"), Math.pow(10, 9), 1 * SCALE));
        add(new GrabBagItem("paperclip.gif", CCKResources.getString("GrabBag.PaperClip"), CCKModel.MIN_RESISTANCE, .7 * SCALE));
        add(new GrabBagItem("penny.gif", CCKResources.getString("GrabBag.Penny"), CCKModel.MIN_RESISTANCE, .6 * SCALE));
        add(new GrabBagItem("eraser.gif", CCKResources.getString("GrabBag.Eraser"), Math.pow(10, 9), .7 * SCALE));
        add(new GrabBagItem("pencil.gif", CCKResources.getString("GrabBag.PencilLead"), 300, 3.5 * SCALE));
        add(new GrabBagItem("hand.gif", CCKResources.getString("GrabBag.Hand"), Math.pow(10, 6), 1 * SCALE));
        add(new GrabBagItem("dog.gif", CCKResources.getString("GrabBag.Dog"), Math.pow(10, 9), 2.5 * SCALE));
    }

    private void add(GrabBagItem grabBagItem) {
        items.add(grabBagItem);
    }

    public int numItems() {
        return items.size();
    }

    public GrabBagItem itemAt(int i) {
        return (GrabBagItem) items.get(i);
    }
}
