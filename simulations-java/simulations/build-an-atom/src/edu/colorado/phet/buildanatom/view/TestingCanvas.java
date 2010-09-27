/* Copyright 2007, University of Colorado */

package edu.colorado.phet.buildanatom.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.module.BuildAnAtomDefaults;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * This canvas is associated with the Testing module, which is used for trying
 * out new visual layouts and such.  This will not be visible in the end
 * product, and at the end of the development cycle it should be deleted.
 */
public class TestingCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
    // Sizes of the inner and outer shell.
    private static final double SMALL_SHELL_RADIUS = 100;
    private static final double LARGE_SHELL_RADIUS = 250;
    
    // Color Test Set 01 - Crude example to give our team an idea of the shapes.
//    private static final Color BACKGROUND_COLOR = Color.BLACK;
//    private static final Color INNER_SHELL_BASE_COLOR = Color.yellow;
//    private static final Color INNER_SHELL_FADE_TO_COLOR = Color.blue;
//    private static final Color OUTER_SHELL_BASE_COLOR = Color.yellow;
//    private static final Color OUTER_SHELL_FADE_TO_COLOR = Color.blue;
    
    // Color Test Set 02 - Black and white - very basic
//    private static final Color BACKGROUND_COLOR = Color.black;
//    private static final Color INNER_SHELL_BASE_COLOR = Color.WHITE;
//    private static final Color INNER_SHELL_FADE_TO_COLOR = BACKGROUND_COLOR;
//    private static final Color OUTER_SHELL_BASE_COLOR = Color.WHITE;
//    private static final Color OUTER_SHELL_FADE_TO_COLOR = BACKGROUND_COLOR;
    
    // Color Test Set 03 - Blue background with gray atoms
    private static final Color BACKGROUND_COLOR = new Color( 185, 205, 229 );
    private static final Color INNER_SHELL_BASE_COLOR = Color.DARK_GRAY;
    private static final Color INNER_SHELL_FADE_TO_COLOR = BACKGROUND_COLOR;
    private static final Color OUTER_SHELL_BASE_COLOR = Color.DARK_GRAY;
    private static final Color OUTER_SHELL_FADE_TO_COLOR = BACKGROUND_COLOR;
    
    // Color Test Set 04 - Blue background with darker colored atoms of contrary color.
