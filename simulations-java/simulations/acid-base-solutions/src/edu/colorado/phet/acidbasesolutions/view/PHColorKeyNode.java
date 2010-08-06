/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.PHPaper;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays a color key for the pH paper.
 * pH values are mapped to colors in the visible spectrum.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHColorKeyNode extends PhetPNode {
    
    private static final Color TITLE_COLOR = Color.BLACK;
    private static final Font TITLE_FONT = new PhetFont( 18 );
    
    private static final Stroke SPECTRUM_OUTLINE_STROKE = new BasicStroke( 1f );
    private static final Color SPECTRUM_OUTLINE_COLOR = Color.BLACK;
    
    private static final Color TICK_LINE_COLOR = Color.BLACK;
    private static final Stroke TICK_LINE_STROKE = new BasicStroke( 1f );
    private static final int TICK_LINE_LENGTH = 5;
    private static final int TICK_INCREMENT = 1;
    
    private static final Color TICK_LABEL_COLOR = Color.BLACK;
    private static final Font TICK_LABEL_FONT = new PhetFont( 12 );
    
    public PHColorKeyNode( final PHPaper paper ) {
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
        
        // spectrum
        SpectrumNode spectrumNode = new SpectrumNode( paper, ABSConstants.PH_COLOR_KEY_SIZE );
        addChild( spectrumNode );
        spectrumNode.setOffset( 0, titleNode.getFullBoundsReference().getMaxY() + 3 );
        
        // tick marks
        LinearFunction f = new Function.LinearFunction( ABSConstants.MIN_PH, ABSConstants.MAX_PH, 0, ABSConstants.PH_COLOR_KEY_SIZE.getWidth() );
        for ( int pH = ABSConstants.MIN_PH; pH <= ABSConstants.MAX_PH; pH = pH + TICK_INCREMENT ) {
            TickMarkNode tickMarkNode = new TickMarkNode( pH );
            addChild( tickMarkNode );
            double x = f.evaluate( pH );
            double y = spectrumNode.getFullBoundsReference().getMaxY();
            tickMarkNode.setOffset( x, y );
        }
        
        setVisible( paper.isVisible() );
    }
    
    /*
     * Color spectrum, origin at upper-left corner.
     */
    private static class SpectrumNode extends PComposite {
        
        public SpectrumNode( PHPaper paper, PDimension size ) {
            
            PImage imageNode = new PImage( createImage( paper, size ) );
            addChild( imageNode );
            
            PPath outlineNode = new PPath( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
            outlineNode.setStroke( SPECTRUM_OUTLINE_STROKE );
            outlineNode.setStrokePaint( SPECTRUM_OUTLINE_COLOR );
            addChild( outlineNode );
        }
       
        private Image createImage( PHPaper paper, PDimension size ) {
            BufferedImage image = new BufferedImage( (int)size.getWidth(), (int)size.getHeight(), BufferedImage.TYPE_INT_RGB );
            Function linearFunction = new Function.LinearFunction( 0, size.getWidth(), ABSConstants.MIN_PH, ABSConstants.MAX_PH ); // map x position to pH
            Graphics2D g2 = image.createGraphics();
            final int dx = 1;
            for ( int x = 0; x < size.getWidth(); x = x + dx ) {
                double wavelength = linearFunction.evaluate( x );
                g2.setColor( paper.createColor( wavelength ) );
                g2.fillRect( x, 0, dx, (int) size.getHeight() );
            }
            g2.dispose();
            return image;
        }
    }
    
    /*
     * Tick mark, a vertical line with a pH value centered below it.
     * Origin at top center of tick line.
     */
    private static class TickMarkNode extends PComposite {
        
        public TickMarkNode( int pH ) {
            
            TickLineNode lineNode = new TickLineNode();
            addChild( lineNode );
            
            TickLabelNode labelNode = new TickLabelNode( pH );
            addChild( labelNode );
            
            double x = -lineNode.getFullBoundsReference().getWidth() / 2;
            double y = 0;
            lineNode.setOffset( x, y );
            x = -labelNode.getFullBoundsReference().getWidth() / 2;
            y = lineNode.getFullBoundsReference().getMaxY() + 1;
            labelNode.setOffset( x, y );
        }
    }
    
    /*
     * Tick line, origin at upper left of bounds.
     */
    private static class TickLineNode extends PPath {
        public TickLineNode() {
            setPathTo( new Line2D.Double( 0, 0, 0, TICK_LINE_LENGTH ) );
            setStroke( TICK_LINE_STROKE );
            setStrokePaint( TICK_LINE_COLOR );
        }
    }
    
    /*
     * Tick label, origin at upper left of bounds.
     */
    private static class TickLabelNode extends PText {
        public TickLabelNode( int pH ) {
            super( String.valueOf( pH ) );
            setTextPaint( TICK_LABEL_COLOR );
            setFont( TICK_LABEL_FONT);
        }
    }
}
