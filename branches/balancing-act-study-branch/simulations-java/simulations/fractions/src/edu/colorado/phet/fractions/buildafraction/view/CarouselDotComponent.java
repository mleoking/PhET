// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.controlpanel.SettingsOnOffPanel;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain.chain;
import static edu.colorado.phet.fractions.FractionsResources.Strings.ONE_THROUGH_FIVE;
import static edu.colorado.phet.fractions.FractionsResources.Strings.SIX_THROUGH_TEN;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components.carouselRadioButton;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components.carouselRadioButtonLabel;

/**
 * Shows the different dots, one for each page and the labels above each dot.
 * Dot is filled in if that is where the user is.  Like on an ipad home screen or on angry birds.
 *
 * @author Sam Reid
 */
class CarouselDotComponent extends PNode {
    public CarouselDotComponent( final IntegerProperty selectedPage ) {
        final int circleDiameter = 16;

        addChild( new HBox( 23, new VBox( text( ONE_THROUGH_FIVE, selectedPage, 0 ), circle( selectedPage, circleDiameter, 0 ) ),
                            new VBox( text( SIX_THROUGH_TEN, selectedPage, 1 ), circle( selectedPage, circleDiameter, 1 ) ) ) );
    }

    //Creates a circle that indicates a particular page.  It is filled if it is the current page, otherwise empty.
    private PhetPPath circle( final IntegerProperty selectedPage, final int circleDiameter, final int index ) {
        return new PhetPPath( new Ellipse2D.Double( 0, 0, circleDiameter, circleDiameter ), new BasicStroke( 2 ), Color.gray ) {{
            selectedPage.addObserver( new VoidFunction1<Integer>() {
                public void apply( final Integer integer ) {
                    setPaint( integer == index ? Color.black : BuildAFractionCanvas.TRANSPARENT );
                }
            } );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseReleased( final PInputEvent event ) {
                    SimSharingManager.sendButtonPressed( chain( carouselRadioButton, index ) );
                    selectedPage.set( index );
                }
            } );
        }};
    }

    //Creates a text node that shows the name of a level above the carousel circrles.
    private PhetPText text( final String levelName, final IntegerProperty selectedPage, final int index ) {
        return new PhetPText( levelName, SettingsOnOffPanel.FONT ) {{
            selectedPage.addObserver( new VoidFunction1<Integer>() {
                public void apply( final Integer integer ) {
                    setTextPaint( integer == index ? Color.black : Color.gray );
                }
            } );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseReleased( final PInputEvent event ) {
                    SimSharingManager.sendButtonPressed( chain( carouselRadioButtonLabel, index ) );
                    selectedPage.set( index );
                }
            } );
        }};
    }
}