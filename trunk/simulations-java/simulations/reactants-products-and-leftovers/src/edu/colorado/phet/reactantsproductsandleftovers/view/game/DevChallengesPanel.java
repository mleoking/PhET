
package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;


public class DevChallengesPanel extends JPanel {

    private final JComboBox[] reactionCombBoxes;
    private final JRadioButton[] beforeRadioButtons;
    private final JRadioButton[] afterRadioButtons;

    public DevChallengesPanel( int level ) {
        int numberOfChallenges = GameModel.getChallengesPerGame();
        reactionCombBoxes = new JComboBox[ numberOfChallenges ];
        beforeRadioButtons = new JRadioButton[ numberOfChallenges ];
        afterRadioButtons = new JRadioButton[ numberOfChallenges ];
        for ( int i = 0; i < numberOfChallenges; i++ ) {
            
        }
    }

    public GameChallenge[] getChallenges() {
        int numberOfChallenges = GameModel.getChallengesPerGame();
        GameChallenge[] challenges = new GameChallenge[ numberOfChallenges ];
        //XXX create challenges from user-interface settings
        return challenges;
    }

}
