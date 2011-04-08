// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.*;

import edu.colorado.phet.bendinglight.model.BendingLightModel;
import edu.colorado.phet.bendinglight.model.MediumColorFactory;
import edu.colorado.phet.bendinglight.view.BendingLightCanvas;
import edu.colorado.phet.bendinglight.view.BendingLightResetAllButtonNode;
import edu.colorado.phet.bendinglight.view.MediumControlPanel;
import edu.colorado.phet.bendinglight.view.ProtractorNode;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PrismsCanvas extends BendingLightCanvas<PrismsModel> {
    private PNode prismLayer = new PNode();

    public PrismsCanvas( final PrismsModel model, BooleanProperty moduleActive, Resettable resetAll ) {
        super( model, moduleActive, new Function1.Identity<Double>(), new Function1.Constant<Double, Boolean>( true ), new Function1.Constant<Double, Boolean>( true ), false,
               new Function2<Shape, Shape, Shape>() {
                   public Shape apply( Shape full, Shape front ) {
                       return front;
                   }
               }, new Function2<Shape, Shape, Shape>() {
                    public Shape apply( Shape full, Shape back ) {
                        return back;
                    }
                }, "laser_knob.png", 150 );
        for ( Prism prism : model.getPrisms() ) {
            addChild( new PrismNode( transform, prism, model.prismMedium ) );
        }

        model.environment.addObserver( new SimpleObserver() {
            public void update() {
                final Color color = MediumColorFactory.getColor( model.environment.getValue().getIndexOfRefraction( BendingLightModel.WAVELENGTH_RED ) );
//                setBackground( new Color( 255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue() ) ); //black background
                setBackground( new Color( color.getRed(), color.getGreen(), color.getBlue() ) );//white background
            }
        } );

        afterLightLayer.addChild( new ControlPanelNode( new MediumControlPanel( this, model.environment, "Environment:", false, model.wavelengthProperty, "0.0000000", 8 ) ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, 10 );
        }} );

        final ControlPanelNode prismToolbox = new ControlPanelNode( new PrismToolboxNode( this, transform, model ) ) {{
            setOffset( 10, stageSize.height - getFullBounds().getHeight() - 10 );
        }};
        beforeLightLayer.addChild( prismToolbox );
        afterLightLayer.addChild( new BendingLightResetAllButtonNode( resetAll, this, stageSize ) );

        //Put the laser control panel node where it leaves enough vertical space for reset button between it and prism control panel
        final LaserControlPanelNode laserControlPanelNode = new LaserControlPanelNode( model.manyRays, model.getLaser().color, model.showReflections, showNormal, showProtractor, model.wavelengthProperty ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, stageSize.height / 2 - getFullBounds().getHeight() / 2 );
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

        addChild( new ProtractorNode( transform, showProtractor, model.getProtractorModel(), new Function2<Shape, Shape, Shape>() {
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