#include "rocc.h"
#include "pmu_defs.h"

char string[64] __attribute__ ((aligned (64))) = "The quick brown fox jumped over the lazy dog";

static inline unsigned long count_chars(char *start, char needle)
{
	unsigned long count;
	asm volatile ("fence");
	ROCC_INSTRUCTION_DSS(2, count, start, needle, 0);
	return count;
}

int main(void)
{
    #ifdef PMU
        start_counters(); 
    #endif

	unsigned long count = count_chars(string + 14, 'o');
	if (count != 3)
		#ifdef PMU
        	end_counters(); 
    	#endif
		return count + 1;
	#ifdef PMU
        end_counters(); 
    #endif
	return 0;
}
