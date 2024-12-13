import matplotlib.pyplot as plt
import numpy as np

import os
import argparse

COLORS=['#9bbb59', '#7030a0', '#ffc000',"#c0504d"  ]



EXPERIMENTS=[    
    "backtrack",
    "dft", 
    "dhrystone", 
    "fc_forward", 
    "graph", 
    "insertionsort", 
    "matrix_mult_rec", 
    "mergesort", 
    "mm", 
    "multiply", 
    "nvdla", 
    "omegalul", 
    "outer_product", 
    "pingd", 
    "pmp", 
    "priority_queue", 
    "pwm", 
    "qsort", 
    "quicksort", 
    "rsort", 
    "sort_search", 
    "spiflashread", 
    "spiflashwrite", 
    "spmv", 
    "streaming-fir",  
    "streaming-passthrough" 
    "symmetric", 
    "towers", 
    "vvadd"  
]


def get_filename_without_extension(file_path):
    filename = os.path.basename(file_path)    
    name_without_extension = filename.split('.')[0]
    return name_without_extension

GLOBAL_COUNTER=0
def get_benchmark_name(file_name):
    for ex in EXPERIMENTS:
        if ex in file_name:
            return ex
    GLOBAL_COUNTER += 1
    return "UNKOWN" + GLOBAL_COUNTER



def get_counter(counter_dict, name):
    return int(counter_dict[name])

def read_counters_file(file_path):
    counters_dict = {}
    with open(file_path, 'r') as file:
        for line in file:   
            line = line.strip()
            if ': ' in line:
                parts = line.split(': ')
                if len(parts) == 2:
                    name, counter = parts
                    try:
                        number = int(counter)
                        counters_dict[name] = counter
                    except ValueError:
                        continue
    return counters_dict

    

def create_CPI_plot(out_name, labels, list_of_values, benchmarks, IPCS, colors=COLORS):
    num_bars = len(list_of_values)
    assert(num_bars == len(benchmarks) and "Numbers of bars do not match length of benchmarks")
    fig, ax = plt.subplots()
    ax.grid(axis='y')
    ax.set_axisbelow(True)


    for bar_position, values in enumerate(list_of_values):
        bottom = np.zeros(len(values)) 
        for i, value in enumerate(values):
            ax.bar(bar_position, value, bottom=bottom[i], label=labels[i] if bar_position == 0 else "", color=colors[i])
            bottom[i + 1:] += value 
            
    
    plt.plot(IPCS, linestyle='--', linewidth=2, marker='^', markersize=15, label="IPC", color="#4bacc6")
    plt.yticks(np.arange(0.0, 1.1, 0.1))
    ax.set_xticks(range(num_bars))
    ax.set_xticklabels(benchmarks)
    ax.tick_params(axis='x', which=u'both',length=0)
    plt.xticks(rotation=90)
    plt.xlim(left=-.75, right=len(list_of_values) -0.25)
    plt.ylim(top=1.0,bottom=0.0)

    ax.legend(loc='upper center', bbox_to_anchor=(0.5, 1.15), frameon=False, ncol=5, fontsize=10)

    fig.subplots_adjust(bottom=0.3)
    plt.savefig(f"{out_name}.svg", format="svg") 
    plt.show()


def parse_arguments():
    parser = argparse.ArgumentParser(description="Parse data and make CPI models")
    parser.add_argument('path', type=str, help="The directory containing data files.")
    parser.add_argument('name', type=str,    help="Name of plot file or folder")

    return parser.parse_args()



def compute_cpi_values_top_level_naive(counters_dict):
    categories = ["frontend", "backend", "bad speculation", "retiring"]
    IPC = get_counter(counters_dict, "Int Ret") / get_counter(counters_dict, "Cycle")
    CPI = 1.0/IPC    
    frontend = 1 - get_counter(counters_dict, "IBuf valid") /  get_counter(counters_dict, "Cycle")
    stall = get_counter(counters_dict, "ID kill") /  get_counter(counters_dict, "Cycle")
    
    bad_spec_stalls = get_counter(counters_dict, "Stall Mispr") * 5
    backend_stalls = get_counter(counters_dict, "Ctrl dependency") + get_counter(counters_dict, "Data depedency")
    bad_spec_ratio =  bad_spec_stalls / (bad_spec_stalls + backend_stalls) 
    backend_ratio =  backend_stalls / (bad_spec_stalls + backend_stalls)
    bad_spec = stall * bad_spec_ratio
    backend_stall = stall * backend_ratio

    dcache_busy = get_counter(counters_dict, "DCache Busy") / get_counter(counters_dict, "Cycle")
    backend = backend_stall + dcache_busy
    retiring = 1 - frontend - backend - bad_spec
    return ["Retiring", "Frontend", "Backend", "Bad speculation"],[retiring, frontend, backend, bad_spec], IPC

def main():
    args = parse_arguments()
    path = args.path
    out_file_name = args.name
    if(os.path.isfile(path)):
        counters = read_counters_file(path)
        benchmark = get_benchmark_name(path)
        labels, values, IPC    = compute_cpi_values_top_level_naive(counters) 
        create_CPI_plot(benchmark, labels, [values], [benchmark], [IPC])
    elif (os.path.isdir(path)):
        files = os.listdir(path)
        values = []
        IPCS = []
        labels = []
        benchmarks = []
        for f in files:
            try:
                counters = read_counters_file(os.path.join(path,f))
                l,v,i = compute_cpi_values_top_level_naive(counters)
                b = get_benchmark_name(f)
            except:
                continue
            benchmarks.append(b)
            values.append(v)
            labels = l
            IPCS.append(i)
        
        create_CPI_plot(out_file_name,labels, values,benchmarks, IPCS)    



if __name__ == "__main__":
    main()