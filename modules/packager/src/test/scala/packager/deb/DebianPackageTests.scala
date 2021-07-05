package packager.deb

import com.eed3si9n.expecty.Expecty.expect
import packager.PackageHelper
import packager.TestUtils.scalaPackagerJar
import packager.config.DebianSettings
import packager.config.BuildSettings.{Deb, PackageExtension}

import scala.util.Properties

class DebianPackageTests extends munit.FunSuite with PackageHelper {

  if (Properties.isLinux) {
    test("should create DEBIAN directory with launcher") {
      val dmgPackage = DebianPackage(launcherSettings, buildSettings)

      // create app directory
      dmgPackage.createDebianDir()

      val debianDirectoryPath = tmpDir / "debian"
      val expectedAppDirectoryPath = debianDirectoryPath / "DEBIAN"
      val expectedEchoLauncherPath =
        debianDirectoryPath / "usr" / "share" / "scala" / packageName
      expect(os.isDir(expectedAppDirectoryPath))
      expect(os.isFile(expectedEchoLauncherPath))
    }

    test("should generate dep package with launcher") {

      val depPackage = DebianPackage(launcherSettings, buildSettings)

      // create dmg package
      depPackage.build()

      expect(os.exists(outputPackagePath))

      // list files which will be installed
      val payloadFiles =
        os.proc("dpkg", "--contents", outputPackagePath).call().out.text().trim
      val expectedScriptPath = os.RelPath("usr") / "bin" / packageName
      val expectedEchoLauncherPath =
        os.RelPath("usr") / "share" / "scala" / packageName

      expect(payloadFiles contains s"./$expectedScriptPath")
      expect(payloadFiles contains s"./$expectedEchoLauncherPath")
    }

    test("should generate dep package with app jar") {

      val depPackage = DebianPackage(jarAppSettings, buildSettings)

      // create dmg package
      depPackage.build()

      expect(os.exists(outputPackagePath))

      // list files which will be installed
      val payloadFiles =
        os.proc("dpkg", "--contents", outputPackagePath).call().out.text().trim
      val expectedScriptPath = os.RelPath("usr") / "bin" / packageName
      val expectedLibPaths = scalaPackagerJar().tail.map(path =>
        os.RelPath("usr") / "share" / "lib" / path.last
      )

      expect(payloadFiles contains s"./$expectedScriptPath")
      expectedLibPaths.foreach(libPath =>
        expect(payloadFiles contains s"./$libPath")
      )
    }
  }

  test("should load launcher script for app jar") {

    val libPath: os.Path = tmpDir / "lib"

    val script = DebianPackage
      .javaAppScript(
        jarAppSettings,
        libPath,
        os.resource / 'packager / 'deb / "launcherJarScript.sh"
      )
      .load

    expect(script.nonEmpty)
  }

  override def extension: PackageExtension = Deb

  override def buildSettings: DebianSettings =
    DebianSettings(
      shared = sharedSettings,
      version = "1.0.0",
      maintainer = "Scala Packager",
      description = "Scala Packager Test",
      debianConflicts = Nil,
      debianDependencies = Nil,
      architecture = "all"
    )
}
