/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.idealgas.controller.command.AddModelElementCmd;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.util.DoubleArrowNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;


/**
 * This class displays a "strip chart" of the decay time for the nucleus,
 * and it also shows the time at which the decay occurred over multiple
 * trials.
 *
 * @author John Blanco
 */
public class AlphaRadiationTimeChart extends PComposite {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Constants for controlling the appearance of the chart.
    private static final Color   BORDER_COLOR = Color.DARK_GRAY;
    private static final float   BORDER_STROKE_WIDTH = 6f;
    private static final Stroke  BORDER_STROKE = new BasicStroke( BORDER_STROKE_WIDTH );
    private static final Color   BACKGROUND_COLOR = Color.WHITE;
    private static final double  AXES_LINE_WIDTH = 2f;
    private static final float   TIME_LINE_STROKE_WIDTH = 2f;
    private static final Stroke  TIME_LINE_STROKE = new BasicStroke( TIME_LINE_STROKE_WIDTH );
    private static final Color   TIME_LINE_COLOR_PRE_DECAY = NuclearPhysics2Constants.POLONIUM_LABEL_COLOR;
    private static final Color   TIME_LINE_COLOR_POST_DECAY = Color.BLUE;
    
    // Total amount of time in milliseconds represented by this chart.
    private static final double TIME_SPAN = 6000;
    
    // Constants that control the marker character.
    private static final String MARKER_CHAR = "*";
    private static final Font MARKER_CHAR_FONT = new PhetDefaultFont( Font.PLAIN, 20 );
    private static final Color MARKER_COLOR = Color.RED;
    
    // Max number of decays that we will keep track of.
    private static final int MAX_DECAYS = 100;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // References to the various components of the chart.
    private PPath _borderNode;
    private PPath _preDecayTimeLine;
    private PPath _postDecayTimeLine;
    private DoubleArrowNode _xAxisOfGraph;
    private DoubleArrowNode _yAxisOfGraph;
    private PText _yAxisLabel;
    private PText _xAxisLabel;
    
    // Variables used for positioning nodes within the graph.
    double _usableAreaOriginX;
    double _usableAreaOriginY;
    double _usableWidth;
    double _usableHeight;
    double _graphOriginX;
    double _graphOriginY;
    
    // Clock that we listen to for our charting.
    ConstantDtClock _clock;
    
    // Variables that control the origins and lengths of the time lines.
    Point2D _preDecayTimeLineOrigin;
    double  _preDecayTimeLineLength;   // In milliseconds
    Point2D _postDecayTimeLineOrigin;
    double  _postDecayTimeLineLength;  // In milliseconds
    
    // Var that tracks whether decay has occurred.
    boolean _decayHasOccurred = false;
    
    // List of decay times for each trial and their markers.
    double _decayTimes[] = new double [MAX_DECAYS];
    int _numDecays = 0;
    PText _markers[] = new PText [MAX_DECAYS];
    int _numMarkers = 0;
   
