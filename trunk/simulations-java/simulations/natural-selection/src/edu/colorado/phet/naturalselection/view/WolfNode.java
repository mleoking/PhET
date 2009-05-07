package edu.colorado.phet.naturalselection.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.model.Wolf;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PAffineTransform;

public class WolfNode extends NaturalSelectionSprite implements Wolf.Listener {
    private PNode wolfGraphic;
    private boolean flipped = false;
    private PImage wolfImage;

    public WolfNode( SpritesNode spritesNode ) {

        wolfGraphic = new PNode();

        wolfImage = NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_WOLF );

        wolfGraphic.addChild( wolfImage );

        addChild( wolfGraphic );

    }

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped( boolean flipped ) {
        // TODO: refactor flipped into NaturalSelectionSprite 
        if ( flipped != this.flipped ) {
            if ( flipped ) {
                wolfGraphic.setTransform( new PAffineTransform( 1, 0, 0, 1, 0, 0 ) );
            }
            else {
                wolfGraphic.setTransform( new PAffineTransform( -1, 0, 0, 1, wolfImage.getWidth(), 0 ) );
            }
        }
        this.flipped = flipped;
    }

    public void setSpriteLocation( double x, double y, double z ) {
        if ( x > getSpriteX() ) {
            setFlipped( false );
        }
        else if ( x < getSpriteX() ) {
            setFlipped( true );
        }
        super.setSpriteLocation( x, y, z );

        reposition();
    }

    public void reposition() {
        // how much to scale the wolf by
        double scaleFactor = getCanvasScale() * 0.25;

        setScale( scaleFactor );

        // the width and height of the wolf when scaled
        double scaledWidth = wolfImage.getWidth() * scaleFactor;
        double scaledHeight = wolfImage.getHeight() * scaleFactor;

        Point2D canvasLocation = getCanvasLocation();

        Point2D.Double location = new Point2D.Double( canvasLocation.getX() - scaledWidth / 2, canvasLocation.getY() - scaledHeight );

        setOffset( location );
    }

    public void onEvent( Wolf.Event event ) {
        Wolf wolf = event.wolf;
        if ( event.type == Wolf.Event.TYPE_POSITION_CHANGED ) {
            setSpriteLocation( wolf.getX(), wolf.getY(), wolf.getZ() );
        }
    }
}
