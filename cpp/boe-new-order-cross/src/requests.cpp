#include "requests.h"

namespace {

void encode_new_order_opt_fields(unsigned char * bitfield_start,
                                 const double price,
                                 const char ord_type,
                                 const char time_in_force,
                                 const unsigned max_floor,
                                 const std::string & symbol,
                                 const char capacity,
                                 const std::string & account)
{
    auto * p = bitfield_start + new_order_bitfield_num();
#define FIELD(name, bitfield_num, bit)                    \
    set_opt_field_bit(bitfield_start, bitfield_num, bit); \
    p = encode_field_##name(p, name);
#include "new_order_opt_fields.inl"
}

unsigned char * encode_new_order_cross_group(unsigned char * p,
                                             const char side,
                                             const double volume,
                                             const std::string & cl_ord_id,
                                             const char capacity,
                                             const std::string & clearing_firm,
                                             const char account_type,
                                             const bool algorithmic_indicator)
{
#define FIELD(name, bitfield_num, bit) \
    p = encode_field_##name(p, name);
#include "new_order_cross_group.inl"
    return p;
}

unsigned char * encode_new_order_cross_multileg_group(unsigned char * p,
                                                      const char side,
                                                      const double volume,
                                                      const std::string & cl_ord_id,
                                                      const char capacity,
                                                      const std::string & clearing_firm,
                                                      const char account_type,
                                                      const std::string & legs,
                                                      const bool algorithmic_indicator)
{
#define FIELD(name, bitfield_num, bit) \
    p = encode_field_##name(p, name);
#include "new_order_cross_multileg_group.inl"
    return p;
}

uint8_t new_order_cross_bitfield_input(uint8_t number)
{
    return 0
#define FIELD(_, bitfield_num, bit) +((bitfield_num) == (number) ? (bit) : 0)
#include "new_order_cross_opt_fields.inl"
            ;
}

uint8_t new_order_cross_multileg_bitfield_input(uint8_t number)
{
    return 0
#define FIELD(_, bitfield_num, bit) +((bitfield_num) == (number) ? (bit) : 0)
#include "new_order_cross_multileg_opt_fields.inl"
            ;
}

uint8_t encode_request_type(const RequestType type)
{
    switch (type) {
    case RequestType::New:
        return 0x38;
    case RequestType::NewCross:
        return 0x7A;
    case RequestType::NewCrossMultileg:
        return 0x85;
    }
    return 0;
}

unsigned char * add_request_header(unsigned char * start, unsigned length, const RequestType type, unsigned seq_no)
{
    *start++ = 0xBA;
    *start++ = 0xBA;
    start = encode(start, static_cast<uint16_t>(length));
    start = encode(start, encode_request_type(type));
    *start++ = 0;
    return encode(start, seq_no);
}

char convert_side(const Side side)
{
    switch (side) {
    case Side::Buy: return '1';
    case Side::Sell: return '2';
    }
    return 0;
}

char convert_ord_type(const OrdType ord_type)
{
    switch (ord_type) {
    case OrdType::Market: return '1';
    case OrdType::Limit: return '2';
    case OrdType::Pegged: return 'P';
    }
    return 0;
}

char convert_time_in_force(const TimeInForce time_in_force)
{
    switch (time_in_force) {
    case TimeInForce::Day: return '0';
    case TimeInForce::IOC: return '3';
    case TimeInForce::GTD: return '6';
    }
    return 0;
}

char convert_capacity(const Capacity capacity)
{
    switch (capacity) {
    case Capacity::Agency: return 'A';
    case Capacity::Principal: return 'P';
    case Capacity::RisklessPrincipal: return 'R';
    }
    return 0;
}

char convert_account_type(const AccountType account_type)
{
    switch (account_type) {
    case AccountType::Client: return '1';
    case AccountType::House: return '3';
    }
    return 0;
}

char convert_position(const Position position)
{
    switch (position) {
    case Position::Open: return 'O';
    case Position::Close: return 'C';
    case Position::None: return 'N';
    }
    return 0;
}

std::string convert_legs(const std::vector<Position> & legs)
{
    std::string str;
    str.reserve(legs.size());
    for (const auto p : legs) {
        str += convert_position(p);
    }
    return str;
}

} // anonymous namespace

