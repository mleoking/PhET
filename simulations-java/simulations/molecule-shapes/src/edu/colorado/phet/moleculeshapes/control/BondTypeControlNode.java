// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.jmephet.JMECursorHandler;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesModule;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Images;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;
import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents.sendEvent;
import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents.sendSystemEvent;

/**
 * Displays a graphic showing a bonding type (single/double/triple/lone pair) where dragging the graphic
 * creates the real bond in 3D. Also has a button to remove a bond of that same type from play.
 */
public class BondTypeControlNode extends PNode {
    private final MoleculeShapesModule module;
    private final PNode graphic;
    private final int bondOrder;
    private final Property<Boolean> enabled;
    private final PNode removeButton;
    protected boolean showingRemoveButton = false;

    public BondTypeControlNode( final MoleculeShapesModule module, final PNode graphic, final int bondOrder, final Property<Boolean> enabled ) {
        this.module = module;
        this.graphic = graphic;
        this.bondOrder = bondOrder;
        this.enabled = enabled;

        addChild( graphic );

        // add a blank background that will allow the user to click on this
        graphic.addChild( 0, new Spacer( graphic.getFullBounds() ) );

        removeButton = new PImage( Images.REMOVE );
        removeButton.setOffset( MoleculeShapesControlPanel.INNER_WIDTH - removeButton.getFullBounds().getWidth(),
                                ( graphic.getFullBounds().getHeight() - removeButton.getFullBounds().getHeight() ) / 2 );
        addChild( removeButton );

        graphic.setOffset( ( MoleculeShapesControlPanel.INNER_WIDTH - removeButton.getFullBounds().getWidth() - graphic.getFullBounds().getWidth() ) / 2, 0 );

        removeButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseClicked( PInputEvent event ) {
                JMEUtils.invoke( new Runnable() {
                    public void run() {
                        PairGroup candidate = getLastMatchingGroup();

                        // if it exists, remove it
                        if ( candidate != null ) {
                            module.getMolecule().removePair( candidate );
                            sendEvent( "bond", "removed", param( "bondOrder", bondOrder ) );

                            //System response for electron and molecule geometry names, copied from code in GeometryNameNode
                            systemResponseForGeometries( module.getMolecule() );
                        }
                    }
                } );
            }
        } );

        module.getMolecule().onGroupChanged.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        updateState();
                    }
                }, true );

        // custom cursor handler for only showing hand when it is enabled
        graphic.addInputEventListener( new JMECursorHandler() {
            @Override public boolean isEnabled() {
                return enabled.get();
            }
        } );

        removeButton.addInputEventListener( new JMECursorHandler() {
            @Override public boolean isEnabled() {
                return showingRemoveButton;
            }
        } );

        addDragEvent( new Runnable() {
            public void run() {
                module.startNewInstanceDrag( bondOrder );

                sendEvent( "bond", "created", param( "bondOrder", bondOrder ) );

                //System response for electron and molecule geometry names, copied from code in GeometryNameNode
                systemResponseForGeometries( module.getMolecule() );
            }
        } );
    }

    /**
     * Invoke the following runnable if we are enabled AND the user presses the left mouse button down
     * on the graphic.
     *
     * @param runnable Code to run (will run in the JME3 thread)
     */
    public void addDragEvent( final Runnable runnable ) {
        graphic.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( final PInputEvent event ) {
                if ( enabled.get() && event.getButton() == MouseEvent.BUTTON1 ) {
                    JMEUtils.invoke( runnable );
                }
            }
        } );
    }

    //System response for electron and molecule geometry names, copied from code in GeometryNameNode
    public static void systemResponseForGeometries( MoleculeModel molecule ) {
        String electronGeometry = molecule.getConfiguration().geometry.name;
        String electronGeometryName = electronGeometry == null ? MoleculeShapesResources.Strings.GEOMETRY__EMPTY : electronGeometry;

        final String moleculeGeometry = molecule.getConfiguration().name;
        String moleculeGeometryName = moleculeGeometry == null ? MoleculeShapesResources.Strings.SHAPE__EMPTY : moleculeGeometry;
        sendSystemEvent( "bondsChanged", param( "electronGeometry", electronGeometryName ), param( "moleculeGeometry", moleculeGeometryName ) );
    }

    private boolean hasMatchingGroup() {
        return getLastMatchingGroup() != null;
    }

    //SRR Suspicious use of bondOrder
    private PairGroup getLastMatchingGroup() {
        // find the last pair group that has the desired bond order
        java.util.List<PairGroup> groups = new ArrayList<PairGroup>( module.getMolecule().getGroups() );

        Collections.reverse( groups ); // reverse it so we pick the last, not the 1st

        for ( PairGroup group : groups ) {
            if ( group.bondOrder == bondOrder ) {
                return group;
            }
        }
        return null;
    }

    protected boolean isEnabled() {
        return module.getMolecule().wouldAllowBondOrder( bondOrder );
    }

    protected void updateState() {
        enabled.set( isEnabled() );
        showingRemoveButton = hasMatchingGroup();

        graphic.setTransparency( enabled.get() ? 1 : 0.7f );
        removeButton.setVisible( showingRemoveButton );
    }

    public PNode getGraphic() {
        return graphic;
    }
}
