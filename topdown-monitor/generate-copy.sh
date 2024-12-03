#!/bin/sh

# get target system
TARGET=${TARGET:="ibex"}
if [ ! -z "$1" ]; then
  TARGET="$1"
fi

# generate for target
case $TARGET in
  ibex)
    echo "Running for ibex"
    scala-cli ./TopdownMonitor.scala ./ibex_topdown_monitor.scala > ../ibex/rtl/ibex_topdown_monitor.sv
    ;;
  rocketchip)
    echo "Not yet implemented"
    ;;
  ?*)
    # unknown option
    printf "Ignoring unknown target: %s\n" "$TARGET" >&2
    ;;
esac
