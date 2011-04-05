// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.clock.TimeSpeedSlider;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.gravityandorbits.GAOStrings;
import edu.colorado.phet.workenergy.controlpanel.WorkEnergyControlPanel;
import edu.colorado.phet.workenergy.model.WorkEnergyModel;
import edu.colorado.phet.workenergy.module.WorkEnergyModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class WorkEnergyCanvas extends PhetPCanvas {
    private final ModelViewTransform2D transform;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 679 );
    private final PNode rootNode;

    public WorkEnergyCanvas( final WorkEnergyModule<?> module, final WorkEnergyModel model ) {
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );
        transform = new ModelViewTransform2D( new Point2D.Double( 0, 0 ), new Point2D.Double( 5, 5 ),
                                              new Point2D.Double( STAGE_SIZE.width * 0.5, STAGE_SIZE.height * 0.86 ), new Point2D.Double( STAGE_SIZE.width, STAGE_SIZE.height * 0.1 ), true );
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        EnergyObjectNode objectNode = new EnergyObjectNode( model.getObject(), transform,
                                                            module.getShowRulerProperty() );//When showing the ruler, also show the origin line for the object
        addChild( new SkyNode( transform ) );
        addChild( new GroundNode( transform ) );
        addChild( objectNode );
        addChild( new WorkEnergyPieChartNode( module.getShowPieChartProperty(), model.getObject(), transform ) );
        addChild( new EnergyLegend( module.getShowPieChartProperty() ) {{
            setOffset( STAGE_SIZE.width - getFullBounds().getWidth() - 2, 2 );
        }} );
        addChild( new WorkEnergyRulerNode( module.getShowRulerProperty(), transform,
                                           new Point2D.Double( model.getObject().getX() - model.getObject().getWidth() / 2, 0 ) ) );
        addChild( new WorkEnergyBarGraphNode( module.getShowEnergyBarChartProperty(), model.getObject() ) {{
            setOffset( 20, 20 );
        }} );

        // Control Panel
        final PNode controlPanelNode = new PNode() {{ //swing border looks truncated in pswing, so draw our own in piccolo
            final PSwing controlPanelPSwing = new PSwing( new WorkEnergyControlPanel( module ) );
            addChild( controlPanelPSwing );
            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, controlPanelPSwing.getFullBounds().getWidth(), controlPanelPSwing.getFullBounds().getHeight(), 10, 10 ), new BasicStroke( 1 ), Color.darkGray ) );
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }};
        addChild( controlPanelNode );

        //Reset all button
        addChild( new ResetAllButtonNode( module, this, (int) ( WorkEnergyControlPanel.CONTROL_FONT.getSize() * 1.3 ), WorkEnergyControlPanel.FOREGROUND, WorkEnergyControlPanel.BACKGROUND ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + 20 );
        }} );

        final Property<Boolean> clockRunning = new Property<Boolean>( true ) {{
            addObserver( new SimpleObserver() {
                public void update() {
                    model.getClock().setRunning( getValue() );
                }
            } );
        }};
        addChild( new FloatingClockControlNode( clockRunning, new Function1<Double, String>() {

            DecimalFormat decimalFormat = new DecimalFormat( "0.0" );

            public String apply( Double aDouble ) {
                return decimalFormat.format( aDouble ) + " seconds";
            }
        }, model.getClock(), GAOStrings.RESET, new Property<Color>( Color.white ) ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
            final TimeSpeedSlider timeSpeedSlider = new TimeSpeedSlider( WorkEnergyModel.DEFAULT_DT / 2, WorkEnergyModel.DEFAULT_DT * 2, model.getClock() ) {{
                makeTransparent( this );
                addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        model.getClock().setDt( getValue() );
                    }
                } );
            }};
            addChild( new PSwing( timeSpeedSlider ) {{
                setOffset( -getFullBounds().getWidth(), 0 );
            }} );
        }} );
    }

    private void makeTransparent( JComponent component ) {
        component.setBackground( new Color( 0, 0, 0, 0 ) );
        component.setOpaque( false );
        for ( Component component1 : component.getComponents() ) {
            if ( component1 instanceof JComponent ) {
                makeTransparent( (JComponent) component1 );
            }
        }
    }

    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }
}
