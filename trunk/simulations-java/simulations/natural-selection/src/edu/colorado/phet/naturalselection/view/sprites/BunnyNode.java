/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view.sprites;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.naturalselection.model.Allele;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.model.Landscape;
import edu.colorado.phet.naturalselection.view.DisplayBunnyNode;
import edu.colorado.phet.naturalselection.view.LandscapeNode;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;
import edu.umd.cs.piccolo.PNode;

/**
 * The piccolo node for bunnies in the main simulation canvas
 *
 * @author Jonathan Olson
 */
public class BunnyNode extends NaturalSelectionSprite implements Bunny.Listener {

    /**
     * The graphical representation of the bunny
     */
    private DisplayBunnyNode displayBunnyNode;

    private PNode displayHolder;

    private boolean cachedFlip = false;

    /**
     * Constructor
     *
     * @param colorPhenotype The color
     * @param teethPhenotype The teeth
     * @param tailPhenotype  The tail
     * @param landscapeNode  The 3-D coordinates handler for this bunnynode
     * @param position       The initial bunny position
     */
    public BunnyNode( Allele colorPhenotype, Allele teethPhenotype, Allele tailPhenotype, LandscapeNode landscapeNode, Point3D position ) {
        super( landscapeNode, position );

        this.landscapeNode = landscapeNode;
        displayBunnyNode = new DisplayBunnyNode( colorPhenotype, teethPhenotype, tailPhenotype );
        displayHolder = new PNode();
        displayHolder.addChild( displayBunnyNode );
        addChild( displayHolder );

        setBunnyOffset();

        rescale();
    }

    public void setPosition( Point3D position ) {
        if ( position.getX() > getPosition().getX() ) {
            setFlipped( false );
        }
        else if ( position.getX() < getPosition().getX() ) {
            setFlipped( true );
        }

        super.setPosition( position );

        rescale();
    }

    public void setFlipped( boolean flipped ) {
        displayBunnyNode.setFlipped( flipped );
        cachedFlip = flipped;
        //setBunnyOffset();
    }

    public void onEvent( Bunny.Event event ) {
        switch( event.type ) {
            case Bunny.Event.TYPE_DIED:
                landscapeNode.removeSprite( this );
                break;
            case Bunny.Event.TYPE_POSITION_CHANGED:
                // TODO: refactor setPosition to Point3D?
                setPosition( event.getPosition() );
                if ( cachedFlip == event.getBunny().isMovingRight() ) {
                    setFlipped( !event.getBunny().isMovingRight() );
                }
                break;
        }
    }

    private void rescale() {
        // how much to scale the bunny by
        double scaleFactor = Landscape.NEARPLANE * 0.25 / getPosition().getZ();


        if ( NaturalSelectionApplication.isHighContrast() ) {
            scaleFactor *= 1.5;
        }

        setScale( scaleFactor );
    }

    private void setBunnyOffset() {
        displayHolder.setOffset( -displayBunnyNode.getBunnyWidth() / 2, -displayBunnyNode.getBunnyHeight() );
    }
}
