/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.IceThicknessTool;
import edu.colorado.phet.glaciers.model.AbstractTool.ToolAdapter;
import edu.colorado.phet.glaciers.model.AbstractTool.ToolListener;
import edu.colorado.phet.glaciers.model.IceThicknessTool.IceThicknessToolListener;
import edu.colorado.phet.glaciers.model.Movable.MovableAdapter;
import edu.colorado.phet.glaciers.model.Movable.MovableListener;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * IceThicknessToolNode is the visual representation of an ice thickness tool.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IceThicknessToolNode extends AbstractToolNode {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private static final NumberFormat ICE_THICKNESS_FORMAT = new DefaultDecimalFormat( "0.0" );
    private static final PDimension HANDLE_SIZE = new PDimension( 5, 20 );
    private static final PDimension CALIPERS_CLOSED_SIZE = new PDimension( 25, 20 );
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private final IceThicknessTool _iceThicknessTool;
    private final IceThicknessToolListener _iceThicknessToolListener;
    private final MovableListener _movableListener;
    private final ToolListener _toolListener;
    private final CalipersNode _calipersNode;
    private final ValueNode _valueNode;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public IceThicknessToolNode( IceThicknessTool iceThicknessTool, ModelViewTransform mvt, TrashCanDelegate trashCan ) {
        super( iceThicknessTool, mvt, trashCan );
        
        _iceThicknessTool = iceThicknessTool;
        _iceThicknessToolListener = new IceThicknessToolListener() {
            public void thicknessChanged() {
                update();
            }
        };
        _iceThicknessTool.addIceThicknessToolListener( _iceThicknessToolListener );
        
        _movableListener = new MovableAdapter() {
            public void positionChanged() {
                update();
            }
        };
        _iceThicknessTool.addMovableListener( _movableListener );
        
        _toolListener = new ToolAdapter() {
            public void draggingChanged() {
                update();
            }
        };
        _iceThicknessTool.addToolListener( _toolListener );

        // When we start dragging, set the tool to its unknown state
        addInputEventListener( new PDragEventHandler() {
            protected void startDrag( PInputEvent event ) {
                updateUnknown();
                super.startDrag( event );
            }
            protected void drag( PInputEvent event ) {}
        } );
        
        _calipersNode = new CalipersNode( CALIPERS_CLOSED_SIZE );
        addChild( _calipersNode );
        _calipersNode.setOffset( 0, 0 );
        
        PNode handleNode = new HandleNode( HANDLE_SIZE );
        addChild( handleNode );
        handleNode.setOffset( -handleNode.getFullBoundsReference().getWidth(), _calipersNode.getFullBoundsReference().getMaxY() );
        
        _valueNode = new ValueNode( getValueFont(), getValueBorder() );
        addChild( _valueNode );
        _valueNode.setOffset( 3, -_valueNode.getFullBoundsReference().getHeight() + _calipersNode.getJawsHeight() );
        
        // initial state
        updateUnknown();
    }
    
    public void cleanup() {
        _iceThicknessTool.removeIceThicknessToolListener( _iceThicknessToolListener );
        _iceThicknessTool.removeMovableListener( _movableListener );
        _iceThicknessTool.removeToolListener( _toolListener );
        super.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // Geometry
    //----------------------------------------------------------------------------
    
    /*
     * Handle on the tool.
     */
    private static class HandleNode extends PComposite {
        
        private static final Stroke STROKE = new BasicStroke( 1f );
        private static final Color STROKE_COLOR = Color.BLACK;
        private static final Color FILL_COLOR = new Color( 85, 14, 14 ); // reddish brown
        
        public HandleNode( PDimension size ) {
            super();
            
            final double w = size.getWidth();
            final double h = size.getHeight();
            
            PPath topPartNode = new PPath( new RoundRectangle2D.Double( 0, 0, w, 0.25 * h, w / 2, w / 2 ) );
            topPartNode.setStroke( STROKE );
            topPartNode.setStrokePaint( STROKE_COLOR );
            topPartNode.setPaint( FILL_COLOR );
            topPartNode.setOffset( 0, 0 );
            
            PPath bottomPartNode = new PPath( new RoundRectangle2D.Double( 0, 0, w, 0.75 * h, w / 2, w / 2 ) );
            bottomPartNode.setStroke( STROKE );
            bottomPartNode.setStrokePaint( STROKE_COLOR );
            bottomPartNode.setPaint( FILL_COLOR );
            bottomPartNode.setOffset( 0, topPartNode.getFullBoundsReference().getHeight() );
            
            addChild( topPartNode );
            addChild( bottomPartNode );
        }
    }
    
    /*
     * Resizable calipers.
     */
    private static class CalipersNode extends PComposite {
        
        private static final Stroke STROKE = new BasicStroke( 1f );
        private static final Color STROKE_COLOR = Color.BLACK;
        private static final Color FILL_COLOR = new Color( 140, 136, 120 ); // metal gray
        private static final double FRAME_THICKNESS = 3;
        
        private PDimension _closedSize;
        private GeneralPath _path;
        private PPath _pathNode;

        public CalipersNode( PDimension closedSize ) {
            super();
            _closedSize = new PDimension( closedSize );
            _path = new GeneralPath();
            _pathNode = new PPath();
            _pathNode.setStroke( STROKE );
            _pathNode.setStrokePaint( STROKE_COLOR );
            _pathNode.setPaint( FILL_COLOR );
            addChild( _pathNode );
            open( 20 );
        }
        
        public double getJawsHeight() {
            return _closedSize.getHeight() / 2;
        }

        public void open( double height ) {
            
            final double w = _closedSize.getWidth();
            final double h = _closedSize.getHeight() + height;
            final double jawsHeight = getJawsHeight();
            final double jawsWidth = jawsHeight / 5;

            _path.reset();
            _path.moveTo( 0f, 0f );
            _path.lineTo( 0f, (float)jawsHeight );
            _path.lineTo( (float)-w, (float)jawsHeight );
            _path.lineTo( (float)-w, (float)( -h + jawsHeight ) );
            _path.lineTo( 0f, (float)( -h + jawsHeight ) );
            _path.lineTo( 0f, (float)( -h + jawsHeight + jawsHeight ) );
            _path.lineTo( (float)-jawsWidth, (float)( -h + jawsHeight + jawsHeight ) );
            _path.lineTo( (float)( -jawsWidth ), (float)( -h + jawsHeight + FRAME_THICKNESS ) );
            _path.lineTo( (float)( -w + FRAME_THICKNESS ), (float)( -h + jawsHeight + FRAME_THICKNESS ) );
            _path.lineTo( (float)( -w + FRAME_THICKNESS ), (float)( jawsHeight - FRAME_THICKNESS ) );
            _path.lineTo( (float)-jawsWidth, (float)( jawsHeight - FRAME_THICKNESS ) );
            _path.lineTo( (float)-jawsWidth, 0f );
            _path.closePath();
            _pathNode.setPathTo( _path );
        }
        
        public void close() {
            open( 0 );
        }
    }
    
    /*
     * Displays the thickness value.
     */
    private static class ValueNode extends PComposite {
        
        private JLabel _thicknessLabel;
        private PSwing _pswing;
        
        public ValueNode( Font font, Border border ) {
            super();

            _thicknessLabel = new JLabel( "?" );
            _thicknessLabel.setFont( font );
           
            JPanel panel = new JPanel();
            panel.setBorder( border );
            EasyGridBagLayout layout = new EasyGridBagLayout( panel );
            layout.setAnchor( GridBagConstraints.EAST );
            panel.setLayout( layout );
            layout.addComponent( _thicknessLabel, 0, 0 );
            
            _pswing = new PSwing( panel );
            addChild( _pswing );
        }
        
        public void setThickness( double thickness ) {
            setThickness( ICE_THICKNESS_FORMAT.format( thickness ) );
        }
        
        public void setThickness( String thickness ) {
            String text =  thickness + " " + GlaciersStrings.UNITS_METERS;
            _thicknessLabel.setText( text );
            _pswing.computeBounds(); //WORKAROUND: PSwing doesn't handle changing size of a JPanel properly
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the tool to match the model.
     */
    private void update() {
        if ( !_iceThicknessTool.isDragging() ) {
            double value = _iceThicknessTool.getThickness();
            _valueNode.setThickness( value );
            double viewDistance = Math.abs( getModelViewTransform().modelToView( 0, value ).getY() );
            _calipersNode.open( viewDistance );
        }
    }
    
    private void updateUnknown() {
        _valueNode.setThickness( "?" );
        _calipersNode.open( 20 );
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    public static Image createImage() {
        
        PNode parentNode = new PNode();
        
        CalipersNode calipersNode = new CalipersNode( CALIPERS_CLOSED_SIZE );
        calipersNode.open( 20 );
        parentNode.addChild( calipersNode );

        PNode handleNode = new HandleNode( HANDLE_SIZE );
        parentNode.addChild( handleNode );
        
        //TODO these 2 lines of code are duplicated from the constructor
        calipersNode.setOffset( 0, 0 );
        handleNode.setOffset( -handleNode.getFullBoundsReference().getWidth(), calipersNode.getFullBoundsReference().getMaxY() );
        
        return parentNode.toImage();
    }
}
