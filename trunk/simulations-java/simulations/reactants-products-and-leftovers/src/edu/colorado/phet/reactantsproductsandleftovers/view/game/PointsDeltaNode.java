// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;

/**
 * Displays the change in points whenever the points value changes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointsDeltaNode extends ShadowPText {
    
    private static final NumberFormat POINTS_FORMAT = new DefaultDecimalFormat( "0.#" );
    private static final Font FONT = new PhetFont( 40 );
    private static final Color COLOR = Color.YELLOW;
    private static final Color SHADOW_COLOR = Color.BLACK;
    private static final double SHADOW_OFFSET = 2;
    
    private double points;
    
    public PointsDeltaNode( final GameModel model ) {
        super();
        setShadowColor( SHADOW_COLOR );
        setShadowOffset( SHADOW_OFFSET, SHADOW_OFFSET );
        setFont( FONT );
        setTextPaint( COLOR );
        setText( "?" );
        model.addGameListener( new GameAdapter() {
            @Override
            public void pointsChanged() {
                double delta = model.getPoints() - points;
                setValue( delta );
                points = model.getPoints();
            }
        });
    }
    
    private void setValue( double value ) {
        String s = "0";
        if ( value > 0 ) {
            s = "+" + POINTS_FORMAT.format(  value  );
        }
        else {
            s = POINTS_FORMAT.format(  value  );
        }
        setText( s );
    }
}
