package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

public class AgeGuessResultNode extends PNode {

	private static final Font TEXT_FONT = new PhetFont(20);
	private static final Color FILL_COLOR_BAD_GUESS = Color.RED;
	private static final Color FILL_COLOR_GOOD_GUESS = Color.GREEN;
	
	private final boolean _guessIsGood;
	private final PPath _backgoundRect;
	private final PImage _icon;
	private final ArrayList<Listener> _listeners = new ArrayList<Listener>();

	/**
	 * Contructor.
	 * 
	 * @param ageGuess - Age to be displayed, in years.
	 * @param isGuessGood - boolean indicating whether the guess was good.
	 */
	public AgeGuessResultNode(double ageGuess, boolean isGuessGood) {
		
		_guessIsGood = isGuessGood;
		
		// Create the text.
		PText ageText = new PText();
		ageText.setFont(TEXT_FONT);
		ageText.setText(NumberFormat.getNumberInstance().format(ageGuess) + " " + NuclearPhysicsStrings.READOUT_UNITS_YRS);
		
		// Create the bounding rectangle so that it encloses the text.
		_backgoundRect = new PPath( new Rectangle2D.Double( 0, 0, ageText.getFullBoundsReference().width * 1.2,
				ageText.getFullBoundsReference().height * 1.3 ) );
		_backgoundRect.setPaint(_guessIsGood ? FILL_COLOR_GOOD_GUESS : FILL_COLOR_BAD_GUESS);
		addChild(_backgoundRect);
		
		// Add the text and set its offset such that it is centered in the
		// rectangle.
		addChild(ageText);
		ageText.setOffset(
				_backgoundRect.getFullBoundsReference().width / 2 - ageText.getFullBoundsReference().width / 2,
				_backgoundRect.getFullBoundsReference().height / 2 - ageText.getFullBoundsReference().height / 2 );
		
		// Add the icon that graphically indicates whether the guess is good or bad.
		if (_guessIsGood){
			_icon = NuclearPhysicsResources.getImageNode("happy_face.png");
		}
		else {
			_icon = NuclearPhysicsResources.getImageNode("frowny_face.png");
		}
		
		_icon.setScale( _backgoundRect.getFullBoundsReference().height / _icon.getFullBoundsReference().height );
		_icon.setOffset(_backgoundRect.getFullBoundsReference().getMaxX() + 3, 0);
		addChild(_icon);
		
		// Add a mouse handler so that the user can click on this in order
		// to clear it.
		setPickable(true);
		setChildrenPickable(true);
        addInputEventListener( new PBasicInputEventHandler(){
			@Override
			public void mouseReleased(PInputEvent event) {
				// The user clicked on this node, which indicates that they
				// with to clear (i.e. remove) the guess result.  Notify any
				// listeners.
				notifyCleared();
			}
        } );
        
        // Add a different cursor so the user will have a clue that they can
        // do something by clicking.
//        Toolkit toolkit = Toolkit.getDefaultToolkit();
//        BufferedImage cursorImage = NuclearPhysicsResources.getImage("x_cursor.png");
//        Dimension size = toolkit.getBestCursorSize(cursorImage.getWidth(), cursorImage.getHeight());
//        Cursor myCursor = toolkit.createCustomCursor(cursorImage,
//        		new Point(size.width / 4, size.height / 4), "myCursor");
//        addInputEventListener(new CursorHandler(myCursor));
        // TODO: Custom cursor was turning out to be a lot of work, so I went
        // with just the hand.  If this is acceptable, delete the commented
        // out code just above.  jblanco, 7/27/2009
        addInputEventListener(new CursorHandler(CursorHandler.HAND));
	}

	public boolean isGuessGood() {
		return _guessIsGood;
	}
	
	private void notifyCleared(){
		for (Listener listener : _listeners){
			listener.userCleared(this);
		}
	}
	
	public void addListener(Listener listener) {
	    if ( !_listeners.contains( listener )){
	        _listeners.add( listener );
	    }
	}

	public boolean removeListener( Listener listener ) {
		return _listeners.remove( listener );
	}
	
	public static interface Listener {
		public void userCleared(AgeGuessResultNode ageGuessResultNode);
	}
}