//    private static final Color BACKGROUND_COLOR = new Color( 100, 149, 237 );
//    private static final Color INNER_SHELL_BASE_COLOR = new Color( 200, 69, 0 );
//    private static final Color INNER_SHELL_FADE_TO_COLOR = BACKGROUND_COLOR;
//    private static final Color OUTER_SHELL_BASE_COLOR = new Color( 200, 69, 0 );
//    private static final Color OUTER_SHELL_FADE_TO_COLOR = BACKGROUND_COLOR;
    
    private static final Color INNER_SHELL_BASE_COLOR_1_ELECTRON = new Color(
            INNER_SHELL_BASE_COLOR.getRed(), 
            INNER_SHELL_BASE_COLOR.getGreen(), 
            INNER_SHELL_BASE_COLOR.getBlue(),
            100);
    private static final Color INNER_SHELL_FADE_TO_COLOR_1_ELECTRON = new Color(
            INNER_SHELL_FADE_TO_COLOR.getRed(), 
            INNER_SHELL_FADE_TO_COLOR.getGreen(), 
            INNER_SHELL_FADE_TO_COLOR.getBlue(),
            50);
    private static final Color INNER_SHELL_BASE_COLOR_2_ELECTRONS = new Color(
            INNER_SHELL_BASE_COLOR.getRed(), 
            INNER_SHELL_BASE_COLOR.getGreen(), 
            INNER_SHELL_BASE_COLOR.getBlue(),
            200);
    private static final Color INNER_SHELL_FADE_TO_COLOR_2_ELECTRONS = new Color(
            INNER_SHELL_FADE_TO_COLOR.getRed(), 
            INNER_SHELL_FADE_TO_COLOR.getGreen(), 
            INNER_SHELL_FADE_TO_COLOR.getBlue(),
            100);
    private static final Color OUTER_SHELL_BASE_COLOR_1_ELECTRON = new Color(
            OUTER_SHELL_BASE_COLOR.getRed(), 
            OUTER_SHELL_BASE_COLOR.getGreen(), 
            OUTER_SHELL_BASE_COLOR.getBlue(),
            100);
    private static final Color OUTER_SHELL_FADE_TO_COLOR_1_ELECTRON = new Color(
            OUTER_SHELL_FADE_TO_COLOR.getRed(), 
            OUTER_SHELL_FADE_TO_COLOR.getGreen(), 
            OUTER_SHELL_FADE_TO_COLOR.getBlue(),
            50);
    private static final Color OUTER_SHELL_BASE_COLOR_4_ELECTRONS = new Color(
            OUTER_SHELL_BASE_COLOR.getRed(), 
            OUTER_SHELL_BASE_COLOR.getGreen(), 
            OUTER_SHELL_BASE_COLOR.getBlue(),
            200);
    private static final Color OUTER_SHELL_FADE_TO_COLOR_4_ELECTRONS = new Color(
            OUTER_SHELL_FADE_TO_COLOR.getRed(), 
            OUTER_SHELL_FADE_TO_COLOR.getGreen(), 
            OUTER_SHELL_FADE_TO_COLOR.getBlue(),
            100);
    private static final Color OUTER_SHELL_BASE_COLOR_8_ELECTRONS = new Color(
            OUTER_SHELL_BASE_COLOR.getRed(), 
            OUTER_SHELL_BASE_COLOR.getGreen(), 
            OUTER_SHELL_BASE_COLOR.getBlue(),
            255);
    private static final Color OUTER_SHELL_FADE_TO_COLOR_8_ELECTRONS = new Color(
            OUTER_SHELL_FADE_TO_COLOR.getRed(), 
            OUTER_SHELL_FADE_TO_COLOR.getGreen(), 
            OUTER_SHELL_FADE_TO_COLOR.getBlue(),
            200);
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // Model
    private BuildAnAtomModel model;
    
    // View 
    private PNode rootNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public TestingCanvas( BuildAnAtomModel model ) {
        super( BuildAnAtomDefaults.VIEW_SIZE );
        
        this.model = model;
        
        setBackground( BACKGROUND_COLOR );
        
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );
        
        // Create the gradients.  Note that making the radius a bit smaller
        // than the shape of the circle on which it will be used creates a
        // fuzzier-looking edge, which is what we are after.
        Paint innerShell1ElectronPaint = new RoundGradientPaint( 0, 0, INNER_SHELL_BASE_COLOR_1_ELECTRON,
                new Point2D.Double( SMALL_SHELL_RADIUS * 0.75, SMALL_SHELL_RADIUS * 0.75), INNER_SHELL_FADE_TO_COLOR_1_ELECTRON );
        Paint innerShell2ElectronsPaint = new RoundGradientPaint( 0, 0, INNER_SHELL_BASE_COLOR_2_ELECTRONS,
                new Point2D.Double( SMALL_SHELL_RADIUS * 0.75, SMALL_SHELL_RADIUS * 0.75), INNER_SHELL_FADE_TO_COLOR_2_ELECTRONS );
        Paint outerShell1ElectronPaint = new RoundGradientPaint( 0, 0, OUTER_SHELL_BASE_COLOR_1_ELECTRON,
                new Point2D.Double( LARGE_SHELL_RADIUS * 0.75, LARGE_SHELL_RADIUS * 0.75), OUTER_SHELL_FADE_TO_COLOR_1_ELECTRON );
        Paint outerShell4ElectronsPaint = new RoundGradientPaint( 0, 0, OUTER_SHELL_BASE_COLOR_4_ELECTRONS,
                new Point2D.Double( LARGE_SHELL_RADIUS * 0.75, LARGE_SHELL_RADIUS * 0.75), OUTER_SHELL_FADE_TO_COLOR_4_ELECTRONS );
        Paint outerShell8ElectronsPaint = new RoundGradientPaint( 0, 0, OUTER_SHELL_BASE_COLOR_8_ELECTRONS,
                new Point2D.Double( LARGE_SHELL_RADIUS * 0.75, LARGE_SHELL_RADIUS * 0.75), OUTER_SHELL_FADE_TO_COLOR_8_ELECTRONS );
        
        // Create the shapes of the circles that will be used to define the
        // shapes of the inner and outer shells.
        Ellipse2D innerShellShape = new Ellipse2D.Double( -SMALL_SHELL_RADIUS, -SMALL_SHELL_RADIUS,
                SMALL_SHELL_RADIUS * 2, SMALL_SHELL_RADIUS * 2 );
        Ellipse2D outerShellShape = new Ellipse2D.Double( -LARGE_SHELL_RADIUS, -LARGE_SHELL_RADIUS,
                LARGE_SHELL_RADIUS * 2, LARGE_SHELL_RADIUS * 2 );
        
        // Add a ball for the inner shell with 1 electron.
        PNode innerShell1ElectronNode = new PhetPPath( innerShellShape, innerShell1ElectronPaint );
        innerShell1ElectronNode.setOffset( 300, 250 );
        rootNode.addChild( innerShell1ElectronNode );
        
        // Add a ball for the inner shell with 2 electrons.
        PNode innerShell2ElectronsNode = new PhetPPath( innerShellShape, innerShell2ElectronsPaint );
        innerShell2ElectronsNode.setOffset( 900, 250 );
        rootNode.addChild( innerShell2ElectronsNode );
        
        // Add a rectangle that can be used to examine the gradient.
