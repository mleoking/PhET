/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.phasechanges;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

// TODO: JPB TBD - Make the labels into translatable strings.

/**
 * This class displays a phase diagram suitable for inclusion on the control
 * panel of a PhET simulation.
 *
 * @author John Blanco
 */
public class PhaseDiagram extends PhetPCanvas {

    // Constants that control the size of the canvas.
    public static final int WIDTH = 200;
    public static final int HEIGHT = (int)((double)WIDTH * 0.8);
    
    // Constants that control the look of the axes.
    public static final double AXES_LINE_WIDTH = 1;
    public static final double AXES_ARROW_HEAD_WIDTH = 5 * AXES_LINE_WIDTH;
    public static final double AXES_ARROW_HEAD_HEIGHT = 8 * AXES_LINE_WIDTH;
    public static final double HORIZ_AXIS_SIZE_PROPORTION = 0.8;
    public static final double VERT_AXIS_SIZE_PROPORTION = 0.80;
    
    // Constants that control the location of the origin for the graph.
    public static final double xOriginOffset = 0.10 * (double)WIDTH;
    public static final double yOriginOffset = 0.85 * (double)HEIGHT;
    public static final double xUsableRange = WIDTH * HORIZ_AXIS_SIZE_PROPORTION - AXES_ARROW_HEAD_HEIGHT;
    public static final double yUsableRange = HEIGHT * VERT_AXIS_SIZE_PROPORTION - AXES_ARROW_HEAD_HEIGHT;
    
    // Font for the labels used on the axes.
    public static final int AXIS_LABEL_FONT_SIZE = 14;
    public static final Font AXIS_LABEL_FONT = new PhetFont(AXIS_LABEL_FONT_SIZE);
    
    // Fonts for labels in the interior of the diagram.
    public static final int LARGER_INNER_FONT_SIZE = 14;
    public static final Font LARGER_INNER_FONT = new PhetFont(LARGER_INNER_FONT_SIZE);
    public static final int SMALLER_INNER_FONT_SIZE = 12;
    public static final Font SMALLER_INNER_FONT = new PhetFont(SMALLER_INNER_FONT_SIZE);
    
    // Colors for the various sections of the diagram.
    public static final Color BACKGROUND_COLOR_FOR_SOLID = new Color(0xC6BDD6);
    public static final Color BACKGROUND_COLOR_FOR_LIQUID = new Color(0xFFFFCC);
    public static final Color BACKGROUND_COLOR_FOR_GAS = new Color(0xCEE5CE);
    
    // Constants that control the appearance of the phase diagram for the
    // various substances.  Note that all points are controlled as proportions
    // of the overall graph size and not as absolute values.
    public static final double POINT_MARKER_DIAMETER = 4;
    public static final Point2D DEFAULT_TOP_OF_SOLID_LIQUID_CURVE = new Point2D.Double(xUsableRange/2 + xOriginOffset, 
            yOriginOffset - yUsableRange);
    public static final Point2D DEFAULT_TRIPLE_POINT = new Point2D.Double(xOriginOffset + (xUsableRange * 0.32), 
            yOriginOffset - (yUsableRange * 0.2));
    public static final Point2D DEFAULT_CRITICAL_POINT = new Point2D.Double(xOriginOffset + (xUsableRange * 0.8), 
            yOriginOffset - (yUsableRange * 0.45));
    public static final Point2D DEFAULT_SOLID_LABEL_LOCATION = new Point2D.Double(xOriginOffset + (xUsableRange * 0.2), 
            yOriginOffset - (yUsableRange * 0.6));
    public static final Point2D DEFAULT_LIQUID_LABEL_LOCATION = new Point2D.Double(xOriginOffset + (xUsableRange * 0.7), 
            yOriginOffset - (yUsableRange * 0.7));
    public static final Point2D DEFAULT_GAS_LABEL_LOCATION = new Point2D.Double(xOriginOffset + (xUsableRange * 0.7), 
            yOriginOffset - (yUsableRange * 0.1));
    
    // Variables that define the appearance of the phase diagram.
    private PPath m_triplePoint;
    private PPath m_criticalPoint;
    private PPath m_solidLiquidLine;
    private PPath m_solidAreaBackground;
    private PPath m_liquidGasLine;
    private PPath m_liquidAreaBackground;
    private PText m_solidLabel;
    private PText m_liquidLabel;
    private PText m_gasLabel;
    private PPath m_gasAreaBackground;
    private PPath m_superCriticalAreaBackground;
    private PText m_triplePointLabel1;
    private PText m_triplePointLabel2;
    private PText m_criticalPointLabel1;
    private PText m_criticalPointLabel2;
    
