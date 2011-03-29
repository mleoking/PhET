// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.view.DielectricNode.DielectricChargeView;
import edu.colorado.phet.capacitorlab.view.PlateNode.BottomPlateNode;
import edu.colorado.phet.capacitorlab.view.PlateNode.TopPlateNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;

/**
 * Visual representation of a capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CapacitorNode extends PhetPNode {

    private final BatteryCapacitorCircuit circuit;
    private final CLModelViewTransform3D mvt;
    private final PlateNode topPlateNode, bottomPlateNode;
    private final DielectricNode dielectricNode;
    private final EFieldNode eFieldNode;

    // observable properties
    private final Property<Boolean> plateChargeVisibleProperty, eFieldVisibleProperty;

    public CapacitorNode( BatteryCapacitorCircuit circuit, CLModelViewTransform3D mvt,
            boolean plateChargeVisible, boolean eFieldVisible, DielectricChargeView dielectricChargeView ) {

        this.circuit = circuit;
        this.mvt = mvt;

        plateChargeVisibleProperty = new Property<Boolean>( plateChargeVisible );
        eFieldVisibleProperty = new Property<Boolean>( eFieldVisible );

        // child nodes
        topPlateNode = new TopPlateNode( circuit, mvt );
        bottomPlateNode = new BottomPlateNode( circuit, mvt );
        dielectricNode = new DielectricNode( circuit, mvt, CLConstants.DIELECTRIC_OFFSET_RANGE, dielectricChargeView );
        eFieldNode = new EFieldNode( circuit, mvt );

        // rendering order
        addChild( bottomPlateNode );
        addChild( eFieldNode );
        addChild( dielectricNode ); // dielectric between the plates
        addChild( topPlateNode );

        // observers
        {
            // update geometry when dimensions change
            SimpleObserver o = new SimpleObserver() {
                public void update() {
                    updateGeometry();
                }
            };
            circuit.getCapacitor().addPlateSizeObserver( o );
            circuit.getCapacitor().addPlateSeparationObserver( o );
            circuit.getCapacitor().addDielectricOffsetObserver( o );

            plateChargeVisibleProperty.addObserver( new SimpleObserver() {
                public void update() {
                    topPlateNode.setChargeVisible( isPlateChargeVisible() );
                    bottomPlateNode.setChargeVisible( isPlateChargeVisible() );
                }
            } );

            eFieldVisibleProperty.addObserver( new SimpleObserver() {
                public void update() {
                    eFieldNode.setVisible( isEFieldVisible() );
                }
            } );
        }
    }

    public void reset() {
        plateChargeVisibleProperty.reset();
        eFieldVisibleProperty.reset();
        dielectricNode.reset();
    }

    public DielectricNode getDielectricNode() {
        return dielectricNode;
    }

    public void setDielectricVisible( boolean visible ) {
        dielectricNode.setVisible( visible );
    }

    public void addPlateChargeVisibleObserver( SimpleObserver o ) {
        plateChargeVisibleProperty.addObserver( o );
    }

    public Property<Boolean> getPlateChargeVisibileProperty() {
        return plateChargeVisibleProperty;
    }

    public void setPlateChargeVisible( boolean visible ) {
        plateChargeVisibleProperty.setValue( visible );
    }

    public boolean isPlateChargeVisible() {
        return plateChargeVisibleProperty.getValue();
    }

    public void addEFieldVisibleObserver( SimpleObserver o ) {
        eFieldVisibleProperty.addObserver( o );
    }

    public Property<Boolean> getEFieldVisibleProperty() {
        return eFieldVisibleProperty;
    }

    public void setEFieldVisible( boolean visible ) {
        eFieldVisibleProperty.setValue( visible );
    }

    public boolean isEFieldVisible() {
        return eFieldVisibleProperty.getValue();
    }

    private void updateGeometry() {

        Capacitor capacitor = circuit.getCapacitor();

        // geometry
        topPlateNode.setSize( capacitor.getPlateSize() );
        bottomPlateNode.setSize( capacitor.getPlateSize() );
        dielectricNode.setSize( capacitor.getDielectricSize() );

        // layout nodes with zero dielectric offset
        double x = 0;
        double y = -( capacitor.getPlateSeparation() / 2 ) - capacitor.getPlateSize().getHeight();
        double z = 0;
        topPlateNode.setOffset( mvt.modelToViewDelta( x, y, z ) );
        y = -capacitor.getDielectricSize().getHeight() / 2;
        dielectricNode.setOffset( mvt.modelToViewDelta( x, y, z ) );
        y = capacitor.getPlateSeparation() / 2;
        bottomPlateNode.setOffset( mvt.modelToViewDelta( x, y, z ) );

        // adjust the dielectric offset
        updateDielectricOffset();
    }

    private void updateDielectricOffset() {
        double x = circuit.getCapacitor().getDielectricOffset();
        double y = -circuit.getCapacitor().getDielectricSize().getHeight() / 2;
        double z = 0;
        dielectricNode.setOffset( mvt.modelToViewDelta( x, y, z ) );
    }
}
