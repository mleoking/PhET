/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.util.TimesTenNumberFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FormattedNumberNode;
import edu.colorado.phet.common.piccolophet.nodes.RectangularBackgroundNode;
import edu.colorado.phet.phscale.PHScaleImages;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.colorado.phet.phscale.util.ConstantPowerOfTenNumberFormat;
import edu.umd.cs.piccolo.nodes.PImage;
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
    
    private static final Font VALUE_FONT = new PhetFont( 16 );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final Color VALUE_BACKGROUND_COLOR = new Color( 255, 255, 255, 128 ); // translucent white
    private static final Insets VALUE_INSETS = new Insets( 1, 1, 1, 1 );
    
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
        H3ONode h3oNode = new H3ONode.Big();
        OHNode ohNode = new OHNode.Big();
        H2ONode h2oNode = new H2ONode.Big();
        
        // values
        _h3oCountNode = new ValueNode( H3O_FORMAT );
        _ohCountNode = new ValueNode( OH_FORMAT );
        _h2oCountNode = new ValueNode( H2O_FORMAT );
        
        // update before setting offsets so that we have meaningful sizes for the value nodes
        update();
        
        addChild( h2oNode );
        addChild( h3oNode );
        addChild( ohNode );
        addChild( _h3oCountNode );
        addChild( _h2oCountNode );
        addChild( _ohCountNode );
        
        h2oNode.setOffset( 0, 0 );
        h3oNode.setOffset( h2oNode.getFullBoundsReference().getCenterX() - h3oNode.getFullBoundsReference().getWidth() - 15, h2oNode.getFullBoundsReference().getHeight() + 10 );
        ohNode.setOffset( h2oNode.getFullBoundsReference().getCenterX() + 15, h2oNode.getFullBoundsReference().getHeight() + 40 );
        _h2oCountNode.setOffset( h2oNode.getFullBoundsReference().getCenterX() - 25, h2oNode.getFullBoundsReference().getCenterY() + 5 );
        _h3oCountNode.setOffset( h3oNode.getFullBoundsReference().getCenterX() - 50, h3oNode.getFullBoundsReference().getCenterY() - 10 );
        _ohCountNode.setOffset( ohNode.getFullBoundsReference().getX() + 5, ohNode.getFullBoundsReference().getCenterY() - 15 );
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
