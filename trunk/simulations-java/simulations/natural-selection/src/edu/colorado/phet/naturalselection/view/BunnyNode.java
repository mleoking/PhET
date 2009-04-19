package edu.colorado.phet.naturalselection.view;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.model.Allele;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.model.ColorGene;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

public class BunnyNode extends PNode implements Bunny.BunnyListener {
    private PImage whiteImage;
    private PImage brownImage;

    private boolean isWhite;

    public BunnyNode( Allele colorPhenotype, Allele teethPhenotype, Allele tailPhenotype ) {
        whiteImage = NaturalSelectionResources.getImageNode( "bunny_2_white.png" );
        brownImage = NaturalSelectionResources.getImageNode( "bunny_2_brown.png" );

        if ( colorPhenotype == ColorGene.WHITE_ALLELE ) {
            isWhite = true;
            addChild( whiteImage );
        }
        else {
            isWhite = false;
            addChild( brownImage );
        }
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

    public void onBunnyChangeColor( Allele allele ) {
        if ( isWhite ) {
            if ( allele == ColorGene.WHITE_ALLELE ) {
                return;
            }
            else {
                removeChild( whiteImage );
                addChild( brownImage );
            }
        }
        else {
            if ( allele == ColorGene.BROWN_ALLELE ) {
                return;
            }
            else {
                removeChild( brownImage );
                addChild( whiteImage );
            }
        }

        isWhite = allele == ColorGene.WHITE_ALLELE;
    }
}
