package edu.colorado.phet.phscale.view;

import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.colorado.phet.phscale.util.TimesTenFormat;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;


public class MoleculeCountNode extends PComposite {
    
    private static final TimesTenFormat H3O_FORMAT = new TimesTenFormat( "0.00" );
    private static final TimesTenFormat OH_FORMAT = new TimesTenFormat( "0.00" );
    private static final TimesTenFormat H2O_FORMAT = new TimesTenFormat( "0" );
    
    private static final double X_SPACING = 15;
    private static final double Y_SPACING = 20;
    private static final Font FONT = new PhetFont( 25 );
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    
    private final HTMLNode _h3oCountNode;
    private final HTMLNode _ohCountNode;
    private final HTMLNode _h2oCountNode;
    
    public MoleculeCountNode( Liquid liquid ) {
        super();
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        H3ONode h3oNode = new H3ONode();
        OHNode ohNode = new OHNode();
        H2ONode h2oNode = new H2ONode();
        _h3oCountNode = new HTMLNode(); //XXX
        _h3oCountNode.setFont( FONT );
        _ohCountNode = new HTMLNode(); //XXX
        _ohCountNode.setFont( FONT );
        _h2oCountNode = new HTMLNode(); //XXX
        _h2oCountNode.setFont( FONT );
        
        addChild( h3oNode );
        addChild( ohNode );
        addChild( h2oNode );
        addChild( _h3oCountNode );
        addChild( _ohCountNode );
        addChild( _h2oCountNode );
        
        h3oNode.setOffset( 0, 0 );
        ohNode.setOffset( h3oNode.getFullBoundsReference().getX(), h3oNode.getFullBoundsReference().getMaxY() + Y_SPACING );
        h2oNode.setOffset( ohNode.getFullBoundsReference().getX(), ohNode.getFullBoundsReference().getMaxY() + Y_SPACING );
        PBounds bH3O = h3oNode.getFullBoundsReference();
        PBounds bOH = ohNode.getFullBoundsReference();
        PBounds bH2O = h2oNode.getFullBoundsReference();
        final double maxX = Math.max( bH3O.getMaxX(), Math.max( bOH.getMaxX(), bH2O.getMaxX() ) );
        _h3oCountNode.setOffset( maxX + X_SPACING, bH3O.getY() + ( bH3O.getHeight() - _h3oCountNode.getFullBoundsReference().getHeight() ) / 2 );
        _ohCountNode.setOffset( maxX + X_SPACING, bOH.getY() + ( bOH.getHeight() - _ohCountNode.getFullBoundsReference().getHeight() ) / 2 );
        _h2oCountNode.setOffset( maxX + X_SPACING, bH2O.getY() + ( bH2O.getHeight() - _h2oCountNode.getFullBoundsReference().getHeight() ) / 2 );
        
        scale( 0.60 ); //XXX
        
        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    private void update() {
        _h3oCountNode.setHTML( H3O_FORMAT.format( _liquid.getNumberOfMoleculesH3O() ) );
        _ohCountNode.setHTML( OH_FORMAT.format( _liquid.getNumberOfMoleculesOH() ) );
        _h2oCountNode.setHTML( H2O_FORMAT.format( _liquid.getNumberOfMoleculesH2O() ) );
    }
}