    /**
     * Constructor.
     */
    public PhaseDiagram(){

        setPreferredSize( new Dimension(WIDTH, HEIGHT) );

        // Initialize the variables for the lines, points, and shapes in the
        // phase diagram.  The order in which these are added is important.
        m_gasAreaBackground = new PPath();
        m_gasAreaBackground.setPaint( BACKGROUND_COLOR_FOR_GAS );
        m_gasAreaBackground.setStrokePaint( BACKGROUND_COLOR_FOR_GAS );
        addWorldChild( m_gasAreaBackground );
        m_superCriticalAreaBackground = new PPath();
        m_superCriticalAreaBackground.setPaint( getSuperCriticalRegionPaint() );
        m_superCriticalAreaBackground.setStrokePaint( getSuperCriticalRegionPaint() );
        addWorldChild( m_superCriticalAreaBackground );
        m_liquidAreaBackground = new PPath();
        m_liquidAreaBackground.setPaint( BACKGROUND_COLOR_FOR_LIQUID );
        m_liquidAreaBackground.setStrokePaint( BACKGROUND_COLOR_FOR_LIQUID );
        addWorldChild( m_liquidAreaBackground );
        m_solidAreaBackground = new PPath();
        m_solidAreaBackground.setPaint( BACKGROUND_COLOR_FOR_SOLID );
        m_solidAreaBackground.setStrokePaint( BACKGROUND_COLOR_FOR_SOLID );
        addWorldChild( m_solidAreaBackground );
        m_solidLiquidLine = new PPath();
        addWorldChild( m_solidLiquidLine );
        m_liquidGasLine = new PPath();
        addWorldChild( m_liquidGasLine );
        m_triplePoint = new PPath(new Ellipse2D.Double(0, 0, POINT_MARKER_DIAMETER, POINT_MARKER_DIAMETER));
        m_triplePoint.setPaint( Color.BLACK );
        addWorldChild( m_triplePoint );
        m_criticalPoint = new PPath(new Ellipse2D.Double(0, 0, POINT_MARKER_DIAMETER, POINT_MARKER_DIAMETER));
        m_criticalPoint.setPaint( Color.BLACK );
        addWorldChild( m_criticalPoint );
        
        // Create the labels that will exist inside the phase diagram.
        m_solidLabel = new PText("solid");
        m_solidLabel.setFont( LARGER_INNER_FONT );
        addWorldChild( m_solidLabel );
        m_liquidLabel = new PText("liquid");
        m_liquidLabel.setFont( LARGER_INNER_FONT );
        addWorldChild( m_liquidLabel );
        m_gasLabel = new PText("gas");
        m_gasLabel.setFont( LARGER_INNER_FONT );
        addWorldChild( m_gasLabel );
        m_triplePointLabel1 = new PText("triple");
        m_triplePointLabel1.setFont( SMALLER_INNER_FONT );
        addWorldChild( m_triplePointLabel1 );
        m_triplePointLabel2 = new PText("point");
        m_triplePointLabel2.setFont( SMALLER_INNER_FONT );
        addWorldChild( m_triplePointLabel2 );
        m_criticalPointLabel1 = new PText("critical");
        m_criticalPointLabel1.setFont( SMALLER_INNER_FONT );
        addWorldChild( m_criticalPointLabel1 );
        m_criticalPointLabel2 = new PText("point");
        m_criticalPointLabel2.setFont( SMALLER_INNER_FONT );
        addWorldChild( m_criticalPointLabel2 );
        
        // Create and add the axes for the graph.
        
        ArrowNode horizontalAxis = new ArrowNode( new Point2D.Double(xOriginOffset, yOriginOffset), 
                new Point2D.Double(xOriginOffset + (HORIZ_AXIS_SIZE_PROPORTION * WIDTH), yOriginOffset), 
                AXES_ARROW_HEAD_HEIGHT, AXES_ARROW_HEAD_WIDTH, AXES_LINE_WIDTH );
        horizontalAxis.setPaint( Color.BLACK );
        addWorldChild( horizontalAxis );
        
        ArrowNode verticalAxis = new ArrowNode( new Point2D.Double(xOriginOffset, yOriginOffset), 
                new Point2D.Double(xOriginOffset, yOriginOffset - VERT_AXIS_SIZE_PROPORTION * HEIGHT), 
                AXES_ARROW_HEAD_HEIGHT, AXES_ARROW_HEAD_WIDTH, AXES_LINE_WIDTH );
        verticalAxis.setPaint( Color.BLACK );
        addWorldChild( verticalAxis );
        
        // Create and add the labels for the axes.
        // TODO: JPB TBD - Make these into translatable strings if kept.
        PText horizontalAxisLabel = new PText("Temperature");
        horizontalAxisLabel.setOffset( WIDTH - (horizontalAxisLabel.getFullBoundsReference().width * 1.1), 
                yOriginOffset + horizontalAxisLabel.getFullBoundsReference().height * 0.3);
        addWorldChild( horizontalAxisLabel );
        
        PText horizontalOriginLabel = new PText("0 K");
        horizontalOriginLabel.setOffset( xOriginOffset - horizontalOriginLabel.getFullBoundsReference().width * 0.3, 
                yOriginOffset + horizontalOriginLabel.getFullBoundsReference().height * 0.3);
        addWorldChild( horizontalOriginLabel );
        
        PText verticalAxisLabel = new PText("Pressure");
        verticalAxisLabel.setOffset( xOriginOffset - (verticalAxisLabel.getFullBoundsReference().height * 1.1),
                verticalAxisLabel.getFullBoundsReference().width * 1.6);
        verticalAxisLabel.rotate( 3 * Math.PI / 2 );
        addWorldChild( verticalAxisLabel );
        
        PText verticalAxisOriginLabel = new PText("0");
        verticalAxisOriginLabel.setOffset( 
                xOriginOffset - (verticalAxisOriginLabel.getFullBoundsReference().height * 1.1), yOriginOffset);
        verticalAxisOriginLabel.rotate( 3 * Math.PI / 2 );
        addWorldChild( verticalAxisOriginLabel );
        
        // Draw the initial phase diagram.
        drawPhaseDiagram( 0 );
    }
    
