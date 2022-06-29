"use strict";
function createException(name, message) {
    function ParsingError(...args) {
        this.name = name;
        this.message = message(...args);
    }
    ParsingError.prototype = Object.create(Error.prototype);
    ParsingError.prototype.name = name;
    ParsingError.prototype.constructor = ParsingError;
    return ParsingError;
}

const LostBracket = createException("LostBracket", (pos) => "Expected ')' instead of element at pos " + pos);
const WrongNumberElements = createException("WrongNumberElements", (exp, where) => "Expected " + exp + " in " + where);
const UnexpectedElement = createException("UnexpectedElement", (exp, pos, what) => "Expected " + exp + " at pos " + pos + ' ' + what)
const UnknownElement = createException("UnknownElement", (element) => "Unknown element: " + element);

function createOperation(calculate, derivative, sign) {
    return function (...operands) {
        return {
            calculate: calculate,
            derivative: derivative,
            sign: sign,
            operands: [...operands],
            evaluate: function (...vars) {
                return this.calculate(...this.operands.map(operation => operation.evaluate(...vars)));
            },
            toString: function () {
                return this.operands.map(operation => operation.toString()).join(' ') + ' ' + this.sign;
            },
            diff: function (name) {
                return this.derivative(...this.operands, ...this.operands.map(operation => operation.diff(name)));
            },
            prefix: function () {
                return '(' + this.sign + ' ' + this.operands.map(operation => operation.prefix()).join(' ') + ')';
            },
            postfix: function () {
                return '(' + this.operands.map(operation => operation.postfix()).join(' ') + ' ' + this.sign + ')';
            }
        }
    }
}

function Const(value) {
    return {
        evaluate: function (...vars) {
            return value;
        },
        toString: function () {
            return value.toString();
        },
        diff: function (name) {
            return Const.ZERO;
        },
        prefix: function () {
            return value.toString();
        },
        postfix: function () {
            return value.toString();
        }
    }
}

Const.ZERO = new Const(0);
Const.ONE = new Const(1);
Const.TWO = new Const(2);
Const.E = new Const(Math.E);

const ln = (x) => new Log(Const.E, x);

const sum = (...list) => list.reduce((ans, element) => ans + element);

const varIndexes = {'x': 0, 'y': 1, 'z': 2};

function Variable(letter) {
    return {
        evaluate: function (...vars) {
            return vars[varIndexes[letter]];
        },
        toString: function () {
            return letter;
        },
        diff: function (name) {
            return (letter === name ? Const.ONE : Const.ZERO);
        },
        prefix: function () {
            return letter;
        },
        postfix: function () {
            return letter;
        }
    }
}

const Add = createOperation(
    (a, b) => a + b,
    (x, y, dx, dy) => new Add(dx, dy),
    '+'
);
const Subtract = createOperation(
    (a, b) => a - b,
    (x, y, dx, dy) => new Subtract(dx, dy),
    '-'
);
const Multiply = createOperation(
    (a, b) => a * b,
    (x, y, dx, dy) => new Add(new Multiply(dx, y), new Multiply(x, dy)),
    '*'
);
const Divide = createOperation(
    (a, b) => a / b,
    (x, y, dx, dy) => new Divide(new Subtract(new Multiply(dx, y), new Multiply(x, dy)), new Multiply(y, y)),
    '/'
);
const Negate = createOperation(
    a => -a,
    (x, dx) => new Negate(dx),
    "negate"
);
const Pow = createOperation(
    (a, b) => Math.pow(a, b),
    (x, y, dx, dy) => {
        let ex = ln(x);
        return new Multiply(
            new Pow(x, new Subtract(y, Const.ONE)),
            new Add(
                new Multiply(dx, y),
                new Multiply(new Multiply(x, dy), ex)
            )
        );
    },
    "pow"
);
const Log = createOperation(
    (a, b) => Math.log(Math.abs(b)) / Math.log(Math.abs(a)),
    (x, y, dx, dy) => {
        let ex = ln(x), ey = ln(y);
        return new Divide(
            new Subtract(
                new Multiply(new Multiply(x, dy), ex),
                new Multiply(new Multiply(dx, y), ey)
            ),
            new Multiply(
                new Multiply(x, y),
                new Multiply(ex, ex)
            )
        );
    },
    "log"
);
const Mean = createOperation(
    (...operands) => operands.reduce((sum, element) => sum + element) / operands.length,
    (...operands) => new Divide(operands.splice(operands.length / 2, operands.length / 2).reduce((sum, element) =>
        new Add(sum, element), Const.ZERO), new Const(operands.length)),
    "mean"
);
const Var = createOperation(
    (...operands) => sum(...operands.map(x => x * x)) / operands.length - Math.pow(sum(...operands) / operands.length, 2),
    (...operands) => {
        let original = operands.slice(0, operands.length / 2);
        let dif = operands.slice(operands.length / 2, operands.length);
        let multy = original.map((x, ind) => new Multiply(x, dif[ind]));
        return new Subtract(
            new Multiply(Const.TWO, new Mean(...multy)),
            new Multiply(
                Const.TWO,
                new Multiply(new Mean(...original), new Mean(...dif)))
        );
    },
    "var"
);

