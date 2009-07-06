package edu.colorado.phet.acidbasesolutions.view.beaker;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.util.PNodeUtils;
import edu.colorado.phet.acidbasesolutions.view.moleculecounts.MoleculeCountsNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Composite node that contains all of the visuals related to the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeakerNode extends PComposite {
    
    private static double MAX_VOLUME = 1; // liters
    
    private final PHProbeNode probeNode;
    private final MoleculeCountsNode moleculeCountsNode;
    private final BeakerLabelNode beakerLabelNode;
    private final RatioDotsNode dotsNode;
    
    public BeakerNode( PDimension vesselSize, AqueousSolution solution ) {
        
        VesselNode vesselNode = new VesselNode( vesselSize, MAX_VOLUME );
        
        double probeHeight = vesselSize.getHeight() + 55;
        probeNode = new PHProbeNode( probeHeight, solution );
        
        SolutionNode solutionNode = new SolutionNode( vesselSize );
        
        moleculeCountsNode = new MoleculeCountsNode( solution );
        
        PDimension labelSize = new PDimension( 0.9 * vesselSize.getWidth(), 0.1 * vesselSize.getHeight() );
        beakerLabelNode = new BeakerLabelNode( labelSize, solution );
        
        // clipping path for "dot" views
        PBounds containerBounds = new PBounds( 0, 0, vesselSize.getWidth(), vesselSize.getHeight() );
        PClip dotsClippingPath = new PClip();
        dotsClippingPath.setPathTo( new Rectangle2D.Double( containerBounds.getX(), containerBounds.getY(), containerBounds.getWidth(), containerBounds.getHeight() ) );
        dotsClippingPath.setStroke( null );
        
        dotsNode = new RatioDotsNode( solution, containerBounds );
        dotsClippingPath.addChild( dotsNode ); // clip to solution bounds
        
        // rendering order
        addChild( solutionNode );
        addChild( probeNode );
        addChild( dotsClippingPath );
        addChild( vesselNode );
        addChild( moleculeCountsNode );
        addChild( beakerLabelNode );
        
        // layout
        vesselNode.setOffset( 0, 0 );
        solutionNode.setOffset( vesselNode.getOffset() );
        // molecule counts inside the vessel
        double xOffset = vesselNode.getXOffset() - PNodeUtils.getOriginXOffset( moleculeCountsNode ) + 25;
        double yOffset = vesselNode.getYOffset() - PNodeUtils.getOriginYOffset( moleculeCountsNode ) + 20;
        moleculeCountsNode.setOffset( xOffset, yOffset );
        // label at bottom of vessel
        PBounds vfb = vesselNode.getFullBoundsReference();
        xOffset = vfb.getMinX() + ( vfb.getWidth() - beakerLabelNode.getFullBoundsReference().getWidth() ) / 2;
        yOffset = vfb.getMaxY() - beakerLabelNode.getFullBoundsReference().getHeight() - 20;
        beakerLabelNode.setOffset( xOffset, yOffset );
        // probe at right side of vessel, tip of probe at bottom of vessel
        xOffset = vfb.getMaxX() - probeNode.getFullBoundsReference().getWidth() - 100;
        yOffset = vfb.getMaxY() - probeNode.getFullBoundsReference().getHeight();
        probeNode.setOffset( xOffset, yOffset );
    }
    
    public void setProbeVisible( boolean visible ) {
        probeNode.setVisible( visible );
    }
    
    public boolean isProbeVisible() {
        return probeNode.getVisible();
    }
    
    public void setSoluteComponentsRatioVisible( boolean visible ) {
        dotsNode.setSoluteComponentsVisible( visible );
    }
    
    public boolean isSoluteComponentsRatioVisible() {
        return dotsNode.isSoluteComponentsVisible();
    }
    
    public void setHydroniumHydroxideRatioVisible( boolean visible ) {
        dotsNode.setHydroniumHydroxideVisible( visible );
    }
    
    public boolean isHydroniumHydroxideRatioVisible() {
        return dotsNode.isHydroniumHydroxideVisible();
    }
    
    public void setMoleculeCountsVisible( boolean visible ) {
        moleculeCountsNode.setVisible( visible );
    }
    
    public boolean isMoleculeCountsVisible() {
        return moleculeCountsNode.getVisible();
    }
    
    public void setBeakerLabelVisible( boolean visible ) {
        beakerLabelNode.setVisible( visible );
    }
    
    public boolean isBeakerLabelVisible() {
        return beakerLabelNode.getVisible();
    }
    
    public void setProductMoleculeCountsVisible( boolean visible ) {
        moleculeCountsNode.setProductVisible( visible );
    }
    
    public void setReactantMoleculeCountsVisible( boolean visible ) {
        moleculeCountsNode.setReactantVisible( visible );
    }
}
