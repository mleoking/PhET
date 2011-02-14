// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import edu.colorado.phet.lightreflectionandrefraction.view.LightReflectionAndRefractionCanvas;
import edu.colorado.phet.lightreflectionandrefraction.view.MediumNode;

/**
 * @author Sam Reid
 */
public class IntroCanvas extends LightReflectionAndRefractionCanvas<IntroModel> {
    public IntroCanvas( IntroModel model ) {
        super( model );
        mediumNode.addChild( new MediumNode( transform, model.topMedium ) );
        mediumNode.addChild( new MediumNode( transform, model.bottomMedium ) );

        addChild( new ControlPanelNode( new MediumControlPanel( this, model.topMedium, model.colorMappingFunction ) ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, transform.modelToViewY( 0 ) - 10 - getFullBounds().getHeight() );
        }} );
        addChild( new ControlPanelNode( new MediumControlPanel( this, model.bottomMedium, model.colorMappingFunction ) ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, transform.modelToViewY( 0 ) + 10 );
        }} );
    }
}
