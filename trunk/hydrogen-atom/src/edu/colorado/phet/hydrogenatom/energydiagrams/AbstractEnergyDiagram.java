/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.energydiagrams;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Observer;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * AbstractEnergyDiagram is the base class for all energy diagrams.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractEnergyDiagram extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final String FONT_NAME = HAConstants.DEFAULT_FONT_NAME;
    private static final int FONT_STYLE = HAConstants.DEFAULT_FONT_STYLE;
    private static final int DEFAULT_FONT_SIZE = HAConstants.DEFAULT_FONT_SIZE;
    private static final String FONT_SIZE_RESOURCE = "energyDiagram.font.size";
    
    private static final Dimension DIAGRAM_SIZE = new Dimension( 250, 420 );
    private static final double X_MARGIN = 5;
    private static final double Y_MARGIN = 10;
    private static final Dimension ARROW_SIZE = new Dimension( 13, 13 );
    private static final float AXIS_STROKE_WIDTH = 2;
    
    private static final Color BACKGROUND_COLOR = new Color( 240, 240, 240 );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Color AXIS_STROKE_COLOR = Color.BLACK;
    private static final Color ARROW_COLOR = Color.BLACK;
    private static final Color AXIS_LABEL_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractHydrogenAtom _atom;
    private ElectronNode _electronNode;
    private Rectangle2D _drawingArea; // area that we can draw in
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractEnergyDiagram() {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        // Fonts
        int fontSize = SimStrings.getInt( FONT_SIZE_RESOURCE, DEFAULT_FONT_SIZE );
        Font font = new Font( FONT_NAME, FONT_STYLE, fontSize );
        
        // Background
        PClip backgroundNode = new PClip();
        backgroundNode.setPathTo( new Rectangle2D.Double( 0, 0, DIAGRAM_SIZE.width, DIAGRAM_SIZE.height ) );
        backgroundNode.setPaint( BACKGROUND_COLOR );
        backgroundNode.setStroke( new BasicStroke( 2f ) );
        backgroundNode.setStrokePaint( BACKGROUND_STROKE_COLOR );
        addChild( backgroundNode );
        
        // Y-axis
        PPath axisNode = new PPath();
        axisNode.setPathTo( new Line2D.Double( 0, ARROW_SIZE.height - 1, 0, DIAGRAM_SIZE.height - ( 2 * Y_MARGIN ) ) );
        axisNode.setStroke( new BasicStroke( AXIS_STROKE_WIDTH ) );
        axisNode.setStrokePaint( AXIS_STROKE_COLOR );

        // Arrow head on y-axis
        PPath arrowNode = new PPath();
        GeneralPath arrowPath = new GeneralPath();
        arrowPath.moveTo( 0, 0 );
        arrowPath.lineTo( (float)( ARROW_SIZE.width / 2 ), (float)ARROW_SIZE.height );
        arrowPath.lineTo( (float)( -ARROW_SIZE.width / 2 ), (float)ARROW_SIZE.height );
        arrowPath.closePath();
        arrowNode.setPathTo( arrowPath );
        arrowNode.setPaint( ARROW_COLOR );
        arrowNode.setStroke( null );
        
        // Y-axis label
        PText axisLabelNode = new PText( SimStrings.get( "label.energyDiagram.yAxis" ) );
        axisLabelNode.setFont( font );
        axisLabelNode.setTextPaint( AXIS_LABEL_COLOR );
        axisLabelNode.rotate( Math.toRadians( -90 ) );
        
        // Electron
        _electronNode = new ElectronNode();

        // Layering
        backgroundNode.addChild( axisNode );
        backgroundNode.addChild( arrowNode );
        backgroundNode.addChild( axisLabelNode );
        backgroundNode.addChild( _electronNode );
        
        // Positions
        backgroundNode.setOffset( 0, 0 );
        PBounds bb = backgroundNode.getFullBounds();
        PBounds alb = axisLabelNode.getFullBounds();
        axisLabelNode.setOffset( 5, bb.getY() + bb.getHeight() - 10 );
        alb = axisLabelNode.getFullBounds();
        axisNode.setOffset( alb.getX() + alb.getWidth() + 5, Y_MARGIN );
        arrowNode.setOffset( axisNode.getFullBounds().getX() + ( AXIS_STROKE_WIDTH / 2.0 ), Y_MARGIN );
        
        // Determine the "safe" drawing area
        double x = arrowNode.getOffset().getX() + arrowNode.getWidth() + X_MARGIN;
        double y = 0;
        double w = backgroundNode.getWidth() - X_MARGIN - x;
        double h = backgroundNode.getHeight();
        _drawingArea = new Rectangle2D.Double( x, y, w, h );
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the atom that is associated with this diagram.
     * 
     * @return AbstractHydrogenAtom
     */
    protected AbstractHydrogenAtom getAtom() {
        return _atom;
    }
    
    /**
     * Sets the atom that is associated with this diagram.
     * If the diagram is visible, start observing the atom.
     * 
     * @param atom
     */
    public void setAtom( AbstractHydrogenAtom atom ) {
        clearAtom();
        if ( atom != null ) {
            _atom = atom;
            initElectronPosition();
            if ( isVisible() ) {
                _atom.addObserver( this );
            }
        }
    }
    
    /**
     * Removes the association between this diagram and any atom.
     */
    public void clearAtom() {
        if ( _atom != null ) {
            _atom.deleteObserver( this );
            _atom = null;
        }
    }
    
    /**
     * Gets the Piccolo node that represents the electron.
     * 
     * @return ElectronNode
     */
    protected ElectronNode getElectronNode() {
        return _electronNode;
    }
    
    /**
     * Get the bounds of the area that subclasses can draw stuff in.
     * 
     * @return Rectangle2D
     */
    protected Rectangle2D getDrawingArea() {
        return _drawingArea;
    }
    
    /**
     * Initializes the position of the electron.
     * This is called each time the diagram's atom is set.
     * The default implementation puts the electron near the upper-left 
     * corner of the "safe" drawing area.
     */
    protected void initElectronPosition() {
        _electronNode.setOffset( _drawingArea.getX(), _drawingArea.getY() + Y_MARGIN );
    }
    
    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------
    
    /**
     * Sets the visibility of this diagram.
     * While the diagram is invisible, it does not observer the atom.
     * 
     * @param visible
     */
    public void setVisible( boolean visible ) {
        if ( _atom != null ) {
            if ( visible ) {
                _atom.addObserver( this );
            }
            else {
                _atom.deleteObserver( this );
            }
        }
        super.setVisible( visible );
    }
}