//        Rectangle2D gradientTestRectShape = new Rectangle2D.Double(-LARGE_SHELL_RADIUS, -LARGE_SHELL_RADIUS,
//                LARGE_SHELL_RADIUS * 2, LARGE_SHELL_RADIUS * 2);
//        PNode gradientTestRectNode = new PhetPPath( gradientTestRectShape, innerShell1ElectronPaint );
//        gradientTestRectNode.setOffset( 1600, 250 );
//        rootNode.addChild( gradientTestRectNode );
        
        // Add a representation of a full inner shell with an outer shell that
        // contains 1 electron.
        PNode innerShell2ElectronsNode02 = new PhetPPath( innerShellShape, innerShell2ElectronsPaint );
        innerShell2ElectronsNode02.setOffset( 300, 800 );
        rootNode.addChild( innerShell2ElectronsNode02 );
        PNode outerShell1ElectronNode = new PhetPPath( outerShellShape, outerShell1ElectronPaint );
        outerShell1ElectronNode.setOffset( 300, 800 );
        rootNode.addChild( outerShell1ElectronNode );
        
        // Add a representation of a full inner shell with an outer shell that
        // contains 4 electrons.
        PNode innerShell2ElectronsNode03 = new PhetPPath( innerShellShape, innerShell2ElectronsPaint );
        innerShell2ElectronsNode03.setOffset( 900, 800 );
        rootNode.addChild( innerShell2ElectronsNode03 );
        PNode outerShell4ElectronsNode = new PhetPPath( outerShellShape, outerShell4ElectronsPaint );
        outerShell4ElectronsNode.setOffset( 900, 800 );
        rootNode.addChild( outerShell4ElectronsNode );
        
        // Add a representation of a full inner shell with an outer shell that
        // contains 8 electrons.
        PNode innerShell2ElectronsNode04 = new PhetPPath( innerShellShape, innerShell2ElectronsPaint );
        innerShell2ElectronsNode04.setOffset( 1500, 800 );
        rootNode.addChild( innerShell2ElectronsNode04 );
        PNode outerShell8ElectronsNode = new PhetPPath( outerShellShape, outerShell8ElectronsPaint );
        outerShell8ElectronsNode.setOffset( 1500, 800 );
        rootNode.addChild( outerShell8ElectronsNode );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout of stuff on the canvas.
     */
    @Override
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( BuildAnAtomConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }
        
        //XXX lay out nodes
    }
}
