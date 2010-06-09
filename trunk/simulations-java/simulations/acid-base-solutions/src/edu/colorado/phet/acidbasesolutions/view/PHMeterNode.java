/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.ModelListener;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.AqueousSolutionChangeListener;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * pH meter, displays the pH of a solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHMeterNode extends PComposite {
    
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

    private AqueousSolution solution;
    private AqueousSolutionChangeListener listener;
    private final DisplayNode displayNode;
    
    public PHMeterNode( double height, final ABSModel model ) {
        this( height );
        
        model.addModelListener( new ModelListener() {
            public void solutionChanged() {
                setSolution( model.getSolution() );
            }
        });
        
        this.solution = model.getSolution();
        this.listener = new AqueousSolutionChangeListener() {
            public void initialConcentrationChanged() {
                update();
            }
        };
        solution.addAqueousSolutionChangeListener( listener );
        
        update();
    }
    
    /*
     * Private constructor, has no knowledge of the model.
     */
    private PHMeterNode( double height ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        this.displayNode = new DisplayNode();
        
        TipNode tipNode = new TipNode();
        tipNode.scale( 25 );
        
        final double shaftHeight = height - displayNode.getFullBoundsReference().getHeight() - tipNode.getFullBoundsReference().getHeight();
        ShaftNode shaftNode = new ShaftNode( SHAFT_WIDTH, shaftHeight );

        addChild( shaftNode );
        addChild( tipNode );
        addChild( displayNode );
        
        PBounds db = displayNode.getFullBoundsReference();
        PBounds sb = shaftNode.getFullBoundsReference();
        displayNode.setOffset( 0, 0 );
        shaftNode.setOffset( 0.85 * ( db.getWidth() - sb.getWidth() ), db.getHeight() - 0.5 * DISPLAY_BORDER_WIDTH );
        sb = shaftNode.getFullBoundsReference();
        PBounds tb = tipNode.getFullBoundsReference();
        tipNode.setOffset( sb.getX() + ( sb.getWidth() - tb.getWidth() ) / 2, sb.getY() + sb.getHeight() );
    }
    
    private void update() {
        PHValue value = new PHValue( solution.getPH() );
        displayNode.setValue( value );
    }
    
    private void setSolution( AqueousSolution solution ) {
        if ( solution != this.solution ) {
            this.solution.removeAqueousSolutionChangeListener( listener );
            this.solution = solution;
            this.solution.addAqueousSolutionChangeListener( listener );
            update();
        }
    }
    
    /*
     * Read-out that displays the pH value.
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
            if ( pH != null ) {
                final double doubleValue = pH.getValue();
                String stringValue = MessageFormat.format( ABSStrings.PATTERN_LABEL_VALUE, ABSStrings.PH, DISPLAY_FORMAT.format( doubleValue ) );
                valueNode.setText( stringValue );
            }
            else {
                valueNode.setText( "" );  
            }
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
