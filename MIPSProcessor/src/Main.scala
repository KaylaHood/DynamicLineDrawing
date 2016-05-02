
import java.io.FileInputStream
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.FileOutputStream
import java.io.BufferedOutputStream
import java.io.DataOutputStream 

/**
 * @author khood
 */

case class InputException(message : String) extends Exception(message)

object Main {
   var pc = new Register(Array.fill(32)(0))
  def main(args:Array[String]) ={
    if(!(args(0).endsWith(".in")) || !(args(1).endsWith(".in"))) {
      throw InputException("Oops! One or both input filenames did not end with \".in\"")
    }
    val dis = new DataInputStream(new BufferedInputStream(new FileInputStream(args(0))))
    val mem1 = Array.fill(0x1000)(dis.readInt)
    val mem2 = Array.fill((1 << 14) - 0x1000)(dis.readInt)
    dis.close
    val dis2 = new DataInputStream(new BufferedInputStream(new FileInputStream(args(1))))
    val regs = Array.fill(32)(dis2.readInt)
    dis2.close
    
    val InstructionMem = new Memory(mem1)
    val DataMem = new Memory(mem2)
  }
}