package org.rev6.scf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

/**
 * SshTask for simulating an scp from a remote server.  A valid ScpFile 
 * must be passed in either during construction or via the set
 * @author jwhaley
 */
public class ScpDownload extends SshTask
{
  private static final String SCP_DOWNLOAD_COMMAND = "scp -f ";
  
  private ScpFile scpFile;
  
  public ScpDownload()
  {
    super();
  }
  
  public ScpDownload(ScpFile scpFile)
  {
    super();
    this.scpFile = scpFile;
  }
  
//  @Override
  void execute(Session sshSession) throws SshException
  {
    InputStream in = null;
    OutputStream out = null;
    FileOutputStream fos = null;
    ChannelExec channel = null;
    try
    {
      try
      {
        long fileSize;
        String cmd = SCP_DOWNLOAD_COMMAND + this.scpFile.getRemotePath();

        channel = connectToChannel(sshSession,cmd);        
        fos = new FileOutputStream(scpFile.getLocalFile());
        in = channel.getInputStream();
        out = channel.getOutputStream();
        
        sendAck(out);             
        
        fileSize = getFileSizeFromStream(in);
        skipFileName(in);
        
        sendAck(out);
        
        writePayloadToFile(in,out,fos,fileSize);
      }
      finally
      {
        if (out != null)
          out.close();
        if (in != null)
          in.close();
        if (fos != null)
          fos.close();
        if (channel != null)
          channel.disconnect();
      }
    }
    catch (Exception e)
    {
      throw new SshException(e);
    }
  }
  
  private long getFileSizeFromStream(InputStream in) 
  throws SshException,IOException
  {
    long filesize = 0L;
    
    if (checkAck(in) != 'C')
    {
      throw new SshException("Scp download from failed. Reason: Initializing "
          + " size response returned a status that is not 'C'"); 
    }

    in.skip(5); //receive the expected '0644 '
    
    while(true)
    {
      int b = in.read(); 
      if(b < 0)
      {
        throw new SshException("Scp download from failed. Reason: reading " 
          + "file size returned a response of less than 0.");
      }
      if(b == ' ')
        break;
      filesize = filesize * 10L + (long) (b - '0');
    }
    return filesize;
  }
  
  private void skipFileName(InputStream in) throws IOException
  { 
    for (int b = in.read(); b != '\n'; b = in.read()) 
    { 
      continue;
    }
  }
  
  private void writePayloadToFile(InputStream in, OutputStream out, 
      FileOutputStream fos, long fileSize) throws SshException,IOException
  {
    byte[] inBuffer = new byte[1024];
    int readSize;
    while (true)
    {
      int bytesRead;

      if (inBuffer.length < fileSize)
        readSize = inBuffer.length;
      else
        readSize = (int) fileSize;
   
      bytesRead = in.read(inBuffer,0,readSize);
      
      if (bytesRead < 0)
      {
        throw new SshException("Scp download from failed.  Reason: Unable to "
            + "download payload of file.");
      }
      
      fos.write(inBuffer, 0, bytesRead);
      fileSize -= bytesRead;
      
      if (fileSize == 0L) break;
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
}