package s1.gui

import s1.demo.*
import s1.gui.GUI_constants as GUI

import scala.collection.mutable.Buffer
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{VBox, HBox, Pane}
import scalafx.scene.paint.Color
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, MenuItem, MenuButton, ButtonBar, CheckBox, TextField}
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.Background
import scalafx.scene.control.Label
import scalafx.beans.property.StringProperty
import scalafx.beans.value.ObservableValue
import s1.demo.MainApp.audioPlayer


/** STUDENTS DON'T HAVE TO TOUCH THIS CLASS OR UNDERSTAND IT
 *
 * [[DemoGUI]] is the primary stage for the [[JFXApp3]] used.
 * This object collects the GUI components together.
 * Most GUI components are defined in this file. The [[DemoArea]] 
 * (a Canvas where the effects are drawn on) is defined in its own file.
 *
 * @param app the [[JFXApp3]] where this [[DemoGUI]] is the primary stage
 * @param effects all the effects to be showcased in the application
 * @param demo all the effects of the demo
 * @param delay the delay between drawn frames of an effect
 */

object DemoGUI extends JFXApp3.PrimaryStage:

  // Get some useful references from MainApp
  val app: JFXApp3 = MainApp
  val effects = MainApp.allEffects
  val demo = MainApp.demo
  val delay = MainApp.delay

  var currentDemo: Option[DemoArea] = None
  var playingMusic = false
  var playingDemo = false

  /**
    * Set up the scene and its layout here.
    * We aim for something like this:

      ======================================================
      |  change effect    |  checkboxes  | restart effect  |
      |  (drop down menu) |  for music   |  (button)       |
      ======================================================
      |                                                    |
      |                                                    |
      |                                                    |
      |                                                    |
      |                                                    |
      |                                                    |
      |                    effect plays here               |
      |                                                    |
      |                                                    |
      |                                                    |
      |                                                    |
      |                                                    |
      |                                                    |
      ======================================================
    */
  title = GUI.title

  // set the root scene for this stage
  val rootScene = new Scene:
    fill = Color.Black

  scene = rootScene

  // ----- CONTAINER FOR THE MENU BUTTONS AND EFFECT CANVAS ----- //
  val rootContainer = new VBox:
    padding = GUI.rootContainerPadding
    spacing = GUI.rootContainerSpacing
  
  // ----- TOP ROW FOR THE BUTTONS ----- //
  val menuBar = new ButtonBar:
    background = GUI.menuBarBackground
    minWidth = GUI.minimumWidth
    padding = GUI.menuBarPadding

  // ----- BUTTON FOR RESTARTING THE EFFECT ----- //
  val restartButton = new Button:
    text = GUI.restartButtonText
    onAction = (event) => {
      currentDemo match
        case Some(d) => 
          playingMusic = musicCheckBox.selected.value
          currentDemo = resetEffect(d.effects)
        case None => 
    }
  
  // ----- BUTTON FOR STARTING THE MENU (should be located in the drop down menu) ----- //
  val startDemoButton = new MenuItem:
    text = GUI.startDemoButtonText
    onAction = (event) => 
      restartButton.text = GUI.restartDemoText
      playingDemo = true
      playingMusic = musicCheckBox.selected.value
      currentDemo = resetEffect(demo)
  
  // ----- CHECKBOXES FOR PLAYING MUSIC ----- //
  val musicCheckBox = new CheckBox(GUI.musicCheckBoxText):
    selected = true
  
  // ----- CONTAINER FOR CHECKBOXES ----- //
  val checkBoxes = new VBox:
    children = Vector(musicCheckBox)
    spacing = GUI.checkBoxesSpacing
  
  // ----- THE DROP DOWN MENU FOR THE EFFECTS ----- //
  val changeEffectMenu = new MenuButton:
    text = GUI.changeEffectDropdownMenuText
    items = for (effect <- effects)
            yield new MenuItem:
              text = effect.name
              onAction = (event) => 
                restartButton.text = GUI.restartButtonText
                playingDemo = false
                playingMusic = musicCheckBox.selected.value
                currentDemo = resetEffect(Buffer(effect))
    items.add(startDemoButton)
  
  // add first the container for all the GUI components
  rootScene.root = rootContainer

  // add the top row (control area) to the GUI
  rootContainer.children += menuBar

  // add the buttons to the top row container
  menuBar.buttons = Vector(changeEffectMenu, 
                          checkBoxes,
                          restartButton)

  // create a container for the effects, i.e., container for the canvas that is drawn on
  // (the canvas, DemoArea, is always recreated whenever an effect is reset)
  val demoPane = Pane()
  rootContainer.children += demoPane


  // used merely for the Waveform-tool
  val waveformBand: StringProperty = StringProperty(GUI.WF_initBandIndex)
  val waveformThres: StringProperty = StringProperty(GUI.WF_initThres)
  val waveformStarttime: StringProperty = StringProperty(GUI.WF_initStartingTimestamp)
    


  /**
    * Reset the GUI for a new effect. The given effects Buffer should
    * contain only a single effect, if they should not be chained
    * 
    * If the effects Buffer contains multiple effects, this function
    * do not have to be called each time the effect switches. This functionality is handled in [[DemoArea]].
    *
    * @param effects the effects to be played on the GUI.
    * @return A new [[DemoArea]] canvas where the effects of the Buffer are drawn.
    */
  def resetEffect(effects: Buffer[Effect]): Option[DemoArea] =
    // Check if there is a demo playing at the moment, stop it if true
    currentDemo match
      case Some(demo) => demo.stopAnimating()
      case None =>

    // Create a new DemoArea where the effects are drawn
    val newEffects = effects.map(_.newInstance)
    val newDemo = DemoArea(newEffects, playingMusic)
    val w = newDemo.effects.head.width
    val h = newDemo.effects.head.height
    width = math.max(w, GUI.minimumWidth)
    height = h + menuBar.height.toInt

    // add auxiliary control buttons for the Waveform-tool
    if newEffects.head.name == GUI.WF_waveformEffectname then
      if menuBar.buttons.size == 3 then
        val a = new TextField {text = waveformBand.value; maxWidth = 80}
        val b = new TextField {text = waveformThres.value; maxWidth = 80}
        val c = new TextField {text = waveformStarttime.value}
        val d = new TextField {text = MainApp.audioPlayer.numOfBands.toString; maxWidth = 100}
        menuBar.buttons.prependAll(Vector(
          new VBox:
            children += (new Label(GUI.WF_startTimestampText) {minWidth = 90}, c)
          ,
          HBox(10, VBox(Label(GUI.WF_bandIndexLabel), a), VBox(Label(GUI.WF_thresLabel), b)),
          VBox(Label(GUI.WF_nofBandsLabel), d)
        ))
        waveformBand <== a.text
        waveformThres <== b.text
        waveformStarttime <== c.text
        d.text.onChange { (source, oldValue, newValue) => 
          MainApp.audioPlayer.numOfBands = 
            try
              math.max(2,newValue.toInt)
            catch
              case e: Exception => 
                GUI.WF_defaultNumOfBands
        }
    else
      if menuBar.buttons.size > 3 then menuBar.buttons.remove(0, 3)

    demoPane.children.clear()
    demoPane.children += newDemo

    newDemo.startAnimating()

    Some(newDemo)
  end resetEffect



  /**
    * update the GUI bounds based on the size of the DemoArea
    *
    * @param w the width of the current effect to be played
    * @param h the height of the current effect to be played
    */
  def updateGUIBounds(w: Int, h: Int) =
    this.width = math.max(w, GUI.minimumWidth)
    this.height = h + menuBar.height.toInt
  end updateGUIBounds

end DemoGUI