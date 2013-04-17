/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2009-06-26 10:56:39 -0500 (Fri, 26 Jun 2009) $
 * $Revision: 11127 $
 *
 * Copyright (C) 2003-2005  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jmol.viewer;

import org.jmol.util.Escape;
import org.jmol.util.Logger;

import org.jmol.modelset.ModelSet;

import java.util.Hashtable;
import java.util.BitSet;
import java.util.Map;

class AnimationManager {

  Viewer viewer;

  AnimationManager(Viewer viewer) {
    this.viewer = viewer;
  }

  int currentModelIndex = 0;

  void setCurrentModelIndex(int modelIndex) {
    setCurrentModelIndex(modelIndex, true);  
  }
  
  void setCurrentModelIndex(int modelIndex, boolean clearBackgroundModel) {
    int formerModelIndex = currentModelIndex;
    ModelSet modelSet = viewer.getModelSet();
    int modelCount = (modelSet == null ? 0 : modelSet.getModelCount());
    if (modelCount == 1)
      currentModelIndex = modelIndex = 0;
    else if (modelIndex < 0 || modelIndex >= modelCount)
      modelIndex = -1;
    String ids = null;
    boolean isSameSource = false;
    if (currentModelIndex != modelIndex) {
      if (modelCount > 0) {
        boolean toDataFrame = viewer.isJmolDataFrame(modelIndex);
        boolean fromDataFrame = viewer.isJmolDataFrame(currentModelIndex);
        if (fromDataFrame)
          viewer.setJmolDataFrame(null, -1, currentModelIndex);
        if (currentModelIndex != -1)
          viewer.saveModelOrientation();
        if (fromDataFrame || toDataFrame) {
          ids = viewer.getJmolFrameType(modelIndex) 
          + " "  + modelIndex + " <-- " 
          + " " + currentModelIndex + " " 
          + viewer.getJmolFrameType(currentModelIndex);
          
          isSameSource = (viewer.getJmolDataSourceFrame(modelIndex) == viewer
              .getJmolDataSourceFrame(currentModelIndex));
        }
      }
      currentModelIndex = modelIndex;
      if (ids != null) {
        if (modelIndex >= 0)
          viewer.restoreModelOrientation(modelIndex);
        if (isSameSource && ids.indexOf("quaternion") >= 0 
            && ids.indexOf("ramachandran") < 0
            && ids.indexOf(" property ") < 0) {
          viewer.restoreModelRotation(formerModelIndex);
        }
      }
    }
    viewer.setTrajectory(currentModelIndex);
    viewer.setFrameOffset(currentModelIndex);
    if (currentModelIndex == -1 && clearBackgroundModel)
      setBackgroundModelIndex(-1);  
    viewer.setTainted(true);
    setFrameRangeVisible();
    setStatusFrameChanged();
    if (modelSet != null) {
      if (!viewer.getSelectAllModels())
        viewer.setSelectionSubset(viewer.getModelUndeletedAtomsBitSet(currentModelIndex));
    }
  
  }

  private void setStatusFrameChanged() {
    if (viewer.getModelSet() != null)
      viewer.setStatusFrameChanged(animationOn ? -2 - currentModelIndex
          : currentModelIndex);
  }
  
  int backgroundModelIndex = -1;
  void setBackgroundModelIndex(int modelIndex) {
    ModelSet modelSet = viewer.getModelSet();
    if (modelSet == null || modelIndex < 0 || modelIndex >= modelSet.getModelCount())
      modelIndex = -1;
    backgroundModelIndex = modelIndex;
    if (modelIndex >= 0)
      viewer.setTrajectory(modelIndex);
    viewer.setTainted(true);
    setFrameRangeVisible(); 
  }
  
  private BitSet bsVisibleFrames = new BitSet();
  BitSet getVisibleFramesBitSet() {
    return bsVisibleFrames;
  }
  
