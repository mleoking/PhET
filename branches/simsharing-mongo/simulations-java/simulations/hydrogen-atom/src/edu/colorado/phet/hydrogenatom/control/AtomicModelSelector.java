// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.hydrogenatom.control;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.HAResources;
import edu.colorado.phet.hydrogenatom.enums.AtomicModel;
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

    private final ArrayList<ButtonNode> buttons;
    private final PPath _selectionIndicator;
    private final EventListenerList _listenerList;

    private AtomicModel _selectedModel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public AtomicModelSelector() {

        buttons = new ArrayList<ButtonNode>();

        // Fonts
        Font titleFont = new PhetFont( Font.BOLD, HAResources.getInt( "atomicModelSelector.title.font.size", HAConstants.DEFAULT_FONT_SIZE ) );
        Font continuumFont = new PhetFont( Font.PLAIN, HAResources.getInt( "atomicModelSelector.continuum.font.size", HAConstants.DEFAULT_FONT_SIZE ) );
        Font buttonFont = new PhetFont( Font.BOLD, HAResources.getInt( "atomicModelSelector.button.font.size", HAConstants.DEFAULT_FONT_SIZE ) );

        // Panel
        PImage panel = HAResources.getImageNode( HAConstants.IMAGE_ATOMIC_MODEL_PANEL );

        // Title
        HTMLNode titleNode = new HTMLNode( HAResources.getString( "label.atomicModel" ) );
        titleNode.setFont( titleFont );
        titleNode.setHTMLColor( TITLE_COLOR );

        // Continuum range labels
        PText classicalLabel = new PText( HAResources.getString( "label.classical" ) );
        classicalLabel.setFont( continuumFont );
        classicalLabel.setTextPaint( CONTINUUM_QUANTUM_COLOR ); // use opposite color for text

        PText quantumLabel = new PText( HAResources.getString( "label.quantum" ) );
        quantumLabel.setFont( continuumFont );
        quantumLabel.setTextPaint( CONTINUUM_CLASSICAL_COLOR ); // use opposite color for text

        // Buttons
        VoidFunction1<AtomicModel> selectionFunction = new VoidFunction1<AtomicModel>() {
            public void apply( AtomicModel model ) {
                setSelection( model );
            }
        };
        buttons.add( new ButtonNode( AtomicModel.BILLIARD_BALL, "button.billiardBall", buttonFont, HAConstants.IMAGE_BILLIARD_BALL_BUTTON, selectionFunction ) );
        buttons.add( new ButtonNode( AtomicModel.PLUM_PUDDING, "button.plumPudding", buttonFont, HAConstants.IMAGE_PLUM_PUDDING_BUTTON, selectionFunction ) );
        buttons.add( new ButtonNode( AtomicModel.SOLAR_SYSTEM, "button.solarSystem", buttonFont, HAConstants.IMAGE_SOLAR_SYSTEM_BUTTON, selectionFunction ) );
        buttons.add( new ButtonNode( AtomicModel.BOHR, "button.bohr", buttonFont, HAConstants.IMAGE_BOHR_BUTTON, selectionFunction ) );
        buttons.add( new ButtonNode( AtomicModel.DEBROGLIE, "button.deBroglie", buttonFont, HAConstants.IMAGE_DEBROGLIE_BUTTON, selectionFunction ) );
        buttons.add( new ButtonNode( AtomicModel.SCHRODINGER, "button.schrodinger", buttonFont, HAConstants.IMAGE_SCHRODINGER_BUTTON, selectionFunction ) );

        // Continuum dimensions
        double continuumWidth = 0;
        double continuumHeight = 0;
        {
            double w1 = classicalLabel.getFullBounds().getHeight(); // will be rotated 90 degrees
            double w2 = quantumLabel.getFullBounds().getHeight(); // will be rotated 90 degrees
            continuumWidth = Math.max( w1, w2 ) + 2;
            continuumHeight = 0;
            for ( ButtonNode button : buttons ) {
                continuumHeight += button.getFullBoundsReference().getHeight();
            }
            continuumHeight += ( buttons.size() - 1 ) * BUTTON_SPACING;
        }

        // Panel dimensions
        {
            // width
            double maxNodeWidth = titleNode.getFullBoundsReference().getWidth();
            for ( ButtonNode button : buttons ) {
                maxNodeWidth = Math.max( maxNodeWidth, button.getFullBoundsReference().getWidth() );
            }
            double panelWidth = maxNodeWidth + CONTINUUM_SPACING + continuumWidth + ( 2 * X_MARGIN );

            // height
            double panelHeight = Y_MARGIN + titleNode.getFullBoundsReference().getHeight() + BUTTON_SPACING + continuumHeight + Y_MARGIN;
            panelHeight += ( 2 * Y_MARGIN ); // a little extra space at the bottom

            // Scale the panel image
            PBounds pb = panel.getFullBounds();
            double scaleX = panelWidth / pb.getWidth();
            double scaleY = panelHeight / pb.getHeight();
            AffineTransform xform = new AffineTransform();
            xform.scale( scaleX, scaleY );
            panel.setTransform( xform );
        }

        // Selection indicator, appears behind selected button's image.
        {
            _selectionIndicator = new PPath();
            double w = panel.getFullBounds().getWidth() - continuumWidth - CONTINUUM_SPACING - ( 2 * X_MARGIN );
            double h = buttons.get( 0 ).getImageHeight() + 4; // Assumes that all buttons are the same size!
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
            GradientPaint gradient = new GradientPaint( 0f, 0f, CONTINUUM_CLASSICAL_COLOR, (float) w, 0f, CONTINUUM_QUANTUM_COLOR );
            track.setPaint( gradient );

            continuumNode.addChild( track );
            continuumNode.addChild( classicalLabel );
            continuumNode.addChild( quantumLabel );

            final double xmargin = 5;
            track.setOffset( 0, 0 );
            classicalLabel.setOffset( xmargin,
                                      ( track.getFullBounds().getHeight() - classicalLabel.getFullBounds().getHeight() ) / 2 );
            quantumLabel.setOffset( track.getFullBounds().getWidth() - quantumLabel.getFullBounds().getWidth() - xmargin,
                                    ( track.getFullBounds().getHeight() - quantumLabel.getFullBounds().getHeight() ) / 2 );
        }

        // Layout, front to back
        addChild( panel );
        addChild( titleNode );
        addChild( _selectionIndicator );
        for ( ButtonNode button : buttons ) {
            addChild( button );
        }
        addChild( continuumNode );

        // Positioning
        {
            panel.setOffset( 0, 0 );
            double panelWidth = panel.getFullBounds().getWidth();

            titleNode.setOffset( ( panelWidth - titleNode.getFullBounds().getWidth() ) / 2, Y_MARGIN );

            PNode previousNode = titleNode;
            for ( ButtonNode button : buttons ) {
                setOffsetCentered( button, previousNode, panel, continuumNode, BUTTON_SPACING );
                previousNode = button;
            }

            PBounds pb = panel.getFullBounds();
            AffineTransform xform = new AffineTransform();
            xform.translate( pb.getWidth() - continuumNode.getFullBounds().getHeight() - X_MARGIN, buttons.get( 0 ).getFullBounds().getY() );
            xform.rotate( Math.toRadians( 90 ) );
            xform.translate( 0, -continuumNode.getFullBounds().getHeight() );
            continuumNode.setTransform( xform );
        }

        // Event handling 
        {
            _listenerList = new EventListenerList();
            panel.setPickable( false );
            continuumNode.setPickable( false );
            titleNode.setPickable( false );
            classicalLabel.setPickable( false );
            quantumLabel.setPickable( false );
            _selectionIndicator.setPickable( false );
        }

        // Default state
        setSelection( AtomicModel.BILLIARD_BALL );
    }

    /*
     * Handles alignment of a button.
     */
    private void setOffsetCentered( PNode node, PNode nodeAbove, PNode panel, PNode continuumNode, double spacing ) {
        double x = ( panel.getFullBounds().getWidth() - continuumNode.getFullBounds().getHeight() - CONTINUUM_SPACING - node.getFullBounds().getWidth() ) / 2;
        double y = nodeAbove.getFullBounds().getMaxY() + spacing;
        node.setOffset( x, y );
    }

    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the selection.
     *
     * @param model which model to select
     */
    public void setSelection( AtomicModel model ) {
        _selectedModel = model;
        updateUI();
        fireChangeEvent( new ChangeEvent( this ) );
    }

    /**
     * Sets the selection.
     *
     * @return AtomicModel
     */
    public AtomicModel getSelection() {
        return _selectedModel;
    }


    /**
     * Gets the name of the model that is selected.
     * This name will not contain any HTML markup; it will be a simple text string.
     *
     * @return String
     */
    public String getSelectionName() {
        String resourceName = null;
        if ( _selectedModel == AtomicModel.BILLIARD_BALL ) {
            resourceName = "label.billiardBall";
        }
        else if ( _selectedModel == AtomicModel.PLUM_PUDDING ) {
            resourceName = "label.plumPudding";
        }
        else if ( _selectedModel == AtomicModel.SOLAR_SYSTEM ) {
            resourceName = "label.solarSystem";
        }
        else if ( _selectedModel == AtomicModel.BOHR ) {
            resourceName = "label.bohr";
        }
        else if ( _selectedModel == AtomicModel.DEBROGLIE ) {
            resourceName = "label.deBroglie";
        }
        else if ( _selectedModel == AtomicModel.SCHRODINGER ) {
            resourceName = "label.schrodinger";
        }
        return HAResources.getString( resourceName );
    }

    /*
     * Gets the selected button.
     */
    private ButtonNode getSelectedButton() {
        ButtonNode selectedButton = null;
        for ( ButtonNode button : buttons ) {
            if ( button.getModel() == _selectedModel ) {
                selectedButton = button;
            }
        }
        assert ( selectedButton != null );
        return selectedButton;
    }

    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /*
    * Updates the UI to match the selected model.
    * This controls highlighting, etc.
    */
    private void updateUI() {

        // set selection state of each button
        for ( ButtonNode button : buttons ) {
            button.setSelection( _selectedModel );
        }

        // position the selection indicator behind the selected button's image
        PBounds sbb = getSelectedButton().getFullBounds();
        PBounds sib = _selectionIndicator.getFullBounds();
        double x = sbb.getX() - ( ( sib.getWidth() - sbb.getWidth() ) / 2 );
        double y = sbb.getMaxY() - sib.getHeight() + ( ( sib.getHeight() - getSelectedButton().getImageHeight() ) / 2 );
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
        for ( int i = 0; i < listeners.length; i += 2 ) {
            if ( listeners[i] == ChangeListener.class ) {
                ( (ChangeListener) listeners[i + 1] ).stateChanged( event );
            }
        }
    }

    /*
     * Button that consists of a label centered above an image.
     * Origin is at upper-left corner of bounding rectangle.
     * When the button is pressed, a specified function is called.
     */
    private static class ButtonNode extends PNode {

        private final AtomicModel model;
        private final HTMLNode htmlNode;
        private final PImage imageNode;

        public ButtonNode( final AtomicModel model, String htmlResourceName, Font font, String imageResourceName, final VoidFunction1<AtomicModel> pressedFunction ) {

            this.model = model;

            // label
            htmlNode = new HTMLNode( HAResources.getString( htmlResourceName ) );
            htmlNode.setFont( font );
            addChild( htmlNode );

            // image
            imageNode = new PImage( HAResources.getImage( imageResourceName ) );
            addChild( imageNode );

            // layout, label centered above image
            double maxWidth = Math.max( htmlNode.getFullBounds().getWidth(), imageNode.getFullBoundsReference().getWidth() );
            double x = ( maxWidth / 2 ) - ( htmlNode.getFullBoundsReference().getWidth() / 2 );
            double y = 0;
            htmlNode.setOffset( x, y );
            x = ( maxWidth / 2 ) - ( imageNode.getFullBoundsReference().getWidth() / 2 );
            y = htmlNode.getFullBoundsReference().getMaxY() + LABEL_SPACING;
            imageNode.setOffset( x, y );

            // events
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    pressedFunction.apply( model );
                }
            } );
        }

        public void setSelection( AtomicModel model ) {
            htmlNode.setHTMLColor( model == this.model ? BUTTON_SELECTED_COLOR : BUTTON_DESELECTED_COLOR );
        }

        public AtomicModel getModel() {
            return model;
        }

        public double getImageHeight() {
            return imageNode.getFullBoundsReference().getHeight();
        }
    }
}
