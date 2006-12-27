package electron.man.laws;

import electron.man.Motion;

/*Rules for moving a single man.*/

public interface MotionChooser {
    public Motion getMotion();
}
