package edu.colorado.phet.simsharinganalysis.gui

// Copyright 2002-2011, University of Colorado

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing._
import edu.colorado.phet.common.phetcommon.application.PhetPersistenceDir
import java.util.Properties
import java.io.{File, FileInputStream, FileOutputStream}

/**
 * Show a 2d plot of student activity as a function of time.  Row = student machine, x-axis is time and color coding is activity
 * @author Sam Reid
 */
object PlotStudentActivity extends App {

  var lastUsedFile = ""

  //TODO: is duplicate with below
  val properties = new Properties {
    val file = new File(new PhetPersistenceDir(), "simsharing-analysis.properties")
    if ( file.exists() ) {
      load(new FileInputStream(file))
    }
  }
  if ( properties.containsKey("file") ) {
    lastUsedFile = properties.get("file").toString
  }

  SwingUtilities.invokeLater(new Runnable {
    def run() {

      new JFrame {
        val f = this
        setJMenuBar(new JMenuBar {
          add(new JMenu("File") {
            add(new JMenuItem("Load...") {
              addActionListener(new ActionListener {
                def actionPerformed(e: ActionEvent) {
                  val chooser = new JFileChooser(lastUsedFile) {
                    setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
                  }
                  val result = chooser.showOpenDialog(f)
                  result match {
                    case JFileChooser.APPROVE_OPTION => {
                      val canvas = new StudentActivityCanvas(chooser.getSelectedFile.getAbsolutePath)
                      setContentPane(canvas)
                      SwingUtilities.invokeLater(new Runnable {
                        def run() {

                          f.repaint()
                          canvas.paintImmediately(0, 0, canvas.getWidth, canvas.getHeight)
                          f.setBounds(f.getX, f.getY, f.getWidth - 1, f.getHeight)
                          f.setBounds(f.getX, f.getY, f.getWidth + 1, f.getHeight)
                        }
                      })

                      //Save user selection
                      val d = new PhetPersistenceDir()
                      d.mkdirs()
                      val file = new File(d, "simsharing-analysis.properties")
                      val properties = new Properties()
                      properties.put("file", chooser.getSelectedFile.getAbsolutePath)
                      properties.store(new FileOutputStream(file), "Sim Sharing Analysis properties, Stored by PlotStudentActivity")

                      lastUsedFile = chooser.getSelectedFile.getAbsolutePath
                    }
                    case _ => {}
                  }
                }
              })
            })
            addSeparator()
            add(new JMenuItem("Exit") {
              addActionListener(new ActionListener {
                def actionPerformed(e: ActionEvent) {
                  System.exit(0)
                }
              })
            })
          })
        })

        val properties = new Properties {
          val file = new File(new PhetPersistenceDir(), "simsharing-analysis.properties")
          if ( file.exists() ) {
            load(new FileInputStream(file))
          }
        }
        if ( properties.containsKey("file") ) {
          val file = properties.get("file").toString
          setContentPane(new StudentActivityCanvas(file))
        }

        setSize(1024, 768)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
      }.setVisible(true)
    }
  })

  def toX(dt: Long) = 200.0 + dt.toDouble / 1000.0 / 60.0 * 2.0 * 10.0

  def toDeltaX(dt: Long) = toX(dt) - toX(0)
}