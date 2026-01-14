package s1.audio

import collection.mutable.Buffer
import scalafx.scene.media.Media
import scalafx.scene.media.MediaErrorEvent
import scalafx.scene.media.MediaMarkerEvent
import scalafx.scene.media.MediaView
import scalafx.scene.media.MediaPlayer
import scalafx.scene.media.AudioClip
import javafx.util.Duration
import scalafx.Includes.*

import java.io.File
import java.net.URLEncoder

/** Although encouraged, studying this class is optional!
  * If you want to touch the music (pause, alter speed, etc.), please study the methods in this class!
  * 
  * 
  * This class is used to play audio over the visual effects.
  * The constructor parameter defines the demo music. 
  * Moreover, auxiliary audio clips can be played over this music.
  *
  * @param path the path to the demo music. E.g. "/sound/entropy.wav"
  */
class AudioPlayer(var musicPath: String):
    
    private var _volume = 100.0
    def volume = _volume

    private var _muted = false
    def muted = _muted

    private var _paused = false
    def paused = _paused

    private var _rate = 1.0
    def rate = _rate

    private var _startingTimestamp = 0.s
    def startingTimestamp: scalafx.util.Duration = _startingTimestamp

    /**
      * Play the music that is found from [[musicPath]].
      * Note that you can change settings before playing!
      * 
      * These settings include:
      *      - volume
      *      - rate
      *      - mute
      *      - starting timestamp
      *
      * Any markers must be placed before playing! (You can do this in your effect's onStart method.)
      * 
      * @param loop Boolean corresponding to whether the music should be played on loop
      */
    def play(loop: Boolean = true): Unit = 
        val mediaFilePath = (pathPrefix + musicPath).replace(" ", "%20")
        println("loading media from file: " + mediaFilePath)
        val media = new Media(mediaFilePath);
        println("load successful")
        val mediaPlayer = MediaPlayer(media)
        currentMediaPlayer = Some(mediaPlayer)
        mediaPlayer.audioSpectrumNumBands = numOfBands

        val audioSpectrum = AudioSpectrum(this)
        mediaPlayer.audioSpectrumListener = audioSpectrum
        currentAudioSpectrum = Some(audioSpectrum)

        mediaPlayer.volume = _volume/100.0
        mediaPlayer.mute = _muted
        mediaPlayer.setRate(_rate)
        mediaPlayer.startTime = startingTimestamp

        // note: timestamps reset to zero every loop
        // (i.e., timestamps never exceed the length of the song)
        if loop then mediaPlayer.cycleCount = MediaPlayer.Indefinite

        // add the markers to this media
        mediaMarkersToBeAdded.foreach(media.markers ++= _)
        mediaPlayer.onMarker = markerFunctionToBeAdded

        mediaPlayer.play()
    end play

    def stop(): Unit =
        if currentMediaPlayer.isDefined then
            currentMediaPlayer.get.stop()
            currentMediaPlayer.get.dispose()
            currentMediaPlayer = None
    end stop

    /**
      * Set the rate on taking samples. This essentially alters the speed of music.
      * Note that timestamps work well with this, and e.g. with 
      * a relative rate of 2.0, a timestamp of 10.s is reached in 5 seconds.
      * 
      * The rate can be altered while audio is already playing.
      *
      * @param rate the relative rate of the music
      */
    def setRelativeRate(rate: Double): Unit = 
        if currentMediaPlayer.isDefined then
            currentMediaPlayer.get.setRate(rate)
        _rate = rate
    end setRelativeRate
    
    /**
      * Toggle pause for the music.
      * 
      * Pauses the music if music is playing,
      * unpauses the music if music is paused.
      */
    def togglePause(): Unit = 
        if currentMediaPlayer.isDefined then
            if paused then
                currentMediaPlayer.get.play()
                _paused = false
            else
                currentMediaPlayer.get.pause()
                _paused = true
    end togglePause

    /**
      * Adjusts the mute setting.
      *
      * @param mute Boolean corresponding to whether the music should be muted
      */
    def muted_=(mute: Boolean): Unit = 
        if currentMediaPlayer.isDefined then
            currentMediaPlayer.get.setMute(mute)
        _muted = mute
    end muted_=
    
    /**
      * Adjusts the volume setting.
      *
      * @param vol Volume level in the range [0, 100]
      */
    def volume_=(vol: Double): Unit = 
        val vol_clamped = math.max(0,math.min(100.0, vol))
        if currentMediaPlayer.isDefined then
            currentMediaPlayer.get.volume = vol_clamped/100.0
        _volume = vol_clamped
    end volume_=
    
    /**
      * Get the current timestamp of the audio.
      *
      * @return a [[Duration]] that represents the current timestamp of the audio
      */
    def timestamp: Duration = 
        if currentMediaPlayer.isDefined then
            currentMediaPlayer.get.currentTime.value
        else
            0.ms
    end timestamp

    /**
      * Adjusts the starting timestamp setting.
      * This has no effect when audio is already playing (unless the playing is restarted).
      *
      * @param timestamp a timestamp where playing should start
      */
    def startingTimestamp_=(timestamp: Duration): Unit = 
        _startingTimestamp = timestamp
    end startingTimestamp_=
    
    /**
      * Restart playing the demo music from the given timestamp.
      * E.g. audioPlayer.seek(3.s) or audioPlayer.seek(3000.ms).
      * To use e.g. 3.s or 3000.ms, import scalafx.Includes._
      *
      * @param time timestamp of the new playback time
      */
    def seek(time: Duration): Unit =
        if currentMediaPlayer.isDefined then 
            currentMediaPlayer.get.seek(time)
    end seek

    /**
      * Used to play an audio clip. 
      * You can play audio clips over the demo music 
      * by just calling this method.
      * 
      * The general settings do not affect audio clips.
      * You must give preferred settings as the parameters when calling this method.
      *
      * @param path the path to the audio clip sound file (e.g. "sound/audio_clip.mp3")
      * @param volume the relative volume of the clip in the range [0, 100]
      * @param pan the panning of the clip in the range [-1, 1], where -1 is entirely left and 1 is entirely right
      */
    def playClip(path: String, volume: Double = 100.0, pan: Double = 0.0): Unit = 
        val audioClip = AudioClip(pathPrefix + path)
        audioClip.volume = volume / 100.0
        audioClip.pan = pan
        audioClip.play()
    end playClip

    



    // ---- totally optional studying material from here on ---- //





    private val mediaMarkersToBeAdded: Buffer[Map[String, Duration]] = Buffer()
    private var markerFunctionToBeAdded: MediaMarkerEvent => Unit = 
        (event: MediaMarkerEvent) => {}

    private var currentAudioSpectrum: Option[AudioSpectrum] = None

    // the frequency range of the music is divided into numOfBands sections.
    // note: this doesn't affect the quality of the music, but the quality of the spectrum data!
    private var _numOfBands = 128 //You are free to change this! Minimum is 2
    def numOfBands = _numOfBands
    def numOfBands_=(bands: Int) = _numOfBands = bands


    /**
      * Get a Vector of the most recent spectrum magnitudes, i.e., amplitudes for each band.
      * The value to be returned updates every 0.1s by default.
      * Calling this method multiple times within this interval should return the same values.
      * Note that changing the volume of the audio player affects these amplitudes.
      *
      * @return a Vector of [[numOfBands]] Floats corresponding to the current magnitudes of each audio band.
      */
    def getAudioAmplitudes: Vector[Float] = 
        if currentAudioSpectrum.isDefined then 
            val sd = currentAudioSpectrum.get.latestSpetrumData
            if sd.isDefined then 
                sd.get.magnitudes.toVector
            else
                Vector.fill(numOfBands)(0f)
        else 
            Vector.fill(numOfBands)(0f)
    end getAudioAmplitudes

    /**
      * Get the average amplitude over a subsection of all the bands.
      * This just computed the average value of the return value of 
      * getAudioAmpltiudes over specific bands.
      * 
      * @param startBandIndex index of the first band (inclusive) in the average
      * @param endBandIndex index of the last band (exclusive!) in this chosen range
      * 
      * @return average amplitude of some bands.
      */
    def getAverageAmplitudeBetweenBands(startBandIndex: Int, endBandIndex: Int): Float =
        require(startBandIndex < endBandIndex)
        require(endBandIndex <= numOfBands)
        val amplitudes = this.getAudioAmplitudes
        val size = endBandIndex - startBandIndex
        amplitudes.drop(startBandIndex).take(size).sum / size
    end getAverageAmplitudeBetweenBands

    /**
      * Add markers to the audio. 
      * A mark is a ([[String]], [[Duration]]) tuple.
      * When the timestamp of the audio reaches the [[Duration]] of a mark,
      * an event is triggered, with the mark (the tuple) being given to a handler.
      * 
      * Define your own handler with the [[defineMarkerFunction]] method.
      *
      * @param markers a [[Map]] of ([[String]], [[Duration]]) pairs.
      */
    def addMarkers(markers: Map[String, Duration]): Unit =
        mediaMarkersToBeAdded += markers
        if currentMediaPlayer.isDefined then 
            currentMediaPlayer.get.media.markers ++= markers
    end addMarkers

    /**
      * Clear the AudioPlayer from the added markers 
      * You must clear the markers with this method if you want 
      * new audio to be played without the markers of the previous audio.
      */
    def clearMarkers(): Unit = 
        mediaMarkersToBeAdded.clear()
        if currentMediaPlayer.isDefined then
            currentMediaPlayer.get.media.markers.clear()
    end clearMarkers

    /**
      * Define the function that handles the marker events.
      * Look into the [[AudioEffect]] example on how to define such function.
      *
      * @param func event handler for the marker events
      */
    def defineMarkerFunction(func: MediaMarkerEvent => Unit): Unit =
        markerFunctionToBeAdded = func
        if currentMediaPlayer.isDefined then
            currentMediaPlayer.get.onMarker = func
    end defineMarkerFunction
    



    // ---- variables and methods that are probably not interesting for the average student ---- //


    private var currentMediaPlayer: Option[MediaPlayer] = None
    def mediaPlayer = currentMediaPlayer

    private val CWD = System.getProperty("user.dir").replace("\\","/")
    private val pathPrefix = "file:///" + CWD + "/"

end AudioPlayer