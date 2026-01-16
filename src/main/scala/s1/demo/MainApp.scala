package s1.demo

import s1.gui.*
import s1.demo.effects.*
import s1.audio.AudioPlayer
import s1.demo.Effect

import scala.collection.mutable.Buffer
import scalafx.application.JFXApp3
import scalafx.scene.layout.BorderPane
import scalafx.scene.control.MenuBar
import scalafx.scene.control.Menu
import scalafx.Includes._


/**
 * [[MainApp]] extends [[JFXApp3]] which makes it a runnable ScalaFX application.
 * 
 * The app draws and plays single effects taken from the effects folder.
 * 
 * Multiple effects can be chained together (each effect ending when its [[next]] method returns true)
 * in the [[demo]] Buffer. This demo can be played by selecting "Change Effect" and "Start Demo" in the GUI.
 * 
 * Music can be added by changing the path to the audio file in [[defaultMusic]].
 * If music is not required for your effects/demo, you can simply set the volume to 0 or 
 * check the checkboxes in the GUI (when application is running).
 *
 * The variable [[stage]] in the [[start]] method declares the primary stage needed for JFXApp3.
 * In order to build your own effects and demos, 
 * you don't have to touch this variable or any of the GUI components (in the gui directory).
 * 
 * ----------------------------------------------
 * 
 * To begin building your own effects, please take a look at the example effects and 
 * the abstract class [[Effect]] that you are to extend.
 * 
 * To add your own effect to the program:
 *    - Create a new Scala file in the effects folder
 *    - This file should only contain a Scala class that extends [[Effect]]
 *    - Make sure the file's name and the class' name match! (case sensitive!)
 *    - After implementing your own [[Effect]], run [[MainApp]] (the object in this file)
 *    - You should see your own effect in the dropdown menu "Change Effect" in the GUI
 * 
 * To create a demo of chained effects:
 *    - Instantiate effects to the [[demo]] Buffer
 *    - When running [[MainApp]], select "Change Effect" and "Start Demo" from the dropdown menu
 */

object MainApp extends JFXApp3:

  val defaultMusic = "sound/entropy.wav"
  val audioPlayer = AudioPlayer(defaultMusic)

  /**
    * Refreshes per minute. You may want to have it divisibile with the bpm of your music.
    * e.g. 480 is divisible with 60 and 120.
    *     As an example with refreshrate 2880 and 60 bpm, 
    *     if you want to call e.g. Hit() every beat, you would call Hit() every 48th tick. (2880 / 60 = 48)
    */
  val refreshRate = 2880

  // All effects from the effects folder. This variable decides which effects are available in the GUI.
  val allEffects: Vector[Effect] = EffectLoader.loadEffects()

  /**
    * This Buffer holds the Effects that are played when "Start Demo" is selected in the GUI.
    * Each effect is played until its [[next]] method returns true.
    * When an effect's [[next]] returns true, the following effect in the Buffer starts.
    */
  val demo: Buffer[Effect] =
      Buffer(LaserHeart())

  // the volume level for the music, can be changed while already playing music
  audioPlayer.volume = 100.0

  // essentially the speed for the music, can be changed while already playing music
  audioPlayer.setRelativeRate(1.0)

  // the delay between effect frames (in milliseconds)
  val delay = 1000*60/this.refreshRate

  def start(): Unit = 
    stage = DemoGUI
    

end MainApp