  private void setFrameRangeVisible() {
    bsVisibleFrames.clear();
    if (backgroundModelIndex >= 0)
      bsVisibleFrames.set(backgroundModelIndex);
    if (currentModelIndex >= 0) {
      bsVisibleFrames.set(currentModelIndex);
      return;
    }
    if (frameStep == 0)
      return;
    int nDisplayed = 0;
    int frameDisplayed = 0;
    for (int i = firstModelIndex; i != lastModelIndex; i += frameStep)
      if (!viewer.isJmolDataFrame(i)) {
        bsVisibleFrames.set(i);
        nDisplayed++;
        frameDisplayed = i;
      }
    if (firstModelIndex == lastModelIndex || !viewer.isJmolDataFrame(lastModelIndex)
        || nDisplayed == 0) {
      bsVisibleFrames.set(lastModelIndex);
      if (nDisplayed == 0)
        firstModelIndex = lastModelIndex;
      nDisplayed = 0;
    }
    if (nDisplayed == 1 && currentModelIndex < 0)
      setCurrentModelIndex(frameDisplayed);
  }

  AnimationThread animationThread;

  boolean inMotion = false;
  void setInMotion(boolean inMotion) {
    this.inMotion = inMotion;
  }

  /****************************************************************
   * Animation support
   ****************************************************************/
  
  int firstModelIndex;
  int lastModelIndex;
  int frameStep;

  void initializePointers(int frameStep) {
    firstModelIndex = 0;
    int modelCount = viewer.getModelCount();
    lastModelIndex = (frameStep == 0 ? 0 
        : modelCount) - 1;
    this.frameStep = frameStep;
    viewer.setFrameVariables(firstModelIndex, lastModelIndex);
  }

  void clear() {
    setAnimationOn(false);
    setCurrentModelIndex(0);
    currentDirection = 1;
    setAnimationDirection(1);
    setAnimationFps(10);
    setAnimationReplayMode(0, 0, 0);
    initializePointers(0);
  }
  
  Map<String, Object> getAnimationInfo(){
    Map<String, Object> info = new Hashtable<String, Object>();
    info.put("firstModelIndex", Integer.valueOf(firstModelIndex));
    info.put("lastModelIndex", Integer.valueOf(lastModelIndex));
    info.put("animationDirection", Integer.valueOf(animationDirection));
    info.put("currentDirection", Integer.valueOf(currentDirection));
    info.put("displayModelIndex", Integer.valueOf(currentModelIndex));
    info.put("displayModelNumber", viewer.getModelNumberDotted(currentModelIndex));
    info.put("displayModelName", (currentModelIndex >=0 ? viewer.getModelName(currentModelIndex) : ""));
    info.put("animationFps", Integer.valueOf(animationFps));
    info.put("animationReplayMode", getAnimationModeName());
    info.put("firstFrameDelay", new Float(firstFrameDelay));
    info.put("lastFrameDelay", new Float(lastFrameDelay));
    info.put("animationOn", Boolean.valueOf(animationOn));
    info.put("animationPaused", Boolean.valueOf(animationPaused));
    return info;
  }
 
  String getState(StringBuffer sfunc) {
    int modelCount = viewer.getModelCount();
    if (modelCount < 2)
      return "";
    StringBuffer commands = new StringBuffer();
    if (sfunc != null) {
      sfunc.append("  _setFrameState;\n");
      commands.append("function _setFrameState() {\n");
    }
    commands.append("# frame state;\n");
    
    commands.append("# modelCount ").append(modelCount)
        .append(";\n# first ").append(
             viewer.getModelNumberDotted(0)).append(";\n# last ").append(
             viewer.getModelNumberDotted(modelCount - 1)).append(";\n");
    if (backgroundModelIndex >= 0)
      StateManager.appendCmd(commands, "set backgroundModel " + 
          viewer.getModelNumberDotted(backgroundModelIndex));
    BitSet bs = viewer.getFrameOffsets();
    if (bs != null)
      StateManager.appendCmd(commands, "frame align " + Escape.escape(bs));
    StateManager.appendCmd(commands, 
        "frame RANGE " + viewer.getModelNumberDotted(firstModelIndex) + " "
            + viewer.getModelNumberDotted(lastModelIndex));
    StateManager.appendCmd(commands, 
        "animation DIRECTION " + (animationDirection == 1 ? "+1" : "-1"));
    StateManager.appendCmd(commands, "animation FPS " + animationFps);
    StateManager.appendCmd(commands, "animation MODE " + getAnimationModeName()
        + " " + firstFrameDelay + " " + lastFrameDelay);
    StateManager.appendCmd(commands, "frame " + viewer.getModelNumberDotted(currentModelIndex));
    StateManager.appendCmd(commands, "animation "
            + (!animationOn ? "OFF" : currentDirection == 1 ? "PLAY"
                : "PLAYREV"));
    if (animationOn && animationPaused)
      StateManager.appendCmd(commands, "animation PAUSE");
    if (sfunc != null)
      commands.append("}\n\n");
    return commands.toString();
  }
  
