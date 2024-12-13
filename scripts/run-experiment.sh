#!/bin/sh
experiments=(
    "backtrack.riscv" 
    "dft.riscv" \ 
    "dhrystone.riscv" \ 
    "fc_forward.riscv" \ 
    "graph.riscv" \ 
    "insertionsort.riscv" \ 
    "matrix_mult_rec.riscv" \ 
    "mergesort.riscv" \ 
    "mm.riscv" \ 
    "multiply.riscv" \ 
    "nvdla.riscv" \ 
    "omegalul.riscv" \ 
    "outer_product.riscv" \ 
    "pingd.riscv" \ 
    "pmp.riscv" \ 
    "priority_queue.riscv" \ 
    "pwm.riscv" \ 
    "qsort.riscv" \ 
    "quicksort.riscv" \ 
    "rsort.riscv" \ 
    "sort_search.riscv" \ 
    "spiflashread.riscv" \ 
    "spiflashwrite.riscv" \ 
    "spmv.riscv" \ 
    "streaming-fir.riscv" \  
    "streaming-passthrough.riscv" 
    "symmetric.riscv" \ 
    "towers.riscv" \ 
    "vvadd.riscv"  
)

export LD_LIBRARY_PATH=lib/

SIM="$1"
OUT="$2"
SUFFIX="$3"

SIM_BASE="${SIM%.*}"

mkdir -p $OUT

for filename in benchmarks/*.riscv; do 
    basename=$(basename "$filename")
    for experiment in ${experiments[@]}; do
        if [[ $experiment == $basename ]]; then
            basename_no_ext="${basename%.*}"
            echo $basename_no_ext
            ./$SIM $filename > "${OUT}/${SIM_BASE}_${basename_no_ext}_${SUFFIX}.cpi" 
        fi
    done
done 

