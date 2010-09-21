package org.rev6.scf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;


public class ScpUpload extends SshTask
{
  private ScpFile scpFile;
  private static final String SCP_UPLOAD_COMMAND = "scp -p -t ";
  
  public ScpUpload()
  {
    super();
  }
  
  public ScpUpload(ScpFile scpFile)
  {
    super();
    this.scpFile = scpFile;
  }
  
//  @Override
  void execute(Session sshSession) throws SshException
  {   
    if (this.scpFile == null)
    {
      throw new SshException("scpFile property has not been set and is null" ,
          new NullPointerException());
    }
    
    InputStream in = null;
    OutputStream out = null;
    InputStream fis = null; 
    ChannelExec channel = null;
    
    try
    {
      try
      {
        final String cmd = SCP_UPLOAD_COMMAND + this.scpFile.getRemotePath();        
        
        channel = this.connectToChannel(sshSession,cmd);
        fis = new FileInputStream(scpFile.getLocalFile());
        in = channel.getInputStream();
        out = channel.getOutputStream();

        if (checkAck(in) != 0)
        {
          throw new SshException("Acknowledgement check failed: Initializing " 
              + "session returned a status code other than 0");
        }
        
        sendFileSizeAndRemotePath(scpFile, out);
        
        if (checkAck(in) != 0)
        {
          throw new SshException("Scp upload failed. Reason: sending filesize "
              + "and filename returned a status code other than 0.");
        }
     
        sendPayloadToServer(out, fis);
        sendEOFToServer(out);
        
        if (checkAck(in) != 0)
        {
          throw new SshException("Scp upload failed.  Reason: sending the  " 
          		+ " file payload resulted a status code other than 0");
        }      
      }
      finally
      {
        if (out != null)
          out.close();
        if (in != null)
          in.close();
        if (fis != null)
          fis.close();
        if (channel != null)
          channel.disconnect();
      }
    }
    catch (Exception e)
    {
      throw new SshException(e);      
    }
  }
  
  private void sendEOFToServer(OutputStream out) throws IOException
  {
    out.write(0);
    out.flush();
  }
  
  private void sendFileSizeAndRemotePath(final ScpFile scpfile, 
      final OutputStream out) throws IOException
  {
    String command = "C0644 " + Long.toString(scpfile.getFileSize()) + " " 
      + scpfile.getRemotePath() + "\n";
    out.write(command.getBytes());
    out.flush();
  }
  
  private void sendPayloadToServer(OutputStream out, InputStream fis)
  throws IOException
  {
    byte[] buf = new byte[1024];
    while (true)
    {
      int len = fis.read(buf, 0, buf.length);
      
      if (len <= 0)
      {
        break;
      }
      else
      {
        out.write(buf, 0, len);
      }
    }
  }
  
  public void setScpFile(ScpFile scpFile)
  {
    if (scpFile == null)
    {
      throw new IllegalArgumentException("scpFile can't be null");
    }
    this.scpFile = scpFile;
  }
  
  public String toString()
  {
    if (this.scpFile != null)
    {
      return this.getClass().getName() + " Task: "  
      + this.scpFile.getLocalFile().getAbsolutePath() + " to "
      + this.scpFile.getRemotePath() + " at a remote host.";
    }
    return this.getClass().getName() + " Task: scpFile property is null.";
  }
}