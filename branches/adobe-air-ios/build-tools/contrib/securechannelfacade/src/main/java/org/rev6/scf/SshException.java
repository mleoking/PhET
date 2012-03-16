package org.rev6.scf;

public class SshException extends Exception
{
  static final long serialVersionUID = 1L;
  public SshException(String message){super(message);}
  public SshException(Throwable cause){super(cause);}
  public SshException(String message, Throwable cause){super(message, cause);}
}
