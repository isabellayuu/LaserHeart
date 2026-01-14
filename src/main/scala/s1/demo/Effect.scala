package s1.demo

import scalafx.scene.canvas.GraphicsContext
import s1.audio.AudioPlayer

/**
 * This is the base class for every demo effect you implement.
 * 
 * @constructor Creates a new demo effect. Effects are shown in the [[DemoArea]].
 * @param width width of the effect in pixels. Note that the GUI has a minimum width of 450
 * @param height height of the effect in pixels
 * @param name name of the effect
 */
abstract class Effect(val width: Int, val height: Int, val name: String):

  /**
   * Changes the state of the effect. Similar to the tick method in Flappy
   */
  def tick(): Unit

  /**
    * Draws the current frame of the effect. This is called after every tick.
    *
    * @param g GraphicsContext of the Canvas where the effect is drawn on
    */
  def drawEffect(g: GraphicsContext): Unit
  
  /**
   * The effects can be manipulated with mouse if desired. This method is called whenever
   * mouse is hovered over the effect.
   *
   * @param x the x-location of the mouse
   * @param y the y-location of the mouse
   */
  def mouseAt(x: Int, y: Int) = {}

  /**
   * The effects can be manipulated with mouse if desired. This method is called whenever
   * mouse is pressed over the effect.
   *
   * @param x the x-location of the mouse
   * @param y the y-location of the mouse
   */
  def mousePress(x: Int, y: Int) = {}

  /**
   * The effects can be manipulated with mouse if desired. This method is called whenever
   * mouse is released over the effect.
   *
   * @param x the x-location of the mouse
   * @param y the y-location of the mouse
   */
  def mouseRelease(x: Int, y: Int) = {}

  /**
    * Reference to the active AudioPlayer.
    * Use this to get access to audio controls and live audio spectrum data.
    * This is given the actual AudioPlayer when the effect becomes active.
    * 
    * Thus, this is safe to .get inside every method in Effect.
    * However, it is still recommended to use e.g. audioPlayer.foreach to call [[AudioPlayer]]'s methods :)
    */
  var audioPlayer: Option[AudioPlayer] = None
  
  /**
   * This method tells the Demo engine when it is time to switch to the next effect
   * If this is the only effect, the method always returns '''false'''
   *
   * @return true when this effect is over, false otherwise 
   */
  def next: Boolean

  /**
    * Override this method in your Effect if you want the particular effect to have some other
    * music than the music that you have given in [[MainApp]].
    * 
    * Does not reset the player in a Demo (chained effects), if 
    * this effect has the same musicPath as the effect before it.
    * I.e. the music does not start over when the effect changes.
    */
  def musicPath = MainApp.defaultMusic

  /**
    * Called just before the effect becomes active (is being drawn on the GUI)
    * 
    * Add audio markers and a corresponding marker function inside this, if wanted.
    */
  def onStart(): Unit = {}

  /**
    * Called just before the effect becomes inactive (some other effect becomes active)
    */
  def onEnd(): Unit = {}

  def newInstance: Effect = this.getClass.newInstance()

end Effect