#pragma once

#include "board.h"

#include <map>
#include <unordered_map>

class Solver
{
private:
    class Solution
    {
    public:
        explicit Solution(const std::vector<Board> & data);

        std::size_t moves() const { return m_moves.empty() ? 0 : m_moves.size() - 1; }

        using const_iterator = std::vector<Board>::const_iterator;

        const_iterator begin() const { return m_moves.begin(); }

        const_iterator end() const { return m_moves.end(); }

    private:
        std::vector<Board> m_moves;
    };

    using Cell = std::pair<std::size_t, std::size_t>;
    using Table = std::vector<std::vector<unsigned>>;

    static std::vector<Board> get_adjacent_boards(const Board & board);
    static std::vector<std::pair<std::size_t, std::size_t>> get_adjacent_coords(std::pair<std::size_t, std::size_t> coords, std::size_t size);
    static std::vector<Board> A_star(const Board & board);
    static std::vector<Board> get_moves(const Board & board, const std::unordered_map<Board, Board> & parent);

public:
    static Solution solve(const Board & initial);
};
