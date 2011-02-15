// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lightreflectionandrefraction.view.LightReflectionAndRefractionCanvas;
import edu.colorado.phet.lightreflectionandrefraction.view.MediumControlPanel;
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

        //add a line that will show the border between the mediums even when both n's are the same... Just a thin line will be fine.
        addChild( new PhetPPath( transform.modelToView( new Line2D.Double( -1, 0, 1, 0 ) ), new BasicStroke( 0.5f ), Color.gray ) {{
            setPickable( false );
        }} );

        addChild( new NormalLine( transform, model.getHeight() ) {{
            showNormal.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( showNormal.getValue() );
                }
            } );
        }} );
    }
}
