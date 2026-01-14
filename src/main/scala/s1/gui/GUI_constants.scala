package s1.gui

import scalafx.geometry.Insets
import scalafx.scene.layout.Background
import scalafx.scene.paint.Color


/** STUDENTS DON'T HAVE TO TOUCH THIS CLASS OR UNDERSTAND IT
 * 
 *  This object holds all hard-coded values for the GUI.
 * 
 */

object GUI_constants:


    //---- general values ----//

    val title = "Oldskool Demo"

    val minimumWidth = 450

    val rootContainerPadding = Insets(0, 0, 0, 0)
    val rootContainerSpacing = 0

    val menuBarPadding = Insets(top = 5, right = 10, bottom = 5, left = 10)
    val menuBarBackground = Background.fill(Color.White)

    val restartButtonText = "Restart Effect"
    val restartDemoText = "Restart Demo"
    val startDemoButtonText = "Start Demo"

    val changeEffectDropdownMenuText = "Change Effect"

    val musicCheckBoxText = "toggle music"
    val checkBoxesSpacing = 5


    //---- values specific to the Waveform tool ----//

    val WF_waveformEffectname = "Waveform"
    val WF_bandIndexLabel = "Band Index"
    val WF_thresLabel = "Threshold"
    val WF_startTimestampText = "Starting timestamp (sec)"
    val WF_nofBandsLabel = "Bands"

    val WF_initBandIndex = "3"
    val WF_initThres = "30"
    val WF_initStartingTimestamp = "0.0"
    val WF_defaultNumOfBands = 128



end GUI_constants
