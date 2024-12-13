//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel:6.6.0"
//> using plugin "org.chipsalliance:::chisel-plugin:6.5.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import _root_.circt.stage.ChiselStage

import chisel3._
import TopdownMonitor._

// =====================
// ===== Interface =====
// =====================
class abstract_ibex_topdown_monitor extends RawModule() {
  val clk_i = IO(Input(Clock()))
  val rst_ni = IO(Input(Bool()))
  val io = IO(new Bundle {
    // input control signals
    val inhibit_i = Input(Bool())

    // global input event signals
    val iside_wait_i,
        dside_wait_i,
        mul_wait_i,
        div_wait_i,
        mispredict_i = Input(Bool())

    // lane input event signals
    val alu_req_i,
        mul_req_i,
        div_req_i,
        lsu_req_i = Input(Bool())

    // output counter increment signals
    val base_comp_incr_o,
        icache_comp_incr_o,
        bpred_comp_incr_o,
        dcache_comp_incr_o,
        ex_comp_incr_o,
        dependency_comp_incr_o = Output(Bool())
  })
}

class ibex_topdown_monitor extends abstract_ibex_topdown_monitor() {

  // combinational logic
  class ibex_topdown_monitor_impl {

    // registers to help generate pulse for multiplication/division acknowledge
    val mul_wait_prev, div_wait_prev = Reg(Bool())
    mul_wait_prev := io.mul_wait_i
    div_wait_prev := io.div_wait_i

    // internal instantiation of the topdown monitor
    val topdown_monitor = Module(new TopdownMonitor(1))

    // ===============================
    // ===== CPI MODEL DECISIONS =====
    // ===============================
    topdown_monitor.io.inhibit_i        := io.inhibit_i
    topdown_monitor.io.icache_wait_i(0) := io.iside_wait_i
    topdown_monitor.io.dcache_wait_i(0) := io.dside_wait_i
    topdown_monitor.io.ex_wait_i(0)     := io.mul_wait_i | io.div_wait_i
    topdown_monitor.io.mispredict_i(0)  := io.mispredict_i
    topdown_monitor.io.lane_request_i(0) := io.alu_req_i |
        (io.mul_req_i & ~mul_wait_prev) |
        (io.div_req_i & ~div_wait_prev) |
         io.lsu_req_i

    // =========================
    // ===== ASSIGN OUTPUT =====
    // =========================
    io.base_comp_incr_o := topdown_monitor.io.base_comp_incr_o(0)
    io.icache_comp_incr_o := topdown_monitor.io.icache_comp_incr_o(0)
    io.bpred_comp_incr_o := topdown_monitor.io.bpred_comp_incr_o(0)
    io.dcache_comp_incr_o := topdown_monitor.io.dcache_comp_incr_o(0)
    io.ex_comp_incr_o := topdown_monitor.io.ex_comp_incr_o(0)
    io.dependency_comp_incr_o := topdown_monitor.io.dependency_comp_incr_o(0)
  }
  var topown_monitor_impl = withClockAndReset(clk_i, !rst_ni) { new ibex_topdown_monitor_impl }
}

object Main extends App {
  println("/* verilator lint_off DECLFILENAME */")
  println(
    ChiselStage.emitSystemVerilog(
      gen = new ibex_topdown_monitor(),
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
  println("/* verilator lint_on DECLFILENAME */")
}
