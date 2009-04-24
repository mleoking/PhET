/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the Single Nucleus Alpha Decay tab of this simulation.
 *
 * @author John Blanco
 */
public class RadioactiveDatingGameCanvas extends PhetPCanvas {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Canvas size.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 100;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
    
    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 0.5;   // 0 = all the way left, 1 = all the way right.
    private final double HEIGHT_TRANSLATION_FACTOR = 0.45; // 0 = all the way up, 1 = all the way down.
    
    // Constants that control where the charts are placed.
    private final double TIME_CHART_FRACTION = 0.2;   // Fraction of canvas for time chart.
    private final double ENERGY_CHART_FRACTION = 0.35; // Fraction of canvas for energy chart.
    
    // Other constants that affect the appearance of the chart.
    private final Color TUNNELING_MARKERS_COLOR = new Color(150, 0, 150);
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private RadioactiveDatingGameModel _model;
    private PNode _backgroundImageLayer;
    private PNode _backgroundImage;
    private PPath _screenSizeRect;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public RadioactiveDatingGameCanvas(RadioactiveDatingGameModel radioactiveDatingGameModel) {
        
        _model = radioactiveDatingGameModel;
        
        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );
        
        // Create the layer where the background will be placed.
        _backgroundImageLayer = new PNode();
        addScreenChild(_backgroundImageLayer);
        
        // Load the background image.
        BufferedImage bufferedImage = NuclearPhysicsResources.getImage( "dating-game-background.png" );
        _backgroundImage = new PImage( bufferedImage );
        _backgroundImageLayer.addChild( _backgroundImage );
        
        // TODO: Temp thing for getting sizes worked out.
        _screenSizeRect = new PPath( new Rectangle2D.Double( 0, 0, 20, 20 ) );
        _screenSizeRect.setStroke( new BasicStroke( 3 ) );
        _screenSizeRect.setStrokePaint( Color.RED );
        addScreenChild( _screenSizeRect );
    }

    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------

    protected void updateLayout(){
    	// Determine the overall size of the canvas.
    	_screenSizeRect.setPathToRectangle(0, 0, (float)(getWidth()*0.999), (float)(getHeight()*0.66));
    	
        // Reload and scale the background image.  This is necessary (I think)
    	// because PNodes can't be scaled differently in the x and y
    	// dimensions, and we want to be able to handle the case where the
    	// user changes the aspect ratio.
    	_backgroundImageLayer.removeChild( _backgroundImage );
    	BufferedImage bufferedImage = NuclearPhysicsResources.getImage( "dating-game-background.png" );
        double xScale = (double)getWidth() / (double)bufferedImage.getWidth();
        double yScale = (0.75) * (double)getHeight() / (double)bufferedImage.getHeight();
        bufferedImage = BufferedImageUtils.rescaleFractional(bufferedImage, xScale, yScale);
        _backgroundImage = new PImage( bufferedImage );
        _backgroundImageLayer.addChild( _backgroundImage );
    }
}