package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.application.ApplicationConstructor
import _root_.edu.colorado.phet.common.phetcommon.view.util.SwingUtils
import _root_.edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing._
import model.ScalaClock
import _root_.edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher
import _root_.edu.colorado.phet.common.phetcommon.application.PhetApplication
import _root_.edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig

object DevLauncher {
  def main(args: Array[String]) = {
    val dialog = new JFrame
    val contentPane = new VerticalLayoutPanel
    val checkBox: JCheckBox = new JCheckBox("Remote Control is an indicator too", LadybugDefaults.remoteIsIndicator)
    checkBox.addActionListener(new ActionListener(){
      def actionPerformed(e: ActionEvent) = {
        LadybugDefaults.remoteIsIndicator=checkBox.isSelected
      }
    })
    contentPane.add(new JLabel("Ladybug 2D Options"))
    contentPane.setFillNone
    contentPane.add(checkBox)
    val jButton: JButton = new JButton("Launch")
    jButton.addActionListener(new ActionListener() {
      def actionPerformed(e: ActionEvent) = {
        val clock = new ScalaClock(30, 30 / 1000.0)
        new Thread(new Runnable() { //put into thread so it can call invokeAndWait
          def run = {
            dialog.dispose
            new PhetApplicationLauncher().launchSim(
              new PhetApplicationConfig(args, "moving-man", "ladybug-2d"),
              new ApplicationConstructor() {
                override def getApplication(a: PhetApplicationConfig): PhetApplication = new PhetApplication(a) {
                  addModule(new LadybugModule(clock))
                }
              })
          }
        }).start

      }
    })
    contentPane.add(jButton)
    dialog.setContentPane(contentPane)
    dialog.pack
    SwingUtils.centerWindowOnScreen(dialog)
    dialog.setVisible(true)


    println("finished")
  }
}