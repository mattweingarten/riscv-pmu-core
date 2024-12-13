
package freechips.rocketchip.rocket

import chisel3._
import TopdownMonitor._

// =====================
// ===== Interface =====
// =====================
class AbstractRocketTopdownMonitor extends RawModule() {
  val io = IO(new Bundle {
    val icache_blocked,
        dcache_blocked,
        ex_reg_valid,
        take_pc_mem,
        mem_direction_misprediction,
        ibuf_valid = Input(Bool())

  })
}

class RocketTopdownMonitor extends AbstractRocketTopdownMonitor() {

  // internal instantiation of the topdown monitor
  val topdown_monitor = Module(new TopdownMonitor(1))

  // ===============================
  // ===== CPI MODEL DECISIONS =====
  // ===============================
  topdown_monitor.io.inhibit_i        := false.B
  topdown_monitor.io.icache_wait_i(0) := io.icache_blocked
  topdown_monitor.io.dcache_wait_i(0) := io.dcache_blocked
  topdown_monitor.io.ex_wait_i(0)     := io.ex_reg_valid
  topdown_monitor.io.mispredict_i(0)  := io.take_pc_mem && io.mem_direction_misprediction
  topdown_monitor.io.lane_request_i(0) := io.ibuf_valid

  // =========================
  // ===== ASSIGN OUTPUT =====
  // =========================
  def getEventSet: EventSet = {
    new EventSet((mask, hits) => (mask & hits).orR, Seq(
      ("Base component cycles", () => topdown_monitor.io.base_comp_incr_o(0)),
      ("I$ component cycles", () => topdown_monitor.io.icache_comp_incr_o(0)),
      ("Branch predictor component cycles", () => topdown_monitor.io.bpred_comp_incr_o(0)),
      ("D$ component cycles", () => topdown_monitor.io.dcache_comp_incr_o(0)),
      ("Execution component cycles", () => topdown_monitor.io.ex_comp_incr_o(0)),
      ("Dependency component cycles", () => topdown_monitor.io.dependency_comp_incr_o(0))
    ))
  }

}
