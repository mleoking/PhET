// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import java.awt.*;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.drag.DielectricOffsetDragHandler;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.view.PlateNode.BottomPlateNode;
import edu.colorado.phet.capacitorlab.view.PlateNode.TopPlateNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;

/**
 * Visual representation of a capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CapacitorNode extends PhetPNode {

    private final Capacitor capacitor;
    private final CLModelViewTransform3D mvt;
    private final PlateNode topPlateNode, bottomPlateNode;
    private final DielectricNode dielectricNode;
    private final EFieldNode eFieldNode;

    public CapacitorNode( Capacitor capacitor, CLModelViewTransform3D mvt, boolean dielectricVisible,
                          final Property<Boolean> plateChargeVisibleProperty, final Property<Boolean> eFieldVisibleProperty,
                          Property<DielectricChargeView> dielectricChargeViewProperty,
                          double maxPlateCharge, double maxExcessDielectricPlateCharge, double maxEffectiveEField, double maxDielectricEField ) {

        this.capacitor = capacitor;
        this.mvt = mvt;

        // child nodes
        topPlateNode = new TopPlateNode( capacitor, mvt, maxPlateCharge );
        bottomPlateNode = new BottomPlateNode( capacitor, mvt, maxPlateCharge );
        dielectricNode = new DielectricNode( capacitor, mvt, dielectricChargeViewProperty, maxExcessDielectricPlateCharge, maxDielectricEField );
        eFieldNode = new EFieldNode( capacitor, mvt, maxEffectiveEField );

        // rendering order
        addChild( bottomPlateNode );
        addChild( eFieldNode );
        addChild( dielectricNode ); // dielectric between the plates
        addChild( topPlateNode );

        dielectricNode.setVisible( dielectricVisible );
        if ( dielectricVisible ) {
            // make dielectric directly draggable if it's visible
            dielectricNode.addInputEventListener( new CursorHandler( Cursor.E_RESIZE_CURSOR ) );
            dielectricNode.addInputEventListener( new DielectricOffsetDragHandler( this, capacitor, mvt, CLConstants.DIELECTRIC_OFFSET_RANGE ) );
        }

        // observers
        {
            // update geometry when dimensions change
            SimpleObserver o = new SimpleObserver() {
                public void update() {
                    updateGeometry();
                }
            };
            capacitor.addPlateSizeObserver( o );
            capacitor.addPlateSeparationObserver( o );
            capacitor.addDielectricOffsetObserver( o );

            plateChargeVisibleProperty.addObserver( new SimpleObserver() {
                public void update() {
                    topPlateNode.setChargeVisible( plateChargeVisibleProperty.get() );
                    bottomPlateNode.setChargeVisible( plateChargeVisibleProperty.get() );
                }
            } );

            eFieldVisibleProperty.addObserver( new SimpleObserver() {
                public void update() {
                    eFieldNode.setVisible( eFieldVisibleProperty.get() );
                }
            } );
        }
    }

    public void cleanup() {
        //TODO remove observers from plateChargesVisible and eFieldVisible
    }

    public DielectricNode getDielectricNode() {
        return dielectricNode;
    }

    public void setDielectricVisible( boolean visible ) {
        dielectricNode.setVisible( visible );
    }

    private void updateGeometry() {

        // geometry
        topPlateNode.setSize( capacitor.getPlateSize() );
        bottomPlateNode.setSize( capacitor.getPlateSize() );
        dielectricNode.setSize( capacitor.getDielectricSize() );

        // layout nodes with zero dielectric offset
        double x = 0;
        double y = -( capacitor.getPlateSeparation() / 2 ) - capacitor.getPlateHeight();
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
        double x = capacitor.getDielectricOffset();
        double y = -capacitor.getDielectricSize().getHeight() / 2;
        double z = 0;
        dielectricNode.setOffset( mvt.modelToViewDelta( x, y, z ) );
    }
}
