package org.rev6.scf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;


public class SshCommand extends SshTask
{ 
  static String SSH_EXEC_COMMAND = "EXEC";
  
  protected final Logger logger = 
    Logger.getLogger(this.getClass().getPackage().getName());

  String command;
  OutputStream out;
  
  public SshCommand(String command, OutputStream out)
  {
    this.setCommand(command);
    this.setOutputStream(out);
  }
  
  public SshCommand(String command)
  {
   this(command, System.out);    
  }
  
//  @Override
  void execute(Session sshSession) throws SshException
  {
    InputStream in = null;
    ChannelExec channel = null;
   
    if (command == null)
    {
      throw new IllegalStateException("command attribute of " + 
          this.getClass().getName() + " can't be null.") ;
    }
    
    try
    {
      try
      { 
        channel = this.connectToChannel(sshSession,command);
        in = channel.getInputStream();
        streamOutput(channel,in);
      }
      finally
      {
        if (in != null)
          in.close();
        if (channel != null)
          channel.disconnect();
      }
    }
    catch (Exception e)
    {
      throw new SshException(e);
    }
  } 
  
  private void streamOutput(ChannelExec channel, InputStream in) 
  throws IOException
  {
    byte[] buf = new byte[1024];
    
    while (true)
    {
      while (in.available() > 1)
      {
        int bytesRead = in.read(buf);
        if (bytesRead < 0 ) break;
        this.out.write(buf);
      }
      if (channel.isClosed()) break;
      sleepForOneSecondAndIgnoreInterrupt();
    }
  }
  
  private void sleepForOneSecondAndIgnoreInterrupt()
  {
    try
    {
      Thread.sleep(1000); 
    }
    catch (InterruptedException e)
    {
      logger.log(Level.FINE,"Interrupt caught and ignored: ", e);
    }
  }
  
  public void setCommand(String command)
  { 
    if (command == null)
    {
      throw new IllegalArgumentException("command can't be null");
    }
    this.command = command;
  }
  
  public void setOutputStream(OutputStream out)
  {
    if (out == null)
    {
      throw new IllegalArgumentException("out can't be null");
    }
   this.out = out;
  }
  
  public String toString()
  {
    if (this.command != null)
    {
      return this.getClass().getName() + " Task: " + this.command;  
    }
    return this.getClass().getName() + "Task: command property is null.";
  }
}
