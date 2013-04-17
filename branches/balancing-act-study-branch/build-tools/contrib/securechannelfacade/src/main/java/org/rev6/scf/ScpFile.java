package org.rev6.scf;

import java.io.File;

public class ScpFile
{
  private final File localFile;
  private final String remotePath;
  
  public ScpFile(final File localFile, final String remotePath)
  {
    if (localFile == null || remotePath == null) 
    {
      throw new IllegalArgumentException("File reference and path must " +
      		"be non-null"); 
    }
    else
    {
      this.localFile = localFile;
      this.remotePath = remotePath;
    }
  }
  
  public ScpFile(final File file)
  {
    this(file,file.getName());
  }
  
  public File getLocalFile()
  {
    return this.localFile;
  }
  
  public String getRemotePath()
  {
    return this.remotePath;
  }
  
  public long getFileSize()
  {
    return this.localFile.length();
  }
}
