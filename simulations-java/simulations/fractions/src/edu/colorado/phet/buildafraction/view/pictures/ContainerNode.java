// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildafraction.view.pictures;

import fj.F;
import fj.data.List;

import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.DynamicCursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.view.SpinnerButtonNode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.buildafraction.view.pictures.SingleContainerNode._splitAll;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.fractions.FractionsResources.Images.*;
import static edu.colorado.phet.fractions.view.FNode.getChildren;
import static edu.colorado.phet.fractionsintro.intro.model.Fraction.sum;

/**
 * Container that can be subdivided into different divisions
 *
 * @author Sam Reid
 */
public class ContainerNode extends PNode {
    public PImage splitButton;
    final IntegerProperty selectedPieceSize = new IntegerProperty( 1 );
    public final DynamicCursorHandler dynamicCursorHandler;
    public final PictureSceneNode parent;
    public final ContainerContext context;
    private boolean inTargetCell = false;
    public final PNode containerLayer;
    private double initialX;
    private double initialY;
    private double initialScale = 1;
    private ArrayList<VoidFunction0> listeners = new ArrayList<VoidFunction0>();
    private final SpinnerButtonNode leftSpinner;
    private final SpinnerButtonNode rightSpinner;
    private final PImage increaseButton;

    public ContainerNode( PictureSceneNode parent, final ContainerContext context, boolean showIncreaseButton ) {
        this.parent = parent;
        this.context = context;

        splitButton = new PImage( Images.SPLIT_BLUE );
        addChild( splitButton );
        splitButton.setVisible( false );
        splitButton.setPickable( false );

        final BufferedImage greenButton = multiScaleToWidth( PLUS_BUTTON, 50 );
        final BufferedImage greenButtonPressed = multiScaleToWidth( PLUS_BUTTON_PRESSED, 50 );
        increaseButton = new PImage( greenButton ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                boolean pressed = false;

                @Override public void mousePressed( final PInputEvent event ) {
                    setImage( greenButtonPressed );
                    pressed = true;
                }

                @Override public void mouseReleased( final PInputEvent event ) {
                    if ( getImage() == greenButtonPressed ) {
                        fireIncreaseEvent();
                    }
                    setImage( greenButton );
                }

                @Override public void mouseExited( final PInputEvent event ) {
                    setImage( greenButton );
                }

                @Override public void mouseEntered( final PInputEvent event ) {
                    if ( pressed ) {
                        setImage( greenButtonPressed );
                    }
                }
            } );
        }};
        splitButton.translate( -splitButton.getFullBounds().getWidth(),
                               -splitButton.getFullBounds().getHeight() );
        dynamicCursorHandler = new DynamicCursorHandler( Cursor.HAND_CURSOR );
        splitButton.addInputEventListener( dynamicCursorHandler );
        splitButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( final PInputEvent event ) {
                SimSharingManager.sendButtonPressed( null );
                splitAll();
                dynamicCursorHandler.setCursor( Cursor.DEFAULT_CURSOR );
            }
        } );
        final VoidFunction1<Boolean> increment = new VoidFunction1<Boolean>() {
            public void apply( final Boolean autoSpinning ) {
                selectedPieceSize.increment();
            }
        };
        final VoidFunction1<Boolean> decrement = new VoidFunction1<Boolean>() {
            public void apply( final Boolean autoSpinning ) {
                selectedPieceSize.decrement();
            }
        };
        containerLayer = new PNode() {{
            addChild( new SingleContainerNode( ContainerNode.this, selectedPieceSize ) );
        }};
        leftSpinner = new SpinnerButtonNode( spinnerImage( LEFT_BUTTON_UP ), spinnerImage( LEFT_BUTTON_PRESSED ), spinnerImage( LEFT_BUTTON_GRAY ), decrement, selectedPieceSize.greaterThan( 1 ) );
        rightSpinner = new SpinnerButtonNode( spinnerImage( RIGHT_BUTTON_UP ), spinnerImage( RIGHT_BUTTON_PRESSED ), spinnerImage( RIGHT_BUTTON_GRAY ), increment, selectedPieceSize.lessThan( 6 ) );
        addChild( new VBox( containerLayer,
                            new HBox( leftSpinner, rightSpinner ) ) );

        if ( showIncreaseButton ) {
            addChild( increaseButton );
            increaseButton.setOffset( containerLayer.getFullBounds().getMaxX() + AbstractFractionsCanvas.INSET, containerLayer.getFullBounds().getCenterY() - increaseButton.getFullBounds().getHeight() / 2 );
        }
    }

    public List<SingleContainerNode> getSingleContainerNodes() {return getSingleContainers(); }

    public static final F<ContainerNode, List<SingleContainerNode>> _getSingleContainerNodes = new F<ContainerNode, List<SingleContainerNode>>() {
        @Override public List<SingleContainerNode> f( final ContainerNode c ) {
            return c.getSingleContainerNodes();
        }
    };

    private void fireIncreaseEvent() {
        final SingleContainerNode child = new SingleContainerNode( this, selectedPieceSize );
        child.setOffset( containerLayer.getFullBounds().getMaxX() + AbstractFractionsCanvas.INSET, containerLayer.getFullBounds().getY() );
        containerLayer.addChild( child );
    }

    public static BufferedImage spinnerImage( final BufferedImage image ) { return multiScaleToWidth( image, 50 ); }

    public static F<ContainerNode, Boolean> _isInTargetCell = new F<ContainerNode, Boolean>() {
        @Override public Boolean f( final ContainerNode containerNode ) {
            return containerNode.isInTargetCell();
        }
    };

    public static F<ContainerNode, Fraction> _getFractionValue = new F<ContainerNode, Fraction>() {
        @Override public Fraction f( final ContainerNode containerNode ) {
            return containerNode.getFractionValue();
        }
    };

    public double getYOffsetForContainer() { return containerLayer.getYOffset(); }

    public void splitAll() {
        getSingleContainers().foreach( _splitAll );
        PInterpolatingActivity activity = splitButton.animateToTransparency( 0, 200 );
        activity.setDelegate( new PActivityDelegate() {
            public void activityStarted( final PActivity activity ) {
            }

            public void activityStepped( final PActivity activity ) {
            }

            public void activityFinished( final PActivity activity ) {
                splitButton.setVisible( false );
                splitButton.setPickable( false );
                dynamicCursorHandler.setCursor( Cursor.DEFAULT_CURSOR );
            }
        } );
        context.syncModelFractions();
        notifyListeners();
    }

    public void setAllPickable( final boolean b ) {
        setPickable( b );
        setChildrenPickable( b );
    }

    public void setInitialPosition( final double x, final double y ) {
        this.initialX = x;
        this.initialY = y;
        setOffset( x, y );
    }

    public void animateHome() {
        animateToPositionScaleRotation( initialX, initialY, initialScale, 0, 200 );
        leftSpinner.animateToTransparency( 1, 200 );
        rightSpinner.animateToTransparency( 1, 200 );
    }

    void notifyListeners() {
        for ( VoidFunction0 listener : listeners ) {
            listener.apply();
        }
    }

    public Fraction getFractionValue() {
        return sum( getSingleContainers().map( SingleContainerNode._getFractionValue ) );
    }

    private List<SingleContainerNode> getSingleContainers() {return getChildren( containerLayer, SingleContainerNode.class );}

    //Get rid of it because it disrupts the layout when dropping into the scoring cell.
    public void removeSplitButton() { removeChild( splitButton ); }

    public void addBackSplitButton() { addChild( splitButton ); }

    public boolean isAtStartingLocation() { return getXOffset() == initialX && getYOffset() == initialY; }

    public static F<ContainerNode, Boolean> _isAtStartingLocation = new F<ContainerNode, Boolean>() {
        @Override public Boolean f( final ContainerNode containerNode ) {
            return containerNode.isAtStartingLocation();
        }
    };

    public Boolean isInTargetCell() {return inTargetCell;}

    public void setInTargetCell( final boolean inTargetCell ) {
        this.inTargetCell = inTargetCell;
        leftSpinner.animateToTransparency( inTargetCell ? 0 : 1, 200 );
        rightSpinner.animateToTransparency( inTargetCell ? 0 : 1, 200 );
    }

    public void setInitialState( final double x, final double y, final double scale ) {
        this.initialX = x;
        this.initialY = y;
        this.initialScale = scale;
        setOffset( x, y );
        setScale( scale );
    }

    public void addListener( final VoidFunction0 listener ) {
        listeners.add( listener );
    }

    public boolean isInToolbox() {
        return isAtStartingLocation() && initialY > 600;
    }

    public void pieceAdded( final RectangularPiece piece ) {
        if ( !splitButton.getVisible() ) {
            splitButton.setVisible( true );
            splitButton.setPickable( true );
            splitButton.setTransparency( 0 );
            splitButton.animateToTransparency( 1, 200 );
            dynamicCursorHandler.setCursor( Cursor.HAND_CURSOR );
        }
        notifyListeners();
    }
}