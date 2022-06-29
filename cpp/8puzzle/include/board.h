#pragma once

#include <string>
#include <vector>

class Board
{
public:
    static Board create_goal(unsigned size);

    static Board create_random(unsigned size);

    Board() = default;

    explicit Board(const std::vector<std::vector<unsigned>> & data);

    explicit Board(const std::vector<std::vector<unsigned>> & data, std::pair<std::size_t, std::size_t> cell);

    std::size_t size() const;

    bool is_goal() const;

    unsigned find_difference(std::size_t element1, std::size_t element2) const;

    unsigned find_difference_row(std::size_t x, std::size_t y) const;

    unsigned find_difference_column(std::size_t x, std::size_t y) const;

    unsigned hamming() const;

    unsigned manhattan() const;

    std::string to_string() const;

    bool is_solvable() const;

    std::pair<std::size_t, std::size_t> get_empty_cell() const;

    std::vector<std::vector<unsigned>> get_table() const;

    friend bool operator==(const Board & lhs, const Board & rhs)
    {
        return lhs.board == rhs.board;
    }

    friend bool operator!=(const Board & lhs, const Board & rhs)
    {
        return lhs.board != rhs.board;
    }

    friend bool operator<(const Board & lhs, const Board & rhs)
    {
        return lhs.board < rhs.board;
    }

    friend std::ostream & operator<<(std::ostream & out, const Board & b)
    {
        return out << b.to_string();
    }

    const std::vector<unsigned> & operator[](std::size_t index) const
    {
        return board[index];
    }

    static std::vector<std::vector<unsigned>> rotate(const std::vector<std::vector<unsigned>> & table);

private:
    std::vector<std::vector<unsigned>> board;
    std::pair<std::size_t, std::size_t> empty_cell = {0, 0};
};

template <>
struct std::hash<Board>
{
    std::size_t operator()(const Board & b) const
    {
        const std::size_t p = 31;
        std::size_t multi = 1;
        std::size_t hash = 0;
        for (std::size_t i = 0; i < b.size(); ++i) {
            for (std::size_t j = 0; j < b.size(); ++j) {
                hash += multi * b[i][j];
                multi *= p;
            }
        }
        return hash;
    }
};
