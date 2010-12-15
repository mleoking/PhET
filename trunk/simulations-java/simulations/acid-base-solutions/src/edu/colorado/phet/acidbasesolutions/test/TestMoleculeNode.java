/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.test;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.constants.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.view.molecules.*;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Test to see if we can programmatically create molecules that look as good as image files.
 * If we can, this would simplify the process of changing molecule colors.
 * Changing the image files is a huge hassle.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestMoleculeNode extends JFrame {
    
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
