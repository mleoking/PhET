/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.module;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsStrings;
import edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Module template.
 */
public class GravityAndOrbitsModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private GravityAndOrbitsModel model;
    private GravityAndOrbitsCanvas canvas;
    private GravityAndOrbitsControlPanel controlPanel;
    private PiccoloClockControlPanel clockControlPanel;
    private Property<Boolean> forcesProperty = new Property<Boolean>( false );
    private Property<Boolean> tracesProperty = new Property<Boolean>( false );
    private Property<Boolean> velocityProperty = new Property<Boolean>( false );
    private Property<Boolean> showMassesProperty = new Property<Boolean>( false );
    private Property<Boolean> moonProperty = new Property<Boolean>( false );
    private Property<Boolean> toScaleProperty = new Property<Boolean>( false );

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GravityAndOrbitsModule( Frame parentFrame ) {
        super( GravityAndOrbitsStrings.TITLE_EXAMPLE_MODULE, new GravityAndOrbitsClock( GravityAndOrbitsDefaults.CLOCK_FRAME_RATE, GravityAndOrbitsDefaults.CLOCK_DT ) );

        // Model
        GravityAndOrbitsClock clock = (GravityAndOrbitsClock) getClock();
        model = new GravityAndOrbitsModel( clock );

        // Canvas
        canvas = new GravityAndOrbitsCanvas( model, this );
        setSimulationPanel( canvas );

        // Control Panel
        controlPanel = new GravityAndOrbitsControlPanel( this, parentFrame, model );
        final PNode controlPanelNode = new PNode() {{ //swing border looks truncated in pswing, so draw our own in piccolo
            final PSwing controlPanelPSwing = new PSwing( controlPanel );
            //This next line works around a layout problem in pswing or swing in which the panel was reporting larger bounds than it displayed
            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, controlPanelPSwing.getFullBounds().getWidth(), controlPanelPSwing.getFullBounds().getHeight(), 10, 10 ), GravityAndOrbitsControlPanel.BACKGROUND ) );
            addChild( controlPanelPSwing );
            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, controlPanelPSwing.getFullBounds().getWidth(), controlPanelPSwing.getFullBounds().getHeight(), 10, 10 ), new BasicStroke( 3 ), Color.green ) );
            setOffset( GravityAndOrbitsCanvas.STAGE_SIZE.getWidth() - getFullBounds().getWidth(), GravityAndOrbitsCanvas.STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }};
        canvas.addChild( controlPanelNode );

        canvas.addChild( new GradientButtonNode( "Reset all", (int) ( GravityAndOrbitsControlPanel.CONTROL_FONT.getSize()*1.3), GravityAndOrbitsControlPanel.BACKGROUND, GravityAndOrbitsControlPanel.FOREGROUND ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + 20 );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    forcesProperty.reset();
                    tracesProperty.reset();
                    velocityProperty.reset();
                    showMassesProperty.reset();
                    moonProperty.reset();
                    toScaleProperty.reset();
                    model.resetAll();
                }
            } );
        }} );

        // Clock controls
        clockControlPanel = new PiccoloClockControlPanel( getClock() );
        clockControlPanel.setRewindButtonVisible( true );
        clockControlPanel.setTimeDisplayVisible( true );
        clockControlPanel.setUnits( GravityAndOrbitsStrings.UNITS_TIME );
        clockControlPanel.setTimeColumns( GravityAndOrbitsDefaults.CLOCK_TIME_COLUMNS );
        setClockControlPanel( clockControlPanel );

        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // reset the clock
        GravityAndOrbitsClock clock = model.getClock();
        clock.resetSimulationTime();
    }

    public Property<Boolean> getForcesProperty() {
        return forcesProperty;
    }

    public Property<Boolean> getTracesProperty() {
        return tracesProperty;
    }

    public Property<Boolean> getVelocityProperty() {
        return velocityProperty;
    }

    public Property<Boolean> getShowMassesProperty() {
        return showMassesProperty;
    }

    public Property<Boolean> getMoonProperty() {
        return moonProperty;
    }

    public Property<Boolean> getToScaleProperty() {
        return toScaleProperty;
    }
}