    private void drawPhaseDiagram(int substance){
        
        // Place the triple point marker.
        m_triplePoint.setOffset( DEFAULT_TRIPLE_POINT.getX() - POINT_MARKER_DIAMETER / 2, 
                DEFAULT_TRIPLE_POINT.getY() - POINT_MARKER_DIAMETER / 2 );
        
        // Add the curve that separates solid and liquid.
        QuadCurve2D solidLiquidCurve = new QuadCurve2D.Double(xOriginOffset, yOriginOffset, 
                (xOriginOffset + xUsableRange) * 0.5, yOriginOffset, DEFAULT_TOP_OF_SOLID_LIQUID_CURVE.getX(),
                DEFAULT_TOP_OF_SOLID_LIQUID_CURVE.getY() );
        
        m_solidLiquidLine.setPathTo( solidLiquidCurve );
        
        // Update the shape of the background for the area that represents the solid phase.
        GeneralPath solidBackground = new GeneralPath(solidLiquidCurve);
        solidBackground.lineTo( (float)xOriginOffset, (float)DEFAULT_TOP_OF_SOLID_LIQUID_CURVE.getY() );
        solidBackground.lineTo( (float)xOriginOffset, (float)yOriginOffset );
        solidBackground.closePath();
        m_solidAreaBackground.setPathTo( solidBackground );

        // Place the critical point marker.
        m_criticalPoint.setOffset( DEFAULT_CRITICAL_POINT.getX() - POINT_MARKER_DIAMETER / 2, 
                DEFAULT_CRITICAL_POINT.getY() - POINT_MARKER_DIAMETER / 2 );

        // Add the curve that separates liquid and gas.
        double controlCurveXPos = DEFAULT_TRIPLE_POINT.getX() + 
            ((DEFAULT_CRITICAL_POINT.getX() - DEFAULT_TRIPLE_POINT.getX()) / 2);
        double controlCurveYPos = DEFAULT_TRIPLE_POINT.getY();
        QuadCurve2D liquidGasCurve = new QuadCurve2D.Double( DEFAULT_TRIPLE_POINT.getX(), DEFAULT_TRIPLE_POINT.getY(),
                controlCurveXPos, controlCurveYPos, DEFAULT_CRITICAL_POINT.getX(), DEFAULT_CRITICAL_POINT.getY() );

        m_liquidGasLine.setPathTo( liquidGasCurve );
        
        // Update the shape of the background for the area that represents the
        // liquid phase.  It is expected that the solid shape overlays this one.
        GeneralPath liquidBackground = new GeneralPath( liquidGasCurve );
        liquidBackground.lineTo( (float)(xOriginOffset + xUsableRange), (float)(yOriginOffset - yUsableRange));
        liquidBackground.lineTo( (float)(DEFAULT_TOP_OF_SOLID_LIQUID_CURVE.getX()),
                (float)(yOriginOffset - yUsableRange));
        liquidBackground.lineTo( (float)(DEFAULT_TRIPLE_POINT.getX()), (float)(DEFAULT_TRIPLE_POINT.getY()) );
        liquidBackground.append( liquidGasCurve, true );
        liquidBackground.closePath();
        m_liquidAreaBackground.setPathTo( liquidBackground );

        // Update the shape of the background for the area that represents the
        // liquid phase.  It is expected that the liquid shape overlays this one.
        GeneralPath gasBackground = new GeneralPath();
        gasBackground.moveTo( (float)xOriginOffset, (float)yOriginOffset );
        gasBackground.lineTo( (float)(DEFAULT_TRIPLE_POINT.getX()), (float)(DEFAULT_TRIPLE_POINT.getY()));
        gasBackground.lineTo( (float)(DEFAULT_CRITICAL_POINT.getX()), (float)(DEFAULT_CRITICAL_POINT.getY()));
        gasBackground.lineTo( (float)(xOriginOffset + xUsableRange), (float)(yOriginOffset));
        gasBackground.lineTo( (float)xOriginOffset, (float)yOriginOffset );
        gasBackground.closePath();
        m_gasAreaBackground.setPathTo( gasBackground );

        // Update the shape of the background for the area that represents the
        // liquid phase.  It is expected that the liquid shape overlays this one.
        GeneralPath superCriticalBackground = new GeneralPath();
        superCriticalBackground.moveTo( (float)(DEFAULT_CRITICAL_POINT.getX()), (float)(DEFAULT_CRITICAL_POINT.getY()));
        superCriticalBackground.lineTo( (float)(xOriginOffset + xUsableRange), (float)(yOriginOffset));
        superCriticalBackground.lineTo( (float)(xOriginOffset + xUsableRange), (float)(yOriginOffset - yUsableRange));
        superCriticalBackground.lineTo( (float)(DEFAULT_CRITICAL_POINT.getX()), (float)(DEFAULT_CRITICAL_POINT.getY()));
        superCriticalBackground.closePath();
        m_superCriticalAreaBackground.setPathTo( superCriticalBackground );

        // Locate the labels.  They are centered on their locations, which
        // hopefully will work better for translated strings.
        m_solidLabel.setOffset( DEFAULT_SOLID_LABEL_LOCATION.getX() - m_solidLabel.getFullBoundsReference().width / 2,
                DEFAULT_SOLID_LABEL_LOCATION.getY() - m_solidLabel.getFullBoundsReference().height / 2);
        m_liquidLabel.setOffset( DEFAULT_LIQUID_LABEL_LOCATION.getX() - m_liquidLabel.getFullBoundsReference().width / 2,
                DEFAULT_LIQUID_LABEL_LOCATION.getY() - m_liquidLabel.getFullBoundsReference().height / 2);
        m_gasLabel.setOffset( DEFAULT_GAS_LABEL_LOCATION.getX() - m_gasLabel.getFullBoundsReference().width / 2,
                DEFAULT_GAS_LABEL_LOCATION.getY() - m_gasLabel.getFullBoundsReference().height / 2);
        m_triplePointLabel2.setOffset( DEFAULT_TRIPLE_POINT.getX() - m_triplePointLabel2.getFullBoundsReference().width * 1.2,
                DEFAULT_TRIPLE_POINT.getY() - m_triplePointLabel2.getFullBoundsReference().height * 0.5 );
        m_triplePointLabel1.setOffset( m_triplePointLabel2.getFullBoundsReference().x,
                m_triplePointLabel2.getFullBoundsReference().y - m_triplePointLabel2.getFullBoundsReference().height * 0.8);
        m_criticalPointLabel2.setOffset( DEFAULT_CRITICAL_POINT.getX() + 4, DEFAULT_CRITICAL_POINT.getY() );
        m_criticalPointLabel1.setOffset( m_criticalPointLabel2.getFullBoundsReference().x,
                m_criticalPointLabel2.getFullBoundsReference().y - m_criticalPointLabel2.getFullBoundsReference().height * 0.8);
    }
    
    /**
     * Create a paint that is a good transition between the color in the gaseous
     * region and the color in the liquid region.
     */
    private Paint getSuperCriticalRegionPaint(){
        
        
//        int red, green, blue;
//        red = (color1.getRed() + color2.getRed()) / 2;
//        green = (color1.getGreen() + color2.getGreen()) / 2;
//        blue = (color1.getBlue() + color2.getBlue()) / 2;
//        return(new Color(red, green, blue));
        Point2D top = new Point2D.Double(xOriginOffset + (0.9 * xUsableRange), yOriginOffset - (yUsableRange * 0.9));
        Point2D bottom = new Point2D.Double(xOriginOffset + (0.9 * xUsableRange), yOriginOffset + (yUsableRange * 0.1));
        return new GradientPaint(bottom, BACKGROUND_COLOR_FOR_GAS, top, BACKGROUND_COLOR_FOR_LIQUID);
    }
}
