// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.statesofmatter.module.solidliquidgas;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.engine.WaterMoleculeStructure;
import edu.colorado.phet.statesofmatter.model.particle.ArgonAtom;
import edu.colorado.phet.statesofmatter.model.particle.ConfigurableStatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.model.particle.HydrogenAtom;
import edu.colorado.phet.statesofmatter.model.particle.NeonAtom;
import edu.colorado.phet.statesofmatter.model.particle.OxygenAtom;
import edu.colorado.phet.statesofmatter.view.ModelViewTransform;
import edu.colorado.phet.statesofmatter.view.ParticleNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author John Blanco
 */
public class MoleculeImageLabel extends JLabel {
    private static final ModelViewTransform PARTICLE_MVT = new ModelViewTransform( 300, 300, 0, 0, false, true );
    private static final double PARTICLE_SCALING_FACTOR = 0.05;
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 1 );

    public MoleculeImageLabel( final int moleculeID, final MultipleParticleModel model ) {
        PNode particleNode = null;
        switch( moleculeID ) {
            case StatesOfMatterConstants.ARGON:
                particleNode = new ParticleNode( new ArgonAtom( 0, 0 ), PARTICLE_MVT, false, true, false );
                break;
            case StatesOfMatterConstants.NEON:
                particleNode = new ParticleNode( new NeonAtom( 0, 0 ), PARTICLE_MVT, false, true, false );
                break;
            case StatesOfMatterConstants.DIATOMIC_OXYGEN:
                // Need to create a diatomic oxygen node.  The model-view
                // transform used here was empirically determined.
                ModelViewTransform oxygenMvt = new ModelViewTransform( 300, 300, 0, 0, false, true );
                particleNode = new PNode();
                particleNode.addChild( new ParticleNode( new OxygenAtom( -StatesOfMatterConstants.DIATOMIC_PARTICLE_DISTANCE / 2, 0 ), oxygenMvt, false, true, false ) );
                particleNode.addChild( new ParticleNode( new OxygenAtom( StatesOfMatterConstants.DIATOMIC_PARTICLE_DISTANCE / 2, 0 ), oxygenMvt, false, true, false ) );
                break;
            case StatesOfMatterConstants.WATER:
                // Need to create a water molecule node.  The model-view
                // transform used here was empirically determined.
                ModelViewTransform waterMvt = new ModelViewTransform( 500, 500, 0, 0, false, true );
                WaterMoleculeStructure wms = WaterMoleculeStructure.getInstance();
                particleNode = new PNode();
                particleNode.addChild( new ParticleNode( new HydrogenAtom( wms.getStructureArrayX()[1], wms.getStructureArrayY()[1] ), waterMvt, false, true, false ) );
                particleNode.addChild( new ParticleNode( new OxygenAtom( wms.getStructureArrayX()[0], wms.getStructureArrayY()[0] ), waterMvt, false, true, false ) );
                particleNode.addChild( new ParticleNode( new HydrogenAtom( wms.getStructureArrayX()[2], wms.getStructureArrayY()[2] ), waterMvt, false, true, false ) );
                break;
            case StatesOfMatterConstants.USER_DEFINED_MOLECULE:
                particleNode = new ParticleNode( new ConfigurableStatesOfMatterAtom( 0, 0 ), PARTICLE_MVT, false, true, false );
                break;
            default:
                assert false; // Should never get here, fix if it does.
                particleNode = new ParticleNode( new OxygenAtom( 0, 0 ), PARTICLE_MVT, false, true, false );
        }

        // Note to future maintainers:  The particle node doesn't use the
        // MVT for scaling, only for positioning, which is why a separate
        // scaling operation is needed.
        particleNode.setScale( PARTICLE_SCALING_FACTOR );

        setIcon( new ImageIcon( particleNode.toImage() ) );

        //When the user clicks on the image label, also set the model to choose that atom/molecule
        addMouseListener( new MouseAdapter() {
            @Override public void mousePressed( MouseEvent e ) {
                if ( model.getMoleculeType() != moleculeID ) {
                    model.setMoleculeType( moleculeID );
                }
            }
        } );
    }
}
