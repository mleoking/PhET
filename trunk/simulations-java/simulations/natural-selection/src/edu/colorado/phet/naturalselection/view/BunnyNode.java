package edu.colorado.phet.naturalselection.view;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.umd.cs.piccolo.PNode;

public class BunnyNode extends PNode implements Bunny.BunnyListener {
    public BunnyNode() {
        addChild( NaturalSelectionResources.getImageNode( "bunny_2_white.png" ) );
    }

    public void onBunnyInit( Bunny bunny ) {

    }

    public void onBunnyDeath( Bunny bunny ) {
        setVisible( false );
    }

    public void onBunnyReproduces( Bunny bunny ) {

    }

    public void onBunnyAging( Bunny bunny ) {

    }
}
