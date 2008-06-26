package edu.colorado.phet.phscale.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FormattedNumberNode;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;


public class MoleculeCountNode extends PComposite {
    
    private static final TimesTenNumberFormat H3O_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final TimesTenNumberFormat OH_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final TimesTenNumberFormat H2O_FORMAT = new TimesTenNumberFormat( "0" );
    
    private static final double X_SPACING = 15;
    private static final double Y_SPACING = 20;
    private static final Font VALUE_FONT = new PhetFont( 25 );
    private static final Color VALUE_COLOR = Color.BLACK;
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    
    private final FormattedNumberNode _h3oCountNode;
    private final FormattedNumberNode _ohCountNode;
    private final FormattedNumberNode _h2oCountNode;
    
    public MoleculeCountNode( Liquid liquid ) {
        super();
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        // icons
        H3ONode h3oNode = new H3ONode();
        OHNode ohNode = new OHNode();
        H2ONode h2oNode = new H2ONode();
        
        // values
        _h3oCountNode = new FormattedNumberNode( H3O_FORMAT, 0, VALUE_FONT, VALUE_COLOR );
        _ohCountNode = new FormattedNumberNode( OH_FORMAT, 0, VALUE_FONT, VALUE_COLOR );
        _h2oCountNode = new FormattedNumberNode( H2O_FORMAT, 0, VALUE_FONT, VALUE_COLOR );
        
        // update before positions so that layout uses values
        update();
        
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
        _h3oCountNode.setOffset( maxX + X_SPACING, bH3O.getCenterY() - _h3oCountNode.getFullBoundsReference().getHeight() / 2 );
        _ohCountNode.setOffset( maxX + X_SPACING, bOH.getCenterY() - _ohCountNode.getFullBoundsReference().getHeight() / 2 );
        _h2oCountNode.setOffset( maxX + X_SPACING, bH2O.getCenterY() - _h2oCountNode.getFullBoundsReference().getHeight() / 2 );
        
        scale( 0.60 ); //XXX
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    private void update() {
        _h3oCountNode.setValue( _liquid.getMoleculesH3O() );
        _ohCountNode.setValue( _liquid.getMoleculesOH() );
        _h2oCountNode.setValue( _liquid.getMoleculesH2O() );
    }
}
