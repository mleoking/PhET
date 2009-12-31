package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.umd.cs.piccolo.nodes.PText;


public class PointsDeltaNode extends PText {
    
    private static final NumberFormat POINTS_FORMAT = new DefaultDecimalFormat( "0.#" );
    private static final Font FONT = new PhetFont( 40 );
    private static final Color COLOR = Color.YELLOW;
    
    private double points;
    
    public PointsDeltaNode( final GameModel model ) {
        super();
        setFont( FONT );
        setTextPaint( COLOR );
        setText( "?" );
        model.addGameListener( new GameAdapter() {
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
