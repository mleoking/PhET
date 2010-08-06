/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.PHPaper;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays a color key for the pH paper.
 * Shows a set of discrete colors, one for each integer value of pH.
 * This is similar to the color charts that come with real pH paper.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DiscreteColorKeyNode extends PhetPNode {
    
    private static final Color TITLE_COLOR = Color.BLACK;
    private static final Font TITLE_FONT = new PhetFont( 18 );
    
    private static final PDimension COLOR_CHIP_SIZE = new PDimension( 23, 50 );
    private static final int COLOR_CHIP_X_SPACING = 2;
    
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final Font LABEL_FONT = new PhetFont( 13 );
    
    public DiscreteColorKeyNode( final PHPaper paper ) {
        // not interactive
        setPickable( false );
        setChildrenPickable( false );
        
        paper.addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            public void visibilityChanged() {
                setVisible( paper.isVisible() );
            }
        });
        
        // title
        PText titleNode = new PText( ABSStrings.PH_COLOR_KEY );
        titleNode.setTextPaint( TITLE_COLOR );
        titleNode.setFont( TITLE_FONT );
        addChild( titleNode );
        
        // colors
        double x = 0;
        double y = titleNode.getFullBoundsReference().getMaxY() + 4;
        for ( int pH = ABSConstants.MIN_PH; pH <= ABSConstants.MAX_PH; pH++ ) {
            PHColorNode tickMarkNode = new PHColorNode( paper, pH );
            addChild( tickMarkNode );
            tickMarkNode.setOffset( x, y );
            x += COLOR_CHIP_SIZE.getWidth() + COLOR_CHIP_X_SPACING;
        }
        
        setVisible( paper.isVisible() );
    }
    
    /*
     * Color for a specific pH, origin at upper left.
     */
    private static class PHColorNode extends PComposite {
        
        public PHColorNode( PHPaper paper, int pH ) {
            
            ColorChipNode chipNode = new ColorChipNode( paper.createColor( pH ) );
            addChild( chipNode );
            
            LabelNode labelNode = new LabelNode( pH );
            addChild( labelNode );
            
            double x = chipNode.getFullBoundsReference().getCenterX() - ( labelNode.getFullBoundsReference().getWidth() ) / 2;
            double y = chipNode.getFullBoundsReference().getMaxY() + 1;
            labelNode.setOffset( x, y );
        }
    }
    
    /*
     * Color chip, origin at upper left.
     */
    private static class ColorChipNode extends PPath {
        public ColorChipNode( Color color ) {
            setPathTo( new Rectangle2D.Double( 0, 0, COLOR_CHIP_SIZE.getWidth(), COLOR_CHIP_SIZE.getHeight() ) );
            setPaint( color );
            setStroke( null );
        }
    }
    
    /*
     * Tick label, origin at upper left.
     */
    private static class LabelNode extends PText {
        public LabelNode( int pH ) {
            super( String.valueOf( pH ) );
            setTextPaint( LABEL_COLOR );
            setFont( LABEL_FONT);
        }
    }
}
