#pragma once

#include <cstdint>
#include <numeric>
#include <string>

struct Fraction
{
    Fraction() = default;
    Fraction(std::int64_t number);
    Fraction(std::int64_t _numerator, std::int64_t _denominator);

    std::int64_t numerator() const;
    std::int64_t denominator() const;

    operator double() const;

    std::string str() const;

    Fraction operator+(const Fraction & frac) const;
    Fraction operator-(const Fraction & frac) const;
    Fraction operator*(const Fraction & frac) const;
    Fraction operator/(const Fraction & frac) const;

    Fraction & operator+=(const Fraction & frac);
    Fraction & operator-=(const Fraction & frac);
    Fraction & operator*=(const Fraction & frac);
    Fraction & operator/=(const Fraction & frac);

    template <class T>
    Fraction operator+(T number) const;
    template <class T>
    Fraction operator-(T number) const;
    template <class T>
    Fraction operator*(T number) const;
    template <class T>
    Fraction operator/(T number) const;

    template <class T>
    Fraction & operator+=(const T & number);
    template <class T>
    Fraction & operator-=(const T & number);
    template <class T>
    Fraction & operator*=(const T & number);
    template <class T>
    Fraction & operator/=(const T & number);

    Fraction operator-() const;

    bool operator==(const Fraction & frac) const;
    bool operator!=(const Fraction & frac) const;
    bool operator<(const Fraction & frac) const;
    bool operator<=(const Fraction & frac) const;
    bool operator>(const Fraction & frac) const;
    bool operator>=(const Fraction & frac) const;

    bool operator==(double number) const;
    bool operator!=(double number) const;
    bool operator<(double number) const;
    bool operator<=(double number) const;
    bool operator>(double number) const;
    bool operator>=(double number) const;

    template <class T>
    bool operator==(T number) const;
    template <class T>
    bool operator!=(T number) const;
    template <class T>
    bool operator<(T number) const;
    template <class T>
    bool operator<=(T number) const;
    template <class T>
    bool operator>(T number) const;
    template <class T>
    bool operator>=(T number) const;

    friend std::ostream & operator<<(std::ostream & out, const Fraction & frac);

private:
    std::int64_t numer = 0;
    std::uint64_t denom = 1;
};
template <class T>
Fraction Fraction::operator+(T number) const
{
    return *this + Fraction(number);
}
template <class T>
Fraction Fraction::operator-(T number) const
{
    return *this - Fraction(number);
}
template <class T>
Fraction Fraction::operator*(T number) const
{
    return *this * Fraction(number);
}
template <class T>
Fraction Fraction::operator/(T number) const
{
    return *this / Fraction(number);
}
template <class T>
Fraction & Fraction::operator+=(const T & number)
{
    *this += Fraction(number);
    return *this;
}
template <class T>
Fraction & Fraction::operator-=(const T & number)
{
    *this -= Fraction(number);
    return *this;
}
template <class T>
Fraction & Fraction::operator*=(const T & number)
{
    *this *= Fraction(number);
    return *this;
}
template <class T>
Fraction & Fraction::operator/=(const T & number)
{
    *this /= Fraction(number);
    return *this;
}
template <class T>
bool Fraction::operator==(T number) const
{
    return *this == Fraction(number);
}
template <class T>
bool Fraction::operator!=(T number) const
{
    return *this != Fraction(number);
}
template <class T>
bool Fraction::operator<(T number) const
{
    return *this < Fraction(number);
}
template <class T>
bool Fraction::operator<=(T number) const
{
    return *this <= Fraction(number);
}
template <class T>
bool Fraction::operator>(T number) const
{
    return *this > Fraction(number);
}
template <class T>
bool Fraction::operator>=(T number) const
{
    return *this >= Fraction(number);
}