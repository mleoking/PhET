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

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;


public abstract class AbstractEnergyDiagram extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final String FONT_NAME = HAConstants.DEFAULT_FONT_NAME;
    private static final int FONT_STYLE = HAConstants.DEFAULT_FONT_STYLE;
    private static final int DEFAULT_FONT_SIZE = HAConstants.DEFAULT_FONT_SIZE;
    private static final String FONT_SIZE_RESOURCE = "energyDiagram.font.size";
    
    private static final Dimension DIAGRAM_SIZE = new Dimension( 250, 380 );
    private static final double MARGIN = 10;
    private static final Dimension ARROW_SIZE = new Dimension( 13, 13 );
    private static final float AXIS_STROKE_WIDTH = 2;
    
    private static final Color BACKGROUND_COLOR = new Color( 240, 240, 240 );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Color AXIS_STROKE_COLOR = Color.BLACK;
    private static final Color ARROW_COLOR = Color.BLACK;
    private static final Color AXIS_LABEL_COLOR = Color.BLACK;
    
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
        PPath backgroundNode = new PPath( new Rectangle2D.Double( 0, 0, DIAGRAM_SIZE.width, DIAGRAM_SIZE.height ) );
        backgroundNode.setPaint( BACKGROUND_COLOR );
        backgroundNode.setStroke( new BasicStroke( 2f ) );
        backgroundNode.setStrokePaint( BACKGROUND_STROKE_COLOR );
        
        // Y-axis
        PPath axisNode = new PPath();
        axisNode.setPathTo( new Line2D.Double( 0, ARROW_SIZE.height - 1, 0, DIAGRAM_SIZE.height - ( 2 * MARGIN ) ) );
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
        ElectronNode electronNode = new ElectronNode();

        addChild( backgroundNode );
        addChild( axisNode );
        addChild( arrowNode );
        addChild( axisLabelNode );
        addChild( electronNode );
        
        backgroundNode.setOffset( 0, 0 );
        PBounds bb = backgroundNode.getFullBounds();
        PBounds alb = axisLabelNode.getFullBounds();
        axisLabelNode.setOffset( 5, bb.getY() + bb.getHeight() - 10 );
        alb = axisLabelNode.getFullBounds();
        axisNode.setOffset( alb.getX() + alb.getWidth() + 5, MARGIN );
        arrowNode.setOffset( axisNode.getFullBounds().getX() + ( AXIS_STROKE_WIDTH / 2.0 ), MARGIN );
        electronNode.setOffset( bb.getX() + 50, bb.getMaxY() - 20 );
    }
}
