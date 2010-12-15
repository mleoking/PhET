/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.test;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.constants.ABSSymbols;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Test to see if we can programmatically create molecules that look as good as image files.
 * If we can, this would simplify the process of changing molecule colors.
 * Changing the image files is a huge hassle.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestMoleculeNode extends JFrame {
    
    private static class AMinusNode extends PComposite {

        public AMinusNode() {

            // attributes
            double diameter = 24;
            Color color = ABSColors.A_MINUS.darker();
            Color hiliteColor = Color.WHITE;
            Stroke stroke = new BasicStroke( 0.5f );
            Color strokeColor = color.darker();

            // atoms
            SphericalNode atom = new SphericalNode( diameter, createPaint( diameter, color, hiliteColor ), stroke, strokeColor, false );

            // rendering order
            PComposite parentNode = new PComposite();
            addChild( parentNode );
            parentNode.addChild( atom );

            // layout
            parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
        }
    }

    private static class BNode extends PComposite {
        
        public BNode() {
            
            // attributes
            double diameter = 24;
            Color color = ABSColors.B.darker();
            Color hiliteColor = Color.WHITE;
            Stroke stroke = new BasicStroke( 0.5f );
            Color strokeColor = color.darker();
            
            // atoms
            SphericalNode atom = new SphericalNode( diameter, createPaint( diameter, color, hiliteColor ), stroke, strokeColor, false ); 
            
            // rendering order
            PComposite parentNode = new PComposite();
            addChild( parentNode );
            parentNode.addChild( atom );
            
            // layout
            parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
        }
    }
    
    private static class BHPlusNode extends PComposite {
        
        public BHPlusNode() {
            
            // attributes
            double diameterBig = 24;
            double diameterSmall = 14;
            Color color = ABSColors.BH_PLUS.darker();
            Color hiliteColor = Color.WHITE;
            Stroke stroke = new BasicStroke( 0.5f );
            Color strokeColor = color.darker();

            // atom nodes
            SphericalNode atomBig = new SphericalNode( diameterBig, createPaint( diameterBig, color, hiliteColor ), stroke, strokeColor, false );
            SphericalNode atomSmall = new SphericalNode( diameterSmall, createPaint( diameterSmall, color, hiliteColor ), stroke, strokeColor, false );

            // rendering order
            PComposite parentNode = new PComposite();
            addChild( parentNode );
            parentNode.addChild( atomSmall );
            parentNode.addChild( atomBig );

            // layout
            double x = 0;
            double y = 0;
            atomBig.setOffset( x, y );
            x = atomBig.getFullBoundsReference().getMinX() + ( 0.25 * atomSmall.getFullBoundsReference().getWidth() );
            y = atomBig.getFullBoundsReference().getCenterX() - ( 0.75 * atomSmall.getFullBoundsReference().getHeight() );
            atomSmall.setOffset( x, y );
            parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
        }
    }

    private static class H2ONode extends PComposite {

        public H2ONode() {

            // attributes
            double diameterBig = 24;
            double diameterSmall = 14;
            Color color = ABSColors.H2O.darker();
            Color hiliteColor = Color.WHITE;
            Stroke stroke = new BasicStroke( 0.5f );
            Color strokeColor = color.darker();

            // atom nodes
            SphericalNode atomBig = new SphericalNode( diameterBig, createPaint( diameterBig, color, hiliteColor ), stroke, strokeColor, false );
            SphericalNode atomSmallTop = new SphericalNode( diameterSmall, createPaint( diameterSmall, color, hiliteColor ), stroke, strokeColor, false );
            SphericalNode atomSmallBottom = new SphericalNode( diameterSmall, createPaint( diameterSmall, color, hiliteColor ), stroke, strokeColor, false );

            // rendering order
            PComposite parentNode = new PComposite();
            addChild( parentNode );
            parentNode.addChild( atomSmallTop );
            parentNode.addChild( atomBig );
            parentNode.addChild( atomSmallBottom );

            // layout
            double x = 0;
            double y = 0;
            atomBig.setOffset( x, y );
            x = atomBig.getXOffset();
            y = atomBig.getFullBoundsReference().getMinY() - ( 0.25 * atomSmallTop.getFullBoundsReference().getHeight() );
            atomSmallTop.setOffset( x, y );
            x = atomBig.getFullBoundsReference().getMinX();
            y = atomBig.getFullBoundsReference().getMaxY() - ( 0.25 * atomBig.getFullBoundsReference().getHeight() );
            atomSmallBottom.setOffset( x, y );
            parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
        }
    }
    
    private static class H3OPlusNode extends PComposite {
        
        public H3OPlusNode() {
            
            // attributes
            double diameterBig = 24;
            double diameterSmall = 14;
            Color color = ABSColors.H3O_PLUS.darker();
            Color hiliteColor = Color.WHITE;
            Stroke stroke = new BasicStroke( 0.5f );
            Color strokeColor = color.darker();

            // atom nodes
            SphericalNode atomBig = new SphericalNode( diameterBig, createPaint( diameterBig, color, hiliteColor ), stroke, strokeColor, false );
            SphericalNode atomSmallLeft = new SphericalNode( diameterSmall, createPaint( diameterSmall, color, hiliteColor ), stroke, strokeColor, false );
            SphericalNode atomSmallTopRight = new SphericalNode( diameterSmall, createPaint( diameterSmall, color, hiliteColor ), stroke, strokeColor, false );
            SphericalNode atomSmallBottomRight = new SphericalNode( diameterSmall, createPaint( diameterSmall, color, hiliteColor ), stroke, strokeColor, false );

            // rendering order
            PComposite parentNode = new PComposite();
            addChild( parentNode );
            parentNode.addChild( atomSmallTopRight );
            parentNode.addChild( atomSmallBottomRight );
            parentNode.addChild( atomBig );
            parentNode.addChild( atomSmallLeft );

            // layout
            double x = 0;
            double y = 0;
            atomBig.setOffset( x, y );
            x = atomBig.getFullBoundsReference().getMinX();
            y = atomBig.getFullBoundsReference().getCenterX();
            atomSmallLeft.setOffset( x, y );
            x = atomBig.getFullBoundsReference().getMaxX() - ( 0.5 * atomSmallTopRight.getFullBoundsReference().getWidth() );
            y = atomBig.getFullBoundsReference().getMinY();
            atomSmallTopRight.setOffset( x, y );
            x = atomBig.getFullBoundsReference().getMaxX() - ( 0.5 * atomSmallBottomRight.getFullBoundsReference().getWidth() );
            y = atomBig.getFullBoundsReference().getMaxY();
            atomSmallBottomRight.setOffset( x, y );
            parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
        }
    }
    
    private static class HANode extends PComposite {
        
        public HANode() {
            
            // attributes
            double diameterBig = 24;
            double diameterSmall = 14;
            Color color = ABSColors.HA.darker();
            Color hiliteColor = Color.WHITE;
            Stroke stroke = new BasicStroke( 0.5f );
            Color strokeColor = color.darker();

            // atom nodes
            SphericalNode atomBig = new SphericalNode( diameterBig, createPaint( diameterBig, color, hiliteColor ), stroke, strokeColor, false );
            SphericalNode atomSmall = new SphericalNode( diameterSmall, createPaint( diameterSmall, color, hiliteColor ), stroke, strokeColor, false );

            // rendering order
            PComposite parentNode = new PComposite();
            addChild( parentNode );
            parentNode.addChild( atomBig );
            parentNode.addChild( atomSmall );

            // layout
            double x = 0;
            double y = 0;
            atomBig.setOffset( x, y );
            x = atomBig.getFullBoundsReference().getMinX();
            y = atomBig.getFullBoundsReference().getCenterX() - ( 0.15 * atomSmall.getFullBoundsReference().getHeight() );
            atomSmall.setOffset( x, y );
            parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
        }
    }
    
    private static class MPlusNode extends PComposite {

        public MPlusNode() {
            
            // attributes
            double diameter = 22;
            Color color = ABSColors.M_PLUS.darker();
            Color hiliteColor = Color.WHITE;
            Stroke stroke = new BasicStroke( 0.5f );
            Color strokeColor = color.darker();
            
            // atoms
            SphericalNode atom = new SphericalNode( diameter, createPaint( diameter, color, hiliteColor ), stroke, strokeColor, false ); 
            
            // rendering order
            PComposite parentNode = new PComposite();
            addChild( parentNode );
            parentNode.addChild( atom );
            
            // layout
            parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
        }
    }
    
    private static class MOHNode extends PComposite {

        public MOHNode() {

            // attributes
            double diameterBig = 24;
            double diameterMedium = 19;
            double diameterSmall = 14;
            Color color = ABSColors.MOH.darker();
            Color hiliteColor = Color.WHITE;
            Stroke stroke = new BasicStroke( 0.5f );
            Color strokeColor = color.darker();

            // atom nodes
            SphericalNode atomBig = new SphericalNode( diameterBig, createPaint( diameterBig, color, hiliteColor ), stroke, strokeColor, false );
            SphericalNode atomMedium = new SphericalNode( diameterMedium, createPaint( diameterMedium, color, hiliteColor ), stroke, strokeColor, false );
            SphericalNode atomSmall = new SphericalNode( diameterSmall, createPaint( diameterSmall, color, hiliteColor ), stroke, strokeColor, false );
            
            // minus 
            PPath minusNode = new PPath( new Line2D.Double( 0, 0, diameterMedium / 4, 0) );
            minusNode.setStroke( new BasicStroke( 1f ) );
            minusNode.setStrokePaint( Color.BLACK );
            
            // plus
            PComposite plusNode = new PComposite();
            {
                final double length = diameterMedium / 4;
                PPath horizontalNode = new PPath( new Line2D.Double( 0, length / 2, length, length / 2 ) );
                horizontalNode.setStroke( new BasicStroke( 1f ) );
                horizontalNode.setStrokePaint( Color.BLACK );
                plusNode.addChild( horizontalNode );
                PPath verticalNode = new PPath( new Line2D.Double( length / 2, 0, length / 2, length ) );
                verticalNode.setStroke( new BasicStroke( 1f ) );
                verticalNode.setStrokePaint( Color.BLACK );
                plusNode.addChild( verticalNode );
            }

            // rendering order
            PComposite parentNode = new PComposite();
            addChild( parentNode );
            parentNode.addChild( atomBig );
            parentNode.addChild( atomMedium );
            parentNode.addChild( atomSmall );
            parentNode.addChild( minusNode );
            parentNode.addChild( plusNode );

            // layout
            double x = 0;
            double y = 0;
            atomBig.setOffset( x, y );
            x = atomBig.getFullBoundsReference().getCenterX() - ( 1.2 * atomMedium.getFullBoundsReference().getWidth() );
            y = atomBig.getFullBoundsReference().getCenterY();
            atomMedium.setOffset( x, y );
            x = atomBig.getFullBoundsReference().getMaxX();
            y = atomBig.getFullBoundsReference().getCenterY() - ( 0.5 * atomSmall.getFullBoundsReference().getHeight() );
            atomSmall.setOffset( x, y );
            x = atomBig.getFullBoundsReference().getCenterX() - ( minusNode.getFullBoundsReference().getWidth() / 2 );
            y = atomBig.getFullBoundsReference().getMaxY() + ( 0.15 * atomBig.getFullBoundsReference().getHeight() );
            minusNode.setOffset( x, y );
            x = atomMedium.getFullBoundsReference().getCenterX() - ( plusNode.getFullBoundsReference().getWidth() / 2 );
            y = minusNode.getFullBoundsReference().getCenterY() - ( plusNode.getFullBoundsReference().getHeight() / 2 );
            plusNode.setOffset( x, y );
            parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
        }
    }
    
    private static class OHMinusNode extends PComposite {
        
        public OHMinusNode() {
            
            // attributes
            double diameterBig = 24;
            double diameterSmall = 14;
            Color color = ABSColors.OH_MINUS.darker();
            Color hiliteColor = Color.WHITE;
            Stroke stroke = new BasicStroke( 0.5f );
            Color strokeColor = color.darker();

            // atom nodes
            SphericalNode atomBig = new SphericalNode( diameterBig, createPaint( diameterBig, color, hiliteColor ), stroke, strokeColor, false );
            SphericalNode atomSmall = new SphericalNode( diameterSmall, createPaint( diameterSmall, color, hiliteColor ), stroke, strokeColor, false );
            
            // rendering order
            PComposite parentNode = new PComposite();
            addChild( parentNode );
            parentNode.addChild( atomBig );
            parentNode.addChild( atomSmall );

            // layout
            double x = 0;
            double y = 0;
            atomBig.setOffset( x, y );
            x = atomBig.getFullBoundsReference().getMaxX();
            y = atomBig.getFullBoundsReference().getCenterY() - ( 0.45 * atomSmall.getFullBoundsReference().getHeight() );
            atomSmall.setOffset( x, y );
            parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
        }
    }
    
    private static Paint createPaint( double diameter, Color color, Color hiliteColor ) {
        return new RoundGradientPaint( -diameter/4, -diameter/4, hiliteColor, new Point2D.Double( diameter/4, diameter/4 ), color );
    }
    
    public TestMoleculeNode() {
        
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 650, 600 ) );
        
        final double xStart = 50;
        final double yStart = 50;
        final double deltaX = 300;
        final double deltaY = 100;
        
        double x = xStart;
        double y = yStart;
        
        addNodes( canvas, ABSSymbols.A_MINUS, new AMinusNode(), ABSImages.A_MINUS_MOLECULE, x, y );
        y += deltaY;
        addNodes( canvas, ABSSymbols.B, new BNode(), ABSImages.B_MOLECULE, x, y );
        y += deltaY;
        addNodes( canvas, ABSSymbols.BH_PLUS, new BHPlusNode(), ABSImages.BH_PLUS_MOLECULE, x, y );
        y += deltaY;
        addNodes( canvas, ABSSymbols.H2O, new H2ONode(), ABSImages.H2O_MOLECULE, x, y );
        
        x += deltaX;
        y = yStart;
        
        addNodes( canvas, ABSSymbols.H3O_PLUS, new H3OPlusNode(), ABSImages.H3O_PLUS_MOLECULE, x, y );
        y += deltaY;
        addNodes( canvas, ABSSymbols.HA, new HANode(), ABSImages.HA_MOLECULE, x, y );
        y += deltaY;
        addNodes( canvas, ABSSymbols.M_PLUS, new MPlusNode(), ABSImages.M_PLUS_MOLECULE, x, y );
        y += deltaY;
        addNodes( canvas, ABSSymbols.MOH, new MOHNode(), ABSImages.MOH_MOLECULE, x, y );
        y += deltaY;
        addNodes( canvas, ABSSymbols.OH_MINUS, new OHMinusNode(), ABSImages.OH_MINUS_MOLECULE, x, y );
        y += deltaY;
        
        setContentPane( canvas );
        pack();
    }
    
    private void addNodes( PCanvas canvas, String name, PNode moleculeNode, Image image, double xOffset, double yOffset ) {
        
        double deltaX = 75;
        
        HTMLNode nameNode = new HTMLNode( name );
        nameNode.setFont( new PhetFont() );
        nameNode.setOffset( xOffset, yOffset );
        canvas.getLayer().addChild( nameNode );
        
        moleculeNode.setOffset( xOffset + deltaX, yOffset );
        canvas.getLayer().addChild( moleculeNode );
        
        PNode imageMOH = new PImage( image );
        imageMOH.setOffset( xOffset + ( 2 * deltaX ), yOffset );
        canvas.getLayer().addChild( imageMOH );
    }
    
    public static void main( String[] args ) {
        JFrame frame = new TestMoleculeNode();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }

}
