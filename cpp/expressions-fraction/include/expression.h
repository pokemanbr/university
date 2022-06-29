#pragma once

#include "fraction.h"

#include <map>
#include <memory>
#include <ostream>
#include <string>
#include <utility>

struct Expression
{
    virtual Fraction eval(std::map<std::string, Fraction> const & values = {}) const = 0;

    virtual Expression * clone() const = 0;

    virtual std::string str() const = 0;

    friend std::ostream & operator<<(std::ostream & out, const Expression & expression)
    {
        return out << expression.str();
    }

    virtual ~Expression() = default;
};

struct Add : Expression
{
    std::shared_ptr<Expression> element1;
    std::shared_ptr<Expression> element2;

    Add(const Expression & _element1, const Expression & _element2)
        : element1(_element1.clone())
        , element2(_element2.clone())
    {
    }

    Add(std::shared_ptr<Expression> _element1, std::shared_ptr<Expression> _element2)
        : element1(std::move(_element1))
        , element2(std::move(_element2))
    {
    }

    Expression * clone() const override
    {
        return new Add(element1, element2);
    }

    Fraction eval(std::map<std::string, Fraction> const & values = {}) const override
    {
        return element1->eval(values) + element2->eval(values);
    }

    std::string str() const override
    {
        return '(' + element1->str() + " + " + element2->str() + ')';
    }

    ~Add() = default;
};

struct Subtract : Expression
{
    std::shared_ptr<Expression> element1;
    std::shared_ptr<Expression> element2;

    Subtract(const Expression & _element1, const Expression & _element2)
        : element1(_element1.clone())
        , element2(_element2.clone())
    {
    }

    Subtract(std::shared_ptr<Expression> _element1, std::shared_ptr<Expression> _element2)
        : element1(std::move(_element1))
        , element2(std::move(_element2))
    {
    }

    Expression * clone() const override
    {
        return new Subtract(element1, element2);
    }

    Fraction eval(std::map<std::string, Fraction> const & values = {}) const override
    {
        return element1->eval(values) - element2->eval(values);
    }

    std::string str() const override
    {
        return '(' + element1->str() + " - " + element2->str() + ')';
    }

    ~Subtract() = default;
};

struct Multiply : Expression
{
    std::shared_ptr<Expression> element1;
    std::shared_ptr<Expression> element2;

    Multiply(const Expression & _element1, const Expression & _element2)
        : element1(_element1.clone())
        , element2(_element2.clone())
    {
    }

    Multiply(std::shared_ptr<Expression> _element1, std::shared_ptr<Expression> _element2)
        : element1(std::move(_element1))
        , element2(std::move(_element2))
    {
    }

    Expression * clone() const override
    {
        return new Multiply(element1, element2);
    }

    Fraction eval(std::map<std::string, Fraction> const & values = {}) const override
    {
        return element1->eval(values) * element2->eval(values);
    }

    std::string str() const override
    {
        return '(' + element1->str() + " * " + element2->str() + ')';
    }

    ~Multiply() = default;
};

struct Divide : Expression
{
    std::shared_ptr<Expression> element1;
    std::shared_ptr<Expression> element2;

    Divide(const Expression & _element1, const Expression & _element2)
        : element1(_element1.clone())
        , element2(_element2.clone())
    {
    }

    Divide(std::shared_ptr<Expression> _element1, std::shared_ptr<Expression> _element2)
        : element1(std::move(_element1))
        , element2(std::move(_element2))
    {
    }

    Expression * clone() const override
    {
        return new Divide(element1, element2);
    }

    Fraction eval(std::map<std::string, Fraction> const & values = {}) const override
    {
        return element1->eval(values) / element2->eval(values);
    }

    std::string str() const override
    {
        return '(' + element1->str() + " / " + element2->str() + ')';
    }

    ~Divide() = default;
};

struct Negate : Expression
{
    std::shared_ptr<Expression> element;

    Negate(const Expression & _element)
        : element(_element.clone())
    {
    }

    Negate(std::shared_ptr<Expression> _element)
        : element(std::move(_element))
    {
    }

    Expression * clone() const override
    {
        return new Negate(element);
    }

    Fraction eval(std::map<std::string, Fraction> const & values = {}) const override
    {
        return -element->eval(values);
    }

    std::string str() const override
    {
        return "(-" + element->str() + ')';
    }

    ~Negate() = default;
};

struct Const : Expression
{
    Fraction element;

    Const(std::int64_t _element)
        : element(_element)
    {
    }

    Const(Fraction _element)
        : element(_element)
    {
    }

    Expression * clone() const override
    {
        return new Const(element);
    }

    Fraction eval(std::map<std::string, Fraction> const & values = {}) const override
    {
        values.size();
        return element;
    }

    std::string str() const override
    {
        return element.str();
    }

    ~Const() = default;
};

struct Variable : Expression
{
    std::string element;

    Variable(std::string _element)
        : element(std::move(_element))
    {
    }

    Expression * clone() const override
    {
        return new Variable(element);
    }

    Fraction eval(std::map<std::string, Fraction> const & values = {}) const override
    {
        return values.at(element);
    }

    std::string str() const override
    {
        return element;
    }

    ~Variable() = default;
};

Add operator+(const Expression & element1, const Expression & element2)
{
    return Add(element1, element2);
}
Subtract operator-(const Expression & element1, const Expression & element2)
{
    return Subtract(element1, element2);
}
Multiply operator*(const Expression & element1, const Expression & element2)
{
    return Multiply(element1, element2);
}
Divide operator/(const Expression & element1, const Expression & element2)
{
    return Divide(element1, element2);
}
Negate operator-(const Expression & element)
{
    return Negate(element);
}