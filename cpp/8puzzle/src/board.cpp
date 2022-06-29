#include "board.h"

#include <algorithm>
#include <cmath>
#include <numeric>
#include <random>
#include <sstream>

Board Board::create_goal(const unsigned size)
{
    std::vector<std::vector<unsigned>> table(size);
    for (std::size_t i = 0; i < size; ++i) {
        table[i].resize(size);
    }
    for (std::size_t i = 0; i < size; ++i) {
        std::iota(table[i].begin(), table[i].end(), i * size + 1);
    }
    table[size - 1][size - 1] = 0;
    return Board(table, {size - 1, size - 1});
}

std::vector<std::vector<unsigned>> Board::rotate(const std::vector<std::vector<unsigned>> & table)
{
    std::vector<std::vector<unsigned>> result = table;
    std::size_t half = table.size() / 2;
    for (std::size_t i = 0; i < half; ++i) {
        std::size_t corner1 = result.size() - i - 1;
        for (std::size_t j = i; j < corner1; ++j) {
            unsigned temp = result[i][j];
            std::size_t corner2 = result.size() - j - 1;
            result[i][j] = result[j][corner1];
            result[j][corner1] = result[corner1][corner2];
            result[corner1][corner2] = result[corner2][i];
            result[corner2][i] = temp;
        }
    }
    return result;
}

Board Board::create_random(const unsigned size)
{
    std::vector<std::vector<unsigned>> table(size);
    for (std::size_t i = 0; i < size; ++i) {
        table[i].resize(size);
    }
    std::vector<unsigned> numbers(size * size);
    std::iota(numbers.begin(), numbers.end(), 0);
    std::shuffle(numbers.begin(), numbers.end(), std::random_device());
    for (std::size_t i = 0; i < size; ++i) {
        for (std::size_t j = 0; j < size; ++j) {
            table[i][j] = numbers.back();
            numbers.pop_back();
        }
    }
    Board game(table);
    if (!game.is_solvable()) {
        game.board = rotate(game.board);
    }
    return game;
}

Board::Board(const std::vector<std::vector<unsigned>> & data)
    : board(data)
{
    for (std::size_t i = 0; i < size(); ++i) {
        for (std::size_t j = 0; j < size(); ++j) {
            if (data[i][j] == 0) {
                empty_cell = {i, j};
            }
        }
    }
}

Board::Board(const std::vector<std::vector<unsigned int>> & data, std::pair<std::size_t, std::size_t> cell)
    : board(data)
    , empty_cell(cell)
{
}

std::size_t Board::size() const
{
    return board.size();
}

bool Board::is_goal() const
{
    return size() == 0 || (empty_cell.first == size() - 1 && empty_cell.second == size() - 1 && hamming() == 0);
}

unsigned Board::hamming() const
{
    unsigned count = 0;
    unsigned number = 0;
    unsigned last = size() * size() - 1;
    for (std::size_t i = 0; i < size(); ++i) {
        for (std::size_t j = 0; j < size(); ++j) {
            if (number == last) {
                number = 0;
            }
            else {
                ++number;
            }
            if (board[i][j] != number) {
                ++count;
            }
        }
    }
    return count;
}

unsigned Board::find_difference(std::size_t element1, std::size_t element2) const
{
    if (element1 > element2) {
        return element1 - element2;
    }
    return element2 - element1;
}

unsigned Board::find_difference_row(std::size_t x, std::size_t y) const
{
    return find_difference(x, (board[x][y] - 1) / size());
}

unsigned Board::find_difference_column(std::size_t x, std::size_t y) const
{
    return find_difference(y, (board[x][y] - 1) % size());
}

unsigned Board::manhattan() const
{
    unsigned distance = 0;
    for (std::size_t i = 0; i < size(); ++i) {
        for (std::size_t j = 0; j < size(); ++j) {
            if (board[i][j] != 0) {
                distance += find_difference_row(i, j) + find_difference_column(i, j);
            }
        }
    }
    return distance;
}

std::string Board::to_string() const
{
    std::stringstream str;
    for (std::size_t i = 0; i < size(); ++i) {
        for (std::size_t j = 0; j < size(); ++j) {
            str << board[i][j];
            if (j + 1 != size()) {
                str << ' ';
            }
        }
        str << '\n';
    }
    return str.str();
}

bool Board::is_solvable() const
{
    if (is_goal()) {
        return true;
    }
    unsigned count = 0;
    for (std::size_t i1 = 0; i1 < size(); ++i1) {
        for (std::size_t j1 = 0; j1 < size(); ++j1) {
            if (board[i1][j1] == 0 && size() % 2 == 0) {
                count += 1 + i1;
            }
            else {
                for (std::size_t i2 = i1; i2 < size(); ++i2) {
                    for (std::size_t j2 = (i2 == i1 ? j1 + 1 : 0); j2 < size(); ++j2) {
                        count += (board[i2][j2] != 0 && board[i2][j2] < board[i1][j1] ? 1 : 0);
                    }
                }
            }
        }
    }
    return count % 2 == 0;
}

std::pair<std::size_t, std::size_t> Board::get_empty_cell() const
{
    return empty_cell;
}

std::vector<std::vector<unsigned>> Board::get_table() const
{
    return board;
}
