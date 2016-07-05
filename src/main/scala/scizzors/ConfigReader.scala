package scizzors

import java.io.File
import java.net.URL

import com.typesafe.config._
import com.typesafe.config.impl.ConfigImpl
import scala.collection.JavaConverters._
import scala.util.Try

import ammonite.ops.Path

/** Cloned from https://github.com/skinny-framework/skinny-framework/blob/master/common/src/main/scala/skinny/util/TypesafeConfigReader.scala
  */
trait ConfigReader {

  /**
   * Loads a configuration file.
   *
   * @param path path to configuration file
   * @return config
   */
  def load(path: Path): Config = load(path.toNIO.toFile)

  /**
   * Loads a configuration file.
   *
   * @param file file
   * @return config
   */
  def load(file: File): Config = ConfigFactory.parseFile(file)

  /**
   * Loads a configuration file.
   *
   * @param resource file resource
   * @return config
   */
  def load(resource: String): Config = ConfigFactory.load(getClass.getClassLoader, resource)

  /**
   * Loads config values without system properties.
   *
   * @param resource file resource
   * @return config
   */
  def loadWithoutSystemProperties(resource: String): Config = {
    val loader: ClassLoader = getClass.getClassLoader
    val parseOptions: ConfigParseOptions = ConfigParseOptions.defaults.setClassLoader(loader)
    val config: Config = ConfigImpl.parseResourcesAnySyntax(resource, parseOptions).toConfig
    config.resolve(ConfigResolveOptions.defaults)
  }

  /**
   * Loads a configuration file as Map object.
   *
   * @param resource file resource
   * @return Map object
   */
  def loadAsMap(resource: String): Map[String, String] = fromConfigToMap(load(resource))

  /**
   * Loads config values without system properties.
   *
   * @param resource file resource
   * @return Map object
   */
  def loadAsMapWithoutSystemProperties(resource: String): Map[String, String] = {
    fromConfigToMap(loadWithoutSystemProperties(resource))
  }

  /**
   * Loads a Map object from Typesafe-config object.
   *
   * @param config config
   * @return Map object
   */
  def fromConfigToMap(config: Config): Map[String, String] = {
    def extract(map: java.util.Map[String, Any]): Map[String, String] = {
      map.asScala.flatMap {
        case (parentKey, value: java.util.Map[_, _]) =>
          extract(value.asInstanceOf[java.util.Map[String, Any]]).map { case (k, v) => s"${parentKey}.${k}" -> v }
        case (key, value) => Map(key -> value)
      }
    }.map { case (k, v) => k -> v.toString }.toMap

    config.root().keySet().asScala.flatMap { parentKey =>
      config.root().unwrapped().get(parentKey) match {
        case map: java.util.Map[_, _] =>
          extract(config.root().unwrapped().asInstanceOf[java.util.Map[String, Any]])
        case value =>
          Map(parentKey -> value)
      }
    }.map { case (k, v) => k -> v.toString }.toMap
  }

}