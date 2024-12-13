#!/bin/sh

OUT_DIR=/users/$1

"cd $OUT_DIR"

mkdir lib/
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$OUT_DIR/lib

mkdir tests/