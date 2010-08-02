/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.PHMeter;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeListener;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.AqueousSolutionChangeListener;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * pH meter, displays the pH of a solution. Origin is at the tip of the probe.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHMeterNode extends PhetPNode {

    private static final Color SHAFT_COLOR = Color.LIGHT_GRAY;
    private static final Color SHAFT_STROKE_COLOR = Color.BLACK;
    private static final Stroke SHAFT_STROKE = new BasicStroke( 0.25f );

    private static final Color TIP_COLOR = Color.BLACK;

    private static final Color DISPLAY_BORDER_COLOR = Color.DARK_GRAY;
    private static final double DISPLAY_BORDER_WIDTH = 3;
    private static final double DISPLAY_BORDER_MARGIN = 12;

    private static final Font DISPLAY_FONT = new PhetFont( Font.BOLD, 24 );
    private static final DecimalFormat DISPLAY_FORMAT = new DecimalFormat( "#0.00" );
    private static final Color DISPLAY_BACKGROUND = Color.LIGHT_GRAY;

    private PHMeter meter;
    private AqueousSolution solution;
    private AqueousSolutionChangeListener listener;
    private final DisplayNode displayNode;

    public PHMeterNode( final PHMeter meter ) {
        this( meter.getShaftSizeReference(), meter.getTipSizeRefernence() );

        this.meter = meter;
        meter.addModelElementChangeListener( new SolutionRepresentationChangeListener() {

            public void solutionChanged() {
                setSolution( meter.getSolution() );
            }
            
            public void locationChanged() {
                //TODO map location from model to view coordinate frame
                setOffset( meter.getLocationReference() );
                updateDisplay();
            }

            public void visibilityChanged() {
                setVisible( meter.isVisible() );
            }

        } );

        this.solution = meter.getSolution();
        this.listener = new AqueousSolutionChangeListener() {

            public void strengthChanged() {
                updateDisplay();
            }

            public void concentrationChanged() {
                updateDisplay();
            }
        };
        solution.addAqueousSolutionChangeListener( listener );

        addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        addInputEventListener( new PDragSequenceEventHandler() {

            private double clickYOffset; // y-offset of mouse click from meter's origin, in parent's coordinate frame

            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                Point2D pMouse = event.getPositionRelativeTo( getParent() );
                clickYOffset = pMouse.getY() - meter.getLocationReference().getY();
            }

            protected void drag( final PInputEvent event ) {
                super.drag( event );
                Point2D pMouse = event.getPositionRelativeTo( getParent() );
                double x = getXOffset();
                double y = pMouse.getY() - clickYOffset;
                //TODO map y from view to model coordinate frame
                meter.setLocation( x, y );
            }
        } );

        setOffset( meter.getLocationReference() );
        setVisible( meter.isVisible() );
        updateDisplay();
    }

    private void updateDisplay() {
        displayNode.setValue( meter.getValue() );
    }

    /*
     * Private constructor, has no knowledge of the model.
     */
    private PHMeterNode( PDimension shaftSize, PDimension tipSize ) {
        
        this.displayNode = new DisplayNode();

        TipNode tipNode = new TipNode( tipSize );

        // make the shaft a little long, so that it overlap with the tip and the display, so we don't see seams
        final double yOverlap = 0.05 * tipSize.getHeight();
        ShaftNode shaftNode = new ShaftNode( shaftSize.width, shaftSize.height + ( 2 * yOverlap ) );

        addChild( shaftNode );
        addChild( tipNode );
        addChild( displayNode );

        // layout, origin at tip of probe
        double x = -tipNode.getFullBoundsReference().getWidth() / 2;
        double y = -tipNode.getFullBoundsReference().getHeight();
        tipNode.setOffset( x, y );
        x = -shaftNode.getFullBoundsReference().getWidth() / 2;
        y = tipNode.getFullBoundsReference().getMinY() - shaftNode.getFullBoundsReference().getHeight() + yOverlap;
        shaftNode.setOffset( x, y );
        x = -0.15 * displayNode.getFullBoundsReference().getWidth();
        y = shaftNode.getFullBoundsReference().getMinY() - displayNode.getFullBoundsReference().getHeight() + yOverlap;
        displayNode.setOffset( x, y );
    }

    private void setSolution( AqueousSolution solution ) {
        if ( solution != this.solution ) {
            this.solution.removeAqueousSolutionChangeListener( listener );
            this.solution = solution;
            this.solution.addAqueousSolutionChangeListener( listener );
            updateDisplay();
        }
    }

    /*
     * Read-out that displays the pH value, origin at upper left.
     */
    private static class DisplayNode extends PComposite {

        private PText valueNode;

        public DisplayNode() {
            super();

            valueNode = new PText();
            valueNode.setFont( DISPLAY_FONT );
            setValue( new Double( 15 ) ); // initialize before layout

            PComposite parentNode = new PComposite();
            parentNode.addChild( valueNode );
            valueNode.setOffset( 0, 0 );

            PBounds pb = parentNode.getFullBoundsReference();
            Shape backgroundShape = new RoundRectangle2D.Double( 0, 0, pb.getWidth() + 2 * DISPLAY_BORDER_MARGIN, pb.getHeight() + 2 * DISPLAY_BORDER_MARGIN, 10, 10 );
            PPath backgroundNode = new PPath( backgroundShape );
            addChild( backgroundNode );
            backgroundNode.setPaint( DISPLAY_BACKGROUND );
            backgroundNode.setStroke( new BasicStroke( (float) DISPLAY_BORDER_WIDTH ) );
            backgroundNode.setStrokePaint( DISPLAY_BORDER_COLOR );
            backgroundNode.addChild( parentNode );
            parentNode.setOffset( DISPLAY_BORDER_MARGIN, DISPLAY_BORDER_MARGIN );
        }

        public void setValue( Double value ) {
            String text = null;
            if ( value != null ) {
                PHValue phValue = new PHValue( value );
                text = MessageFormat.format( ABSStrings.PATTERN_LABEL_VALUE, ABSStrings.PH, DISPLAY_FORMAT.format( phValue.getValue() ) );
            }
            else {
                text = MessageFormat.format( ABSStrings.PATTERN_LABEL_VALUE, ABSStrings.PH, ABSStrings.PH_METER_NO_VALUE );
            }
            valueNode.setText( text );
        }
    }

    /*
     * Shaft of the probe, origin at upper left.
     */
    private static class ShaftNode extends PPath {

        public ShaftNode( double width, double height ) {
            super();
            setPathTo( new Rectangle2D.Double( 0, 0, width, height ) );
            setPaint( SHAFT_COLOR );
            setStroke( SHAFT_STROKE );
            setStrokePaint( SHAFT_STROKE_COLOR );
        }
    }

    /*
     * Creates a tip whose dimensions are 1 x 2.5, origin at upper left.
     */
    private static class TipNode extends PPath {
        
        public TipNode( PDimension size ) {
            this( (float) size.width, (float) size.height );
        }
    
        public TipNode( float w, float h ) {
            
            float rectangleHeight = 0.6f * h;
            float pointHeight = h - rectangleHeight;
            float cornerRadius = 0.4f * w;
            
            // rounded corners at top
            Shape roundRect = new RoundRectangle2D.Float( 0f, 0f, w, rectangleHeight, cornerRadius, cornerRadius );

            // mask out rounded corners at bottom
            Shape rect = new Rectangle2D.Float( 0f, rectangleHeight / 2, w, rectangleHeight / 2 );

            // point at the bottom
            GeneralPath triangle = new GeneralPath();
            triangle.moveTo( 0f, h - pointHeight );
            triangle.lineTo( w / 2f, h );
            triangle.lineTo( w, h - pointHeight );
            triangle.closePath();

            // constructive area geometry
            Area area = new Area( roundRect );
            area.add( new Area( rect ) );
            area.add( new Area( triangle ) );

            setPathTo( area );
            setPaint( TIP_COLOR );
            setStroke( null );
        }
    }
}
