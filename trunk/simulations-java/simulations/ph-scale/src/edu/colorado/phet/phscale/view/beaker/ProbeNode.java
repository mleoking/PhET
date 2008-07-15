/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view.beaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.PHValue;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;


public class ProbeNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color SHAFT_COLOR = Color.DARK_GRAY;
    private static final double SHAFT_WIDTH = 10;
    
    private static final Color TIP_COLOR = Color.BLACK;
    
    private static final Color DISPLAY_BORDER_COLOR = SHAFT_COLOR;
    private static final double DISPLAY_BORDER_WIDTH = 3;
    private static final double DISPLAY_BORDER_MARGIN = 8;
    
    private static final Font DISPLAY_FONT = new PhetFont( Font.BOLD, PHScaleConstants.CONTROL_FONT_SIZE );
    private static final DecimalFormat DISPLAY_FORMAT = new DecimalFormat( "#0.00" );
    private static final double DISPLAY_X_SPACING = 8;
    private static final Color DISPLAY_BACKGROUND = Color.LIGHT_GRAY;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    private final DisplayNode _displayNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ProbeNode( double height, Liquid liquid ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        _displayNode = new DisplayNode();
        
        TipNode tipNode = new TipNode();
        tipNode.scale( 25 );
        
        final double shaftHeight = height - _displayNode.getFullBoundsReference().getHeight() - tipNode.getFullBoundsReference().getHeight();
        ShaftNode shaftNode = new ShaftNode( SHAFT_WIDTH, shaftHeight );

        addChild( shaftNode );
        addChild( tipNode );
        addChild( _displayNode );
        
        PBounds db = _displayNode.getFullBoundsReference();
        PBounds sb = shaftNode.getFullBoundsReference();
        _displayNode.setOffset( 0, 0 );
        shaftNode.setOffset( 0.85 * ( db.getWidth() - sb.getWidth() ), db.getHeight() - 0.5 * DISPLAY_BORDER_WIDTH );
        sb = shaftNode.getFullBoundsReference();
        PBounds tb = tipNode.getFullBoundsReference();
        tipNode.setOffset( sb.getX() + ( sb.getWidth() - tb.getWidth() ) / 2, sb.getY() + sb.getHeight() );
        
        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the display to show the liquid's pH value.
     */
    private void update() {
        _displayNode.setValue( _liquid.getPH() );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * Read-out that displays the pH value.
     */
    private static class DisplayNode extends PComposite {
        
        private PText _valueNode;
        
        public DisplayNode() {
            super();
            
            PText labelNode = new PText( PHScaleStrings.LABEL_PH );
            labelNode.setFont( DISPLAY_FONT );
            
            _valueNode = new PText( "XXX.XX" );
            _valueNode.setFont( DISPLAY_FONT );
            
            PComposite parentNode = new PComposite();
            parentNode.addChild( labelNode );
            parentNode.addChild( _valueNode );
            labelNode.setOffset( 0, 0 );
            _valueNode.setOffset( labelNode.getFullBoundsReference().getWidth() + DISPLAY_X_SPACING, 0 );
            
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
                String stringValue = DISPLAY_FORMAT.format( doubleValue );
                _valueNode.setText( stringValue );
            }
            else {
                _valueNode.setText( "" );  
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
            setStroke( null );
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
