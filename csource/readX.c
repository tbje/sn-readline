#include "readX.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <readline/readline.h>

int foo(float y, float z) {
  printf("%f - %f", y, z);
  return 1;
};


void set_rl_attempted_completion_func(rl_completion_func_t f) {
  rl_attempted_completion_function = f;
  return;
};
