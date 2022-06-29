"use strict";

const abstractOperation = operation => (...operands) => (...vars) => operation(...operands.map(x => x(...vars)));

const add = abstractOperation((a, b) => a + b);
const subtract = abstractOperation((a, b) => a - b);
const multiply = abstractOperation((a, b) => a * b);
const divide = abstractOperation((a, b) => a / b);
const negate = abstractOperation(a => -a);
const abs = abstractOperation(a => Math.abs(a));
const iff = abstractOperation((num, one, two) => (num >= 0 ? one : two));

const namesConst = {"pi": Math.PI, 'e': Math.E};
const varIndexes = {'x': 0, 'y': 1, 'z': 2};
const variable = letter => (...vars) => vars[varIndexes[letter]];
const cnst = value => x => value;
const pi = cnst(namesConst["pi"]), e = cnst(namesConst['e']);

const operations = {
    '+': [add, 2],
    '-': [subtract, 2],
    '*': [multiply, 2],
    '/': [divide, 2],
    "negate": [negate, 1],
    "abs": [abs, 1],
    "iff": [iff, 3]
};

const parse = expression => (...vars) => expression.split(' ').filter(str => (str !== '')).reduce((stack, element) => {
    if (element in operations) {
        stack.push(cnst(operations[element][0](...stack.splice(-operations[element][1]))(vars)));
    } else if (element in varIndexes) {
        stack.push(cnst(variable(element)(...vars)));
    } else if (element in namesConst) {
        stack.push(cnst(namesConst[element]));
    } else {
        stack.push(cnst(parseInt(element)));
    }
    return stack;
}, [])[0]();


// println(parse("       3  4 + x - ")(5, 0, 0));
// let expr = add(
//     subtract(
//         multiply(
//             variable("x"),
//             variable("x")
//         ),
//         multiply(
//             cnst(2),
//             variable("x")
//         ),
//     ),
//     cnst(1)
// );
//
// for (let x = 0; x <= 10; x++) {
//     println(expr(x, 0, 0));
// }
