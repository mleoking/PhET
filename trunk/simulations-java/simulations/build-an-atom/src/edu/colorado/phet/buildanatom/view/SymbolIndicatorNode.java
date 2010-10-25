package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class defines a PNode that represents a symbol for an atom, and the
 * format of the symbol is similar to how that element would be represented
 * on the periodic table.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class SymbolIndicatorNode extends PNode {

    public static final Font SYMBOL_FONT = new PhetFont( 28, true );
    public static final Font NUMBER_FONT = new PhetFont( 20, true );

    private final Atom atom;
    private final PText symbol;
    private final PText protonCount;
    private final PNode boundingBox;
    private final PText massNumberNode;
    private final PText chargeNode;

    public SymbolIndicatorNode( final Atom atom) {
        //has to be big enough to hold Ne with 2 digit numbers on both sides
        double width = 83;
        double height = 83;

        this.atom = atom;
        atom.addObserver( new SimpleObserver() {
            public void update() {
                updateSymbol();
            }
        } );

        boundingBox = new PhetPPath( new Rectangle2D.Double( 0, 0, width, height ), Color.white,
                new BasicStroke( 1 ), Color.black );
        addChild( boundingBox );

        // Textual symbol.
        symbol = new PText();
        symbol.setFont( SYMBOL_FONT );
        addChild( symbol );

        // Proton number.
        protonCount = new PText();
        protonCount.setFont( NUMBER_FONT );
        protonCount.setTextPaint( Color.RED );
        addChild( protonCount );

        massNumberNode = new PText();
        massNumberNode.setFont( NUMBER_FONT );
        massNumberNode.setTextPaint( Color.BLACK );
        addChild( massNumberNode );

        chargeNode = new PText();
        chargeNode.setFont(NUMBER_FONT);
        addChild( chargeNode );

        updateSymbol();
    }

    private void updateSymbol(){
        symbol.setText( atom.getSymbol() );
        symbol.setOffset( boundingBox.getFullBoundsReference().getCenterX() - symbol.getFullBoundsReference().width / 2,
                boundingBox.getFullBoundsReference().getCenterY() - symbol.getFullBoundsReference().height / 2 );
        protonCount.setText( atom.getNumProtons()==0?"":"" + atom.getNumProtons() );
        final double OFFSET_FRACTION = 0.65;
        final double TEXT_INSET = 2;
        protonCount.setOffset( symbol.getFullBoundsReference().getMinX() - protonCount.getFullBoundsReference().width,
                symbol.getFullBoundsReference().getMaxY()-protonCount.getFullBounds().getHeight()* (1-OFFSET_FRACTION) );

        if (chargeNode.getFullBounds().getMaxX() > boundingBox.getFullBounds().getMaxX()){
            chargeNode.setOffset( boundingBox.getFullBounds().getMaxX()-chargeNode.getFullBounds().getWidth(),chargeNode.getOffset().getY() );
        }
        if (protonCount.getFullBounds().getMinX() < boundingBox.getFullBounds().getMinX()){
            protonCount.setOffset( boundingBox.getFullBounds().getMinX()+TEXT_INSET,protonCount.getFullBoundsReference().getY() );
        }

        massNumberNode.setText( atom.getAtomicMassNumber()==0?"":"" + atom.getAtomicMassNumber());
        massNumberNode.setOffset( symbol.getFullBoundsReference().getMinX() - massNumberNode.getFullBoundsReference().width,
                symbol.getFullBoundsReference().getMinY()-massNumberNode.getFullBounds().getHeight()* OFFSET_FRACTION );
        if (massNumberNode.getFullBounds().getMinX() < boundingBox.getFullBounds().getMinX()){
            massNumberNode.setOffset( boundingBox.getFullBounds().getMinX()+TEXT_INSET,massNumberNode.getFullBoundsReference().getY() );
        }

        chargeNode.setText( atom.getAtomicMassNumber()==0?"":atom.getFormattedCharge() );
        Paint chargePaint = Color.BLACK;
        if (atom.getCharge() > 0){
            chargePaint = Color.RED;
        }
        else if ( atom.getCharge() < 0){
            chargePaint = Color.BLUE;
        }
        chargeNode.setTextPaint( chargePaint );
        chargeNode.setOffset( symbol.getFullBoundsReference().getMaxX(),
                symbol.getFullBoundsReference().getMinY()-chargeNode.getFullBounds().getHeight()* OFFSET_FRACTION );

        if (chargeNode.getFullBounds().getMaxX() > boundingBox.getFullBounds().getMaxX()){
            chargeNode.setOffset( boundingBox.getFullBounds().getMaxX()-chargeNode.getFullBounds().getWidth()-TEXT_INSET,chargeNode.getOffset().getY() );
        }
    }
}
