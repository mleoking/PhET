package edu.colorado.phet.buildanatom.game;

/**
 * @author Sam Reid
 */
public class BAAFactory implements IChallengeFactory {
    public GameChallenge[] createChallenges( int numberOfChallenges, int level, int maxQuantity, GameChallenge.ChallengeVisibility challengeVisibility ) {
        return new GameChallenge[]{new NullChallenge()};
    }
}
