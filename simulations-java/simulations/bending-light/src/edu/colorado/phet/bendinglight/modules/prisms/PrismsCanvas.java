// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.*;

import edu.colorado.phet.bendinglight.view.*;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.*;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PrismsCanvas extends BendingLightCanvas<PrismsModel> {
    private PNode prismLayer = new PNode();

    public PrismsCanvas( final PrismsModel model, BooleanProperty moduleActive, Resettable resetAll ) {
        super( model, moduleActive, new Function1.Identity<Double>(), new Function1.Constant<Double, Boolean>( true ), new Function1.Constant<Double, Boolean>( true ), false, resetAll, new Function2<Shape, Shape, Shape>() {
            public Shape apply( Shape full, Shape front ) {
                return front;
            }
        }, new Function2<Shape, Shape, Shape>() {
            public Shape apply( Shape full, Shape back ) {
                return back;
            }
        }, "laser_knob.png" );
        for ( Prism prism : model.getPrisms() ) {
            addChild( new PrismNode( transform, prism, model.prismMedium ) );
        }

        model.environment.addObserver( new SimpleObserver() {
            public void update() {
                final Color color = model.colorMappingFunction.getValue().apply( model.environment.getValue().getIndexOfRefraction() );
                setBackground( new Color( 255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue() ) );
//                setBackground( Color.black );
            }
        } );

        afterLightLayer.addChild( new ControlPanelNode( new MediumControlPanel( this, model.environment, model.colorMappingFunction, "Environment:", false ) ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, 10 );
        }} );

        final ControlPanelNode prismToolbox = new ControlPanelNode( new PrismToolboxNode( this, transform, model ) ) {{
            setOffset( 10, stageSize.height - getFullBounds().getHeight() - 10 );
        }};
        beforeLightLayer.addChild( prismToolbox );

        final ControlPanelNode prismMediumControlPanel = new ControlPanelNode( new MediumControlPanel( this, model.prismMedium, model.colorMappingFunction, "Objects:", false ) ) {{
            setOffset( prismToolbox.getFullBounds().getMaxX() + 10, stageSize.height - getFullBounds().getHeight() - 10 );
        }};
        afterLightLayer.addChild( prismMediumControlPanel );

        final BendingLightResetAllButtonNode resetButton = new BendingLightResetAllButtonNode( resetAll, this ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, prismMediumControlPanel.getFullBounds().getMinY() - 10 - getFullBounds().getHeight() );
        }};
        afterLightLayer.addChild( resetButton );

        final LaserControlPanelNode laserControlPanelNode = new LaserControlPanelNode( model.manyRays, model.getLaser().color, model.showReflections, showNormal, showProtractor ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, resetButton.getFullBounds().getMinY() - 10 - getFullBounds().getHeight() );
        }};
        afterLightLayer.addChild( laserControlPanelNode );

        showNormal.addObserver( new SimpleObserver() {
            public void update() {
                model.updateModel();//could do this completely in the view, but simpler just to have the model recompute everything
            }
        } );
        model.addIntersectionListener( new VoidFunction1<Intersection>() {
            public void apply( Intersection intersection ) {
                if ( showNormal.getValue() ) {
                    final IntersectionNode node = new IntersectionNode( transform, intersection );
                    intersection.addCleanupListener( new VoidFunction0() {
                        public void apply() {
                            removeChild( node );
                        }
                    } );
                    addChild( node );
                }
            }
        } );
        beforeLightLayer.addChild( prismLayer );

        addChild( new ProtractorNode( transform, showProtractor, new ProtractorModel( 0, 0 ), new Function2<Shape, Shape, Shape>() {
            public Shape apply( Shape innerBar, Shape outerCircle ) {
                return innerBar;
            }
        }, new Function2<Shape, Shape, Shape>() {
            public Shape apply( Shape innerBar, Shape outerCircle ) {
                return outerCircle;
            }
        }, 0.65 ) {{
            showProtractor.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( showProtractor.getValue() );
                }
            } );
        }} );
    }

    public void addPrismNode( PrismNode node ) {
        prismLayer.addChild( node );
    }

    public void removePrismNode( PrismNode node ) {
        prismLayer.removeChild( node );
    }

    @Override
    public void resetAll() {
        super.resetAll();
        prismLayer.removeAllChildren();//TODO: see PrismToolboxNode for how prism nodes are created and managed; it doesn't use normal MVC pattern
    }
}