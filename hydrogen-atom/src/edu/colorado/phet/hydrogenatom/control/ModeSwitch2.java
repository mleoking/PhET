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
import java.awt.geom.GeneralPath;

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
public class ModeSwitch2 extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font BIG_FONT = new Font( HAConstants.FONT_NAME, Font.BOLD, 20 );
    private static final Font LITTLE_FONT = new Font( HAConstants.FONT_NAME, Font.PLAIN, 14 );
    private static final Color ON_COLOR = Color.WHITE;
    private static final Color OFF_COLOR = Color.LIGHT_GRAY;
    private static final Stroke MARKER_STROKE = new BasicStroke( 2f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetPNode _switchDownNode, _switchUpNode;
    private PPath _markerDownNode, _markerUpNode;
    private PText _experimentTitle, _experimentSubtitle, _predictionTitle, _predictionSubtitle;
    private EventListenerList _listenerList;
    private boolean _isExperimentMode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ModeSwitch2() {
        super();
        
        _listenerList = new EventListenerList();
        
        PImage panel = PImageFactory.create( HAConstants.IMAGE_MODE_PANEL );
        _switchDownNode = new PhetPNode( PImageFactory.create( HAConstants.IMAGE_MODE_SWITCH_DOWN ) );
        _switchUpNode = new PhetPNode( PImageFactory.create( HAConstants.IMAGE_MODE_SWITCH_UP ) );
        
        _experimentTitle = new PText( SimStrings.get( "label.experiment") );
        _experimentTitle.setFont( BIG_FONT );
        _experimentSubtitle = new PText( SimStrings.get( "label.whatReallyHappens") );
        _experimentSubtitle.setFont( LITTLE_FONT );
        _predictionTitle = new PText( SimStrings.get( "label.prediction") );
        _predictionTitle.setFont( BIG_FONT );
        _predictionSubtitle = new PText( SimStrings.get( "label.whatTheModelPredicts") );
        _predictionSubtitle.setFont( LITTLE_FONT );
        
        _markerUpNode = new PPath();
        {
            GeneralPath path = new GeneralPath();
            path.moveTo( 0, 0 );
            path.lineTo( 10, -10 );
            path.lineTo( 30, -10 );
            _markerUpNode.setPathTo( path );
            _markerUpNode.setStroke( MARKER_STROKE );
            _markerUpNode.setStrokePaint( ON_COLOR ); 
        }
        
        _markerDownNode = new PPath();
        {
            GeneralPath path = new GeneralPath();
            path.moveTo( 0, 0 );
            path.lineTo( 10, 10 );
            path.lineTo( 30, 10 );
            _markerDownNode.setPathTo( path );
            _markerDownNode.setStroke( MARKER_STROKE );
            _markerDownNode.setStrokePaint( ON_COLOR );
        }
        
        addChild( panel );
        addChild( _markerUpNode );
        addChild( _markerDownNode );
        addChild( _experimentTitle );
        addChild( _experimentSubtitle );
        addChild( _predictionTitle );
        addChild( _predictionSubtitle );
        addChild( _switchDownNode );
        addChild( _switchUpNode );
        
        // Positions
        {
            PBounds pb = panel.getFullBounds();
            PBounds sb = _switchDownNode.getFullBounds();

            _switchDownNode.setOffset( pb.getX() + 10, pb.getY() + ( pb.getHeight() / 2 ) - ( sb.getHeight() / 2 ) );
            _switchUpNode.setOffset( _switchDownNode.getOffset() );

            sb = _switchDownNode.getFullBounds();
            PBounds mub = _markerUpNode.getFullBounds();
            _markerUpNode.setOffset( sb.getX() + sb.getWidth() + 5, sb.getY() - mub.getHeight() + 5 );
            _markerDownNode.setOffset( sb.getX() + sb.getWidth() + 5, sb.getY() + sb.getHeight() + 5 );
            
            mub = _markerUpNode.getFullBounds();
            _experimentTitle.setOffset( mub.getX() + mub.getWidth() + 5, pb.getY() + 15 );
            PBounds etb = _experimentTitle.getFullBounds();
            _experimentSubtitle.setOffset( etb.getX(), etb.getY() + etb.getHeight() );
            PBounds psb = _predictionSubtitle.getFullBounds();
            _predictionSubtitle.setOffset( mub.getX() + mub.getWidth() + 5, pb.getY() + pb.getHeight() - psb.getHeight() - 15 );
            psb = _predictionSubtitle.getFullBounds();
            PBounds ptb = _predictionTitle.getFullBounds();
            _predictionTitle.setOffset( psb.getX(), psb.getY() - ptb.getHeight() );
        }
        
        _switchDownNode.addInputEventListener( new CursorHandler() );
        _switchDownNode.addInputEventListener( new PBasicInputEventHandler() { 
            public void mousePressed( PInputEvent event ) {
                setExperimentSelected( true );
            }
        } );
        _switchUpNode.addInputEventListener( new CursorHandler() );
        _switchUpNode.addInputEventListener( new PBasicInputEventHandler() { 
            public void mousePressed( PInputEvent event ) {
                setExperimentSelected( false );
            }
        } );
        
        setExperimentSelected( false );
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    public void setExperimentSelected( boolean selected ) {
        _isExperimentMode = selected;
        updateUI();
        fireChangeEvent( new ChangeEvent( this ) );
    }
    
    public boolean isExperimentSelected() {
        return _isExperimentMode;
    }
    
    public boolean isPredictionSelected() {
        return !_isExperimentMode;
    }
    
    private void updateUI() {
        _switchDownNode.setVisible( !_isExperimentMode );
        _switchUpNode.setVisible( _isExperimentMode );
        
        Color upColor = _isExperimentMode ? ON_COLOR : OFF_COLOR;
        _markerUpNode.setStrokePaint( upColor );
        _experimentTitle.setTextPaint( upColor );
        _experimentSubtitle.setTextPaint( upColor );
        
        Color downColor = !_isExperimentMode ? ON_COLOR : OFF_COLOR;
        _markerDownNode.setStrokePaint( downColor );
        _predictionTitle.setTextPaint( downColor );
        _predictionSubtitle.setTextPaint( downColor );
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
