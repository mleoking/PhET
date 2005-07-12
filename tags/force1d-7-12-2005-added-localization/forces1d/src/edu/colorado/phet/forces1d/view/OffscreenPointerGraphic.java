package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphicListener;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.forces1d.model.Block;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 22, 2004
 * Time: 8:34:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class OffscreenPointerGraphic extends GraphicLayerSet {
    private static final Font font = new Font( "Lucida Sans", Font.BOLD, 28 );
    private DecimalFormat decimalFormat = new DecimalFormat( "#0.0" );
    private PhetTextGraphic textGraphic;
    private BlockGraphic blockGraphic;
    private WalkwayGraphic container;
    private int y = 50;

    public OffscreenPointerGraphic( final Force1DPanel component, final BlockGraphic blockGraphic, final WalkwayGraphic walkway ) {
        super( component );
        this.blockGraphic = blockGraphic;
        this.container = walkway;

        textGraphic = new PhetTextGraphic( component, font, "", Color.blue, 0, 0 );
        addGraphic( textGraphic );
        blockGraphic.addPhetGraphicListener( new PhetGraphicListener() {
            public void phetGraphicChanged( PhetGraphic phetGraphic ) {
                boolean neg = blockGraphic.getBounds().getX() <= walkway.getBounds().getX();
                boolean pos = blockGraphic.getBounds().getX() >= walkway.getBounds().getX() + walkway.getWidth();
                int insetX = 30;
                if( neg || pos ) {
                    Block block = blockGraphic.getBlock();
                    double x = block.getPosition();
                    String locStr = decimalFormat.format( x );
                    textGraphic.setText( locStr + " " + SimStrings.get( "OffscreenPointerGraphic.meters" ) );
                    int yRel = textGraphic.getHeight() + 10;

                    Point2D.Double source = new Point2D.Double( textGraphic.getWidth() / 2, yRel );
                    Point2D.Double dst = new Point2D.Double( source.getX() + x, yRel );
                    AbstractVector2D arrowVector = new Vector2D.Double( source, dst );
                    int maxArrowLength = textGraphic.getWidth() / 2;
                    if( arrowVector.getMagnitude() > maxArrowLength ) {
                        arrowVector = arrowVector.getInstanceOfMagnitude( maxArrowLength );
                    }

                    setBoundsDirty();
                    Rectangle bounds = getBounds();
                    if( pos ) {
                        //TODO these used to use walkway.getX(), when that meant x on the screen.
                        setLocation( (int)( walkway.getBounds().getX() + walkway.getWidth() - bounds.width - insetX ), y );
                    }
                    else {
                        setLocation( (int)( walkway.getBounds().getX() + insetX ), y );
                    }
                    setVisible( true );
                }
                else {
                    setVisible( false );
                }
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
            }
        } );
    }

    public PhetTextGraphic getTextGraphic() {
        return textGraphic;
    }

}
