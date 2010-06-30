/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.ModelChangeAdapter;
import edu.colorado.phet.acidbasesolutions.model.ABSModelElement.ModelElementChangeListener;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.AqueousSolutionChangeListener;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
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
    private static final double SHAFT_WIDTH = 10;

    private static final Color TIP_COLOR = Color.BLACK;

    private static final Color DISPLAY_BORDER_COLOR = Color.DARK_GRAY;
    private static final double DISPLAY_BORDER_WIDTH = 3;
    private static final double DISPLAY_BORDER_MARGIN = 12;

    private static final Font DISPLAY_FONT = new PhetFont( Font.BOLD, 24 );
    private static final DecimalFormat DISPLAY_FORMAT = new DecimalFormat( "#0.00" );
    private static final Color DISPLAY_BACKGROUND = Color.LIGHT_GRAY;

    private ABSModel model;
    private AqueousSolution solution;
    private AqueousSolutionChangeListener listener;
    private final DisplayNode displayNode;

    public PHMeterNode( final ABSModel model ) {
        this( model.getPHMeter().getShaftLength() );

        this.model = model;
        model.addModelChangeListener( new ModelChangeAdapter() {

            @Override
            public void solutionChanged() {
                setSolution( model.getSolution() );
            }
        } );

        model.getPHMeter().addModelElementChangeListener( new ModelElementChangeListener() {

            public void locationChanged() {
                //TODO map location from model to view coordinate frame
                setOffset( model.getPHMeter().getLocationReference() );
                updateDisplay();
            }

            public void visibilityChanged() {
                setVisible( model.getPHMeter().isVisible() );
            }

        } );

        this.solution = model.getSolution();
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
                clickYOffset = pMouse.getY() - model.getPHMeter().getLocationReference().getY();
            }

            protected void drag( final PInputEvent event ) {
                super.drag( event );
                Point2D pMouse = event.getPositionRelativeTo( getParent() );
                double x = getXOffset();
                double y = pMouse.getY() - clickYOffset;
                //TODO map y from view to model coordinate frame
                if ( isInBoundsY( y ) ) {
                    model.getPHMeter().setLocation( x, y );
                }
            }
            
            private boolean isInBoundsY( double y ) {
                return ( y > 220 && y < 400 ); //TODO calculate based on probe length and position of solution surface
            }
        } );

        setOffset( model.getPHMeter().getLocationReference() );
        setVisible( model.getPHMeter().isVisible() );
        updateDisplay();
    }

    private void updateDisplay() {
        if ( model.getBeaker().inSolution( model.getPHMeter().getLocationReference() ) ) {
            displayNode.setValue( new PHValue( model.getSolution().getPH() ) );
        }
        else {
            displayNode.setValue( null );
        }
    }

    /*
     * Private constructor, has no knowledge of the model.
     */
    private PHMeterNode( double shaftHeight ) {
        super();

        this.displayNode = new DisplayNode();

        TipNode tipNode = new TipNode();
        tipNode.scale( 25 );

        ShaftNode shaftNode = new ShaftNode( SHAFT_WIDTH, shaftHeight );

        addChild( shaftNode );
        addChild( tipNode );
        addChild( displayNode );

        // layout, origin at tip of probe
        double yOverlap = 5;
        double x = -tipNode.getFullBoundsReference().getWidth() / 2;
        double y = -tipNode.getFullBoundsReference().getHeight();
        tipNode.setOffset( x, y );
        x = -shaftNode.getFullBoundsReference().getWidth() / 2;
        y = tipNode.getFullBoundsReference().getMinY() - shaftNode.getFullBoundsReference().getHeight() + yOverlap;
        shaftNode.setOffset( x, y );
        x = -0.85 * displayNode.getFullBoundsReference().getWidth();
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
            setValue( new PHValue( 15 ) ); // initialize before layout

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

        public void setValue( PHValue pH ) {
            String text = null;
            if ( pH != null ) {
                final double doubleValue = pH.getValue();
                text = MessageFormat.format( ABSStrings.PATTERN_LABEL_VALUE, ABSStrings.PH, DISPLAY_FORMAT.format( doubleValue ) );
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

        public TipNode() {
            super();

            // rounded corners at top
            Shape roundRect = new RoundRectangle2D.Float( 0f, 0f, 1f, 1.5f, 0.4f, 0.4f );

            // mask out rounded corners at bottom
            Shape rect = new Rectangle2D.Float( 0f, 0.5f, 1f, 1f );

            // point at the bottom
            GeneralPath triangle = new GeneralPath();
            triangle.moveTo( 0f, 1.5f );
            triangle.lineTo( 0.5f, 2.5f );
            triangle.lineTo( 1f, 1.5f );
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
