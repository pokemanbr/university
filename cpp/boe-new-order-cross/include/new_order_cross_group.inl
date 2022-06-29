#ifndef FIELD
#  error You need to define FIELD macro
#else
FIELD(side, 1, 1)
FIELD(volume, 1, 4)
FIELD(cl_ord_id, 1, 20)
FIELD(capacity, 1, 1)
FIELD(clearing_firm, 1, 4)
FIELD(account_type, 1, 1)
FIELD(algorithmic_indicator, 1, 1)
#undef FIELD
#endif
