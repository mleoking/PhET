// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.lightreflectionandrefraction.view.LightReflectionAndRefractionCanvas;
import edu.colorado.phet.lightreflectionandrefraction.view.MediumControlPanel;

/**
 * @author Sam Reid
 */
public class PrismsCanvas extends LightReflectionAndRefractionCanvas<PrismsModel> {
    public PrismsCanvas( final PrismsModel model ) {
        super( model );
        for ( Prism prism : model.getPrisms() ) {
            addChild( new PrismNode( transform, prism, model.prismMedium ) );
        }

        model.outerMedium.addObserver( new SimpleObserver() {
            public void update() {
                setBackground( model.colorMappingFunction.getValue().apply( model.outerMedium.getValue().getIndexOfRefraction() ) );
            }
        } );

        addChild( new ControlPanelNode( new MediumControlPanel( this, model.outerMedium, model.colorMappingFunction ) ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, transform.modelToViewY( 0 ) - 10 - getFullBounds().getHeight() );
        }} );
        addChild( new ControlPanelNode( new MediumControlPanel( this, model.prismMedium, model.colorMappingFunction ) ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, transform.modelToViewY( 0 ) + 10 );
        }} );
    }
}
