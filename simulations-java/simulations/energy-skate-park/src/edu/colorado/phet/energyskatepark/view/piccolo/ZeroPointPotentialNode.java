// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Dec 27, 2005
 * Time: 9:29:24 PM
 */

public class ZeroPointPotentialNode extends PhetPNode {
    private EnergySkateParkSimulationPanel panel;
    private EnergySkateParkModel model;

    public ZeroPointPotentialNode( final EnergySkateParkSimulationPanel energySkateParkSimulationPanel, final EnergySkateParkModel model ) {
        this.panel = energySkateParkSimulationPanel;
        this.model = model;

        PhetPPath background = new PhetPPath( new Line2D.Double( 0, 0, 5000, 0 ), new BasicStroke( 30, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 3 ), new Color( 0, 0, 0, 0 ) );
        addChild( background );

        PhetPPath path = new PhetPPath( new Line2D.Double( 0, 0, 5000, 0 ), new BasicStroke( 3, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 3, new float[]{16, 8}, 0 ), Color.blue );
        addChild( path );

        ShadowPText text = new ShadowPText( EnergySkateParkStrings.getString( "label.zero-potential-energy" ) );
        text.setFont( new PhetFont( 16, true ) );
        text.setTextPaint( Color.black );
        text.setShadowColor( new Color( 128, 128, 255 ) );
        addChild( text );
        text.setOffset( 2, 2 );

        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                double dy = event.getCanvasDelta().getHeight();
                PDimension dim = new PDimension( 0, dy );
                energySkateParkSimulationPanel.getPhetRootNode().screenToWorld( dim );
                energySkateParkSimulationPanel.getEnergySkateParkModel().translateZeroPointPotentialY( dim.getHeight() );
            }
        } );
        addInputEventListener( new CursorHandler() );
        model.addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void zeroPointPotentialYChanged() {
                update();
            }
        } );
        energySkateParkSimulationPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                update();
            }

            public void componentShown( ComponentEvent e ) {
                update();
            }
        } );
        energySkateParkSimulationPanel.addListener( new EnergySkateParkSimulationPanel.Adapter() {
            public void zoomChanged() {
                update();
            }
        } );
        update();
    }

    private void update() {
        double y = model.getZeroPointPotentialY();
        Point2D.Double pt = new Point2D.Double( 0, y );
        panel.getPhetRootNode().worldToScreen( pt );
        double viewY = pt.getY();
        setOffset( 0, viewY );
    }
}
