package edu.colorado.phet.website.tests.redirections;

public class Request {
    private final String addr;
    private final int status;

    Request( String addr, int status ) {
        this.addr = addr;
        this.status = ( status == 304 || status == 200 ) ? 1 : status;
    }

    public String getAddr() {
        return addr;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public int hashCode() {
        return new Integer( status ).hashCode() + addr.hashCode();
    }

    @Override
    public boolean equals( Object o ) {
        return ( o instanceof Request && ( (Request) o ).getAddr().equals( getAddr() ) && ( (Request) o ).getStatus() == getStatus() );
    }

    @Override
    public String toString() {
        if ( status == 1 ) {
            return "OK";
        }
        else {
            return String.valueOf( status );
        }
    }

    public boolean isOk() {
        return status == 1;
    }
}
