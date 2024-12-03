
package TopdownMonitor

import chisel3._
//import chisel3.util.{BitPat, Fill, Cat, Reverse, PriorityEncoderOH, PopCount, MuxLookup}
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._

abstract class AbstractTopdownMonitor(N_LANES: Int) extends RawModule() {
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
        lsu_req_i = Input(Vec(N_LANES, Bool()))

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
    // generate pulse for multiplication/division acknowledge
    val mul_wait_prev, div_wait_prev = Reg(Bool())
    mul_wait_prev := io.mul_wait_i
    div_wait_prev := io.div_wait_i

    for (i <- 0 until N_LANES) {
      io.base_comp_incr_o(i) := false.B
      io.icache_comp_incr_o(i) := false.B
      io.bpred_comp_incr_o(i) := false.B
      io.dcache_comp_incr_o(i) := false.B
      io.ex_comp_incr_o(i) := false.B
      io.dependency_comp_incr_o(i) := false.B
      when (~io.inhibit_i) {
        when (~(io.alu_req_i(i) |
              (io.mul_req_i(i) & ~mul_wait_prev) |
              (io.div_req_i(i) & ~div_wait_prev) |
              io.lsu_req_i(i))) {
          when (io.iside_wait_i) {
            io.icache_comp_incr_o(i) := true.B
          } .elsewhen (io.mispredict_i) {
            io.bpred_comp_incr_o(i) := true.B
          } .elsewhen (io.dside_wait_i) {
            io.dcache_comp_incr_o(i) := true.B
          } .elsewhen (io.mul_wait_i | io.div_wait_i) {
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

  val topdownMonitorImpl = withClockAndReset(clk_i, !rst_ni) { new TopdownMonitorImpl }
}
