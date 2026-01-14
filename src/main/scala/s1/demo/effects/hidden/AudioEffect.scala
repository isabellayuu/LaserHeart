package s1.demo.effects.hidden

import s1.demo.Effect

import scala.math.*
import scala.util.Random

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.Includes._
import scalafx.scene.media.MediaMarkerEvent
import Util.Vector3D
import scala.collection.mutable.Buffer
import scalafx.util.Duration

/**
 * [[AudioEffect]] utilizes audio markers and audio spectrum data.
 * 
 * To see this effect in the GUI, change the packaging (first line of this file) to [[s1.demo.effects]]
 * and move this file to the effects folder (one level above the current residence)
 */
class AudioEffect extends Effect(600, 600, "AudioEffect"):

    /**
      * force the effect to use this particular music even if [[MainApp.defaultMusic]] is different
      */ 
    override def musicPath: String = "sound/xtrium.mp3"

    var clock = 0

    var ovalColor = Color.DarkGoldenrod

    var bandIndex = 0
    var threshold = 13f
    var currently_over_threshold = false

    var allBands = Vector.fill(128)(0f)

    var ballDirection = Vector3D(0,0,0)
    var ballPosition = Vector3D(200,200,0)
    val moveSpeed = 0.7

    def changeAudioBandAndThreshold(band: Int, thres: Float) =
        bandIndex = band
        threshold = thres
    end changeAudioBandAndThreshold

    var check = false
    def doSomething() =
        if !check then
            val newXdir = Random.between(-1.0, 1.0)
            val newYdir = Random.between(-1.0, 1.0)


            val newDir = Vector3D(newXdir, newYdir, 0).normalize
            ballDirection = newDir
            check = true
        else
            ballDirection = ballDirection.reverseX
            ballDirection = ballDirection.reverseY

    end doSomething

    val timestampLabels: Buffer[(String, Int, Int)] = Buffer()

    def addTimestampLabel(timestamp: Duration) = 
        val asstring = timestamp.toSeconds().toString().take(4)
        val xPos = Random.between(200, 400)
        val yPos = Random.between(200, 400)
        timestampLabels += ((asstring, xPos, yPos))

    end addTimestampLabel


    /**
      * In this function, we specify functionality that runs when the Effect become active.
      * In particular, we add markers to the audio (i.e. timestamps for when something should happen)
      */
    override def onStart(): Unit = 

        audioPlayer.foreach( (ap) => 

            /**
              * Here we add markers on a Map.
              * Note that we do not have to add all markers in a single Map.
              */
            ap.addMarkers(Map(
                "SOMETHING" -> 1.s,
                "ANOTHER THING" -> 4050.ms)
            )
            ap.addMarkers(Map(
                "FINAL THING" -> 10.s,
                "tagmiss_example" -> 7000.ms)
            )

            /**
              * Here we define a function for handling the marker events,
              * i.e., what to do when each marked timestamp is reached.
              */
            ap.defineMarkerFunction( 
                (event: MediaMarkerEvent) => 
                    val tag = event.marker._1
                    val timestamp = event.marker._2

                    tag match
                        case "SOMETHING" => 
                            ovalColor = Color.RosyBrown
                            addTimestampLabel(timestamp)
                        case "ANOTHER THING" => 
                            ovalColor = Color.ForestGreen
                            addTimestampLabel(timestamp)
                        case "FINAL THING" => 
                            ovalColor = Color.FireBrick
                            addTimestampLabel(timestamp)
                            //ap.seek(0.s) // replay audio from beginning
                        case _ => println("could not match tag " + tag)
                    end match
            )
        )
    end onStart

    /**
     * Here we specify what to draw on the canvas after each tick().
     */
    def drawEffect(g: GraphicsContext): Unit = 

        g.setFill(Color.White)
        g.fillRect(0,0,width,height)

        g.setFill(ovalColor)
        g.fillOval(ballPosition.x,ballPosition.y,200,200)

        g.setStroke(Color.Black)
        g.strokeText("time: " + (if audioPlayer.isDefined then audioPlayer.get.timestamp.toSeconds() else "no audio"), 50, 50)

        timestampLabels.foreach(tuple => g.strokeText(tuple._1, tuple._2, tuple._3))

    end drawEffect

    /**
     * Here we modify the state of the effect.
     * This function is called every [[MainApp.delay]] milliseconds when this effect is active
     * (active meaning that it was chosen from the menu and drawn on the GUI)
     */
    def tick(): Unit = 

        clock += 1

        if audioPlayer.isDefined then
            allBands = audioPlayer.get.getAudioAmplitudes
        
        if allBands(bandIndex) > threshold then
            if !currently_over_threshold then
                currently_over_threshold = true
                doSomething()
            //else
                // we are already over the threshold, so do nothing
                // (we only want to do something when the threshold is surpassed)

        if (allBands(bandIndex) < threshold-1) then
            currently_over_threshold = false
        
        ballPosition = ballPosition + ballDirection*moveSpeed

    end tick

    /**
    * Here we specify when this effect should end in the demo (and switch to the next effect)
    *
    * @return true when the effect has ended, and false if it has not yet ended
    */
    def next = false



    // do nothing with the mouse inputs
    override def mouseAt(x: Int, y: Int): Unit = 
        {}
    end mouseAt

    override def mousePress(x: Int, y: Int): Unit = 
        {}
    end mousePress

    override def mouseRelease(x: Int, y: Int): Unit = 
        {}
    end mouseRelease

end AudioEffect