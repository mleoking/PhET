// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import fj.F;
import fj.data.List;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.activities.PActivityDelegateAdapter;
import edu.colorado.phet.common.piccolophet.event.DynamicCursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeType;
import edu.colorado.phet.fractions.buildafraction.view.DisablePickingWhileAnimating;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.common.view.SpinnerButtonNode;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager.sendButtonPressed;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain.chain;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.fractions.FractionsResources.Images.*;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.SingleContainerNode._undoAll;
import static edu.colorado.phet.fractions.common.math.Fraction.sum;
import static edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas.INSET;
import static edu.colorado.phet.fractions.common.view.FNode.getChildren;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components.*;

/**
 * Container that can be subdivided into different divisions
 *
 * @author Sam Reid
 */
public class ContainerNode extends PNode {

    //Button that can be used to send all pieces back to the toolbox.
    private final UndoButton undoButton;

    //For showing the divisions
    final IntegerProperty selectedPieceSize = new IntegerProperty( 1 );

    private final DynamicCursorHandler dynamicCursorHandler;
    public final ShapeSceneNode parent;
    public final ContainerContext context;
    private final ShapeType shapeType;
    private boolean inTargetCell = false;
    private final PNode containerLayer;
    private double initialX;
    private double initialY;
    private double initialScale = 1;
    private final SpinnerButtonNode leftSpinner;
    private final SpinnerButtonNode rightSpinner;
    private final IncreaseDecreaseButton increaseDecreaseButton;
    private final boolean showIncreaseButton;

