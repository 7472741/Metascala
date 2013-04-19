package metascala
package natives


import java.io.DataInputStream
import vrt.Arr

trait Default extends Bindings{

  val fileLoader = (name: String) => {
    val slashName = s"/$name"

    val loaded = getClass.getResourceAsStream(slashName)
    if (loaded == null) None
    else {
      val stream = new DataInputStream(loaded)
      val bytes = new Array[Byte](stream.available())
      stream.readFully(bytes)
      Some(bytes)
    }
  }

  val properties = Map[String, String](
    /*"java.home" -> "C ->/java_home",
    "sun.boot.class.path" -> "C ->/classes",
    "file.encoding" ->"US_ASCII",
    "java.ext.dirs" -> "",
    "java.vendor" ->"Doppio",
    "java.version" -> "1.6",
    "java.vendor.url" -> "https ->//github.com/int3/doppio",
    "java.class.version" -> "50.0",
    "java.security.debug" -> "access,failure",
    "java.security.auth.debug" -> "access,failure",
    "java.specification.version" -> "1.6",
    "jdk.map.althashing.threshold" -> "-1",
    "line.separator" ->"\n",
    "file.separator" ->"/",
    "path.separator" ->":",
    "user.dir" -> ".",
    "user.home" ->".",
    "user.name" ->"DoppioUser",
    "os.name" ->"doppio",
    "os.arch" -> "js",
    "os.version" -> "0"*/

  )


  val trapped = {
    Seq(
      "java"/(
        "lang"/(
          "Class"/(
            "desiredAssertionStatus0(Ljava/lang/Class;)Z" x value(I)(0, 0),
            "getClassLoader0()Ljava/lang/ClassLoader;" x value(I)(0, 0),
            "getPrimitiveClass(Ljava/lang/String;)Ljava/lang/Class;" x {vt =>
              import vt.vm
              vt.push(vrt.Obj.allocate("java/lang/Class",
                "name" -> vt.pop
              ).address)
            },
            "registerNatives()V" x noOp(0)
          ),
          "Double"/(
            "doubleToRawLongBits(D)J" x noOp(0)
          ),
          "Float"/(
            "intBitsToFloat(I)F" x noOp(0),
            "floatToRawIntBits(F)I" x noOp(0)
          ),
          "Object"/(
            "getClass()Ljava/lang/Class;" x {vt =>

              import vt.vm
              val obj = vt.pop.obj
              val stringAddr = {
                var a = 0
                vt.pushVirtual(obj.cls.name.toDot, a = _)
                a
              }
              vt.push(vrt.Obj.allocate("java/lang/Class",
                "name" -> stringAddr
              ).address)
            },
            "getName0()Ljava/lang/String;" x {vt =>
              vt.pop
              import vt.vm
              vm.ClsTable
              vt.push(vrt.Obj.allocate("java/lang/Class").address)
            },
            "registerNatives()V" x noOp(0)
          ),
          "Throwable"/(
            "fillInStackTrace()Ljava/lang/Throwable;" x {vt =>
              import vt.vm
              //vt.pop // pop dummy
              val throwable = vt.pop.obj
              val trace = vrt.Arr.allocate(imm.Type.Cls("java/lang/StackTraceElement"),
                vt.threadStack.map( f =>
                  vrt.Obj.allocate(imm.Type.Cls("java/lang/StackTraceElement").cls,
                    "declaringClass" -> vt.pushVirtual(f.runningClass.name)(0),
                    "methodName" -> vt.pushVirtual(f.method.method.name)(0),
                    "fileName" -> vt.pushVirtual(f.runningClass.clsData.misc.sourceFile.getOrElse("<unknown file>"))(0),
                    "lineNumber" -> 0
                  ).address
                ).toArray
              )
              throwable("stackTrace") = trace.address

              vt.push(throwable.address)
            }
          ),

          "System"/(
            "arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V" x { vt =>
              val Seq(src, srcIndex, dest, destIndex, length) = Seq(vt.pop, vt.pop, vt.pop, vt.pop, vt.pop).reverse
              System.arraycopy(vt.vm.Heap.memory, src + srcIndex + 2, vt.vm.Heap.memory, dest + destIndex + 2, length)

            },
            "nanoTime()J" x value(J)(0, System.nanoTime()),
            "currentTimeMillis()J" x value(J)(0, System.currentTimeMillis())
          ),
          "String"/(
            "<clinit>()V" x noOp(0)
          ),
          "System"/(
            "registerNatives()V" x noOp(0)
          )
        )
      )
    ).toRoute()
  }

}

