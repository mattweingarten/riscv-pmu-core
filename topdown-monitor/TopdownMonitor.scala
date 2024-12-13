
import chisel3._

package TopdownMonitor {

  // =====================
  // ===== Interface =====
  // =====================
  abstract class AbstractTopdownMonitor(N_LANES: Int) extends RawModule() {

    val io = IO(new Bundle {
      // input control signals
      val inhibit_i = Input(Bool())

      // global input event signals
      val icache_wait_i,
          dcache_wait_i,
          ex_wait_i,
          mispredict_i = Input(Vec(N_LANES, Bool()))

      // lane input event signals
      val lane_request_i = Input(Vec(N_LANES, Bool()))

      // output counter increment signals
      val base_comp_incr_o,
          icache_comp_incr_o,
          bpred_comp_incr_o,
          dcache_comp_incr_o,
          ex_comp_incr_o,
          dependency_comp_incr_o = Output(Vec(N_LANES, Bool()))
    })
  }

  class TopdownMonitor(N_LANES: Int) extends AbstractTopdownMonitor(N_LANES) {
    // gated clock domain
    class TopdownMonitorImpl {
      for (i <- 0 until N_LANES) {
        io.base_comp_incr_o(i) := false.B
        io.icache_comp_incr_o(i) := false.B
        io.bpred_comp_incr_o(i) := false.B
        io.dcache_comp_incr_o(i) := false.B
        io.ex_comp_incr_o(i) := false.B
        io.dependency_comp_incr_o(i) := false.B
        when (~io.inhibit_i) {
          when (~io.lane_request_i(i)) {
            when (io.icache_wait_i(i)) {
              io.icache_comp_incr_o(i) := true.B
            } .elsewhen (io.mispredict_i(i)) {
              io.bpred_comp_incr_o(i) := true.B
            } .elsewhen (io.dcache_wait_i(i)) {
              io.dcache_comp_incr_o(i) := true.B
            } .elsewhen (io.ex_wait_i(i)) {
              io.ex_comp_incr_o(i) := true.B
            } .otherwise {
              io.dependency_comp_incr_o(i) := true.B
            }
          } .otherwise {
            io.base_comp_incr_o(i) := true.B
          }
        }
      }

    }

    val topdownMonitorImpl = new TopdownMonitorImpl
  }

}
