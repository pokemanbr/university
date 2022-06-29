#ifndef FIELD
#  error You need to define FIELD macro
#else
FIELD(symbol, 1, 1)
FIELD(algorithmic_indicator, 1, 64)
FIELD(legs, 3, 16)
#undef FIELD
#endif
