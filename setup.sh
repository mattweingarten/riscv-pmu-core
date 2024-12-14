#!/bin/sh


git submodule update --init
cd chipyard/

git remote set-url origin git@github.com:mattweingarten/chipyard.git

git checkout rocket_chip_cpi


cd generators/rocket-chip/
git remote set-url origin git@github.com:mattweingarten/rocket-chip.git
git checkout rocket_chip
cd ../../


cd generators/boom/
git remote set-url origin git@github.com:mattweingarten/riscv-boom.git
git checkout boom_chip
cd ../../



./build-setup.sh riscv-tools  -s 4 -s 5 -s 6 -s 7 -s 8 -s 9 
cd ../

cd riscv-test


git remote set-url origin git@github.com:mattweingarten/riscv-tests.git
cd .. /

