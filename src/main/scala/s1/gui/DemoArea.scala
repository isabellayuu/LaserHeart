package s1.gui

import s1.demo.{MainApp, Effect}
import s1.audio.AudioPlayer

import scala.collection.mutable.{Buffer, Queue}
import scalafx.scene.canvas.Canvas
import scalafx.scene.input.MouseEvent
import scalafx.Includes._
import scalafx.animation.AnimationTimer


/** STUDENTS DON'T HAVE TO TOUCH THIS CLASS OR UNDERSTAND IT
 *
 * DemoArea hosts one [[Effect]] at a time.
 * The code required to run it is already in place in the
 * [[MainApp]] object.
 *
 */

class DemoArea(val effects: Buffer[Effect], val playMusic: Boolean) extends Canvas:
  demo =>

  var currentEffect = effects.head
  var nextEffects   = effects.tail

  width = currentEffect.width
  height = currentEffect.height

  var audioPlayer: Option[AudioPlayer] = None
  var previousEffectMusic = MainApp.defaultMusic

  var lastUpdateTime: Long = 0L
  var totalDuration: Long = 0L
  var frameCount: Long = 0L
  val frameTime: Long = math.max(1,MainApp.delay) * 1000000

  val timer = AnimationTimer(
    (timestamp: Long) => 
      if lastUpdateTime > 0 then
        val elapsedTime = timestamp - lastUpdateTime
        update(elapsedTime)
      lastUpdateTime = timestamp
  )

  def update(elapsedTime: Long): Unit =
    totalDuration += elapsedTime
    val newframeCount = totalDuration / frameTime

    if newframeCount > frameCount then
      // It theoretically may be that occasionally elapsedTime > 2*frameTime 
      // (i.e. newFrameCount is larger than frameCount by more than 1 frame).
      // To leviate the possible slight visual "slowdown", we call tick() multiple times
      val frameDiff = (newframeCount - frameCount).toInt
      frameCount = newframeCount

      for i <- 0 until frameDiff do
        currentEffect.tick()
      
      currentEffect.drawEffect(demo.graphicsContext2D)

      if currentEffect.next then
          if nextEffects.nonEmpty then
            currentEffect.audioPlayer = None
            val prevMusic = currentEffect.musicPath
    
            currentEffect = nextEffects.head
            val newMusic = currentEffect.musicPath

            nextEffects = nextEffects.tail

            if prevMusic != newMusic then 
              audioPlayer match
                case Some(i) => 
                  i.clearMarkers(); i.stop();
                  i.musicPath = newMusic
                  i.play()
                case None =>
            else
              // dont have to change anything if same music continues
              

            width = currentEffect.width
            height = currentEffect.height
            DemoGUI.updateGUIBounds(currentEffect.width, currentEffect.height)
  end update

  /**
   * Start the animation process and if playMusic is true, also start the audio player
   */
  def startAnimating() = 
    if playMusic then
        val player = MainApp.audioPlayer
        audioPlayer = Some(player)

        player.musicPath = currentEffect.musicPath
        player.play()


    currentEffect.audioPlayer = audioPlayer
    currentEffect.onStart()
    timer.start()
  end startAnimating

  def stopAnimating() =
    audioPlayer match
      case Some(i) => 
        i.clearMarkers()
        i.stop()

      case None =>

    timer.stop()
    currentEffect.onEnd()
    currentEffect.audioPlayer = None
  end stopAnimating

  demo.handleEvent(MouseEvent.Any) { 
    (me: MouseEvent) => 
      me.eventType match 
      case MouseEvent.MousePressed => 
        currentEffect.mousePress(me.x.toInt, me.y.toInt)
      case MouseEvent.MouseMoved =>
        currentEffect.mouseAt(me.x.toInt, me.y.toInt)
      case MouseEvent.MouseReleased =>
        currentEffect.mouseRelease(me.x.toInt, me.y.toInt)
      case _ =>   
  }

end DemoArea
