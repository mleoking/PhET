/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.help;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.RectangleUtils;

/**
 * Help items for the Fourier simulation.
 * Adapted from some code in the Forces 1D simulation.
 *
 * @author Sam Reid, Chris Malley
 * @version $Revision$
 */
public class FourierHelpItem extends CompositePhetGraphic {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double BACKGROUND_LAYER = 1;
    private static final double ARROW_LAYER = 2;
    private static final double TEXT_LAYER = 3;
    
    private static final int ARROW_HEAD_WIDTH = 10;
    private static final int ARROW_TAIL_WIDTH = 3;
    private static final Color ARROW_FILL_COLOR = Color.YELLOW;
    private static final Color ARROW_BORDER_COLOR = Color.BLACK;
    private static final Stroke ARROW_STROKE = new BasicStroke( 1f );
    
    private static final Font TEXT_FONT = new Font( "Lucida Sans", Font.PLAIN, 14 );
    private static final Color TEXT_COLOR = Color.BLACK;
    
    private static final Color BACKGROUND_FILL_COLOR = ARROW_FILL_COLOR;
    private static final Color BACKGROUND_BORDER_COLOR = ARROW_BORDER_COLOR;
    private static final Stroke BACKGROUND_STROKE = ARROW_STROKE;
    private static final int BACKGROUND_CORNER_RADIUS = 15;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HTMLGraphic _textGraphic;
    private PhetShapeGraphic _backgroundGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public FourierHelpItem( Component component, String html ) {
        super( component );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        _textGraphic = new HTMLGraphic( component, TEXT_FONT, html, TEXT_COLOR );

        _backgroundGraphic = new PhetShapeGraphic( component );
        Rectangle bounds = _textGraphic.getBounds();
        Rectangle b = RectangleUtils.expand( bounds, 4, 4 );
        _backgroundGraphic.setShape( new RoundRectangle2D.Double( b.x, b.y, b.width, b.height, BACKGROUND_CORNER_RADIUS, BACKGROUND_CORNER_RADIUS ) );
        _backgroundGraphic.setColor( BACKGROUND_FILL_COLOR );
        _backgroundGraphic.setBorderColor( BACKGROUND_BORDER_COLOR );
        _backgroundGraphic.setStroke( BACKGROUND_STROKE );

        addGraphic( _backgroundGraphic, BACKGROUND_LAYER );
        addGraphic( _textGraphic, TEXT_LAYER );
    }

    //----------------------------------------------------------------------------
    // Arrows with targets
    //----------------------------------------------------------------------------
    
    public void pointUpAt( PhetGraphic target, int arrowLength ) {
        pointUp( arrowLength );
        RelativeLocationSetter.follow( target, this, new RelativeLocationSetter.Bottom( 1 ) );
    }

    public void pointDownAt( PhetGraphic target, int arrowLength ) {
        pointDown( arrowLength );
        RelativeLocationSetter.follow( target, this, new RelativeLocationSetter.Top( 1 ) );
    }
    
    public void pointLeftAt( PhetGraphic target, int arrowLength ) {
        pointLeft( arrowLength );
        RelativeLocationSetter.follow( target, this, new RelativeLocationSetter.Right( 1 ) );
    }
    
    public void pointRightAt( PhetGraphic target, int arrowLength ) {
        pointRight( arrowLength );
        RelativeLocationSetter.follow( target, this, new RelativeLocationSetter.Left( 1 ) );
    }
    
    //----------------------------------------------------------------------------
    // Arrows, no targets
    //----------------------------------------------------------------------------

    public void pointUp( int arrowLength ) {
        Arrow arrow = new Arrow( new Point2D.Double(), new Point2D.Double( 0, -arrowLength ), ARROW_HEAD_WIDTH, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
        PhetShapeGraphic arrowGraphic = new PhetShapeGraphic( getComponent(), arrow.getShape(), ARROW_FILL_COLOR, ARROW_STROKE, ARROW_BORDER_COLOR );
        addGraphic( arrowGraphic, ARROW_LAYER );
        new RelativeLocationSetter.Top().layout( _backgroundGraphic, arrowGraphic );
    }

    private void pointDown( int arrowLength ) {
        Arrow arrow = new Arrow( new Point2D.Double(), new Point2D.Double( 0, arrowLength ), ARROW_HEAD_WIDTH, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
        PhetShapeGraphic arrowGraphic = new PhetShapeGraphic( getComponent(), arrow.getShape(), ARROW_FILL_COLOR, ARROW_STROKE, ARROW_BORDER_COLOR );
        addGraphic( arrowGraphic, ARROW_LAYER );
        new RelativeLocationSetter.Bottom().layout( _backgroundGraphic, arrowGraphic );
    }
    
    public void pointLeft( int arrowLength ) {
        Arrow arrow = new Arrow( new Point2D.Double(), new Point2D.Double( -arrowLength, 0 ), ARROW_HEAD_WIDTH, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
        PhetShapeGraphic arrowGraphic = new PhetShapeGraphic( getComponent(), arrow.getShape(), ARROW_FILL_COLOR, ARROW_STROKE, ARROW_BORDER_COLOR );
        addGraphic( arrowGraphic, ARROW_LAYER );
        new RelativeLocationSetter.Left().layout( _backgroundGraphic, arrowGraphic );
    }
    
    public void pointRight( int arrowLength ) {
        Arrow arrow = new Arrow( new Point2D.Double(), new Point2D.Double( arrowLength, 0 ), ARROW_HEAD_WIDTH, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
        PhetShapeGraphic arrowGraphic = new PhetShapeGraphic( getComponent(), arrow.getShape(), ARROW_FILL_COLOR, ARROW_STROKE, ARROW_BORDER_COLOR );
        addGraphic( arrowGraphic, ARROW_LAYER );
        new RelativeLocationSetter.Right().layout( _backgroundGraphic, arrowGraphic );
    }
}
