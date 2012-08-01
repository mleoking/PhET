// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import fj.F;
import fj.data.List;

import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.DynamicCursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeType;
import edu.colorado.phet.fractions.common.view.SpinnerButtonNode;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.fractions.FractionsResources.Images.*;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.SingleContainerNode._splitAll;
import static edu.colorado.phet.fractions.common.view.FNode.getChildren;
import static edu.colorado.phet.fractions.fractionsintro.common.view.AbstractFractionsCanvas.INSET;
import static edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction.sum;

/**
 * Container that can be subdivided into different divisions
 *
 * @author Sam Reid
 */
public class ContainerNode extends PNode {
    public PImage splitButton;
    final IntegerProperty selectedPieceSize = new IntegerProperty( 1 );
    public final DynamicCursorHandler dynamicCursorHandler;
    public final ShapeSceneNode parent;
    public final ContainerContext context;
    private final ShapeType shapeType;
    private boolean inTargetCell = false;
    public final PNode containerLayer;
    private double initialX;
    private double initialY;
    private double initialScale = 1;
    private final ArrayList<VoidFunction0> listeners = new ArrayList<VoidFunction0>();
    private final SpinnerButtonNode leftSpinner;
    private final SpinnerButtonNode rightSpinner;
    private final IncreaseDecreaseButton increaseDecreaseButton;
    private final boolean showIncreaseButton;

    public ContainerNode( ShapeSceneNode parent, final ContainerContext context, boolean showIncreaseButton, final ShapeType shapeType ) {
        this.parent = parent;
        this.context = context;
        this.shapeType = shapeType;

        splitButton = new PImage( Images.SPLIT_BLUE );
        addChild( splitButton );
        splitButton.setVisible( false );
        splitButton.setPickable( false );

        increaseDecreaseButton = new IncreaseDecreaseButton( new VoidFunction0() {
            public void apply() {
                addContainer();
            }
        }, new VoidFunction0() {
            public void apply() {
                removeContainer();
            }
        }
        );
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
            addChild( new SingleContainerNode( shapeType, ContainerNode.this, selectedPieceSize ) );
        }};
        leftSpinner = new SpinnerButtonNode( spinnerImage( LEFT_BUTTON_UP_GREEN ), spinnerImage( LEFT_BUTTON_PRESSED_GREEN ), spinnerImage( LEFT_BUTTON_GRAY ), decrement, selectedPieceSize.greaterThan( 1 ) );
        rightSpinner = new SpinnerButtonNode( spinnerImage( RIGHT_BUTTON_UP_GREEN ), spinnerImage( RIGHT_BUTTON_PRESSED_GREEN ), spinnerImage( RIGHT_BUTTON_GRAY ), increment, selectedPieceSize.lessThan( 6 ) );
        addChild( new VBox( containerLayer,
                            new HBox( leftSpinner, rightSpinner ) ) );

        this.showIncreaseButton = showIncreaseButton;
        if ( this.showIncreaseButton ) {
            addChild( increaseDecreaseButton );
            increaseDecreaseButton.setOffset( containerLayer.getFullBounds().getMaxX() + INSET, containerLayer.getFullBounds().getCenterY() - increaseDecreaseButton.getFullBounds().getHeight() / 2 );
        }
    }

    public List<SingleContainerNode> getSingleContainerNodes() {return getSingleContainers(); }

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
        increaseDecreaseButton.animateToPositionScaleRotation( child.getFullBounds().getMaxX() + INSET, child.getFullBounds().getCenterY() - increaseDecreaseButton.getFullBounds().getHeight() / 2, 1, 0, 200 );
        if ( getSingleContainerNodes().length() >= 3 ) {
            increaseDecreaseButton.hideIncreaseButton();
        }
        PInterpolatingActivity activity = increaseDecreaseButton.showDecreaseButton();
        activity.setDelegate( new PActivityDelegate() {
            public void activityStarted( final PActivity activity ) {
            }

            public void activityStepped( final PActivity activity ) {
            }

            public void activityFinished( final PActivity activity ) {
                child.animateToTransparency( 1, 200 );
            }
        } );
    }

    private void removeContainer() {
        final SingleContainerNode last = getSingleContainerNodes().last();
        PActivity activity = last.animateToTransparency( 0, 200 );
        activity.setDelegate( new PActivityDelegate() {
            public void activityStarted( final PActivity activity ) {
            }

            public void activityStepped( final PActivity activity ) {
            }

            public void activityFinished( final PActivity activity ) {
                containerLayer.removeChild( last );
                final SingleContainerNode child = getSingleContainerNodes().last();
                increaseDecreaseButton.animateToPositionScaleRotation( child.getFullBounds().getMaxX() + INSET, child.getFullBounds().getCenterY() - increaseDecreaseButton.getFullBounds().getHeight() / 2, 1, 0, 200 );

                if ( getSingleContainerNodes().length() <= 1 ) {
                    increaseDecreaseButton.hideDecreaseButton();
                }
            }
        } );

        increaseDecreaseButton.showIncreaseButton();
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

        while ( getChildrenReference().contains( increaseDecreaseButton ) ) {
            removeChild( increaseDecreaseButton );
        }
        if ( !inTargetCell && showIncreaseButton ) {
            addChild( increaseDecreaseButton );
        }

        for ( SingleContainerNode node : getSingleContainerNodes() ) {
            node.setInTargetCell( inTargetCell );
        }
    }

    public void setInitialState( final double x, final double y, final double scale ) {
        this.initialX = x;
        this.initialY = y;
        this.initialScale = scale;
        setOffset( x, y );
        setScale( scale );
    }

    public boolean isInToolbox() {
        return isAtStartingLocation() && initialY > 500;
    }

    public void pieceAdded( final PieceNode piece ) {
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