std::array<unsigned char, calculate_size(RequestType::New)> create_new_order_request(const unsigned seq_no,
                                                                                     const std::string & cl_ord_id,
                                                                                     const Side side,
                                                                                     const double volume,
                                                                                     const double price,
                                                                                     const OrdType ord_type,
                                                                                     const TimeInForce time_in_force,
                                                                                     const double max_floor,
                                                                                     const std::string & symbol,
                                                                                     const Capacity capacity,
                                                                                     const std::string & account)
{
    static_assert(calculate_size(RequestType::New) == 78, "Wrong New Order message size");

    std::array<unsigned char, calculate_size(RequestType::New)> msg;
    auto * p = add_request_header(&msg[0], msg.size() - 2, RequestType::New, seq_no);
    p = encode_text(p, cl_ord_id, 20);
    p = encode_char(p, convert_side(side));
    p = encode_binary4(p, static_cast<uint32_t>(volume));
    p = encode(p, static_cast<uint8_t>(new_order_bitfield_num()));
    encode_new_order_opt_fields(p,
                                price,
                                convert_ord_type(ord_type),
                                convert_time_in_force(time_in_force),
                                max_floor,
                                symbol,
                                convert_capacity(capacity),
                                account);
    return msg;
}

unsigned char * encode_cross_price_volume(unsigned char * p,
                                          const std::string & cross_id,
                                          const Side side,
                                          const double price,
                                          const double volume)
{
    p = encode_text(p, cross_id, 20);
    p = encode_char(p, '1'); // crossType
    p = encode_char(p, convert_side(side));
    p = encode_price(p, price);
    return encode_binary4(p, static_cast<uint32_t>(volume));
}

unsigned char * encode_order(unsigned char * p, const Order & order)
{
    return encode_new_order_cross_group(p,
                                        convert_side(order.side),
                                        order.volume,
                                        order.cl_ord_id,
                                        convert_capacity(order.capacity),
                                        order.clearing_firm,
                                        convert_account_type(order.account_type),
                                        order.algorithmic_indicator);
}

std::vector<unsigned char> create_new_order_cross_request(
        unsigned seq_no,
        const std::string & cross_id,
        double price,
        const std::string & symbol,
        const Order & agency_order,
        const std::vector<Order> & contra_orders)
{
    const std::size_t size_msg = calculate_size(RequestType::NewCross) + (contra_orders.size() + 1) * new_order_cross_group_size();
    std::vector<unsigned char> msg(size_msg);

    auto * p = add_request_header(&msg[0], msg.size() - 2, RequestType::NewCross, seq_no);
    p = encode_cross_price_volume(p, cross_id, agency_order.side, price, agency_order.volume);

    const uint8_t len_bitfield = static_cast<uint8_t>(new_order_cross_bitfield_num());
    p = encode(p, len_bitfield);
    for (uint8_t i = 1; i <= len_bitfield; i++) {
        p = encode_binary1(p, new_order_cross_bitfield_input(i));
    }

    p = encode_binary2(p, static_cast<uint16_t>(contra_orders.size() + 1));
    p = encode_order(p, agency_order);
    for (std::size_t i = 0; i < contra_orders.size(); i++) {
        p = encode_order(p, contra_orders[i]);
    }
    encode_text(p, symbol, 8);

    return msg;
}

unsigned char * encode_complex_order(unsigned char * p, const ComplexOrder & order)
{
    return encode_new_order_cross_multileg_group(p,
                                                 convert_side(order.order.side),
                                                 order.order.volume,
                                                 order.order.cl_ord_id,
                                                 convert_capacity(order.order.capacity),
                                                 order.order.clearing_firm,
                                                 convert_account_type(order.order.account_type),
                                                 convert_legs(order.legs),
                                                 order.order.algorithmic_indicator);
}

std::vector<unsigned char> create_new_order_cross_multileg_request(
        unsigned seq_no,
        const std::string & cross_id,
        double price,
        const std::string & symbol,
        const ComplexOrder & agency_order,
        const std::vector<ComplexOrder> & contra_orders)
{
    const std::size_t size_msg = calculate_size(RequestType::NewCrossMultileg) + (contra_orders.size() + 1) * new_order_cross_multileg_group_size();
    std::vector<unsigned char> msg(size_msg);

    auto * p = add_request_header(&msg[0], msg.size() - 2, RequestType::NewCrossMultileg, seq_no);
    p = encode_cross_price_volume(p, cross_id, agency_order.order.side, price, agency_order.order.volume);

    const uint8_t len_bitfield = static_cast<uint8_t>(new_order_cross_multileg_bitfield_num());
    p = encode(p, len_bitfield);
    for (uint8_t i = 1; i <= len_bitfield; i++) {
        p = encode_binary1(p, new_order_cross_multileg_bitfield_input(i));
    }

    p = encode_binary2(p, static_cast<uint16_t>(contra_orders.size() + 1));
    p = encode_complex_order(p, agency_order);
    for (std::size_t i = 0; i < contra_orders.size(); i++) {
        p = encode_complex_order(p, contra_orders[i]);
    }
    encode_text(p, symbol, 8);

    return msg;
}
