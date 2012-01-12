// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.constants.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Beaker;
import edu.colorado.phet.acidbasesolutions.model.PHPaper;
import edu.colorado.phet.acidbasesolutions.model.PureWaterSolution;
import edu.colorado.phet.acidbasesolutions.view.PHColorKeyNode;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays all of the images and colors used in the Acid-Base Solutions simulation.
 * Take a screenshot of this window, and run through a colorblindness checker such
 * as http://www.vischeck.com
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSTestColorblindness extends PhetApplication {

    private static class TestColorKeyNode extends PComposite {

        public TestColorKeyNode() {
            AqueousSolution solution = new PureWaterSolution();
            Beaker beaker = new Beaker( solution, new Point2D.Double(), true, ABSConstants.BEAKER_SIZE );
            PHPaper paper = new PHPaper( solution, new Point2D.Double(), true, ABSConstants.PH_PAPER_SIZE, beaker );
            PHColorKeyNode colorKeyNode = new PHColorKeyNode( paper );
            colorKeyNode.scale( 0.75 );
            addChild( colorKeyNode );
        }
    }
    
    private static class TestMoleculeImageNode extends PComposite {
        
        public TestMoleculeImageNode( Image image, String name ) {
            
            PImage imageNode = new PImage( image );
            addChild( imageNode );
            
            HTMLNode htmlNode = new HTMLNode( name );
            htmlNode.setFont( new PhetFont( 12 ) );
            addChild( htmlNode );
            
            // name to right of image
            imageNode.setOffset( 0, 0 );
            htmlNode.setOffset( imageNode.getFullBoundsReference().getMaxX() + 3, imageNode.getFullBoundsReference().getMinY() );
        }
    }
    
    private static class TestColorBarNode extends PComposite {
        
        private static final Dimension BAR_SIZE = new Dimension( 100, 25 );

        public TestColorBarNode( Color color, String name ) {
            
            PPath pathNode = new PPath( new Rectangle2D.Double( 0, 0, BAR_SIZE.width, BAR_SIZE.height ) );
            pathNode.setPaint( color );
            pathNode.setStroke( new BasicStroke( 1f ) );
            pathNode.setStrokePaint( Color.BLACK );
            addChild( pathNode );
            
            HTMLNode htmlNode = new HTMLNode( name );
            htmlNode.setFont( new PhetFont( 12 ) );
            addChild( htmlNode );
            
            // name to right of color bar
            pathNode.setOffset( 0, 0 );
            htmlNode.setOffset( pathNode.getFullBoundsReference().getMaxX() + 3, pathNode.getFullBoundsReference().getMinY() );
        }
    }
    
    private static class TestCanvas extends PhetPCanvas {

        public TestCanvas() {
            
            // pH color key
            PNode colorKeyNode = new TestColorKeyNode();
            addChild( colorKeyNode );
            colorKeyNode.setOffset( 50, 10 );
            
            // molecule images
            {
                double xOffset = colorKeyNode.getXOffset();
                double yOffset = colorKeyNode.getFullBoundsReference().getMaxY() + 10;
                double yDelta = 50;
                
                addMoleculeImage( ABSImages.A_MINUS_MOLECULE, ABSSymbols.A_MINUS, xOffset, yOffset );
                yOffset += yDelta;
                addMoleculeImage( ABSImages.B_MOLECULE, ABSSymbols.B, xOffset, yOffset );
                yOffset += yDelta;
                addMoleculeImage( ABSImages.BH_PLUS_MOLECULE, ABSSymbols.BH_PLUS, xOffset, yOffset );
                yOffset += yDelta;
                addMoleculeImage( ABSImages.H2O_MOLECULE, ABSSymbols.H2O, xOffset, yOffset );
                yOffset += yDelta;
                addMoleculeImage( ABSImages.H3O_PLUS_MOLECULE, ABSSymbols.H3O_PLUS, xOffset, yOffset );
                yOffset += yDelta;
                addMoleculeImage( ABSImages.HA_MOLECULE, ABSSymbols.HA, xOffset, yOffset );
                yOffset += yDelta;
                addMoleculeImage( ABSImages.M_PLUS_MOLECULE, ABSSymbols.M_PLUS, xOffset, yOffset );
                yOffset += yDelta;
                addMoleculeImage( ABSImages.MOH_MOLECULE, ABSSymbols.MOH, xOffset, yOffset );
                yOffset += yDelta;
                addMoleculeImage( ABSImages.OH_MINUS_MOLECULE, ABSSymbols.OH_MINUS, xOffset, yOffset );
                yOffset += yDelta;
            }
            
            // colors
            {
                double xOffset = 150;
                double yOffset = colorKeyNode.getFullBoundsReference().getMaxY() + 10;
                double yDelta = TestColorBarNode.BAR_SIZE.getHeight() + 25;
                
                addColorBar( ABSColors.A_MINUS, ABSSymbols.A_MINUS, xOffset, yOffset );
                yOffset += yDelta;
                addColorBar( ABSColors.B, ABSSymbols.B, xOffset, yOffset );
                yOffset += yDelta;
                addColorBar( ABSColors.BH_PLUS, ABSSymbols.BH_PLUS, xOffset, yOffset );
                yOffset += yDelta;
                addColorBar( ABSColors.H2O, ABSSymbols.H2O, xOffset, yOffset );
                yOffset += yDelta;
                addColorBar( ABSColors.H3O_PLUS, ABSSymbols.H3O_PLUS, xOffset, yOffset );
                yOffset += yDelta;
                addColorBar( ABSColors.HA, ABSSymbols.HA, xOffset, yOffset );
                yOffset += yDelta;
                addColorBar( ABSColors.M_PLUS, ABSSymbols.M_PLUS, xOffset, yOffset );
                yOffset += yDelta;
                addColorBar( ABSColors.MOH, ABSSymbols.MOH, xOffset, yOffset );
                yOffset += yDelta;
                addColorBar( ABSColors.OH_MINUS, ABSSymbols.OH_MINUS, xOffset, yOffset );
                yOffset += yDelta;
                addColorBar( ABSColors.CANVAS_BACKGROUND, "canvas background", xOffset, yOffset );
                yOffset += yDelta;
                addColorBar( ABSColors.CONTROL_PANEL_BACKGROUND, "control panel background", xOffset, yOffset );
                yOffset += yDelta;
            }
        }
        
        private void addChild( PNode child ) {
            getLayer().addChild( child );
        }
        
        private void addMoleculeImage( Image image, String name, double xOffset, double yOffset ) {
            PNode node = new TestMoleculeImageNode( image, name );
            addChild( node );
            node.setOffset( xOffset, yOffset );
        }
        
        private void addColorBar( Color color, String name, double xOffset, double yOffset ) {
            PNode node = new TestColorBarNode( color, name );
            addChild( node );
            node.setOffset( xOffset, yOffset );
        }
    }
    
    private static class TestModule extends PiccoloModule {
        public TestModule() {
            super( "TestModule", new ConstantDtClock( 40, 1 ), true /* startsPaused */ );
            setClockControlPanel( null );
            setControlPanel( null );
            setLogoPanelVisible( false );
            setSimulationPanel( new TestCanvas() );
        }
    }

    public ABSTestColorblindness( PhetApplicationConfig config ) {
        super( config );
        addModule( new TestModule() );
        getPhetFrame().setTitle( "Acid-Base Solution colorblind test" );
    }
    
    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, ABSConstants.PROJECT, ABSTestColorblindness.class );
    }
}
