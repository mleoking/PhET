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
import java.awt.GradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.enums.AtomicModel;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.nodes.HTMLNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * AtomicModelSelector is the control for selecting an atomic model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AtomicModelSelector extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color TITLE_COLOR = Color.BLACK;
    private static final Color CONTINUUM_CLASSICAL_COLOR = Color.WHITE;
    private static final Color CONTINUUM_QUANTUM_COLOR = Color.BLACK;
    private static final Color BUTTON_SELECTED_COLOR = Color.WHITE;
    private static final Color BUTTON_DESELECTED_COLOR = Color.BLACK;
    
    // margins around edges of panel
    private static final double X_MARGIN = 8;
    private static final double Y_MARGIN = 5;
    // space between buttons
    private static final double BUTTON_SPACING = 10;
    // space between a button and its label
    private static final double LABEL_SPACING = 4;
    // space between buttons and the continuum label
    private static final double CONTINUUM_SPACING = 3;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HTMLNode _billiardBallLabel;
    private HTMLNode _plumPuddingLabel;
    private HTMLNode _solarSystemLabel;
    private HTMLNode _bohrLabel;
    private HTMLNode _deBroglieLabel;
    private HTMLNode _schrodingerLabel;
    
    private PImage _billiardBallButton;
    private PImage _plumPuddingButton;
    private PImage _solarSystemButton;
    private PImage _bohrButton;
    private PImage _deBroglieButton;
    private PImage _schrodingerButton;
    
    private PPath _selectionIndicator;
    
    private EventListenerList _listenerList;
    
    private AtomicModel _selectedModel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AtomicModelSelector() {
        
        // PNodes in this list are used to determine the width of the panel
        ArrayList widthNodes = new ArrayList();
        
        // PNodes in this list are used to determine the height of the panel
        ArrayList heightNodes = new ArrayList();
        
        // Fonts
        Font titleFont = new Font( HAConstants.JLABEL_FONT_NAME, Font.BOLD, 
                SimStrings.getInt( "atomicModelSelector.title.font.size", HAConstants.ATOMIC_MODEL_SELECTOR_TITLE_FONT_SIZE ) );
        Font continuumFont = new Font( HAConstants.JLABEL_FONT_NAME, Font.PLAIN, 
                SimStrings.getInt( "atomicModelSelector.continuum.font.size", HAConstants.ATOMIC_MODEL_SELECTOR_CONTINUUM_FONT_SIZE ) ); 
        Font buttonFont = new Font( HAConstants.JLABEL_FONT_NAME, Font.PLAIN,
                SimStrings.getInt( "atomicModelSelector.button.font.size", HAConstants.ATOMIC_MODEL_SELECTOR_BUTTON_FONT_SIZE ) );
        
        // Panel
        PImage panel = PImageFactory.create( HAConstants.IMAGE_ATOMIC_MODEL_PANEL );
        
        // Labels
        HTMLNode atomicModelLabel = new HTMLNode( SimStrings.get( "label.atomicModel" ) );
        atomicModelLabel.setFont( titleFont );
        atomicModelLabel.setHTMLColor( TITLE_COLOR );
        widthNodes.add( atomicModelLabel );
        heightNodes.add( atomicModelLabel );
        
        PText classicalLabel = new PText( SimStrings.get( "label.classical" ) );
        classicalLabel.setFont( continuumFont );
        classicalLabel.setTextPaint( CONTINUUM_QUANTUM_COLOR ); // yes, this is correct
        
        PText quantumLabel = new PText( SimStrings.get( "label.quantum" ) );
        quantumLabel.setFont( continuumFont );
        quantumLabel.setTextPaint( CONTINUUM_CLASSICAL_COLOR ); // yes, this is correct
        
        _billiardBallLabel = new HTMLNode( SimStrings.get( "label.billardBall" ) );
        _billiardBallLabel.setFont( buttonFont );
        widthNodes.add( _billiardBallLabel );
        heightNodes.add( _billiardBallLabel );

        _plumPuddingLabel = new HTMLNode( SimStrings.get( "label.plumPudding" ) );
        _plumPuddingLabel.setFont( buttonFont );
        widthNodes.add( _plumPuddingLabel );
        heightNodes.add( _plumPuddingLabel );
        
        _solarSystemLabel = new HTMLNode( SimStrings.get( "label.solarSystem" ) );
        _solarSystemLabel.setFont( buttonFont );
        widthNodes.add( _solarSystemLabel );
        heightNodes.add( _solarSystemLabel );
        
        _bohrLabel = new HTMLNode( SimStrings.get( "label.bohr" ) );
        _bohrLabel.setFont( buttonFont );
        widthNodes.add( _bohrLabel );
        heightNodes.add( _bohrLabel );
        
        _deBroglieLabel = new HTMLNode( SimStrings.get( "label.deBroglie" ) );
        _deBroglieLabel.setFont( buttonFont );
        widthNodes.add( _deBroglieLabel );
        heightNodes.add( _deBroglieLabel );
        
        _schrodingerLabel = new HTMLNode( SimStrings.get( "label.schrodinger" ) );
        _schrodingerLabel.setFont( buttonFont );
        widthNodes.add( _schrodingerLabel );
        heightNodes.add( _schrodingerLabel );
        
        // Buttons
        _billiardBallButton = PImageFactory.create( HAConstants.IMAGE_BILLIARD_BALL_BUTTON );
        widthNodes.add( _billiardBallButton );
        heightNodes.add( _billiardBallButton );
        
        _plumPuddingButton = PImageFactory.create( HAConstants.IMAGE_PLUM_PUDDING_BUTTON );
        widthNodes.add( _plumPuddingButton );
        heightNodes.add( _plumPuddingButton );
        
        _solarSystemButton = PImageFactory.create( HAConstants.IMAGE_SOLAR_SYSTEM_BUTTON );
        widthNodes.add( _solarSystemButton );
        heightNodes.add( _solarSystemButton );
        
        _bohrButton = PImageFactory.create( HAConstants.IMAGE_BOHR_BUTTON );
        widthNodes.add( _bohrButton );
        heightNodes.add( _bohrButton );
        
        _deBroglieButton = PImageFactory.create( HAConstants.IMAGE_DEBROGLIE_BUTTON );
        widthNodes.add( _deBroglieButton );
        heightNodes.add( _deBroglieButton );
        
        _schrodingerButton = PImageFactory.create( HAConstants.IMAGE_SCHRODINGER_BUTTON );
        widthNodes.add( _schrodingerButton );
        heightNodes.add( _schrodingerButton );
        
        // Panel & continuum sizing
        double continuumWidth = 0;
        double continuumHeight = 0;
        {
            // Height of the panel
            double panelHeight = 0;
            Iterator j = heightNodes.iterator();
            while ( j.hasNext() ) {
                PNode node = (PNode)j.next();
                panelHeight += node.getFullBounds().getHeight();
            }
            panelHeight += ( 6 * LABEL_SPACING );
            panelHeight += ( 7 * BUTTON_SPACING );
            panelHeight += ( 2 * Y_MARGIN );
            
            // Width and height of continuum (dependent on panel height)
            double w1 = classicalLabel.getFullBounds().getHeight(); // will be rotated 90 degrees
            double w2 = quantumLabel.getFullBounds().getHeight(); // will be rotated 90 degrees
            continuumWidth = Math.max( w1, w2 ) + 2;
            continuumHeight = panelHeight - atomicModelLabel.getFullBounds().getHeight() - ( 2 * Y_MARGIN ) - ( 2 * BUTTON_SPACING );
            
            // Width of the panel (dependent on continuum width)
            double panelWidth = 0;
            double maxNodeWidth = 0;
            Iterator i = widthNodes.iterator();
            while ( i.hasNext() ) {
                PNode node = (PNode) i.next();
                if ( node.getFullBounds().getWidth() > maxNodeWidth ) {
                    maxNodeWidth = node.getFullBounds().getWidth();
                }
            }
            panelWidth += maxNodeWidth;
            panelWidth += CONTINUUM_SPACING;
            panelWidth += continuumWidth;
            panelWidth += ( 2 * X_MARGIN );
            
            // Scale the panel
            PBounds pb = panel.getFullBounds();
            double scaleX = panelWidth / pb.getWidth();
            double scaleY = panelHeight / pb.getHeight();
            AffineTransform xform = new AffineTransform();
            xform.scale( scaleX, scaleY );
            panel.setTransform( xform );
        }
        
        // Selection indicator
        {
            _selectionIndicator = new PPath();
            double w = panel.getFullBounds().getWidth() - continuumWidth - CONTINUUM_SPACING - ( 2 * X_MARGIN );
            double h = _billiardBallButton.getFullBounds().getHeight() + 4; // Assumes that all buttons are the same size!
            _selectionIndicator.setPathTo( new Rectangle2D.Double( 0, 0, w, h ) );
            _selectionIndicator.setPaint( BUTTON_SELECTED_COLOR );
            _selectionIndicator.setStroke( null );
        }
        
        // Continuum
        PComposite continuumNode = new PComposite();
        {      
            final double w = continuumHeight;
            final double h = continuumWidth;
            
            PPath track = new PPath( new Rectangle2D.Double( 0, 0, w, h ) );
            track.setStroke( null );
            GradientPaint gradient = new GradientPaint( 0f, 0f, CONTINUUM_CLASSICAL_COLOR, (float)w, 0f, CONTINUUM_QUANTUM_COLOR );
            track.setPaint( gradient );
            
            continuumNode.addChild( track );
            continuumNode.addChild( classicalLabel );
            continuumNode.addChild( quantumLabel );
            
            final double xmargin = 5;
            final double ymargin = 2;
            
            track.setOffset( 0, 0 );
            classicalLabel.setOffset( xmargin, ymargin );
            quantumLabel.setOffset( track.getFullBounds().getWidth() - quantumLabel.getFullBounds().getWidth() - xmargin, ymargin );
        }
        
        // Layout, front to back
        addChild( panel );
        addChild( atomicModelLabel );
        addChild( _selectionIndicator );
        addChild( _billiardBallLabel );
        addChild( _billiardBallButton );
        addChild( _plumPuddingLabel );
        addChild( _plumPuddingButton );
        addChild( _solarSystemLabel );
        addChild( _solarSystemButton );
        addChild( _bohrLabel );
        addChild( _bohrButton );
        addChild( _deBroglieLabel );
        addChild( _deBroglieButton );
        addChild( _schrodingerLabel );
        addChild( _schrodingerButton );
        addChild( continuumNode );
        
        // Positioning
        {
            panel.setOffset( 0, 0 );
            double panelWidth = panel.getFullBounds().getWidth();
            
            atomicModelLabel.setOffset( ( panelWidth - atomicModelLabel.getFullBounds().getWidth() ) / 2, Y_MARGIN );
            
            setOffsetCentered( _billiardBallLabel, atomicModelLabel, panel, continuumNode, BUTTON_SPACING );
            setOffsetCentered( _billiardBallButton, _billiardBallLabel, panel, continuumNode, LABEL_SPACING );
            setOffsetCentered( _plumPuddingLabel, _billiardBallButton, panel, continuumNode, BUTTON_SPACING );
            setOffsetCentered( _plumPuddingButton, _plumPuddingLabel, panel, continuumNode, LABEL_SPACING );
            setOffsetCentered( _solarSystemLabel, _plumPuddingButton, panel, continuumNode, BUTTON_SPACING );
            setOffsetCentered( _solarSystemButton, _solarSystemLabel, panel, continuumNode, LABEL_SPACING );
            setOffsetCentered( _bohrLabel, _solarSystemButton, panel, continuumNode, BUTTON_SPACING );
            setOffsetCentered( _bohrButton, _bohrLabel, panel, continuumNode, LABEL_SPACING );
            setOffsetCentered( _deBroglieLabel, _bohrButton, panel, continuumNode, BUTTON_SPACING );
            setOffsetCentered( _deBroglieButton, _deBroglieLabel, panel, continuumNode, LABEL_SPACING );
            setOffsetCentered( _schrodingerLabel, _deBroglieButton, panel, continuumNode, BUTTON_SPACING );
            setOffsetCentered( _schrodingerButton, _schrodingerLabel, panel, continuumNode, LABEL_SPACING );
            
            PBounds pb = panel.getFullBounds();
            AffineTransform xform = new AffineTransform();
            xform.translate( pb.getWidth() - continuumNode.getFullBounds().getHeight() - X_MARGIN, _billiardBallLabel.getFullBounds().getY() );
            xform.rotate( Math.toRadians( 90 ) );
            xform.translate( 0, -continuumNode.getFullBounds().getHeight() );
            continuumNode.setTransform( xform );
        }
        
        // Event handling 
        {
            _listenerList = new EventListenerList();
            
            panel.setPickable( false );
            continuumNode.setPickable( false );
            atomicModelLabel.setPickable( false );
            classicalLabel.setPickable( false );
            quantumLabel.setPickable( false );
            _selectionIndicator.setPickable( false );
            
            _billiardBallButton.addInputEventListener( new CursorHandler() );
            _billiardBallButton.addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    setSelection( AtomicModel.BILLIARD_BALL );
                }
            } );
            _billiardBallLabel.addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    setSelection( AtomicModel.BILLIARD_BALL );
                }
            } );
            
            _plumPuddingButton.addInputEventListener( new CursorHandler() );
            _plumPuddingButton.addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    setSelection( AtomicModel.PLUM_PUDDING );
                }
            } );
            _plumPuddingLabel.addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    setSelection( AtomicModel.PLUM_PUDDING );
                }
            } );
            
            _solarSystemButton.addInputEventListener( new CursorHandler() );
            _solarSystemButton.addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    setSelection( AtomicModel.SOLAR_SYSTEM );
                }
            } );
            _solarSystemLabel.addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    setSelection( AtomicModel.SOLAR_SYSTEM );
                }
            } );
            
            _bohrButton.addInputEventListener( new CursorHandler() );
            _bohrButton.addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    setSelection( AtomicModel.BOHR );
                }
            } );
            _bohrLabel.addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    setSelection( AtomicModel.BOHR );
                }
            } );
            
            _deBroglieButton.addInputEventListener( new CursorHandler() );
            _deBroglieButton.addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    setSelection( AtomicModel.DEBROGLIE );
                }
            } );
            _deBroglieLabel.addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    setSelection( AtomicModel.DEBROGLIE );
                }
            } );
            
            _schrodingerButton.addInputEventListener( new CursorHandler() );
            _schrodingerButton.addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    setSelection( AtomicModel.SCHRODINGER );
                }
            } );
            _schrodingerLabel.addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    setSelection( AtomicModel.SCHRODINGER );
                }
            } );
        }
        
        // Default state
        setSelection( AtomicModel.BILLIARD_BALL );
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    public void setSelection( AtomicModel model ) {
        _selectedModel = model;
        updateUI();
        fireChangeEvent( new ChangeEvent( this ) );
    }
    
    public AtomicModel getSelection() {
        return _selectedModel;
    }
    
    //XXX This should be removed, the name will contain HTML markup.
    public String getSelectionName() {
        String name = null;
        if ( _selectedModel == AtomicModel.BILLIARD_BALL ) {
            name = _billiardBallLabel.getHTML();
        }
        else if ( _selectedModel == AtomicModel.PLUM_PUDDING ) {
            name = _plumPuddingLabel.getHTML();
        }
        else if ( _selectedModel == AtomicModel.SOLAR_SYSTEM ) {
            name = _solarSystemLabel.getHTML();
        }
        else if ( _selectedModel == AtomicModel.BOHR ) {
            name = _bohrLabel.getHTML();
        }
        else if ( _selectedModel == AtomicModel.DEBROGLIE ) {
            name = _deBroglieLabel.getHTML();
        }
        else if ( _selectedModel == AtomicModel.SCHRODINGER ) {
            name = _schrodingerLabel.getHTML();
        }
        return name;
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private void setOffsetCentered( PNode node, PNode nodeAbove, PNode panel, PNode continuumNode, double spacing ) {
        double x = ( panel.getFullBounds().getWidth() - continuumNode.getFullBounds().getHeight() - CONTINUUM_SPACING - node.getFullBounds().getWidth() ) / 2;
        double y = nodeAbove.getFullBounds().getMaxY() + spacing;
        node.setOffset( x, y );
    }
    
    private void updateUI() {
        
        _billiardBallLabel.setHTMLColor( BUTTON_DESELECTED_COLOR );
        _plumPuddingLabel.setHTMLColor( BUTTON_DESELECTED_COLOR );
        _solarSystemLabel.setHTMLColor( BUTTON_DESELECTED_COLOR );
        _bohrLabel.setHTMLColor( BUTTON_DESELECTED_COLOR );
        _deBroglieLabel.setHTMLColor( BUTTON_DESELECTED_COLOR );
        _schrodingerLabel.setHTMLColor( BUTTON_DESELECTED_COLOR );
        
        PNode selectedButton = null;
        if ( _selectedModel == AtomicModel.BILLIARD_BALL ) {
            selectedButton = _billiardBallButton;
            _billiardBallLabel.setHTMLColor( BUTTON_SELECTED_COLOR );
        }
        else if ( _selectedModel == AtomicModel.PLUM_PUDDING ) {
            selectedButton = _plumPuddingButton;
            _plumPuddingLabel.setHTMLColor( BUTTON_SELECTED_COLOR );
        }
        else if ( _selectedModel == AtomicModel.SOLAR_SYSTEM ) {
            selectedButton = _solarSystemButton;
            _solarSystemLabel.setHTMLColor( BUTTON_SELECTED_COLOR );
        }
        else if ( _selectedModel == AtomicModel.BOHR ) {
            selectedButton = _bohrButton;
            _bohrLabel.setHTMLColor( BUTTON_SELECTED_COLOR );
        }
        else if ( _selectedModel == AtomicModel.DEBROGLIE ) {
            selectedButton = _deBroglieButton;
            _deBroglieLabel.setHTMLColor( BUTTON_SELECTED_COLOR );
        }
        else if ( _selectedModel == AtomicModel.SCHRODINGER ) {
            selectedButton = _schrodingerButton;
            _schrodingerLabel.setHTMLColor( BUTTON_SELECTED_COLOR );
        }
        PBounds sbb = selectedButton.getFullBounds();
        PBounds sib = _selectionIndicator.getFullBounds();
        double x = sbb.getX() - ( (sib.getWidth() - sbb.getWidth() ) / 2 );
        double y = sbb.getY() - ( (sib.getHeight() - sbb.getHeight() ) / 2 );
        _selectionIndicator.setOffset( x, y );
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
