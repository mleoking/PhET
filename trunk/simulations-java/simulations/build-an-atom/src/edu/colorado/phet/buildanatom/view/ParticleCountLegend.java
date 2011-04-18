// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.buildanatom.model.Electron;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.buildanatom.model.Neutron;
import edu.colorado.phet.buildanatom.model.Proton;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class ParticleCountLegend extends PNode {
    private static final double OFFSET_Y = 5;  // vertical spacing between rows
    private static final double MAX_PARTICLE_WIDTH = Proton.RADIUS * 2 + 5;
    private static final double HORIZONTAL_SPACING = 2; // spacing between particles in rows

    static interface Getter {
        int get();
    }

    private static final ModelViewTransform NO_TRANSFORM = ModelViewTransform.createIdentity();
    //Particles shouldn't animate, this is only used for compatibility with ProtonNode and other related classes
    //Another way to solve this would be to factor out a parent class from NeutronNode that just does rendering (i.e. doesn't need a reference to a Neutron)
    private static final ConstantDtClock NO_CLOCK = new ConstantDtClock( 1000, 1 );

    /**
     * Constructor that uses default transparent background color.
     */
    public ParticleCountLegend( final IDynamicAtom atom ) {
        this( atom, new Color( 0, 0, 0, 0 ));
    }

    /**
     * Primary constructor.
     */
    public ParticleCountLegend( final IDynamicAtom atom, Color backgroundColor ) {
        // Set up the layers.
        PNode backgroundLayer = new PNode();
        addChild( backgroundLayer );
        PNode foregroundLayer = new PNode();
        addChild( foregroundLayer );

        // Create the particle rows.
        final ReadoutLegendItem protonItem = new ReadoutLegendItem( BuildAnAtomStrings.PROTONS_READOUT, atom, new Getter() {
            public int get() {
                return atom.getNumProtons();
            }
        }, new PNodeFactory() {
            public PNode createNode() {
                return new ProtonNode( NO_TRANSFORM, new Proton( NO_CLOCK ) );//creates a dummy particle so we can reuse graphics code
            }
        } );
        final ReadoutLegendItem neutronItem = new ReadoutLegendItem( BuildAnAtomStrings.NEUTRONS_READOUT, atom, new Getter() {
            public int get() {
                return atom.getNumNeutrons();
            }
        }, new PNodeFactory() {
            public PNode createNode() {
                return new NeutronNode( NO_TRANSFORM, new Neutron( NO_CLOCK ) );
            }
        } );
        final ReadoutLegendItem electronItem = new ReadoutLegendItem( BuildAnAtomStrings.ELECTRONS_READOUT, atom, new Getter() {
            public int get() {
                return atom.getNumElectrons();
            }
        }, new PNodeFactory() {
            public PNode createNode() {
                return new ElectronNode( NO_TRANSFORM, new Electron( NO_CLOCK ) );
            }
        } );

        foregroundLayer.addChild( protonItem );
        foregroundLayer.addChild( neutronItem );
        foregroundLayer.addChild( electronItem );

        //Layout the components
        protonItem.setOffset( 0, OFFSET_Y );
        neutronItem.setOffset( 0, protonItem.getFullBounds().getMaxY() + OFFSET_Y );
        electronItem.setOffset( 0, neutronItem.getFullBounds().getMaxY() + OFFSET_Y );
        double iconX = 20 + Math.max( protonItem.textNode.getFullBounds().getMaxX(), Math.max( neutronItem.textNode.getFullBounds().getMaxX(), electronItem.textNode.getFullBounds().getMaxX() ) );//leftmost x where the grid items start
        protonItem.setIconXOffset( iconX );
        neutronItem.setIconXOffset( iconX );
        electronItem.setIconXOffset( iconX );

        // Show a border/background around all rows
        final PhetPPath boundsNode = new PhetPPath( backgroundColor, new BasicStroke( 1 ), Color.black );
        backgroundLayer.addChild( boundsNode );
        PropertyChangeListener updateBounds = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                Rectangle2D bounds = RectangleUtils.union( new Rectangle2D[] { protonItem.getFullBounds(), neutronItem.getFullBounds(), electronItem.getFullBounds() } );
                Rectangle2D expanded = RectangleUtils.expandRectangle2D( bounds, 5, 5 );
                boundsNode.setPathTo( new RoundRectangle2D.Double( expanded.getX(), expanded.getY(), Math.max( expanded.getWidth(), 170 ),//start small and expand to the right or it looks unusual by default
                                                                   expanded.getHeight(), 10, 10 ) );
            }
        };
        protonItem.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, updateBounds );
        neutronItem.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, updateBounds );
        electronItem.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, updateBounds );
    }

    private static interface PNodeFactory {
        PNode createNode();
    }

    private static class ReadoutLegendItem extends PNode {
        private final PNode iconChildNode = new PNode();//Shows the images of the particle
        private final PText textNode;
        private double iconX;
        private final Getter getter;
        private final PNodeFactory nodeFactory;

        public ReadoutLegendItem( final String label, IDynamicAtom atom, final Getter getter, final PNodeFactory nodeFactory ) {
            this.getter = getter;
            this.nodeFactory = nodeFactory;
            textNode = new PText( label ) {{setFont( BuildAnAtomConstants.ITEM_FONT );}};
            addChild( iconChildNode );
            addChild( textNode );
            AtomListener updateReadout = new AtomListener.Adapter() {
                @Override
                public void configurationChanged() {
                    updateLayout();
                }
            };
            atom.addAtomListener( updateReadout );
            updateReadout.configurationChanged();
        }

        private void updateLayout() {
            iconChildNode.removeAllChildren();
            for ( int i = 0; i < getter.get(); i++ ) {
                PNode newicon = nodeFactory.createNode();
                newicon.setPickable( false );
                newicon.setChildrenPickable( false );
                newicon.scale( 1.3 );//Make it look a bit bigger
                iconChildNode.addChild( newicon );
                //put next to the previous one
                newicon.setOffset( ( MAX_PARTICLE_WIDTH + HORIZONTAL_SPACING ) * i, 0 );
            }
            iconChildNode.setOffset( iconX, textNode.getFullBounds().getCenterY() );
        }

        public void setIconXOffset( double iconX ) {
            this.iconX = iconX;
            updateLayout();
        }
    }
}
