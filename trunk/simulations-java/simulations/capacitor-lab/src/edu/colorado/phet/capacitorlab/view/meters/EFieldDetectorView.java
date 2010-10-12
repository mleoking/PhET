/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.view.PlusNode;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Electric-field (E-field) detector.
 * This is not a node, but it manages the connections and interactivity of a set of related nodes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EFieldDetectorView {
    
    private static final Font TITLE_FONT = new PhetFont( Font.BOLD, 18 );
    private static final Color TITLE_COLOR = Color.WHITE;
    
    private static final Color BODY_COLOR = Color.BLACK;
    private static final double BODY_CORNER_RADIUS = 15;
    private static final int BODY_X_MARGIN = 5;
    private static final int BODY_Y_MARGIN = 5;
    private static final int BODY_X_SPACING = 2;
    private static final int BODY_Y_SPACING = 4;
    
    private static final Color WIRE_COLOR = Color.BLACK;
    private static final Stroke WIRE_STROKE = new BasicStroke( 3f );
        
    private static final double VECTOR_DISPLAY_WIDTH = 200;
    private static final Color VECTOR_DISPLAY_BACKGROUND = Color.WHITE;
    
    private static final Font CONTROL_FONT = new PhetFont( Font.BOLD, 16 );
    private static final Color CONTROL_COLOR = Color.WHITE;

    private final BatteryCapacitorCircuit circuit; 
    private final ModelViewTransform mvt;
    private final BodyNode bodyNode;
    private final ProbeNode probeNode;
    private final CubicWireNode wireNode;
    
    public EFieldDetectorView( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, PNode dragBoundsNode, boolean dev ) {
        
        this.circuit = circuit;
        this.mvt = mvt;
        
        bodyNode = new BodyNode();
        bodyNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                    probeNode.setVisible( bodyNode.getVisible() );
                }
                else if ( event.getPropertyName().equals( PNode.PROPERTY_FULL_BOUNDS ) ) {
                    updateWire();
                }
            }
        });
        
        probeNode = new ProbeNode( dev );
