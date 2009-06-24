package edu.colorado.phet.acidbasesolutions.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;


public class MatchingGameScoreNode extends PComposite {
    
    private static final int X_SPACING = 10;
    private static final int Y_SPACING = 10;
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final Color VALUE_COLOR = Color.RED;
    private static final Font LABEL_FONT = new PhetFont( 24 );
    private static final Font VALUE_FONT = new PhetFont( Font.BOLD, 24 );
    
    private final IntValueNode pointsValue;
    private final IntValueNode solutionsValue;
    
    public MatchingGameScoreNode() {
        
        LabelNode pointsLabel = new LabelNode( ABSStrings.LABEL_POINTS );
        pointsValue = new IntValueNode();
        LabelNode solutionsLabel = new LabelNode( ABSStrings.LABEL_SOLUTIONS );
        
        solutionsValue = new IntValueNode();
        
        // rendering order
        addChild( pointsLabel );
        addChild( pointsValue );
        addChild( solutionsLabel );
        addChild( solutionsValue );
        
        /* 
         * layout, like this...
         *
         * Points: 25
         * Solutions: 7
         */
        double xOffset = 0;
        double yOffset = 0;
        pointsLabel.setOffset( xOffset, yOffset );
        xOffset = pointsLabel.getFullBoundsReference().getMaxX() + X_SPACING;
        yOffset = pointsLabel.getYOffset();
        pointsValue.setOffset( xOffset, yOffset );
        xOffset = pointsLabel.getXOffset();
        yOffset = pointsLabel.getFullBoundsReference().getMaxY() + Y_SPACING;
        solutionsLabel.setOffset( xOffset, yOffset );
        xOffset = solutionsLabel.getFullBoundsReference().getMaxX() + X_SPACING;
        yOffset = solutionsLabel.getYOffset();
        solutionsValue.setOffset( xOffset, yOffset );
        
        reset();
    }
    
    public void changePoints( int delta ) {
        pointsValue.setValue( pointsValue.getValue() + delta );
    }
    
    public void changeSolutions( int delta ) {
        solutionsValue.setValue( solutionsValue.getValue() + delta );
    }
    
    public void reset() {
        solutionsValue.setValue( 0 );
        pointsValue.setValue( 0 );
    }
    
    private static class LabelNode extends PText {
        public LabelNode( String text ) {
            super( text );
            setFont( LABEL_FONT );
            setTextPaint( LABEL_COLOR );
        }
    }
    
    private static class IntValueNode extends PText {
        
        private int value;
        
        public IntValueNode() {
            super();
            setFont( VALUE_FONT );
            setTextPaint( VALUE_COLOR );
            setValue( 0 );
        }
        
        public void setValue( int value ) {
            setText( String.valueOf( value ) );
        }
        
        public int getValue() {
            return value;
        }
    }

}
