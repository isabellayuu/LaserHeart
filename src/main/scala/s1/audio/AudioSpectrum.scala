package s1.audio

import javafx.scene.media.AudioSpectrumListener
import scalafx.scene.media.MediaPlayer
import collection.mutable.Buffer
import scala.collection.mutable.Queue


/** STUDENTS DON'T HAVE TO TOUCH THIS CLASS OR UNDERSTAND IT
  * 
  * A custom class that inherits the [[AudioSpectrumListener]] interface.
  * This class essentially listens for the audio spectrum data from the [[AudioPlayer]] 
  * and defines what to do with it.
  *
  * @param audioPlayer the [[AudioPlayer]] that should be listened to
  */
class AudioSpectrum(audioPlayer: AudioPlayer) extends AudioSpectrumListener:

    // holds the latest spectrum data (note: there is delay that is defined below)
    private var _latestSpetrumData: Option[SpectrumData] = None
    def latestSpetrumData = _latestSpetrumData

    // this is the delay between audio playing and updating spectrum data
    private val spectrumDelay = 2 // times sample interval, e.g., 2 * 0.1s = 0.2s overall delay 
    private val spectrumDataQueue: Queue[SpectrumData] = Queue.fill(spectrumDelay)(null)

    private val mediaPlayer: MediaPlayer = audioPlayer.mediaPlayer.get

    private val spectrumThreshold = mediaPlayer.audioSpectrumThreshold.toInt

    private val numOfBands = mediaPlayer.audioSpectrumNumBands.value
    private val ampBuffer = Buffer.fill(numOfBands)(0f)

    /**
      * This function specifies what to do with audio spectrum data that is received every 0.1s
      */
    def spectrumDataUpdate(
            timestamp: Double,
            duration: Double,
            magnitudes: Array[Float],
            phases: Array[Float]
            ): Unit = 

        // this is done merely for the smooth decreasing in the visual Waveform tool.
        // otherwise, the amplitudes (visually) can change really drastically in an instant and thus would be hard to analyze :)
        for (i <- 0 until numOfBands) do 
            val mag = (magnitudes(i) - spectrumThreshold) 
            if (ampBuffer(i) < mag) then 
                ampBuffer(i) = mag
            else
                ampBuffer(i) = math.max(0f, ampBuffer(i) - 2.0f)
        
        val data = SpectrumData(timestamp, duration, ampBuffer.toVector, phases.toVector)

        _latestSpetrumData = 
            val d = spectrumDataQueue.dequeue()
            if d == null then None else Some(d)
        
        spectrumDataQueue.enqueue(data)

    
    end spectrumDataUpdate



end AudioSpectrum

/**
  * This class represents an audio spectrum of the demo music
  *
  * @param timestamp the timestamp of the spectrum data (i.e., these magnitudes correspond to this timestamp of the demo music)
  * @param duration the sample interval (0.1s)
  * @param magnitudes the magnitude spectrum (magnitudes for each band), a Vector of size [[numOfBands]]
  * @param phases the phases for each band, a Vector of size [[numOfBands]]
  */
case class SpectrumData(
    timestamp: Double,
    duration: Double,
    magnitudes: Vector[Float],
    phases: Vector[Float]
)