/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import edu.colorado.phet.naturalselection.model.Bunny;

/**
 * Bunny visual used in the generation charts. Essentially adds bunny listening capabilities
 *
 * @author Jonathan Olson
 */
public class GenerationBunnyNode extends DisplayBunnyNode implements Bunny.Listener {

    /**
     * Bunny reference
     */
    private Bunny myBunny;

    /**
     * Constructor
     *
     * @param bunny The bunny we are referencing
     */
    public GenerationBunnyNode( Bunny bunny ) {
        super( bunny.getColorPhenotype(), bunny.getTeethPhenotype(), bunny.getTailPhenotype() );
        myBunny = bunny;
        setDead( !bunny.isAlive() );
        if ( bunny.isMutated() ) {
            setMutated();
        }
        bunny.addListener( this );
    }

    public Bunny getBunny() {
        return myBunny;
    }

    public void cleanup() {
        myBunny.removeListener( this );
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    public void onEvent( Bunny.Event event ) {
        if ( event.type == Bunny.Event.TYPE_DIED ) {
            setDead( true );

            // if a bunny dies, we shouldn't show a border around it in the pedigree
            setSelected( false );
        }
    }
}
