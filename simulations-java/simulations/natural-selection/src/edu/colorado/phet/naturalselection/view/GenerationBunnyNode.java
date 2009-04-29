/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import edu.colorado.phet.naturalselection.model.Bunny;

/**
 * Bunny visual used in the generation charts. Essentially adds bunny listening capabilities
 *
 * @author Jonathan Olson
 */
public class GenerationBunnyNode extends DisplayBunnyNode implements Bunny.BunnyListener {

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

    public void onBunnyInit( Bunny bunny ) {

    }

    public void onBunnyDeath( Bunny bunny ) {
        setDead( true );
    }

    public void onBunnyReproduces( Bunny bunny ) {

    }

    public void onBunnyAging( Bunny bunny ) {

    }

    public void onBunnyChangePosition( double x, double y, double z ) {

    }
}
