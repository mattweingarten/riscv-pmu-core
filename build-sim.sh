#!/bin/sh

#Small helper script to build a Sim and

# $1 = Config 
# $2 = Name of sim out

cd chipyard/sims/verilator

make CONFIG=$1
mv simulator-chipyard.harness-$1 ../../../build/sims/$2.sim