const operations = {'+': Add, '-': Subtract, '*': Multiply, '/': Divide, "negate": Negate, "pow": Pow, "log": Log, "mean" : Mean, "var" : Var};

const makeList = expression => expression.split(' ').filter(str => (str !== ''));

function abstractParser(expression, type) {
    let elements = [], procedures = [];
    makeList(expression.replace(/\(/g, ' ( ').replace(/\)/g, ' ) ')).forEach((component, index) => {
        if (type === "post" && procedures.length > 0 && procedures[procedures.length - 1] in operations && component !== ')') {
            throw new LostBracket(index);
        }
        if (type === "pre" && procedures.length > 0 && procedures[procedures.length - 1] === '(' && !(component in operations)) {
            throw new UnexpectedElement("operation", index, "after opened bracket");
        }
        if (component in operations) {
            if (procedures.length === 0 || procedures[procedures.length - 1] !== '(') {
                throw new UnexpectedElement('(', index, "instead of operation");
            }
            procedures.push(component);
        } else if (component in varIndexes) {
            elements.push(new Variable(component));
        } else if (component === '(') {
            procedures.push(component);
            elements.push(component);
        } else if (component === ')') {
            let list = [];
            while (elements.length > 0 && elements[elements.length - 1] !== '(') {
                list.unshift(elements.pop());
            }
            elements.pop()
            if (procedures[procedures.length - 1] === '(') {
                throw new WrongNumberElements("operation", "brackets");
            }
            let count = operations[procedures[procedures.length - 1]]().calculate.length;
            if (count !== list.length && count !== 0) {
                throw new WrongNumberElements("a smaller number of operands", "brackets");
            }
            elements.push(new operations[procedures[procedures.length - 1]](...list));
            procedures.pop();
            procedures.pop();
        } else {
            if (component.length === 0 || isNaN(component)) {
                throw new UnknownElement(component);
            }
            elements.push(new Const(parseInt(component)));
        }
    });
    if (procedures.length !== 0) {
        throw new WrongNumberElements("more operands", "expression");
    }
    if (elements.length !== 1) {
        throw new WrongNumberElements("more operations", "expression");
    }
    return elements[0];
}

const parsePrefix = expression => abstractParser(expression, "pre");

const parsePostfix = expression => abstractParser(expression, "post");

const parse = expression => makeList(expression).reduce((stack, element) => {
    if (element in operations) {
        stack.push(new operations[element](...stack.splice(-operations[element]().calculate.length)));
    } else if (element in varIndexes) {
        stack.push(new Variable(element));
    } else {
        stack.push(new Const(parseInt(element)));
    }
    return stack;
}, [])[0];