package edu.colorado.phet.naturalselection.view.sprites;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.model.Wolf;
import edu.colorado.phet.naturalselection.view.LandscapeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PAffineTransform;

public class WolfNode extends NaturalSelectionSprite implements Wolf.Listener {
    private PNode wolfHolder;
    private PNode wolfGraphic;
    private boolean flipped = false;
    private boolean flippedInit = false;
    private PImage wolfImage;

    public WolfNode( LandscapeNode landscapeNode, Point3D position ) {
        super( landscapeNode, position );

        wolfHolder = new PNode();
        wolfGraphic = new PNode();
        wolfImage = NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_WOLF );

        wolfGraphic.addChild( wolfImage );
        wolfHolder.addChild( wolfGraphic );
        addChild( wolfHolder );

        wolfHolder.setOffset( -wolfImage.getWidth() / 2, -wolfImage.getHeight() );

        rescale();

    }

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped( boolean flipped ) {
        // TODO: refactor flipped into NaturalSelectionSprite 
        if ( flipped != this.flipped || !flippedInit ) {
            flippedInit = true;
            if ( flipped ) {
                wolfGraphic.setTransform( new PAffineTransform( 1, 0, 0, 1, 0, 0 ) );
            }
            else {
                wolfGraphic.setTransform( new PAffineTransform( -1, 0, 0, 1, wolfImage.getWidth(), 0 ) );
            }
        }
        this.flipped = flipped;
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

    public void rescale() {
        // how much to scale the wolf by
        //double scaleFactor = Landscape.NEARPLANE * 0.25 / getPosition().getZ();
        double scaleFactor = Wolf.wolfScale( getPosition() );

        setScale( scaleFactor );
    }

    public void onEvent( Wolf.Event event ) {
        Wolf wolf = event.wolf;
        if ( event.type == Wolf.Event.TYPE_POSITION_CHANGED ) {
            setPosition( wolf.getPosition() );
            setFlipped( wolf.isFlipped() );
        }
    }
}
