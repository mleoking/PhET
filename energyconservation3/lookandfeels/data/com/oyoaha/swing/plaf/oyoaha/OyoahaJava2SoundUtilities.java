/* ====================================================================
 * Copyright (c) 2001-2003 OYOAHA. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. The names "OYOAHA" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. 
 *    For written permission, please contact email@oyoaha.com.
 *
 * 3. Products derived from this software may not be called "OYOAHA",
 *    nor may "OYOAHA" appear in their name, without prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL OYOAHA OR ITS CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oyoaha.swing.plaf.oyoaha;

import java.awt.*;
import javax.swing.*;
import javax.sound.sampled.*;

class OyoahaJava2SoundUtilities extends OyoahaJava1SoundUtilities
{
  protected static boolean soundInitialized;

  protected static Clip clip2 = null;
  protected static Clip clip3 = null;

  protected static boolean scrollPlay;

  public void initialize(String pressed, String scrolled)
  {
    if(pressed!=null)
    {
      try
      {
        OyoahaLookAndFeel lnf = (OyoahaLookAndFeel)UIManager.getLookAndFeel();
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(lnf.getOyoahaTheme().getInputStream(pressed));

        if(audioInputStream!=null)
        {
          AudioFormat format = audioInputStream.getFormat();
          if ((format.getEncoding() == AudioFormat.Encoding.ULAW) || (format.getEncoding() == AudioFormat.Encoding.ALAW))
          {
            AudioFormat tmp = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), format.getSampleSizeInBits()*2, format.getChannels(), format.getFrameSize()*2, format.getFrameRate(), true);
            audioInputStream = AudioSystem.getAudioInputStream(tmp, audioInputStream);
            format = tmp;
          }
          DataLine.Info info = new DataLine.Info(Clip.class,  audioInputStream.getFormat(), ((int) audioInputStream.getFrameLength()*format.getFrameSize()));
          clip2 = (Clip)AudioSystem.getLine(info);
          clip2.open(audioInputStream);
        }
      }
      catch (Exception ex)
      {

      }
    }

    if(scrolled!=null)
    {
      try
      {
        OyoahaLookAndFeel lnf = (OyoahaLookAndFeel)UIManager.getLookAndFeel();
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(lnf.getOyoahaTheme().getInputStream(scrolled));

        if(audioInputStream!=null)
        {
          AudioFormat format = audioInputStream.getFormat();
          if ((format.getEncoding() == AudioFormat.Encoding.ULAW) || (format.getEncoding() == AudioFormat.Encoding.ALAW))
          {
            AudioFormat tmp = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), format.getSampleSizeInBits()*2, format.getChannels(), format.getFrameSize()*2, format.getFrameRate(), true);
            audioInputStream = AudioSystem.getAudioInputStream(tmp, audioInputStream);
            format = tmp;
          }
          DataLine.Info info = new DataLine.Info(Clip.class,  audioInputStream.getFormat(), ((int) audioInputStream.getFrameLength()*format.getFrameSize()));
          clip3 = (Clip)AudioSystem.getLine(info);
          clip3.open(audioInputStream);
        }
      }
      catch (Exception ex)
      {

      }
    }

    soundInitialized = true;
  }

  public void uninitialize()
  {
    soundInitialized = false;

    if(clip2!=null)
    {
      clip2.stop();
      clip2.flush();
      clip2.close();
      clip2 = null;
    }

    if(clip3!=null)
    {
      clip3.stop();
      clip3.flush();
      clip3.close();
      clip3 = null;
    }
  }

  /**
  *
  */
  public void playStartClick()
  {
    if(!soundInitialized)
    {
      return;
    }

    Component c = OyoahaUtilities.getPressed();

    if(c instanceof JScrollBar || c instanceof JSlider)
    {
      if(clip3!=null)
      {
        clip3.stop();
        clip3.setFramePosition(0);
        clip3.loop(Clip.LOOP_CONTINUOUSLY);
        scrollPlay = true;
      }
    }
    else
    if(clip2!=null)
    {
      clip2.stop();
      clip2.setFramePosition(0);
      clip2.start();
    }
  }

  /**
  *
  */
  public void playStopClick()
  {
    if(!soundInitialized)
    {
      return;
    }

    if(scrollPlay && clip3!=null)
    {
      clip3.stop();
      scrollPlay = false;
    }
  }
}