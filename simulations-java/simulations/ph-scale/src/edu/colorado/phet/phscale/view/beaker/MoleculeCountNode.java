package edu.colorado.phet.phscale.view.beaker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FormattedNumberNode;
import edu.colorado.phet.common.piccolophet.nodes.RectangularBackgroundNode;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.colorado.phet.phscale.util.ConstantPowerOfTenNumberFormat;
import edu.colorado.phet.phscale.view.H2OMoleculeNode;
import edu.colorado.phet.phscale.view.H3OMoleculeNode;
import edu.colorado.phet.phscale.view.OHMoleculeNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * MoleculeCountNode displays the molecule counts shown in the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MoleculeCountNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final TimesTenNumberFormat H3O_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final TimesTenNumberFormat OH_FORMAT = new TimesTenNumberFormat( "0.00" );
    private static final ConstantPowerOfTenNumberFormat H2O_FORMAT = new ConstantPowerOfTenNumberFormat( "0.0", 25 );
    
    private static final double X_SPACING = 10;
    private static final double Y_SPACING = 20;
    private static final Font VALUE_FONT = new PhetFont( Font.BOLD, PHScaleConstants.CONTROL_FONT_SIZE );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final Color VALUE_BACKGROUND_COLOR = new Color( 255, 255, 255, 128 ); // translucent white
    private static final Insets VALUE_INSETS = new Insets( 4, 4, 4, 4 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    
    private final ValueNode _h3oCountNode;
    private final ValueNode _ohCountNode;
    private final ValueNode _h2oCountNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MoleculeCountNode( Liquid liquid ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        // icons
        H3OMoleculeNode h3oMoleculeNode = new H3OMoleculeNode();
        OHMoleculeNode ohMoleculeNode = new OHMoleculeNode();
        H2OMoleculeNode h2oMoleculeNode = new H2OMoleculeNode();
        
        // values
        _h3oCountNode = new ValueNode( H3O_FORMAT );
        _ohCountNode = new ValueNode( OH_FORMAT );
        _h2oCountNode = new ValueNode( H2O_FORMAT );
        
        // update before setting offsets so that we have meaningful sizes for the value nodes
        update();
        
        addChild( h3oMoleculeNode );
        addChild( ohMoleculeNode );
        addChild( h2oMoleculeNode );
        addChild( _h3oCountNode );
        addChild( _ohCountNode );
        addChild( _h2oCountNode );
        
        h3oMoleculeNode.setOffset( 0, 0 );
        ohMoleculeNode.setOffset( h3oMoleculeNode.getFullBoundsReference().getX(), h3oMoleculeNode.getFullBoundsReference().getMaxY() + Y_SPACING );
        h2oMoleculeNode.setOffset( ohMoleculeNode.getFullBoundsReference().getX(), ohMoleculeNode.getFullBoundsReference().getMaxY() + Y_SPACING );
        PBounds bH3O = h3oMoleculeNode.getFullBoundsReference();
        PBounds bOH = ohMoleculeNode.getFullBoundsReference();
        PBounds bH2O = h2oMoleculeNode.getFullBoundsReference();
        final double maxX = Math.max( bH3O.getMaxX(), Math.max( bOH.getMaxX(), bH2O.getMaxX() ) );
        _h3oCountNode.setOffset( maxX + X_SPACING, bH3O.getCenterY() - _h3oCountNode.getFullBoundsReference().getHeight() / 2 );
        _ohCountNode.setOffset( maxX + X_SPACING, bOH.getCenterY() - _ohCountNode.getFullBoundsReference().getHeight() / 2 );
        _h2oCountNode.setOffset( maxX + X_SPACING, bH2O.getCenterY() - _h2oCountNode.getFullBoundsReference().getHeight() / 2 );
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void update() {
        _h3oCountNode.setValue( _liquid.getMoleculesH3O() );
        _ohCountNode.setValue( _liquid.getMoleculesOH() );
        _h2oCountNode.setValue( _liquid.getMoleculesH2O() );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * Displays a formatted number on a background.
     */
    private static class ValueNode extends PComposite {

        private FormattedNumberNode _numberNode;
        
        public ValueNode( NumberFormat format ) {
            _numberNode = new FormattedNumberNode( format, 0, VALUE_FONT, VALUE_COLOR );
            RectangularBackgroundNode backgroundNode = new RectangularBackgroundNode( _numberNode, VALUE_INSETS, VALUE_BACKGROUND_COLOR );
            addChild( backgroundNode );
        }
        
        public void setValue( double value ) {
            _numberNode.setValue( value );
        }
    }
}
