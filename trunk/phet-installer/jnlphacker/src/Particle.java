
import java.awt.*;
import java.util.Random;

/**
 * Class: Particle
 * Class: PACKAGE_NAME
 * User: Ron LeMaster
 * Date: Nov 2, 2003
 * Time: 9:14:22 AM
 */

public class Particle {
	protected int x;
	protected int y;
	protected final Random rng = new Random();

	public Particle( int initialX, int initiallY ) {
		x = initialX;
		y = initiallY;
	}

	public synchronized void move() {
		x += rng.nextInt( 10 ) - 5;
		y += rng.nextInt( 20 ) - 10;
	}

	public void draw( Graphics g ) {
		int lx;
		int ly;

		synchronized( this ) {
			lx = x;
			ly = y;
		}
		g.drawRect( lx, ly, 10, 10 );
	}
}
