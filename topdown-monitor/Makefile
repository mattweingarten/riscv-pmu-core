
ibex: TopdownMonitor.scala ibex_topdown_monitor.scala
	scala-cli ./TopdownMonitor.scala ./ibex_topdown_monitor.scala > ../ibex/rtl/ibex_topdown_monitor.sv

rocket-chip: TopdownMonitor.scala
	cp ./TopdownMonitor.scala ../chipyard/generators/rocket-chip/src/main/scala/rocket/TopdownMonitor.scala
	cp ./RocketTopdownMonitor.scala ../chipyard/generators/rocket-chip/src/main/scala/rocket/RocketTopdownMonitor.scala

boom: TopdownMonitor.scala
	$(info Not yet implemented)
	cp ./TopdownMonitor.scala ../chipyard/generators/boom/src/main/scala/rocket/TopdownMonitor.scala
