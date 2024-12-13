#!/bin/sh


CLOUDLAB="$1"
OUT_DIR=/users/$2
scp chipyard/.conda-env/riscv-tools/lib/*.so $CLOUDLAB:$OUT_DIR/lib

scp  chipyard/.conda-env/lib/*.so $CLOUDLAB:$OUT_DIR/lib
scp  chipyard/.conda-env/lib/*.so.* $CLOUDLAB:$OUT_DIR/lib
scp  scripts/run-experiment.sh $CLOUDLAB:$OUT_DIR/
scp  scripts/cloudlab-setup.sh $CLOUDLAB:$OUT_DIR/
scp  build/sims/* $CLOUDLAB:$OUT_DIR/
scp -r build/benchmarks/ $CLOUDLAB:$OUT_DIR/