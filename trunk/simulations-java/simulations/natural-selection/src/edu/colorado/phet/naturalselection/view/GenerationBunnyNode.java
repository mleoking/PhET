package edu.colorado.phet.naturalselection.view;

import edu.colorado.phet.naturalselection.model.Allele;
import edu.colorado.phet.naturalselection.model.Bunny;

public class GenerationBunnyNode extends DisplayBunnyNode implements Bunny.BunnyListener {

    private Bunny myBunny;

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
}
