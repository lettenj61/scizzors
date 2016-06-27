package scizzors.ops

import java.nio.file.{ Files, Paths, FileSystems, LinkOption }
import java.nio.file.attribute._

import ammonite.ops.Path
import ammonite.ops.FileType

/** Model of basic file attributes which aimed to support cross platform representation,
  * equivalent to Ammonite's `stat` & `stat.full` classes.
  */
trait Attrs {

  def name: String
  def size: Long

  def mtime: FileTime
  def ctime: FileTime
  def atime: FileTime

  /** The owner of the file.
    */
  def owner: UserPrincipal

  /** File, directory, symlink or other.
    */
  def fileType: FileType

  /** Get access to the file persmissions if underlying file system is Linus / Mac OS.
    *
    * Always empty for Windows.
    */
  def permissions: Set[PosixFilePermission]

  /** Get access to the list of ACL when underlying file system is Windows.
    *
    * Always empty for Linux / Mac OS.
    */
  def acl: Seq[AclEntry]

  def isDir = fileType == FileType.Dir
  def isSymLink = fileType == FileType.SymLink
  def isFile = fileType == FileType.File
}

object Attrs {

  private lazy val system = FileSystems.getDefault

  private abstract case class Specific(
      name: String,
      size: Long,
      mtime: FileTime,
      ctime: FileTime,
      atime: FileTime,
      owner: UserPrincipal,
      fileType: FileType) extends Attrs {

  }

  def default(path: ammonite.ops.Path) = {
    import collection.JavaConverters._
    if (sys.props("os.name").toLowerCase.trim contains "windows") {
      make(path)(
        Set.empty,
        Files.getFileAttributeView(path.toNIO, classOf[AclFileAttributeView]).getAcl.asScala.toList
      )
    } else {
      make(path)(
        Files.readAttributes(
          path.toNIO,
          classOf[PosixFileAttributes],
          LinkOption.NOFOLLOW_LINKS
        ).permissions.asScala.toSet,
        Nil
      )
    }
  }

  def make(path: ammonite.ops.Path)(perms: Set[PosixFilePermission],
                                    acls: Seq[AclEntry]): Attrs = {

    val basic = Files.readAttributes(path.toNIO, classOf[BasicFileAttributes])
    val owner = Files.getFileAttributeView(path.toNIO, classOf[FileOwnerAttributeView]).getOwner

    new Specific(
      path.last,
      basic.size,
      basic.lastModifiedTime,
      basic.lastAccessTime,
      basic.creationTime,
      owner,
      if (basic.isRegularFile) FileType.File
      else if (basic.isDirectory) FileType.Dir
      else if (basic.isSymbolicLink) FileType.SymLink
      else if (basic.isOther) FileType.Other
      else ???
    ) {

      def permissions = perms
      def acl = acls
    }
  }
}
