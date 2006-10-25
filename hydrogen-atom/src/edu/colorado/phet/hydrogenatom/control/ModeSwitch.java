/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * ModeSwitch is the control for switching between "Experiment" and "Prediction" modes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ModeSwitch extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // This controls whether "Experiment" or "Prediction" is at the top position.
    private static final boolean EXPERIMENT_IS_AT_TOP = true;
    
    private static final Color ON_COLOR = Color.WHITE;
    private static final Color OFF_COLOR = Color.BLACK;
    private static final Stroke LINE_STROKE = new BasicStroke( 2f );
    private static final double LINE_LENGTH = 10;
    private static final double MARGIN = 10;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetPNode _switchUpNode, _switchDownNode;
    private PPath _topLineNode, _bottomLineNode;
    private PText _topTitleNode, _topSubtitleNode, _bottomTitleNode, _bottomSubtitleNode;
    
    private EventListenerList _listenerList;
    private boolean _isExperimentMode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public ModeSwitch() {
        super();
        
        // Font size 
        int bigFontSize = SimStrings.getInt( "modeSwitch.big.font.size", HAConstants.DEFAULT_FONT_SIZE );
        Font bigFont = new Font( HAConstants.DEFAULT_FONT_NAME, Font.BOLD, bigFontSize );
        int smallFontSize = SimStrings.getInt( "modeSwitch.small.font.size", HAConstants.DEFAULT_FONT_SIZE );
        Font smallFont = new Font( HAConstants.DEFAULT_FONT_NAME, Font.PLAIN, smallFontSize );
        
        PImage panel = PImageFactory.create( HAConstants.IMAGE_MODE_PANEL );
        _switchUpNode = new PhetPNode( PImageFactory.create( HAConstants.IMAGE_MODE_SWITCH_UP ) );
        _switchDownNode = new PhetPNode( PImageFactory.create( HAConstants.IMAGE_MODE_SWITCH_DOWN ) );
        
        if ( EXPERIMENT_IS_AT_TOP ) {
            _topTitleNode = new PText( SimStrings.get( "label.experiment" ) );
            _topSubtitleNode = new PText( SimStrings.get( "label.whatReallyHappens" ) );
            _bottomTitleNode = new PText( SimStrings.get( "label.prediction" ) );
            _bottomSubtitleNode = new PText( SimStrings.get( "label.whatTheModelPredicts" ) );
        }
        else {
            _topTitleNode = new PText( SimStrings.get( "label.prediction") );
            _topSubtitleNode = new PText( SimStrings.get( "label.whatTheModelPredicts") );
            _bottomTitleNode = new PText( SimStrings.get( "label.experiment") );
            _bottomSubtitleNode = new PText( SimStrings.get( "label.whatReallyHappens") );
        }
        _topTitleNode.setFont( bigFont );
        _topSubtitleNode.setFont( smallFont );
        _bottomTitleNode.setFont( bigFont );
        _bottomSubtitleNode.setFont( smallFont );
        
        _topLineNode = new PPath();
        _topLineNode.setPathTo( new Line2D.Double( 0, 0, LINE_LENGTH, 0 ) );
        _topLineNode.setStroke( LINE_STROKE );
        _topLineNode.setStrokePaint( ON_COLOR );

        _bottomLineNode = new PPath();
        _bottomLineNode.setPathTo( new Line2D.Double( 0, 0, LINE_LENGTH, 0 ) );
        _bottomLineNode.setStroke( LINE_STROKE );
        _bottomLineNode.setStrokePaint( ON_COLOR );
        
        // Order is front-to-back
        addChild( panel );
        addChild( _topLineNode );
        addChild( _bottomLineNode );
        addChild( _topTitleNode );
        addChild( _topSubtitleNode );
        addChild( _bottomTitleNode );
        addChild( _bottomSubtitleNode );
        addChild( _switchUpNode );
        addChild( _switchDownNode );
        
        // Positions
        {
            PBounds pb = panel.getFullBounds();
            PBounds sb = _switchDownNode.getFullBounds();

            // Panel
            panel.setOffset( 0, 0 );
            
            // Switch
            _switchDownNode.setOffset( pb.getX() + MARGIN, pb.getY() + ( pb.getHeight() / 3 ) - ( sb.getHeight() / 2 ) );
            _switchUpNode.setOffset( _switchDownNode.getOffset() );
            
            // Markers
            sb = _switchDownNode.getFullBounds();
            PBounds mub = _topLineNode.getFullBounds();
            _topLineNode.setOffset( sb.getX() + sb.getWidth() - 5, sb.getY() - mub.getHeight() + 10 );
            _bottomLineNode.setOffset( sb.getX() + sb.getWidth() - 5, sb.getY() + sb.getHeight() - 10 );
            
            // Top title and subtitle
            mub = _topLineNode.getFullBounds();
            PBounds etb = _topTitleNode.getFullBounds();
            _topTitleNode.setOffset( mub.getX() + mub.getWidth() + 5, mub.getY() - ( etb.getHeight() / 2 ) );
            etb = _topTitleNode.getFullBounds();
            _topSubtitleNode.setOffset( etb.getX(), etb.getY() + etb.getHeight() );
            
            // Bottom title and subtitle
            PBounds mdb = _bottomLineNode.getFullBounds();
            PBounds ptb = _bottomTitleNode.getFullBounds();
            _bottomTitleNode.setOffset( mdb.getX() + mdb.getWidth() + 5, mdb.getY() - ( ptb.getHeight() / 2 ) );
            ptb = _bottomTitleNode.getFullBounds();
            _bottomSubtitleNode.setOffset( ptb.getX(), ptb.getY() + ptb.getHeight() );
        }
        
        // Resize the panel
        {
            removeChild( panel );
            PBounds b = getFullBounds();
            PBounds pb = panel.getFullBounds();
            double scaleX = ( b.getWidth() + ( 2 * MARGIN ) ) / pb.getWidth();
            double scaleY = ( b.getHeight() + ( 2 * MARGIN ) ) / pb.getHeight();
            AffineTransform xform = new AffineTransform();
            xform.scale( scaleX, scaleY );
            panel.setTransform( xform );
            addChild( panel );
            panel.moveToBack();
        }
        
        // Event handling
        {
            _listenerList = new EventListenerList();
            
            // These parts are not interactive
            panel.setPickable( false );
            _topLineNode.setPickable( false );
            _bottomLineNode.setPickable( false );
            
            // Moves the switch to the top position
            PInputEventListener topSwitcher = new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    setExperimentSelected( EXPERIMENT_IS_AT_TOP );
                }
            };
            
            // Moves the switch to the bottom position
            PInputEventListener bottomSwitcher = new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    setExperimentSelected( !EXPERIMENT_IS_AT_TOP );
                }
            };
            
            _switchUpNode.addInputEventListener( new CursorHandler() );
            _switchUpNode.addInputEventListener( bottomSwitcher );
            _topTitleNode.addInputEventListener( topSwitcher );
            _topSubtitleNode.addInputEventListener( topSwitcher );
            
            _switchDownNode.addInputEventListener( new CursorHandler() );
            _switchDownNode.addInputEventListener( topSwitcher );
            _bottomTitleNode.addInputEventListener( bottomSwitcher );
            _bottomSubtitleNode.addInputEventListener( bottomSwitcher );
        }
        
        // Default state
        setExperimentSelected();
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    public void setExperimentSelected() {
        setExperimentSelected( true );
    }
    
    public boolean isExperimentSelected() {
        return _isExperimentMode;
    }
    
    public void setPredictionSelected() {
        setExperimentSelected( false );
    }
    
    public boolean isPredictionSelected() {
        return !_isExperimentMode;
    }
    
    //----------------------------------------------------------------------------
    // Private
    //----------------------------------------------------------------------------
    
    private void setExperimentSelected( boolean selected ) {
        _isExperimentMode = selected;
        updateUI();
        fireChangeEvent( new ChangeEvent( this ) );
    }
    
    /*
     * Updates the UI to match the selected mode.
     * This moves the switch and changes the color of text.
     */
    private void updateUI() {
        
        boolean isUpVisible = EXPERIMENT_IS_AT_TOP ? _isExperimentMode : !_isExperimentMode;
        
        _switchUpNode.setVisible( isUpVisible );
        _switchDownNode.setVisible( !isUpVisible );

        Color upColor = isUpVisible ? ON_COLOR : OFF_COLOR;
        _topLineNode.setStrokePaint( upColor );
        _topTitleNode.setTextPaint( upColor );
        _topSubtitleNode.setTextPaint( upColor );
        
        Color downColor = !isUpVisible ? ON_COLOR : OFF_COLOR;
        _bottomLineNode.setStrokePaint( downColor );
        _bottomTitleNode.setTextPaint( downColor );
        _bottomSubtitleNode.setTextPaint( downColor );
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /**
     * Adds a ChangeListener.
     *
     * @param listener the listener
     */
    public void addChangeListener( ChangeListener listener ) {
        _listenerList.add( ChangeListener.class, listener );
    }

    /**
     * Removes a ChangeListener.
     *
     * @param listener the listener
     */
    public void removeChangeListener( ChangeListener listener ) {
        _listenerList.remove( ChangeListener.class, listener );
    }

    /**
     * Fires a ChangeEvent.
     *
     * @param event the event
     */
    private void fireChangeEvent( ChangeEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ChangeListener.class ) {
                ( (ChangeListener)listeners[i + 1] ).stateChanged( event );
            }
        }
    }
}