  int animationDirection = 1;
  int currentDirection = 1;
  void setAnimationDirection(int animationDirection) {
    this.animationDirection = animationDirection;
    //if (animationReplayMode != ANIMATION_LOOP)
      //currentDirection = 1;
  }

  int animationFps;  // set in stateManager
  
  void setAnimationFps(int animationFps) {
    this.animationFps = animationFps;
  }

  // 0 = once
  // 1 = loop
  // 2 = palindrome
  
  int animationReplayMode = 0;
  float firstFrameDelay, lastFrameDelay;
  int firstFrameDelayMs, lastFrameDelayMs;
  void setAnimationReplayMode(int animationReplayMode,
                                     float firstFrameDelay,
                                     float lastFrameDelay) {
    this.firstFrameDelay = firstFrameDelay > 0 ? firstFrameDelay : 0;
    firstFrameDelayMs = (int)(this.firstFrameDelay * 1000);
    this.lastFrameDelay = lastFrameDelay > 0 ? lastFrameDelay : 0;
    lastFrameDelayMs = (int)(this.lastFrameDelay * 1000);
    if (animationReplayMode >= JmolConstants.ANIMATION_ONCE && animationReplayMode <= JmolConstants.ANIMATION_PALINDROME)
      this.animationReplayMode = animationReplayMode;
    else
      Logger.error("invalid animationReplayMode:" + animationReplayMode);
  }

  void setAnimationRange(int framePointer, int framePointer2) {
    int modelCount = viewer.getModelCount();
    if (framePointer < 0) framePointer = 0;
    if (framePointer2 < 0) framePointer2 = modelCount;
    if (framePointer >= modelCount) framePointer = modelCount - 1;
    if (framePointer2 >= modelCount) framePointer2 = modelCount - 1;
    firstModelIndex = framePointer;
    lastModelIndex = framePointer2;
    frameStep = (framePointer2 < framePointer ? -1 : 1);
    rewindAnimation();
  }

  boolean animationOn = false;
  private void animationOn(boolean TF) {
    animationOn = TF; 
    viewer.setBooleanProperty("_animating", TF);
  }
  
  boolean animationPaused = false;
  void setAnimationOn(boolean animationOn) {
    if (!animationOn || !viewer.haveModelSet()) {
      setAnimationOff(false);
      return;
    }
    if (!viewer.getSpinOn())
      viewer.refresh(3, "Viewer:setAnimationOn");
    setAnimationRange(-1, -1);
    resumeAnimation();
  }

  void setAnimationOff(boolean isPaused) {
    if (animationThread != null) {
      animationThread.interrupt();
      animationThread = null;
    }
    animationPaused = isPaused;
    if (!viewer.getSpinOn())
      viewer.refresh(3, "Viewer:setAnimationOff");
    animationOn(false);
    setStatusFrameChanged();
  }

  void pauseAnimation() {
    setAnimationOff(true);
  }
  
  void reverseAnimation() {
    currentDirection = -currentDirection;
    if (!animationOn)
      resumeAnimation();
  }
  
  int intAnimThread = 0;
  int lastModelPainted;
  void repaintDone() {
    lastModelPainted = currentModelIndex;
  }
  
  void resumeAnimation() {
    if(currentModelIndex < 0)
      setAnimationRange(firstModelIndex, lastModelIndex);
    if (viewer.getModelCount() <= 1) {
      animationOn(false);
      return;
    }
    animationOn(true);
    animationPaused = false;
    if (animationThread == null) {
      intAnimThread++;
      animationThread = new AnimationThread(firstModelIndex, lastModelIndex, intAnimThread);
      animationThread.start();
    }
  }
  
  boolean setAnimationNext() {
    return setAnimationRelative(animationDirection);
  }

  void setAnimationLast() {
    setCurrentModelIndex(animationDirection > 0 ? lastModelIndex : firstModelIndex);
  }

