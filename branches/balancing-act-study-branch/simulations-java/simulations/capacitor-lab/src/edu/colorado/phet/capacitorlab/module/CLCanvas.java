// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for all canvases in the "Capacitor Lab" sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class CLCanvas extends PhetPCanvas {

    // global view properties, directly observable
    private final Property<Boolean> plateChargesVisibleProperty = new Property<Boolean>( CLConstants.PLATE_CHARGES_VISIBLE );
    private final Property<Boolean> eFieldVisibleProperty = new Property<Boolean>( CLConstants.EFIELD_VISIBLE );
    private final Property<DielectricChargeView> dielectricChargeViewProperty = new Property<DielectricChargeView>( CLConstants.DIELECTRIC_CHARGE_VIEW );

    private final CLModel model;
    private final CLModelViewTransform3D mvt; // model-view transform
    private final CLGlobalProperties globalProperties;
    private final PNode rootNode; // root node of our scenegraph, all nodes added below here

    public CLCanvas( CLModel model, CLModelViewTransform3D mvt, CLGlobalProperties globalProperties ) {
        super( CLConstants.CANVAS_RENDERING_SIZE );
        setBackground( CLPaints.CANVAS_BACKGROUND );

        this.model = model;
        this.mvt = mvt;
        this.globalProperties = globalProperties;

        rootNode = new PNode();
        addWorldChild( rootNode );
    }

    public void reset() {
        // global properties of the view
        plateChargesVisibleProperty.reset();
        eFieldVisibleProperty.reset();
        dielectricChargeViewProperty.reset();
    }

    // Adds a child node to the root node.
    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    // Removes a child node from the root node.
    protected void removeChild( PNode node ) {
        if ( node != null && rootNode.indexOfChild( node ) != -1 ) {
            rootNode.removeChild( node );
        }
    }

    protected CLGlobalProperties getGlobalProperties() {
        return globalProperties;
    }

    public Property<Boolean> getPlateChargesVisibleProperty() {
        return plateChargesVisibleProperty;
    }

    public Property<Boolean> getEFieldVisibleProperty() {
        return eFieldVisibleProperty;
    }

    public Property<DielectricChargeView> getDielectricChargeViewProperty() {
        return dielectricChargeViewProperty;
    }

    @Override protected void updateLayout() {
        super.updateLayout();

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }

        // adjust the world bounds
        Point3D p = mvt.viewToModelDelta( worldSize.getWidth(), worldSize.getHeight() );
        model.getWorldBounds().setBounds( 0, 0, p.getX(), p.getY() );
    }
}
