package edu.colorado.phet.workenergy.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsClockControlNode;
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

    public WorkEnergyCanvas( final WorkEnergyModule<?> module, WorkEnergyModel model ) {
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
            setOffset( GravityAndOrbitsCanvas.STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, GravityAndOrbitsCanvas.STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }};
        addChild( controlPanelNode );

        //Reset all button
        addChild( new GradientButtonNode( "Reset all", (int) ( WorkEnergyControlPanel.CONTROL_FONT.getSize() * 1.3 ), WorkEnergyControlPanel.BACKGROUND, WorkEnergyControlPanel.FOREGROUND ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + 20 );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.resetAll();
                }
            } );
        }} );

        addChild( new GravityAndOrbitsClockControlNode( model.getClock() ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }} );
    }

    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }
}
