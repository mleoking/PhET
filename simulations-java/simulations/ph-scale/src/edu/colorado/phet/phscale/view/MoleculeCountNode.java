package edu.colorado.phet.phscale.view;

import java.awt.Font;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;


public class MoleculeCountNode extends PComposite {
    
    private static final DecimalFormat H3O_FORMAT = new DecimalFormat( "0.00E0" );
    private static final DecimalFormat OH_FORMAT = new DecimalFormat( "0.00E0" );
    private static final DecimalFormat H2O_FORMAT = new DecimalFormat( "0E0" );
    
    private static final double Y_SPACING = 20;
    private static final Font FONT = new PhetFont( 25 );
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    
    private final PText _h3oCountNode;
    private final PText _ohCountNode;
    private final PText _h2oCountNode;
    
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
        _h3oCountNode = new PText(); //XXX
        _h3oCountNode.setFont( FONT );
        _ohCountNode = new PText(); //XXX
        _ohCountNode.setFont( FONT );
        _h2oCountNode = new PText(); //XXX
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
        _h3oCountNode.setOffset( maxX, bH3O.getY() + ( bH3O.getHeight() - _h3oCountNode.getFullBoundsReference().getHeight() ) / 2 );
        _ohCountNode.setOffset( maxX, bOH.getY() + ( bOH.getHeight() - _ohCountNode.getFullBoundsReference().getHeight() ) / 2 );
        _h2oCountNode.setOffset( maxX, bH2O.getY() + ( bH2O.getHeight() - _h2oCountNode.getFullBoundsReference().getHeight() ) / 2 );
        
        scale( 0.60 ); //XXX
        
        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    private void update() {
        _h3oCountNode.setText( H3O_FORMAT.format( _liquid.getNumberOfMoleculesH3O() ) );
        _ohCountNode.setText( OH_FORMAT.format( _liquid.getNumberOfMoleculesOH() ) );
        _h2oCountNode.setText( H2O_FORMAT.format( _liquid.getNumberOfMoleculesH2O() ) );
    }
}
