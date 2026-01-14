package s1.demo
import s1.demo.Effect

import scala.collection.mutable.Buffer

import java.io.File


/** STUDENTS DON'T HAVE TO TOUCH THIS CLASS OR UNDERSTAND IT
  * 
  * [[EffectLoader]] loads all the effects from the effects folder, and 
  * creates new instances of these effect classes for the [[MainApp]] object
  * 
  */
object EffectLoader:

    private val effectPackage = "s1.demo.effects"
    private val effectDir = "./src/main/scala/s1/demo/effects"

    def loadEffects(): Vector[Effect] = 

        val dir = File(effectDir)
        val allFiles = dir.listFiles().filterNot(_.isDirectory())

        // we filter out Vector3D and NotImplementedEffect files
        val filteredFiles = Vector("Vector3D.scala")
        val effectFiles = allFiles.filterNot(file => filteredFiles.contains(file.getName()))

        val buf = Buffer[Effect]()
        
        effectFiles.foreach( file => 
            try 
                val class_ = Class.forName(effectPackage + "." + file.getName().takeWhile(_ != '.'))
                println("loaded " + class_.getName())
                buf.append(class_.newInstance().asInstanceOf[Effect])
            catch
                case e: Exception =>
                    // an error is thrown in the above functionality for a file that is 
                    // in the effects folder but not in the package s1.demo.effects (e.g. Vector3D is not in s1.demo.effects)
                    // --> we just skip these

                    // another error is thrown for a file that contains a class with a different name than that of the file itself
                    // e.g. NotImplementedEffect.scala would not load, since the effect's class is actually named 'NotImplemented'
                    // --> MAKE SURE TO NAME YOUR FILE IDENTICAL TO THE CLASS
                    
                    println("could not load a class: " + e.getMessage)
        )

        buf.toVector

    end loadEffects

end EffectLoader
