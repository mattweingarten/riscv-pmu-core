//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel:6.6.0"
//> using plugin "org.chipsalliance:::chisel-plugin:6.5.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import _root_.circt.stage.ChiselStage

import TopdownMonitor._

class ibex_topdown_monitor extends TopdownMonitor(1) {}

object Main extends App {
  println(
    ChiselStage.emitSystemVerilog(
      gen = new ibex_topdown_monitor(),
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
