#include "solver.h"

#include <algorithm>
#include <queue>
#include <set>
#include <unordered_set>

Solver::Solution::Solution(const std::vector<Board> & data)
    : m_moves(data)
{
}

std::vector<std::pair<std::size_t, std::size_t>> Solver::get_adjacent_coords(std::pair<std::size_t, std::size_t> coords, std::size_t size)
{
    std::vector<std::pair<std::size_t, std::size_t>> vec;
    if (coords.first > 0) {
        vec.emplace_back(coords.first - 1, coords.second);
    }
    if (coords.second > 0) {
        vec.emplace_back(coords.first, coords.second - 1);
    }
    if (coords.first + 1 < size) {
        vec.emplace_back(coords.first + 1, coords.second);
    }
    if (coords.second + 1 < size) {
        vec.emplace_back(coords.first, coords.second + 1);
    }
    return vec;
}

std::vector<Board> Solver::get_adjacent_boards(const Board & current_board)
{
    std::vector<Board> boards;
    Table current_table = current_board.get_table();
    Table adjacent_table;
    Cell empty_cell = current_board.get_empty_cell();
    for (const auto & adjacent_coords : get_adjacent_coords(empty_cell, current_board.size())) {
        adjacent_table = current_table;
        std::swap(adjacent_table[adjacent_coords.first][adjacent_coords.second], adjacent_table[empty_cell.first][empty_cell.second]);
        boards.emplace_back(adjacent_table, adjacent_coords);
    }
    return boards;
}

std::vector<Board> Solver::get_moves(const Board & current, const std::unordered_map<Board, Board> & parent)
{
    std::vector<Board> moves;
    moves.push_back(current);
    auto cur = parent.find(current);
    while (cur != parent.end()) {
        moves.push_back(cur->second);
        cur = parent.find(cur->second);
    }
    std::reverse(moves.begin(), moves.end());
    return moves;
}

unsigned h(const Board & board)
{
    return board.manhattan();
}

std::vector<Board> Solver::A_star(const Board & board)
{
    std::set<std::pair<unsigned, Board>> queue;
    std::unordered_map<Board, unsigned> f, g;
    std::unordered_map<Board, Board> parent;
    std::unordered_set<Board> used;
    queue.insert({0, board});
    f[board] = 0;
    g[board] = 0;
    while (!queue.empty()) {
        auto [current_f, current_board] = *queue.begin();
        queue.erase(queue.begin());
        if (current_board.is_goal()) {
            return get_moves(current_board, parent);
        }
        used.insert(current_board);
        for (const auto & adjacent_board : get_adjacent_boards(current_board)) {
            if (used.count(adjacent_board) == 0) {
                unsigned adjacent_g = (current_board.size() < 5 ? g[current_board] + 1 : current_board.manhattan());
                unsigned adjacent_f = adjacent_g + h(adjacent_board);
                if (f.count({adjacent_board}) == 0 || adjacent_g < g[adjacent_board]) {
                    queue.erase({f[adjacent_board], adjacent_board});
                    f[adjacent_board] = adjacent_f;
                    queue.insert({f[adjacent_board], adjacent_board});
                    g[adjacent_board] = adjacent_g;
                    parent[adjacent_board] = current_board;
                }
            }
        }
        f.erase(board);
        g.erase(board);
    }
    return {board};
}

Solver::Solution Solver::solve(const Board & board)
{
    if (board.is_goal()) {
        return Solution({board});
    }
    if (!board.is_solvable()) {
        return Solution({});
    }
    return Solution(A_star(board));
}