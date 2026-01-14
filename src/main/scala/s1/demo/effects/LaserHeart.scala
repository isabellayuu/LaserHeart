package s1.demo.effects

import s1.demo.*

import scala.math.*
import scala.util.Random

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.Includes._
import Util.Vector3D
import scalafx.scene.media.MediaMarkerEvent
import scalafx.util.Duration


/**
 * Effect that has not been implemented yet. Copy this for an effective start on making your own effect.
 */
class LaserHeart extends Effect(width = 600, height = 600, name = "Laser Heart"):
  private val size = 16.0
  private val random = new Random()

  private val Xcenter = width / 2.0
  private val Ycenter = height / 2.0
  private var clock = 0.0

  private var endPoints: Vector[(Double, Double)] = Vector.empty

  /**
   *
   * @param x x-koordinaatti
   * @return x-koordinaatin eli luvun, joka kuvaa stdämen kohtaa vaaksuunnassa
   */

  private def heartXCoords(x: Double): Double =
    14.5 * math.pow(math.sin(x), 3)

  /**
   *
   * @param y y-koordinaatti
   * @return y-koordinaatin eli luvun, joka kuvaa sydämen kohtaa pystysuunnassa
   */

  private def heartYCoords(y: Double): Double =
    12.5 * math.cos(y) - 4.5 * math.cos(2 * y) - 2.0 * math.cos(3 * y) - math.cos(4 * y)

  /**
   *
   * @param GraphicsContext tausta, johon efekti piirretään
   * @param line kasvatetaan viivojen määrää
   * piirtää sydämen taustaan
   */

  def drawEffect(g: GraphicsContext): Unit =
    g.setFill(Color.Black)
    g.fillRect(0, 0, width, height)

    g.setStroke(Color.MediumVioletRed)
    g.setLineWidth(1.0) //viivan paksuus

    for ((x, y) <- endPoints) do // kulkee läpi pisteet
      var line = 0 // viivojen määrä aluksi
      while line < 6 do // toistaa saman viivan kuusi kertaan
        g.strokeLine(Xcenter, Ycenter, x, y) // piirtää viivan keskeltä sydämen reunaan
        line += 1 // kasvattaa viivojen määrää kunnes sen määrä on 6.
  end drawEffect

  /**
   * @param randomAngle arpoo uuden viivan
   * @param x laskee uuden x koordinaatin
   * @param y laskee uuden y koordinaatin
   * Lisää listaan uudet päätepistettä ja päivittää samalla listaa
   *
   */

  def tick(): Unit =
    clock += 1.5 //kasvattaa clock 1,5

    val randomAngle = random.nextDouble() * 2.0 * math.Pi  //valitsee satunnaisen kohdan sydämen reunalta
    val x = Xcenter + heartXCoords(randomAngle) * size  //laskee x koordinaatin
    val y = Ycenter - heartYCoords(randomAngle) * size  // laskee y koordinaatin

    endPoints = endPoints :+ (x, y)  // lisää x ja y päätepisteiden listaan
  end tick

    /**
    * Siirtyy seuraavaan efektiin on clock on suurempi kuin 775
    *
    * @return true, kun efekti on loppunut ja false jos efekti ei ole loppunut
    */
  def next = clock > 775



end LaserHeart
