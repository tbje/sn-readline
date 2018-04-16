#ifndef _MYLIB_H_
#define _MYLIB_H_
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "readline/readline.h"
    // a function prototype for a function exported by library:
extern int foo(float y, float z);   // a very bad function name

extern void set_rl_attempted_completion_func(rl_completion_func_t f);

#endif
