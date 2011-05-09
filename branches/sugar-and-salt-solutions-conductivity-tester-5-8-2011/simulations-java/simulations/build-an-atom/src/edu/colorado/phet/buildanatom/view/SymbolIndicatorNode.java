// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
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
    public static final Font DEFAULT_ELEMENT_NAME_FONT = new PhetFont( 12, true );//This default font is used predominantly in Build an Atom

    private final IDynamicAtom atom;
    private final PText symbol;
    private final PText protonCount;
    private final PNode boundingBox;
    private final PText massNumberNode;
    private final PText chargeNode;
    private final PText elementName;
    private final PNode textContent;//keep the text in a separate PNode so it can be repositioned; this is used in
    // Interactive Isotopes when the charge value isn't displayed to center the text in the white box.

    /**
     * Creates a SymbolIndicatorNode with the default font.
     *
     * @param atom - The atom whose attributes are being depicted in the
     * symbol.
     * @param showElementName - A flag that determines whether the name of
     * the element (e.g. Hydrogen) is shown beneath the symbol.
     */
    public SymbolIndicatorNode( final IDynamicAtom atom, boolean showElementName ) {
        this( atom, showElementName, true );
    }

    /**
     * Primary constructor.  This contains several flags that control various
     * aspects of the symbol's appearance and behavior.
     *
     * @param atom - The atom whose attributes are being depicted in the
     * symbol.
     * @param showElementName - A flag that determines whether the name of
     * the element (e.g. Hydrogen) is shown beneath the symbol.
     * @param showCharge - A flag the determines whether the charge number is
     * depicted. This is often used in cases where the symbol will always be
     * neutral.
     */
    public SymbolIndicatorNode( final IDynamicAtom atom, boolean showElementName, boolean showCharge ) {

        // Must be big enough to hold Ne with 2 digit numbers on both sides.
        double width = 83;
        double height = 83;

        this.atom = atom;
        atom.addAtomListener( new AtomListener.Adapter() {
            @Override
            public void configurationChanged() {
                updateSymbol();
            }
        } );

        boundingBox = new PhetPPath( new Rectangle2D.Double( 0, 0, width, height ), Color.white,
                new BasicStroke( 1 ), Color.black );
        addChild( boundingBox );

        textContent = new PNode();
        addChild( textContent );

        // Textual symbol.
        symbol = new PText();
        symbol.setFont( SYMBOL_FONT );
        textContent.addChild( symbol );

        // Proton number.
        protonCount = new PText();
        protonCount.setFont( NUMBER_FONT );
        protonCount.setTextPaint( PhetColorScheme.RED_COLORBLIND );
        textContent.addChild( protonCount );

        // Mass number.
        massNumberNode = new PText();
        massNumberNode.setFont( NUMBER_FONT );
        massNumberNode.setTextPaint( Color.BLACK );
        textContent.addChild( massNumberNode );

        // Charge value indicator.
        chargeNode = new PText();
        chargeNode.setFont(NUMBER_FONT);
        if (showCharge){
            textContent.addChild( chargeNode );
        }

        // Element name.
        elementName = new PText();
        elementName.setFont( DEFAULT_ELEMENT_NAME_FONT );
        elementName.setTextPaint( Color.RED );
        if ( showElementName ){
            addChild( elementName );
        }

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

        massNumberNode.setText( atom.getMassNumber()==0?"":"" + atom.getMassNumber());
        massNumberNode.setOffset( symbol.getFullBoundsReference().getMinX() - massNumberNode.getFullBoundsReference().width,
                symbol.getFullBoundsReference().getMinY()-massNumberNode.getFullBounds().getHeight()* OFFSET_FRACTION );
        if (massNumberNode.getFullBounds().getMinX() < boundingBox.getFullBounds().getMinX()){
            massNumberNode.setOffset( boundingBox.getFullBounds().getMinX()+TEXT_INSET,massNumberNode.getFullBoundsReference().getY() );
        }

        chargeNode.setText( atom.getMassNumber()==0?"":atom.getFormattedCharge() );
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

        elementName.setText( atom.getName() );
        if (elementName.getFullBoundsReference().width > boundingBox.getFullBoundsReference().width){
            // Make sure the caption is not wider than the bounding box and,
            // if it is, scale it to fit.
            elementName.setScale( boundingBox.getFullBoundsReference().width / elementName.getFullBoundsReference().width );
        }
        elementName.setOffset(
                boundingBox.getFullBoundsReference().getCenterX() - elementName.getFullBoundsReference().width / 2,
                boundingBox.getFullBoundsReference().getMaxY() );

        textContent.centerFullBoundsOnPoint( boundingBox.getFullBoundsReference().getCenterX(), boundingBox.getFullBoundsReference().getCenterY() );
    }
}