    public ContainerNode( ShapeSceneNode parent, final ContainerContext context, boolean showIncreaseButton, final ShapeType shapeType ) {
        this.parent = parent;
        this.context = context;
        this.shapeType = shapeType;

        undoButton = new UndoButton( chain( Components.playAreaUndoButton, ContainerNode.this.hashCode() ) );
        addChild( undoButton );
        undoButton.setVisible( false );
        undoButton.setPickable( false );

        increaseDecreaseButton = new IncreaseDecreaseButton( new VoidFunction0() {
            public void apply() {
                sendButtonPressed( chain( increaseContainersButton, ContainerNode.this.hashCode() + "" ) );
                addContainer();
            }
        }, new VoidFunction0() {
            public void apply() {
                sendButtonPressed( chain( decreaseContainersButton, ContainerNode.this.hashCode() + "" ) );
                removeContainer();
            }
        }
        );
        if ( shapeType == ShapeType.BAR ) {
            undoButton.translate( -undoButton.getFullBounds().getWidth(),
                                  -undoButton.getFullBounds().getHeight() );
        }
        else {
            undoButton.translate( -undoButton.getFullBounds().getWidth() / 2,
                                  -undoButton.getFullBounds().getHeight() / 2 );
        }
        dynamicCursorHandler = new DynamicCursorHandler( Cursor.HAND_CURSOR );
        undoButton.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                undoAll();
            }
        } );
        undoButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( final PInputEvent event ) {
                sendButtonPressed( chain( Components.playAreaUndoButton, ContainerNode.this.hashCode() ) );

                dynamicCursorHandler.setCursor( Cursor.DEFAULT_CURSOR );
            }
        } );
        final VoidFunction1<Boolean> increment = new VoidFunction1<Boolean>() {
            public void apply( final Boolean autoSpinning ) {
                sendButtonPressed( chain( incrementDivisionsButton, ContainerNode.this.hashCode() + "" ) );
                selectedPieceSize.increment();
            }
        };
        final VoidFunction1<Boolean> decrement = new VoidFunction1<Boolean>() {
            public void apply( final Boolean autoSpinning ) {
                sendButtonPressed( chain( decrementDivisionsButton, ContainerNode.this.hashCode() + "" ) );
                selectedPieceSize.decrement();
            }
        };
        containerLayer = new PNode() {{
            addChild( new SingleContainerNode( shapeType, ContainerNode.this, selectedPieceSize ) );
        }};
        leftSpinner = new SpinnerButtonNode( spinnerImage( LEFT_BUTTON_UP_GREEN ), spinnerImage( LEFT_BUTTON_PRESSED_GREEN ), spinnerImage( LEFT_BUTTON_GRAY ), decrement, selectedPieceSize.greaterThan( 1 ) );
        rightSpinner = new SpinnerButtonNode( spinnerImage( RIGHT_BUTTON_UP_GREEN ), spinnerImage( RIGHT_BUTTON_PRESSED_GREEN ), spinnerImage( RIGHT_BUTTON_GRAY ), increment, selectedPieceSize.lessThan( 8 ) );
        addChild( new VBox( containerLayer,
                            new HBox( leftSpinner, rightSpinner ) ) );

        this.showIncreaseButton = showIncreaseButton;
        if ( this.showIncreaseButton ) {
            addChild( increaseDecreaseButton );
            increaseDecreaseButton.setOffset( containerLayer.getFullBounds().getMaxX() + INSET, containerLayer.getFullBounds().getCenterY() - increaseDecreaseButton.getFullBounds().getHeight() / 2 );
        }
    }

    List<SingleContainerNode> getSingleContainerNodes() {return getSingleContainers(); }

    public static final F<ContainerNode, List<SingleContainerNode>> _getSingleContainerNodes = new F<ContainerNode, List<SingleContainerNode>>() {
        @Override public List<SingleContainerNode> f( final ContainerNode c ) {
            return c.getSingleContainerNodes();
        }
    };

    private void addContainer() {
        final SingleContainerNode child = new SingleContainerNode( shapeType, this, selectedPieceSize );
        child.setOffset( containerLayer.getFullBounds().getMaxX() + INSET, containerLayer.getFullBounds().getY() );
        child.setTransparency( 0 );
        containerLayer.addChild( child );
        increaseDecreaseButton.animateToPositionScaleRotation( child.getFullBounds().getMaxX() + INSET, child.getFullBounds().getCenterY() - increaseDecreaseButton.getFullBounds().getHeight() / 2, 1, 0, BuildAFractionModule.ANIMATION_TIME ).setDelegate( new DisablePickingWhileAnimating( increaseDecreaseButton, true ) );
        if ( getSingleContainerNodes().length() >= 2 ) {
            increaseDecreaseButton.hideIncreaseButton();
        }
        PInterpolatingActivity activity = increaseDecreaseButton.showDecreaseButton();
        activity.setDelegate( new CompositeDelegate( new PActivityDelegateAdapter() {
            public void activityFinished( final PActivity activity ) {
                child.animateToTransparency( 1, BuildAFractionModule.ANIMATION_TIME );
                context.containerAdded( ContainerNode.this );
            }
        }, new DisablePickingWhileAnimating( child, true ) ) );
    }

    //When the user presses a "-" button to remove a container from a group, any pieces in the container should go back to the toolbox
    private void removeContainer() {

        //Container to be removed
        final SingleContainerNode last = getSingleContainerNodes().last();

        //if any pieces were in the container, send them back to the toolbox.
        last.undoAll();

        PActivity activity = last.animateToTransparency( 0, BuildAFractionModule.ANIMATION_TIME );
        activity.setDelegate( new PActivityDelegateAdapter() {
            public void activityFinished( final PActivity activity ) {
                containerLayer.removeChild( last );
                final SingleContainerNode child = getSingleContainerNodes().last();
                increaseDecreaseButton.animateToPositionScaleRotation( child.getFullBounds().getMaxX() + INSET, child.getFullBounds().getCenterY() - increaseDecreaseButton.getFullBounds().getHeight() / 2, 1, 0, BuildAFractionModule.ANIMATION_TIME ).setDelegate( new DisablePickingWhileAnimating( increaseDecreaseButton, true ) );

                if ( getSingleContainerNodes().length() <= 1 ) {
                    increaseDecreaseButton.hideDecreaseButton();
                }
            }
        } );

        increaseDecreaseButton.showIncreaseButton();
    }

    private static BufferedImage spinnerImage( final BufferedImage image ) { return multiScaleToWidth( image, 50 ); }

    public static final F<ContainerNode, Boolean> _isInTargetCell = new F<ContainerNode, Boolean>() {
        @Override public Boolean f( final ContainerNode containerNode ) {
            return containerNode.isInTargetCell();
        }
    };

    public static final F<ContainerNode, Fraction> _getFractionValue = new F<ContainerNode, Fraction>() {
        @Override public Fraction f( final ContainerNode containerNode ) {
            return containerNode.getFractionValue();
        }
    };

    public void undoAll() {
        getSingleContainers().foreach( _undoAll );
        PInterpolatingActivity activity = undoButton.animateToTransparency( 0, BuildAFractionModule.ANIMATION_TIME );
        activity.setDelegate( new PActivityDelegateAdapter() {
            public void activityFinished( final PActivity activity ) {
                undoButton.setVisible( false );
                undoButton.setPickable( false );
                dynamicCursorHandler.setCursor( Cursor.DEFAULT_CURSOR );
            }
        } );
        context.syncModelFractions();
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
        animateToPositionScaleRotation( initialX, initialY, initialScale, 0, BuildAFractionModule.ANIMATION_TIME ).setDelegate( new DisablePickingWhileAnimating( this, true ) );
        animateToShowSpinners();
    }

    public void animateToShowSpinners() {
        leftSpinner.animateToTransparency( 1, BuildAFractionModule.ANIMATION_TIME );
        rightSpinner.animateToTransparency( 1, BuildAFractionModule.ANIMATION_TIME );
    }

    public Fraction getFractionValue() {
        return sum( getSingleContainers().map( SingleContainerNode._getFractionValue ) );
    }

    private List<SingleContainerNode> getSingleContainers() {return getChildren( containerLayer, SingleContainerNode.class );}

    //Get rid of it because it disrupts the layout when dropping into the scoring cell.
    public void removeUndoButton() { removeChild( undoButton ); }

    public void addBackUndoButton() { addChild( undoButton ); }

    boolean isAtStartingLocation() { return getXOffset() == initialX && getYOffset() == initialY; }

    public Boolean isInTargetCell() {return inTargetCell;}

    public void setInTargetCell( final boolean inTargetCell, int targetDenominator ) {
        this.inTargetCell = inTargetCell;
        leftSpinner.animateToTransparency( inTargetCell ? 0 : 1, BuildAFractionModule.ANIMATION_TIME );
        rightSpinner.animateToTransparency( inTargetCell ? 0 : 1, BuildAFractionModule.ANIMATION_TIME );

        while ( getChildrenReference().contains( increaseDecreaseButton ) ) {
            removeChild( increaseDecreaseButton );
        }
        if ( !inTargetCell && showIncreaseButton ) {
            addChild( increaseDecreaseButton );
        }

        for ( SingleContainerNode node : getSingleContainerNodes() ) {
            node.setInTargetCell();
        }
        if ( inTargetCell && targetDenominator > 0 ) {
            selectedPieceSize.set( targetDenominator );
        }
    }

    public void setInitialState( final double x, final double y, final double scale ) {
        this.initialX = x;
        this.initialY = y;
        this.initialScale = scale;
        setOffset( x, y );
        setScale( scale );
    }

    //Identify containers as being in the toolbox if they are shrunken
    public boolean isInToolbox() { return Math.abs( getScale() - PieceIconNode.TINY_SCALE ) < 1E-6; }

    public boolean belongsInToolbox() {return initialY > 500;}

    public void pieceAdded() {
        if ( !undoButton.getVisible() ) {
            undoButton.setVisible( true );
            undoButton.setPickable( true );
            undoButton.setTransparency( 0 );
            undoButton.animateToTransparency( 1, BuildAFractionModule.ANIMATION_TIME );
            dynamicCursorHandler.setCursor( Cursor.HAND_CURSOR );
        }
    }

    public void resetNumberOfContainers() {
        for ( int i = 1; i < getSingleContainerNodes().length(); i++ ) {
            removeContainer();
        }
    }

    public Boolean isInPlayArea() {
        return !isInTargetCell() && !isInToolbox();
    }
}