/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import edu.colorado.phet.naturalselection.model.Allele;
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
        super( bunny.getColorGenotype().getPhenotype(), bunny.getTeethGenotype().getPhenotype(), bunny.getTailGenotype().getPhenotype() );
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

    public void onBunnyChangeColor( Allele allele ) {
        setColor( allele );
    }

    public void onBunnyChangeTeeth( Allele allele ) {
        setTeeth( allele );
    }

    public void onBunnyChangeTail( Allele allele ) {
        setTail( allele );
    }
}
