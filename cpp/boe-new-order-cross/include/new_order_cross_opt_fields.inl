#ifndef FIELD
#  error You need to define FIELD macro
#else
FIELD(algorithmic_indicator, 1, 64)
FIELD(symbol, 1, 1)
#undef FIELD
#endif
