// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.flow.model.FluxMeter;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureControlPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.*;
import static java.awt.GridBagConstraints.*;

/**
 * Displays the panel for the flux meter, which includes readouts for rate, area and flux.
 *
 * @author Sam Reid
 */
public class FluxMeterPanelNode extends PNode {

    //Font to use for readouts
    public static final PhetFont FONT = new PhetFont( 15 );

    //Use a horizontal spacing to separate the columns
    private static final Insets insets = new Insets( 0, 6, 0, 3 );

    //Move the HTML units up so the baselines will align (otherwise text like m^2 will center and look odd)
    private final Insets HTML_INSETS = new Insets( insets.top, insets.left, insets.bottom + 8, insets.right );

    public FluxMeterPanelNode( final ModelViewTransform transform, final FluxMeter fluxMeter, final Property<UnitSet> selectedUnits, final UnitSet units ) {

        //Show a panel with the readouts, using a grid bag layout to specify alignments
        addChild( new ControlPanelNode( new SwingLayoutNode( new GridBagLayout() ) {{

            //Format for showing all the numbers, reduce the number of decimals so it's not so overwhelming, but keep at least one for when the area is reduced to the minimum of 0.2m^2
            final DecimalFormat formatter = new DecimalFormat( "0.0" );

            //Populate the table by row
            //Consider using better fractions like: http://changelog.ca/log/2008/07/01/writing_fractions_in_html
            addChild( new Text( FLOW_RATE ), new Constraint( 0, 0, LINE_END, insets, 0, 0 ) );
            addChild( new Text( formatter, fluxMeter.pipe.flowRate, units.flowRate ), new Constraint( 1, 0, LINE_END, insets, 0, 0 ) );
            addChild( new HTML( units.flowRate.getAbbreviation() ), new Constraint( 2, 0, LINE_START, getInsets( units.flowRate.getAbbreviation() ), 0, 0 ) );

            //Area row
            addChild( new Text( AREA ), new Constraint( 0, 1, LINE_END, insets, 0, 0 ) );
            addChild( new Text( formatter, fluxMeter.area, units.area ), new Constraint( 1, 1, LINE_END, insets, 0, 0 ) );
            addChild( new HTML( units.area.getAbbreviation() ), new Constraint( 2, 1, LINE_START, HTML_INSETS, 0, 0 ) );

            //Separator row to signify that the value below is a result of a computation of the previous rows
            addChild( new PhetPPath( new Line2D.Double( 0, 0, 150, 0 ) ), new GridBagConstraints( 0, 2, 3, 1, 1, 0.5, CENTER, NONE, insets, 0, 0 ) );

            //Flux row
            addChild( new Text( FLUX ), new Constraint( 0, 3, LINE_END, insets, 0, 0 ) );
            addChild( new Text( formatter, fluxMeter.flux, units.flux ), new Constraint( 1, 3, LINE_END, insets, 0, 0 ) );
            addChild( new HTML( units.flux.getAbbreviation() ), new Constraint( 2, 3, LINE_START, HTML_INSETS, 0, 0 ) );

        }}, FluidPressureControlPanel.BACKGROUND, new BasicStroke( 2 ), Color.blue ) {{
            final SimpleObserver updateShape = new SimpleObserver() {
                public void update() {

                    //Move when the sensor moves
                    final ImmutableVector2D bottom = fluxMeter.getBottom();
                    final Point2D viewPoint = transform.modelToView( bottom.toPoint2D() );
                    setOffset( viewPoint.getX() - getFullBounds().getWidth() / 2, viewPoint.getY() );
                }
            };

            //Update the shape of the flux meter whenever the user drags it or when the pipe changes shape
            fluxMeter.x.addObserver( updateShape );
            fluxMeter.pipe.addShapeChangeListener( updateShape );

            //Make it so the user can drag the flux meter back and forth along the pipe
            addInputEventListener( new FluxMeterDragHandler( UserComponents.fluxMeterPanel, transform, fluxMeter, this ) );
        }} );

        //Only show the flux meter if the user has selected it in the control panel, and it has the same units as the set selected by the user
        new RichSimpleObserver() {
            @Override public void update() {
                boolean visible = fluxMeter.visible.get() && selectedUnits.get() == units;
                setVisible( visible );

                //Don't let invisible object intercept mouse events
                setPickable( visible );
                setChildrenPickable( visible );
            }
        }.observe( fluxMeter.visible, selectedUnits );
    }

    //For strings that are HTML in one unit set but not the other, try to discover whether it is html or not so it can be vertically centered properly
    private Insets getInsets( String rateUnits ) {

        //Assume that any string with a "<" symbol contains an html fragment HTML.
        //Can't use BasicHTML.isHTMLString because this might just be a fragment
        final boolean isHTML = rateUnits.indexOf( "<" ) >= 0;
        return isHTML ? HTML_INSETS : insets;
    }

    //utility class for showing text readouts with the right font
    private static class Text extends PText {

        Text() {
            this( "" );
        }

        Text( String text ) {
            super( text );
            setFont( FONT );
        }

        public Text( final DecimalFormat formatter, ObservableProperty<Double> value, final Unit unit ) {
            this();
            value.addObserver( new VoidFunction1<Double>() {
                public void apply( Double flux ) {
                    setText( formatter.format( unit.siToUnit( flux ) ) );
                }
            } );
        }
    }

    //utility class for showing units with the right font
    private static class HTML extends HTMLNode {
        HTML( String html ) {
            super( html );
            setFont( FONT );
        }
    }

    //utility class for creating grid bag constraints for the table
    private static class Constraint extends GridBagConstraints {
        Constraint( int gridx, int gridy, int anchor, Insets insets, int ipadx, int ipady ) {
            super( gridx, gridy, 1, 1, 0.5, 0.5, anchor, NONE, insets, ipadx, ipady );
        }
    }

}