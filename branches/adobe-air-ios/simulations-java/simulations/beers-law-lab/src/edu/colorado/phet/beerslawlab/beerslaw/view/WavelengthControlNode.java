// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;

import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.Light;
import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Fill;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.WavelengthControl;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Control for wavelength.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class WavelengthControlNode extends PNode implements Resettable {

    private static final PhetFont FONT = new PhetFont( BLLConstants.CONTROL_FONT_SIZE );
    private static final Dimension WAVELENGTH_CONTROL_TRACK_SIZE = new Dimension( 150, 30 );

    private final Property<Boolean> variableWavelength = new Property<Boolean>( false ); // is the wavelength variable or fixed?

    public WavelengthControlNode( final Property<BeersLawSolution> solution, final Light light ) {

        JLabel wavelengthLabel = new JLabel( MessageFormat.format( Strings.PATTERN_0LABEL, Strings.WAVELENGTH ) );
        wavelengthLabel.setFont( FONT );

        PropertyRadioButton<Boolean> fixedRadioButton =
                new PropertyRadioButton<Boolean>( UserComponents.lambdaMaxRadioButton, Strings.FIXED, variableWavelength, false );
        fixedRadioButton.setFont( FONT );

        PropertyRadioButton<Boolean> variableRadioButton =
                new PropertyRadioButton<Boolean>( UserComponents.variableRadioButton, Strings.VARIABLE, variableWavelength, true );
        variableRadioButton.setFont( FONT );

        // Panel
        final int xSpacing = 4;
        final int ySpacing = 4;
        GridPanel panel = new GridPanel();
        panel.setInsets( new Insets( ySpacing, xSpacing, ySpacing, xSpacing ) );
        panel.setOpaque( false );
        panel.setAnchor( Anchor.WEST );
        panel.setFill( Fill.HORIZONTAL );
        panel.add( wavelengthLabel, 0, 0, 2, 1 );
        panel.add( fixedRadioButton, 1, 0 );
        panel.add( variableRadioButton, 1, 1 );
        PSwing panelNode = new PSwing( panel );

        // Wavelength control
        final BLLWavelengthControl wavelengthControl = new BLLWavelengthControl( WAVELENGTH_CONTROL_TRACK_SIZE, light.wavelength );
        final PNode wavelengthWrapperNode = new ZeroOffsetNode( wavelengthControl );

        final PNode parentNode = new PNode();
        parentNode.addChild( panelNode );
        parentNode.addChild( wavelengthWrapperNode );

        //TODO this is really nasty
        // The bounds of the wavelength control changes as the slider knob is dragged. Prevent the control panel from resizing.
        {
            // remember the wavelength
            final double saveWavelength = light.wavelength.get();

            // determine bounds at min and max wavelength settings
            light.wavelength.set( VisibleColor.MIN_WAVELENGTH );
            final PBounds minWavelengthBounds = wavelengthWrapperNode.getFullBounds();
            light.wavelength.set( VisibleColor.MAX_WAVELENGTH );
            final PBounds maxWavelengthBounds = wavelengthWrapperNode.getFullBounds();

            // restore the wavelength
            light.wavelength.set( saveWavelength );

            final double xOffset = maxWavelengthBounds.getMinX() - minWavelengthBounds.getMinX() + 1;
            wavelengthWrapperNode.setOffset( panelNode.getXOffset() + xOffset, panelNode.getFullBoundsReference().getMaxY() + 6 );

            // add a horizontal strut
            final double strutWidth = maxWavelengthBounds.getMaxX() - minWavelengthBounds.getMinX() + 1;
            final PPath strutNode = new PPath( new Rectangle2D.Double( 0, 0, strutWidth, 1 ) ) {{
                setPickable( false );
                setStroke( null );
            }};
            parentNode.addChild( strutNode );
        }

        // wrap the whole thing in a Piccolo control panel
        addChild( new ControlPanelNode( parentNode ) );

        // When the radio button selection changes...
        variableWavelength.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean isVariable ) {
                if ( isVariable ) {
                    parentNode.addChild( wavelengthWrapperNode );
                }
                else {
                    // Remove the wavelength control from the scenegraph. This causes the control panel to resize.
                    parentNode.removeChild( wavelengthWrapperNode );
                    // Set the light to the current solution's lambdaMax wavelength.
                    light.wavelength.set( solution.get().molarAbsorptivityData.getLambdaMax() );
                }
            }
        } );
    }

    public void reset() {
        variableWavelength.reset();
    }

    // Specialization of wave length control that synchronizes with a Property<Double>
    private static class BLLWavelengthControl extends WavelengthControl {

        public BLLWavelengthControl( Dimension trackSize, final Property<Double> wavelength ) {
            super( UserComponents.wavelengthControl, true, trackSize.width, trackSize.height );

            // set the model value when the control is changed
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    wavelength.set( getWavelength() );
                }
            } );

            // set the control value when the model is changed
            wavelength.addObserver( new VoidFunction1<Double>() {
                public void apply( Double wavelength ) {
                    setWavelength( wavelength );
                }
            } );
        }
    }
}
