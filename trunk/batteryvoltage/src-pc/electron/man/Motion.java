package electron.man;


public interface Motion {
    /*Return true if has more to do.*/
    public boolean update( double dt, Man m );
}
