/** Sam Reid*/
package edu.colorado.phet.cck.phetgraphics_cck;

import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.CircuitListenerAdapter;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.CircuitGraphic;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.HasJunctionGraphic;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.InteractiveBranchGraphic;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.JunctionGraphic;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.components.CircuitComponentInteractiveGraphic;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.toolbox.Toolbox;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common_cck.view.graphics.shapes.Arrow;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetMultiLineTextGraphic;
import edu.colorado.phet.common_cck.view.util.RectangleUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 25, 2004
 * Time: 1:32:51 AM
 * Copyright (c) Jun 25, 2004 by Sam Reid
 */
public class CCKHelp {

    private PositionedHelpItem junctionHelpItem;
    private PositionedHelpItem componentHelpItem;
    private PositionedHelpItem myToolboxHelpItem;
    private CCKPhetgraphicsModule module;
    private ModelViewTransform2D transform;
    private Circuit circuit;
    private CircuitGraphic circuitGraphic;
    private Toolbox toolbox;
    private SimpleObserver observer;

    public CCKHelp( CCKPhetgraphicsModule module ) {
        this.module = module;
        this.transform = module.getTransform();
        this.circuit = module.getCircuit();
        this.circuitGraphic = module.getCircuitGraphic();
        this.toolbox = module.getToolbox();
        Font helpFont = new Font( "Lucida Sans", Font.BOLD, 16 );

        ToolboxTarget toolboxTarget = new ToolboxTarget();
        myToolboxHelpItem = new PositionedHelpItem( SimStrings.get( "CCKHelp.ToolboxHelp" ),
                                                    toolboxTarget, helpFont, getApparatusPanel() );
        observer = new SimpleObserver() {
            public void update() {
                myToolboxHelpItem.changed();
            }
        };
        toolbox.addObserver( observer );
        myToolboxHelpItem.changed();
        JunctionTarget jt = new JunctionTarget();
        junctionHelpItem = new PositionedHelpItem( SimStrings.get( "CCKHelp.JunctionHelp" ),
                                                   jt, helpFont, getApparatusPanel() );
        ComponentTarget ct = new ComponentTarget();
        componentHelpItem = new PositionedHelpItem( SimStrings.get( "CCKHelp.ComponentHelp" ),
                                                    ct, helpFont, getApparatusPanel() );

        getApparatusPanel().addGraphic( myToolboxHelpItem, Double.POSITIVE_INFINITY );
        getApparatusPanel().addGraphic( junctionHelpItem, Double.POSITIVE_INFINITY );
        getApparatusPanel().addGraphic( componentHelpItem, Double.POSITIVE_INFINITY );
    }

    public void setEnabled( boolean h ) {
//        myToolboxHelpItem.setVisible( h );
//        junctionHelpItem.setVisible( h );
//        componentHelpItem.setVisible( h );
        myToolboxHelpItem.setEnabled( h );
        junctionHelpItem.setEnabled( h );
        componentHelpItem.setEnabled( h );
//        if( h ) {
//            getApparatusPanel().addGraphic( myToolboxHelpItem, Double.POSITIVE_INFINITY );
//            getApparatusPanel().addGraphic( junctionHelpItem, Double.POSITIVE_INFINITY );
//            getApparatusPanel().addGraphic( componentHelpItem, Double.POSITIVE_INFINITY );
//        }
//        else {
//            getApparatusPanel().removeGraphic( myToolboxHelpItem );
//            getApparatusPanel().removeGraphic( junctionHelpItem );
//            getApparatusPanel().removeGraphic( componentHelpItem );
//        }
    }

    private ApparatusPanel getApparatusPanel() {
        return module.getApparatusPanel();
    }

    abstract class HelpTarget extends PositionedHelpItem.Target {
        public HelpTarget() {
            transform.addTransformListener( new TransformListener() {
                public void transformChanged( ModelViewTransform2D mvt ) {
                    changed();
                }
            } );
        }

        public void changed() {
            notifyObservers();
        }
    }

    class ToolboxTarget extends HelpTarget {

        public Arrow getArrow( PhetMultiLineTextGraphic textGraphic ) {
            Shape shape = transform.createTransformedShape( toolbox.getBounds2D() );
            Point topLeft = shape.getBounds().getLocation();
            topLeft.translate( -3, -3 );
            Point tail = textGraphic.getRightCenter();
            return new Arrow( tail, topLeft, 5, 5, 2 );
        }

