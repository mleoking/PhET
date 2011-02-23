// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.lightreflectionandrefraction.view.LRARResetAllButtonNode;
import edu.colorado.phet.lightreflectionandrefraction.view.LightReflectionAndRefractionCanvas;
import edu.colorado.phet.lightreflectionandrefraction.view.MediumControlPanel;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PrismsCanvas extends LightReflectionAndRefractionCanvas<PrismsModel> {
    private PNode prismLayer = new PNode();

    public PrismsCanvas( final PrismsModel model, BooleanProperty moduleActive, Resettable resetAll ) {
        super( model, moduleActive, new Function1.Identity<Double>(), new Function1.Constant<Double, Boolean>( true ), new Function1.Constant<Double, Boolean>( true ), false, resetAll );
        for ( Prism prism : model.getPrisms() ) {
            addChild( new PrismNode( transform, prism, model.prismMedium ) );
        }

        model.outerMedium.addObserver( new SimpleObserver() {
            public void update() {
                final Color color = model.colorMappingFunction.getValue().apply( model.outerMedium.getValue().getIndexOfRefraction() );
                setBackground( new Color( 255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue() ) );
//                setBackground( Color.black );
            }
        } );

        beforeLightLayer.addChild( new ControlPanelNode( new MediumControlPanel( this, model.outerMedium, model.colorMappingFunction ) ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, 10 );
        }} );

        final ControlPanelNode prismToolbox = new ControlPanelNode( new PrismToolboxNode( this, transform, model ) ) {{
            setOffset( 10, stageSize.height - getFullBounds().getHeight() - 10 );
        }};
        beforeLightLayer.addChild( prismToolbox );

        final ControlPanelNode prismMediumControlPanel = new ControlPanelNode( new MediumControlPanel( this, model.prismMedium, model.colorMappingFunction ) ) {{
            setOffset( prismToolbox.getFullBounds().getMaxX() + 10, stageSize.height - getFullBounds().getHeight() - 10 );
        }};
        beforeLightLayer.addChild( prismMediumControlPanel );

        final LRARResetAllButtonNode resetButton = new LRARResetAllButtonNode( resetAll, this ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, prismMediumControlPanel.getFullBounds().getMinY() - 10 - getFullBounds().getHeight() );
        }};
        beforeLightLayer.addChild( resetButton );

        final LaserControlPanelNode laserControlPanelNode = new LaserControlPanelNode( model.manyRays, laserView, model.getLaser().color, model.showReflections, showNormal ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, resetButton.getFullBounds().getMinY() - 10 - getFullBounds().getHeight() );
        }};
        beforeLightLayer.addChild( laserControlPanelNode );

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