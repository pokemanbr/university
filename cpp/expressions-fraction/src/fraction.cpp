#include "fraction.h"

#include <iostream>

Fraction::Fraction(std::int64_t number)
    : numer(number)
{
}
std::pair<std::int64_t, std::uint64_t> make_fraction(std::int64_t _n, std::int64_t _d)
{
    std::int64_t numer = _n;
    if (_d < 0) {
        numer *= -1;
        _d *= -1;
    }
    std::uint64_t denom = _d;
    if (_d != 0) {
        std::int64_t gcd = std::gcd(numer, denom);
        numer /= gcd;
        denom /= gcd;
    }
    else {
        if (numer > 0) {
            numer = 1;
        }
        else if (numer < 0) {
            numer = -1;
        }
    }
    return {numer, denom};
}
Fraction::Fraction(std::int64_t _n, std::int64_t _d)
    : numer(make_fraction(_n, _d).first)
    , denom(make_fraction(_n, _d).second)
{
}
std::int64_t Fraction::numerator() const
{
    return numer;
}
std::int64_t Fraction::denominator() const
{
    return denom;
}
Fraction::operator double() const
{
    return static_cast<double>(numer) / static_cast<double>(denom);
}
std::string Fraction::str() const
{
    return std::to_string(numer) + '/' + std::to_string(denom);
}
Fraction & Fraction::operator+=(const Fraction & frac)
{
    std::uint64_t gcd = std::gcd(denom, frac.denom);
    numer = frac.denom / gcd * numer + denom / gcd * frac.numer;
    denom = denom / gcd * frac.denom;
    return *this;
}
Fraction Fraction::operator+(const Fraction & frac) const
{
    Fraction result = *this;
    result += frac;
    return result;
}
Fraction & Fraction::operator-=(const Fraction & frac)
{
    operator+=(-frac);
    return *this;
}
Fraction Fraction::operator-(const Fraction & frac) const
{
    Fraction result = *this;
    result -= frac;
    return result;
}
Fraction & Fraction::operator*=(const Fraction & frac)
{
    std::int64_t gcd1 = std::gcd(numer, frac.denom);
    numer /= gcd1;
    std::int64_t gcd2 = std::gcd(denom, frac.numer);
    denom /= gcd2;
    numer *= frac.numer / gcd2;
    denom *= frac.denom / gcd1;
    std::int64_t gcd = std::gcd(numer, denom);
    numer /= gcd;
    denom /= gcd;
    return *this;
}
Fraction Fraction::operator*(const Fraction & frac) const
{
    Fraction result = *this;
    result *= frac;
    return result;
}
Fraction & Fraction::operator/=(const Fraction & frac)
{
    if (frac.numerator() != 0) {
        operator*=(Fraction(frac.denominator(), frac.numerator()));
    }
    return *this;
}
Fraction Fraction::operator/(const Fraction & frac) const
{
    Fraction result = *this;
    result /= frac;
    return result;
}
Fraction Fraction::operator-() const
{
    return Fraction(-numer, denom);
}
bool Fraction::operator==(const Fraction & frac) const
{
    return numer == frac.numer && denom == frac.denom;
}
bool Fraction::operator!=(const Fraction & frac) const
{
    return !(*this == frac);
}
bool Fraction::operator<(const Fraction & frac) const
{
    std::uint64_t gcd = std::gcd(denom, frac.denom);
    return static_cast<std::int64_t>(frac.denom / gcd) * numer < static_cast<std::int64_t>(denom / gcd) * frac.numer;
}
bool Fraction::operator<=(const Fraction & frac) const
{
    return *this < frac || *this == frac;
}
bool Fraction::operator>(const Fraction & frac) const
{
    return !(*this <= frac);
}
bool Fraction::operator>=(const Fraction & frac) const
{
    return !(*this < frac);
}
std::ostream & operator<<(std::ostream & out, const Fraction & frac)
{
    return out << frac.str();
}
bool Fraction::operator==(double number) const
{
    return static_cast<double>(*this) == number;
}
bool Fraction::operator!=(double number) const
{
    return static_cast<double>(*this) != number;
}
bool Fraction::operator<(double number) const
{
    return static_cast<double>(*this) < number;
}
bool Fraction::operator<=(double number) const
{
    return static_cast<double>(*this) <= number;
}
bool Fraction::operator>(double number) const
{
    return static_cast<double>(*this) > number;
}
bool Fraction::operator>=(double number) const
{
    return static_cast<double>(*this) >= number;
}