        public Point getTextLocation() {
            Shape shape = toolbox.getShape();
            Point topLeft = new Point( shape.getBounds().getLocation() );
            if( myToolboxHelpItem == null ) {
                return null;
            }
            else {
                topLeft.translate( (int)( -myToolboxHelpItem.getTextBounds().getWidth() - 20 ), -45 );
                return topLeft;
            }
        }
    }

    class ComponentTarget extends ChangeTarget {

        public Arrow getArrow( PhetMultiLineTextGraphic textGraphic ) {
            InteractiveGraphic g = circuitGraphic.getGraphic( circuit.branchAt( 0 ) );
            if( g instanceof CircuitComponentInteractiveGraphic ) {
                CircuitComponentInteractiveGraphic ccig = (CircuitComponentInteractiveGraphic)g;
                Point target = transform.modelToView( ccig.getCircuitComponentGraphic().getCircuitComponent().getCenter() );
                return new Arrow( textGraphic.getLeftCenter(), target, 5, 5, 2 );
            }
            else if( g instanceof InteractiveBranchGraphic ) {
                InteractiveBranchGraphic ibg = (InteractiveBranchGraphic)g;
                Point target = transform.modelToView( ibg.getBranch().getCenter() );
                return new Arrow( textGraphic.getLeftCenter(), target, 5, 5, 2 );
            }
            return null;
        }

        public Point getTextLocation() {
            if( circuit.numBranches() > 0 ) {
                InteractiveGraphic g = circuitGraphic.getGraphic( circuit.branchAt( 0 ) );
                if( g instanceof CircuitComponentInteractiveGraphic ) {
                    CircuitComponentInteractiveGraphic ccig = (CircuitComponentInteractiveGraphic)g;
                    Point target = transform.modelToView( ccig.getCircuitComponentGraphic().getCircuitComponent().getCenter() );
                    target.translate( 10, (int)( componentHelpItem.getTextBounds().getHeight() / 2 + 20 ) );
                    return target;
                }
                else if( g instanceof InteractiveBranchGraphic ) {
                    InteractiveBranchGraphic ibg = (InteractiveBranchGraphic)g;
                    Point target = transform.modelToView( ibg.getBranch().getCenter() );
                    target.translate( 10, (int)componentHelpItem.getTextBounds().getHeight() / 2 + 20 );
                    return target;
                }
                return null;
            }
            else {
                return null;
            }
        }

    }

    abstract class ChangeTarget extends HelpTarget {
        protected ChangeTarget() {

            circuit.addCircuitListener( new CircuitListenerAdapter() {
                public void branchesMoved( Branch[] branches ) {
                    changed();
                }

                public void junctionRemoved( Junction junction ) {
                    changed();
                }

                public void branchRemoved( Branch branch ) {
                    changed();
                }

                public void junctionsMoved() {
                    changed();
                }
            } );
        }
    }

    class JunctionTarget extends ChangeTarget {

        public Arrow getArrow( PhetMultiLineTextGraphic textGraphic ) {
            HasJunctionGraphic g = circuitGraphic.getGraphic( circuit.junctionAt( 0 ) );
            JunctionGraphic jg = g.getJunctionGraphic();
            Shape shape = jg.getShape();
            Rectangle shapeBounds = shape.getBounds();

            Point tail = textGraphic.getLeftCenter();
            Point tip = RectangleUtils.getTopCenter( shapeBounds );
            Arrow arrow = new Arrow( tail, tip, 5, 5, 2 );
            return arrow;
        }

        public Point getTextLocation() {
            if( circuitGraphic.numJunctionGraphics() > 0 ) {
                HasJunctionGraphic g = circuitGraphic.getGraphic( circuit.junctionAt( 0 ) );
                if( g == null ) {
                    throw new RuntimeException( "No graphic for junction: " + circuit.junctionAt( 0 ) );
                }
                JunctionGraphic jg = g.getJunctionGraphic();
                Shape shape = jg.getShape();
                Rectangle bounds = shape.getBounds();
                Point target = bounds.getLocation();
                Rectangle2D phiRect = junctionHelpItem.getTextBounds();
                if( phiRect == null ) {
                    throw new RuntimeException( "Junction help item bounds=null." );
                }
                else {
                    target.translate( (int)( bounds.width + 5 ), (int)( -phiRect.getHeight() / 2 - 15 ) );
                    return target;
                }
            }
            else {
                return null;
            }
        }
    }
}