//        probeNode.rotate( -mvt.getYaw() ); // rotated so that it's sticking into the capacitor
        probeNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PNode.PROPERTY_FULL_BOUNDS ) ) {
                    updateWire();
                }
            }
        });
        
        wireNode = new CubicWireNode( WIRE_COLOR, -25, 100 ); //XXX constants
        
        // interactivity
        bodyNode.addInputEventListener( new CursorHandler() );
        probeNode.addInputEventListener( new CursorHandler() );
        bodyNode.addInputEventListener( new BoundedDragHandler( bodyNode, dragBoundsNode ) );
        probeNode.addInputEventListener( new BoundedDragHandler( probeNode, dragBoundsNode ) );
        
        updateWire();
    }
    
    private void updateWire() {
        // connect to left center of body
        double x = bodyNode.getFullBoundsReference().getMinX();
        double y = bodyNode.getFullBoundsReference().getCenterY();
        Point2D bodyConnectionPoint = new Point2D.Double( x, y );
        // connect to bottom center of probe
        x = probeNode.getFullBoundsReference().getCenterX();
        y = probeNode.getFullBoundsReference().getMaxY();
        Point2D probeConnectionPoint = new Point2D.Double( x, y );
        wireNode.setEndPoints( bodyConnectionPoint, probeConnectionPoint );
    }
    
    public PNode getBodyNode() {
        return bodyNode;
    }
    
    public PNode getProbeNode() {
        return probeNode;
    }
    
    public PNode getWireNode() {
        return wireNode;
    }
    
    /*
     * Body of the meter, origin at upper-left corner of bounding rectangle.
     */
    private static class BodyNode extends PhetPNode {
        
        public BodyNode() {
            
            PText titleNode = new PText( CLStrings.TITLE_ELECTRIC_FIELD );
            titleNode.setTextPaint( TITLE_COLOR );
            titleNode.setFont( TITLE_FONT );
            
            PImage closeButtonNode = new PImage( CLImages.CLOSE_BUTTON );
            addChild( closeButtonNode );
            
            ControlPanel controlPanel = new ControlPanel();
            PSwing controlPanelNode = new PSwing( controlPanel );
            
            VectorDisplayNode vectorDisplayNode = new VectorDisplayNode( VECTOR_DISPLAY_WIDTH, controlPanelNode.getFullBoundsReference().getHeight() );

            double width = controlPanelNode.getFullBoundsReference().getWidth() + vectorDisplayNode.getFullBoundsReference().getWidth() + ( 2 * BODY_X_MARGIN ) + BODY_X_SPACING;
            double height = titleNode.getFullBoundsReference().getHeight() + BODY_Y_SPACING + Math.max( controlPanelNode.getFullBoundsReference().getHeight(), vectorDisplayNode.getFullBoundsReference().getHeight() ) + ( 2 * BODY_Y_MARGIN );
            PPath backgroundNode = new PPath( new RoundRectangle2D.Double( 0, 0, width, height, BODY_CORNER_RADIUS, BODY_CORNER_RADIUS ) );
            backgroundNode.setPaint( BODY_COLOR );
            backgroundNode.setStroke( null );
            
            // rendering order
            addChild( backgroundNode );
            addChild( titleNode );
            addChild( closeButtonNode );
            addChild( controlPanelNode );
            addChild( vectorDisplayNode );
            
            // layout
            double x = 0;
            double y = 0;
            backgroundNode.setOffset( x, y );
            x = backgroundNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
            y = BODY_Y_MARGIN;
            titleNode.setOffset( x, y );
            x = backgroundNode.getFullBoundsReference().getMaxX() - closeButtonNode.getFullBoundsReference().getWidth() - BODY_X_MARGIN;
            y = backgroundNode.getFullBoundsReference().getMinY() + BODY_Y_MARGIN;
            closeButtonNode.setOffset( x, y );
            x = BODY_X_MARGIN;
            y = titleNode.getFullBoundsReference().getMaxY() + BODY_Y_SPACING;
            controlPanelNode.setOffset( x, y );
            x = controlPanelNode.getFullBoundsReference().getMaxX() + BODY_X_SPACING;
            y = controlPanelNode.getYOffset();
            vectorDisplayNode.setOffset( x, y );
            
            // interactivity
            closeButtonNode.addInputEventListener( new CursorHandler() );
            
            closeButtonNode.addInputEventListener( new PBasicInputEventHandler() {
                @Override
                public void mouseReleased( PInputEvent event ) {
                    BodyNode.this.setVisible( false );
                }
            });
        }
    }
    
    /*
     * Swing control panel that appears in the body.
     */
    private static class ControlPanel extends GridPanel {
        
        private final JCheckBox plateCheckBox, dielectricCheckBox, sumCheckBox, showValuesCheckBox;
        
        public ControlPanel() {
            
            setBackground( BODY_COLOR );
            
            JLabel showVectorsLabel = new JLabel( CLStrings.LABEL_SHOW_VECTORS );
            showVectorsLabel.setFont( CONTROL_FONT );
            showVectorsLabel.setForeground( CONTROL_COLOR );
            
            plateCheckBox = new JCheckBox( CLStrings.CHECKBOX_PLATE );
            plateCheckBox.setFont( CONTROL_FONT );
            plateCheckBox.setForeground( CLPaints.PLATE_EFIELD_VECTOR );
            
            dielectricCheckBox = new JCheckBox( CLStrings.CHECKBOX_DIELECTRIC );
            dielectricCheckBox.setFont( CONTROL_FONT );
            dielectricCheckBox.setForeground( CLPaints.DIELECTRIC_EFIELD_VECTOR );
            
            sumCheckBox = new JCheckBox( CLStrings.CHECKBOX_SUM );
            sumCheckBox.setFont( CONTROL_FONT );
            sumCheckBox.setForeground( CLPaints.SUM_EFIELD_VECTOR );
            
            showValuesCheckBox = new JCheckBox( CLStrings.CHECKBOX_SHOW_VALUES );
            showValuesCheckBox.setFont( CONTROL_FONT );
            showValuesCheckBox.setForeground( CONTROL_COLOR );
            
            // layout
            setAnchor( Anchor.WEST );
            int row = 0;
            int column = 0;
            add( showVectorsLabel, row++, column );
            add( plateCheckBox, row++, column );
            add( dielectricCheckBox, row++, column );
            add( sumCheckBox, row++, column );
            add( Box.createVerticalStrut( 20 ), row++, column );
            add( showValuesCheckBox, row++, column );
        }
    }
    
    /*
     * Rectangular area where the vectors are displayed.
     * Vectors are clipped to this area.
     */
    
    private static final class VectorDisplayNode extends PPath {
        
        public VectorDisplayNode( double width, double height ) {
            
            PPath backgroundNode = new PPath( new Rectangle2D.Double( 0, 0, width, height ) );
            backgroundNode.setPaint( VECTOR_DISPLAY_BACKGROUND );
            backgroundNode.setStroke( null );
            addChild( backgroundNode );
            
            //XXX add vector nodes
        }
    }
    
    /*
     * Probe.
     * Origin is in the center of the probe's crosshairs, and the location of the crosshairs 
     * is dependent on the image file.  The only way to align the crosshairs and the origin
     * is via visual inspection. Running with -dev will add an additional node that allows 
     * you to visually check alignment.
     */
    private static class ProbeNode extends PhetPNode {
        
        public ProbeNode( boolean dev ) {
            super();
            
            PImage imageNode = new PImage( CLImages.EFIELD_PROBE );
            addChild( imageNode );
            double x = -imageNode.getFullBoundsReference().getWidth() / 2;
            double y = 0.078 * -imageNode.getFullBoundsReference().getHeight(); // multiplier is dependent on image file
            imageNode.setOffset( x, y );
            
            // Put a '+' at origin to check that probe image is offset properly.
            if ( dev ) {
                PlusNode plusNode = new PlusNode( 12, 2, Color.GRAY );
                addChild( plusNode );
            }
        }
    }
    
    /*
     * Wire that connects the probe to the body.
     */
    private static class CubicWireNode extends PPath {

        private final double controlPointDx, controlPointDy;

        public CubicWireNode( Color color, double controlPointDx, double controlPointDy ) {
            this.controlPointDx = controlPointDx;
            this.controlPointDy = controlPointDy;
            setStroke( WIRE_STROKE );
            setStrokePaint( color );
        }

        public void setEndPoints( Point2D startPoint, Point2D endPoint ) {
            Point2D ctrl1 = new Point2D.Double( startPoint.getX() + controlPointDx, startPoint.getY() );
            Point2D ctrl2 = new Point2D.Double( endPoint.getX(), endPoint.getY() + controlPointDy );
            setPathTo( new CubicCurve2D.Double( startPoint.getX(), startPoint.getY(), ctrl1.getX(), ctrl1.getY(), ctrl2.getX(), ctrl2.getY(), endPoint.getX(), endPoint.getY() ) );
        }
    }
}
