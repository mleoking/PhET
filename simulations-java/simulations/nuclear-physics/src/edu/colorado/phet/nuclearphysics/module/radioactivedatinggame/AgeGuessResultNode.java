package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D.Double;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

public class AgeGuessResultNode extends PNode {

	private static final Font TEXT_FONT = new PhetFont(18);
	private static final Color FILL_COLOR_BAD_GUESS = Color.RED;
	private static final Color FILL_COLOR_GOOD_GUESS = Color.GREEN;
	
	private final boolean _guessIsGood;
	private final PPath _backgoundRect;

	/**
	 * Contructor.
	 * 
	 * @param age - Age to be displayed, in milliseconds.
	 * @param isGuessGood - boolean indicating whether the guess was good.
	 */
	public AgeGuessResultNode(double age, boolean isGuessGood) {
		
		_guessIsGood = isGuessGood;
		
		// Create the text.
		PText ageText = new PText();
		ageText.setFont(TEXT_FONT);
		ageText.setText(String.format("%.0f", MultiNucleusDecayModel.convertMsToYears(age)) 
				+ " " + NuclearPhysicsStrings.READOUT_UNITS_YRS);
		
		// Create the bounding rectangle so that it encloses the text.
		_backgoundRect = new PPath( new Rectangle2D.Double( 0, 0, ageText.getFullBoundsReference().width * 1.2,
				ageText.getFullBoundsReference().height * 1.5 ) );
		_backgoundRect.setPaint(_guessIsGood ? FILL_COLOR_GOOD_GUESS : FILL_COLOR_BAD_GUESS);
		addChild(_backgoundRect);
		
		// Add the text and set its offset such that it is centered in the
		// rectangle.
		addChild(ageText);
		ageText.setOffset(
				_backgoundRect.getFullBoundsReference().width / 2 - ageText.getFullBoundsReference().width / 2,
				_backgoundRect.getFullBoundsReference().height / 2 - ageText.getFullBoundsReference().height / 2 );
	}

	public boolean isGuessGood() {
		return _guessIsGood;
	}
}