    // Factor for converting milliseconds to pixels.
    double _msToPixelsFactor = 1; // Arbitrary init val, updated later.

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AlphaRadiationTimeChart(ConstantDtClock clock, AtomicNucleus nucleus) {
        
        // Save the clock and register as a listener.
        _clock = clock;
        _clock.addClockListener( new ClockAdapter(){
            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked(ClockEvent clockEvent){
                advanceTimeLine(clockEvent);
            }
            
            public void simulationTimeReset(ClockEvent clockEvent){
                resetTimeLine();
            }
        });
        
        // Register as a listener for the nucleus so that we can be informed
        // when it decays.
        nucleus.addListener(new AtomicNucleus.Listener(){
            public void positionChanged(){
                // Do nothing, since we don't care about this.
            }
            public void atomicWeightChanged(int newAtomicWeight){
                // It is safe to assume that this means that the decay event
                // has occurred.
                setDecayOccurred();
            }
        });

        // Create the border for this chart.
        _borderNode = new PPath();
        _borderNode.setStroke( BORDER_STROKE );
        _borderNode.setStrokePaint( BORDER_COLOR );
        _borderNode.setPaint( BACKGROUND_COLOR );
        addChild( _borderNode );      
        
        // Create the pre-decay time line.
        _preDecayTimeLine = new PPath();
        _preDecayTimeLine.setStroke( TIME_LINE_STROKE );
        _preDecayTimeLine.setStrokePaint( TIME_LINE_COLOR_PRE_DECAY );
        _preDecayTimeLine.setPaint( BACKGROUND_COLOR );
        addChild( _preDecayTimeLine );   
        
        // Create the post-decay time line.
        _postDecayTimeLine = new PPath();
        _postDecayTimeLine.setStroke( TIME_LINE_STROKE );
        _postDecayTimeLine.setStrokePaint( TIME_LINE_COLOR_POST_DECAY );
        _postDecayTimeLine.setPaint( BACKGROUND_COLOR );
        addChild( _postDecayTimeLine );   
        
        // Initialize the time line.
        _preDecayTimeLineOrigin = new Point2D.Double();
        _preDecayTimeLine.moveTo( 0, 0 );
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    /**
     * This method is called to re-scale the chart, which generally occurs
     * when the overall size of the simulation is changed.
     * 
     * @param 
     */
    private void updateBounds( Rectangle2D rect ) {

        // Recalculate the usable area and origin for the chart.
        _usableAreaOriginX = rect.getX() + BORDER_STROKE_WIDTH;
        _usableAreaOriginY = rect.getY() + BORDER_STROKE_WIDTH;
        _usableWidth       = rect.getWidth() - ( BORDER_STROKE_WIDTH * 2 );
        _usableHeight      = rect.getHeight() - ( BORDER_STROKE_WIDTH * 2);

        // Update the multiplier used for converting from pixels to milliseconds.
        _msToPixelsFactor = 0.9 * _usableWidth / TIME_SPAN;
        
        // Redraw the chart based on these recalculated values.
        update();
    }
    
    /**
     * Redraw the chart based on the current state.
     */
    private void update(){
        
        // Set up the border for the chart.
        _borderNode.setPathTo( new RoundRectangle2D.Double( 
                _usableAreaOriginX,
                _usableAreaOriginY,
                _usableWidth,
                _usableHeight,
                20,
                20 ) );
        
        // Position the pre-decay time line.
        _preDecayTimeLine.reset();
        _preDecayTimeLineOrigin.setLocation( _usableAreaOriginX + _usableWidth / 20, 
                _usableAreaOriginY + _usableHeight/2);
        _preDecayTimeLine.moveTo( (float)_preDecayTimeLineOrigin.getX(), (float)_preDecayTimeLineOrigin.getY() );
        _preDecayTimeLine.lineTo( (float)(_preDecayTimeLineOrigin.getX() + _preDecayTimeLineLength * _msToPixelsFactor), 
                (float)_preDecayTimeLineOrigin.getY());
        
        // If decay has already occurred, position the post-decay time line too.
        if (_decayHasOccurred){
            _postDecayTimeLine.reset();
            _postDecayTimeLineOrigin.setLocation( 
                    _preDecayTimeLineOrigin.getX() + _preDecayTimeLineLength * _msToPixelsFactor, 
                    _preDecayTimeLineOrigin.getY());
            _postDecayTimeLine.moveTo( (float)_postDecayTimeLineOrigin.getX(), (float)_preDecayTimeLineOrigin.getY() );
            _postDecayTimeLine.lineTo( 
                    (float)(_postDecayTimeLineOrigin.getX() + _postDecayTimeLineLength * _msToPixelsFactor), 
                    (float)_postDecayTimeLineOrigin.getY());            
        }
        
        // Reposition the markers.
        for (int i = 0; i < _numMarkers; i++){
            double xPos = _usableAreaOriginX + _usableWidth / 20 + _decayTimes[i] * _msToPixelsFactor - _markers[i].getWidth() / 2;
            double yPos = _usableAreaOriginY + _usableHeight * 0.70 - _markers[i].getHeight() / 2;
            _markers[i].setOffset(xPos, yPos);
        }
    }

    /**
     * This method causes the chart to resize itself based on the (presumably
     * different) size of the overall canvas on which it appears.
     * 
     * @param rect - Position on the canvas where this chart should appear.
     */
    public void componentResized( Rectangle2D rect ) {
        updateBounds( rect );
    }
    
    /**
     * Move the appropriate time line forward on the chart.
     * 
     * @param clockEvent
     */
    private void advanceTimeLine(ClockEvent clockEvent){
        
        if (!_decayHasOccurred){

            // Extend the pre-decay time line, but not if we are
            // heading off the chart.
            if (_preDecayTimeLineLength < TIME_SPAN){
                
                // Update the length of the time line.
                _preDecayTimeLineLength += clockEvent.getSimulationTimeChange();

                // Extend the line itself.
                _preDecayTimeLine.lineTo( 
                        (float)(_preDecayTimeLineOrigin.getX() + _preDecayTimeLineLength * _msToPixelsFactor),
                        (float)_preDecayTimeLineOrigin.getY());
            }
        }
        else{
            
            // Extend the post-decay time line, but not if we are
            // heading off the chart.
            if (_postDecayTimeLineLength + _preDecayTimeLineLength < TIME_SPAN){
                
                // Update the length of the time line.
                _postDecayTimeLineLength += clockEvent.getSimulationTimeChange();

                // Extend the line itself.
                _postDecayTimeLine.lineTo( 
                        (float)(_postDecayTimeLineOrigin.getX() + _postDecayTimeLineLength * _msToPixelsFactor),
                        (float)_postDecayTimeLineOrigin.getY());
            }
        }
    }

    /**
     * Reset the time lines back to 0.  Note that this does NOT reset the 
     * decay markers.
     */
    private void resetTimeLine(){
        
        _decayHasOccurred = false;
        _preDecayTimeLineLength = 0;
        _postDecayTimeLineLength = 0;
        _postDecayTimeLine.reset();
        update();
    }
    
    /**
     * Sets the indication that decay has occurred, which means it will draw a
     * different line from here on.
     */
    private void setDecayOccurred(){
        
        // Set our flag that tracks this.
        _decayHasOccurred = true;
        
        // Add a marker and record the time when this decay occurred.
        if (_numDecays < MAX_DECAYS){
            addDecayMarker( _preDecayTimeLineLength );
           _decayTimes[_numDecays++] = _preDecayTimeLineLength;
        }
        
        _postDecayTimeLineOrigin = new Point2D.Double(
                _preDecayTimeLineOrigin.getX() + _preDecayTimeLineLength * _msToPixelsFactor,
                _preDecayTimeLineOrigin.getY());
        _postDecayTimeLineLength = 0;
        _postDecayTimeLine.moveTo( (float)_postDecayTimeLineOrigin.getX(), (float)_postDecayTimeLineOrigin.getY() );
    }
    
    /**
     * This method adds a marker to the chart to represent a decay event.
     * 
     * @param decayTime - Time at which to place the event.
     */
    private void addDecayMarker(double decayTime){
        
        // Create the marker and set the font attributes.
        PText marker = new PText(MARKER_CHAR);
        marker.setFont( MARKER_CHAR_FONT );
        marker.setTextPaint( MARKER_COLOR );
        
        // Position the marker, accounting for the height and width of the
        // text within it.
        double xPos = _usableAreaOriginX + _usableWidth / 20 + decayTime * _msToPixelsFactor - marker.getWidth() / 2;
        double yPos = _usableAreaOriginY + _usableHeight * 0.70 - marker.getHeight() / 2;
        marker.setOffset(xPos, yPos);
        
        // Retain a reference to the marker so we can move it during updates.
        _markers[_numMarkers++] = marker;
 
        // Add this node as a child.
        addChild(marker);
    }
}
