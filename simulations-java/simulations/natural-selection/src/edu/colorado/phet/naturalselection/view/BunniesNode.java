package edu.colorado.phet.naturalselection.view;

import java.util.ArrayList;
import java.util.Iterator;

import edu.umd.cs.piccolo.PNode;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.model.Bunny;

public class BunniesNode extends PNode implements NaturalSelectionModel.NaturalSelectionModelListener {

    private ArrayList bunnyNodes;

    public BunniesNode() {
        bunnyNodes = new ArrayList();

    }


    public void onMonthChange( String monthName ) {

    }

    public void onGenerationChange( int generation ) {

    }

    public void reset() {
        Iterator iter = bunnyNodes.iterator();
        while( iter.hasNext() ) {
            BunnyNode bunnyNode = (BunnyNode) iter.next();
            removeChild( bunnyNode );
        }
        bunnyNodes.clear();
    }

    public void onNewBunny( Bunny bunny ) {
        BunnyNode bunnyNode = new BunnyNode();
        bunnyNode.setOffset( 3200 * Math.random(), 1000 * Math.random() );
        addChild( bunnyNode );
        bunny.addListener( bunnyNode );
        bunnyNodes.add( bunnyNode );
    }
}