  void rewindAnimation() {
    setCurrentModelIndex(animationDirection > 0 ? firstModelIndex : lastModelIndex);
    currentDirection = 1;
    viewer.setFrameVariables(firstModelIndex, lastModelIndex);
  }
  
  boolean setAnimationPrevious() {
    return setAnimationRelative(-animationDirection);
  }

  boolean setAnimationRelative(int direction) {
    
//    if (true)
  //    return true;
    
    
    int frameStep = this.frameStep * direction * currentDirection;
    int modelIndexNext = currentModelIndex + frameStep;
    boolean isDone = (modelIndexNext > firstModelIndex
        && modelIndexNext > lastModelIndex || modelIndexNext < firstModelIndex
        && modelIndexNext < lastModelIndex);    
    if (isDone) {
      switch (animationReplayMode) {
      case JmolConstants.ANIMATION_ONCE:
        return false;
      case JmolConstants.ANIMATION_LOOP:
        modelIndexNext = (animationDirection == currentDirection ? firstModelIndex
            : lastModelIndex);
        break;
      case JmolConstants.ANIMATION_PALINDROME:
        currentDirection = -currentDirection;
        modelIndexNext -= 2 * frameStep;
      }
    }
    //Logger.debug("next="+modelIndexNext+" dir="+currentDirection+" isDone="+isDone);
    int nModels = viewer.getModelCount();
    if (modelIndexNext < 0 || modelIndexNext >= nModels)
      return false;
    setCurrentModelIndex(modelIndexNext);
    return true;
  }
  
  String getAnimationModeName() {
    switch (animationReplayMode) {
    case JmolConstants.ANIMATION_LOOP:
      return "LOOP";
    case JmolConstants.ANIMATION_PALINDROME:
      return "PALINDROME";
    default:
      return "ONCE";
    }
  }

  class AnimationThread extends Thread {
    final int framePointer;
    final int framePointer2;
    int intThread;

    AnimationThread(int framePointer, int framePointer2, int intAnimThread) {
      this.framePointer = framePointer;
      this.framePointer2 = framePointer2;
      this.setName("AnimationThread");
      intThread = intAnimThread;
    }

    @Override
    public void run() {
      long timeBegin = System.currentTimeMillis();
      int targetTime = 0;
      int sleepTime;
      //int holdTime = 0;
      if (Logger.debugging)
        Logger.debug("animation thread " + intThread + " running");
      viewer.requestRepaintAndWait();

      try {
        sleepTime = targetTime - (int) (System.currentTimeMillis() - timeBegin);
        if (sleepTime > 0)
          Thread.sleep(sleepTime);
        boolean isFirst = true;
        while (!isInterrupted() && animationOn) {
          if (currentModelIndex == framePointer) {
            targetTime += firstFrameDelayMs;
            sleepTime = targetTime
                - (int) (System.currentTimeMillis() - timeBegin);
            if (sleepTime > 0)
              Thread.sleep(sleepTime);
          }
          if (currentModelIndex == framePointer2) {
            targetTime += lastFrameDelayMs;
            sleepTime = targetTime
                - (int) (System.currentTimeMillis() - timeBegin);
            if (sleepTime > 0)
              Thread.sleep(sleepTime);
          }
          if (!isFirst && lastModelPainted == currentModelIndex && !setAnimationNext()) {
            Logger.debug("animation thread " + intThread + " exiting");
            setAnimationOff(false);
            return;
          }
          isFirst = false;
          targetTime += (1000 / animationFps);
          sleepTime = targetTime
              - (int) (System.currentTimeMillis() - timeBegin);

          while(!isInterrupted() && animationOn && !viewer.getRefreshing()) {
            Thread.sleep(10); 
          }
          if (!viewer.getSpinOn())
            viewer.refresh(1, "animationThread");
          sleepTime = targetTime
              - (int) (System.currentTimeMillis() - timeBegin);
          if (sleepTime > 0)
            Thread.sleep(sleepTime);
        }
      } catch (InterruptedException ie) {
        Logger.debug("animation thread interrupted!");
        try {
          setAnimationOn(false);
        } catch (Exception e) {
          // null pointer -- don't care;
        }
      }
    }
  }
 
}
