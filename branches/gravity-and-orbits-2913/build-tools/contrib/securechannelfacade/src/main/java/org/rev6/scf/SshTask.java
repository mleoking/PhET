package org.rev6.scf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


public abstract class SshTask
{
  protected final Logger logger = 
    Logger.getLogger(this.getClass().getPackage().getName());
  
  /**
   * Checks the input stream from the ssh session for status codes.
   * @param  in InputStream representing the ssh channel
   * @return int representing the status code.  0 returned if succesful.  
   * @throws IOException if there was a problem reading the InputStram
   */
  int checkAck(InputStream in) throws IOException
  {
    int b = in.read();
    
    // b may be
    // 0 for success,
    // 1 for error,
    // 2 for fatal error,
    
    if (b == 0)
      return b;
    if (b == -1)
      return b;
    
    if (b == 1 || b == 2)
    {
      StringBuffer sb = new StringBuffer();
      int c;
      
      do
      {
        c = in.read();
        sb.append((char) c);
      }
      while (c != '\n');
      
      if (b == 1)
      {
        logger.severe("scp failed with an error - reason: " + sb.toString());
      }
      if (b == 2)
      {
        logger.severe("scp failed with a fatal error - reason: " 
            + sb.toString());
      }
    }
    return b;
  }

  
  void sendAck(OutputStream out) throws IOException
  {
    out.write(0);
    out.flush();
  }
  
  ChannelExec connectToChannel(Session sshSession, String cmd)
    throws JSchException
  {
    ChannelExec channel = (ChannelExec) sshSession.openChannel("exec");
    channel.setCommand(cmd);
    channel.connect();
    return channel;
  }
  
  
  abstract void execute(Session session) throws SshException;
}
