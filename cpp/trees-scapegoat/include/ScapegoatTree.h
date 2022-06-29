#pragma once

#include <vector>

class ScapegoatTree
{
    double alpha = 0.7;

    class Node;

    Node * root = nullptr;

    bool checkBalanced(Node * node) const;
    static Node * buildHeightBalancedTree(std::vector<int> & array, unsigned int left, unsigned int right);
    static Node * rebuildTree(Node * scapegoat);

public:
    ScapegoatTree() = default;
    ScapegoatTree(double alpha);

    bool contains(int value) const;
    bool insert(int value);
    bool remove(int value);

    std::size_t size() const;
    bool empty() const;

    std::vector<int> values() const;

    ~ScapegoatTree();
};
