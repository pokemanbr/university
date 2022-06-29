#include "ScapegoatTree.h"

#include <stdexcept> // for std::invalid_argument exception

class ScapegoatTree::Node
{
    void link_child(Node *& main, Node * node)
    {
        main = node;
        if (node) {
            main->parent = this;
        }
    }

public:
    int key = 0;
    int sizeTree = 0;
    Node * left = nullptr;
    Node * right = nullptr;
    Node * parent = nullptr;

    void link_left(Node * node)
    {
        link_child(left, node);
    }

    void link_right(Node * node)
    {
        link_child(right, node);
    }

    static bool has(Node * node, int value);
    static int minimum(Node * node);
    static std::size_t numbersOfNodes(Node * node);
    static Node * add(Node * node, int value);
    static Node * erase(Node * node, int value);
    static void fillVector(Node * node, std::vector<int> & values);

    Node() = default;

    Node(const int value)
        : key(value)
        , sizeTree(1)
    {
    }

    Node(Node * parent, const int value)
        : key(value)
        , sizeTree(1)
        , parent(parent)
    {
    }

    ~Node()
    {
        delete left;
        delete right;
    }
};

bool ScapegoatTree::Node::has(ScapegoatTree::Node * node, const int value)
{
    if (!node) {
        return false;
    }
    if (node->key == value) {
        return true;
    }
    if (node->left && value < node->key) {
        return has(node->left, value);
    }
    if (node->right && node->key < value) {
        return has(node->right, value);
    }
    return false;
}

int ScapegoatTree::Node::minimum(Node * node)
{
    return (node->left ? minimum(node->left) : node->key);
}

std::size_t ScapegoatTree::Node::numbersOfNodes(Node * node)
{
    return (node ? node->sizeTree : 0);
}

ScapegoatTree::Node * ScapegoatTree::Node::add(Node * node, const int value)
{
    if (!node) {
        node = new Node(nullptr, value);
        return node;
    }
    ++node->sizeTree;
    Node *& child = (value < node->key ? node->left : node->right);
    if (!child) {
        child = new Node(node, value);
        return child;
    }
    return add(child, value);
}

ScapegoatTree::Node * ScapegoatTree::Node::erase(Node * node, const int value)
{
    if (!node) {
        return node;
    }
    --node->sizeTree;
    if (value < node->key) {
        node->link_left(erase(node->left, value));
    }
    else if (node->key < value) {
        node->link_right(erase(node->right, value));
    }
    else {
        if (!node->left && !node->right) {
            delete node;
            return nullptr;
        }
        if (!node->right) {
            node->left->parent = node->parent;
            Node * left = node->left;
            node->left = nullptr;
            delete node;
            return left;
        }
        if (!node->left) {
            node->right->parent = node->parent;
            Node * right = node->right;
            node->right = nullptr;
            delete node;
            return right;
        }
        node->key = Node::minimum(node->right);
        node->link_right(erase(node->right, node->key));
    }
    return node;
}

void ScapegoatTree::Node::fillVector(Node * node, std::vector<int> & values)
{
    if (!node) {
        return;
    }
    fillVector(node->left, values);
    values.push_back(node->key);
    fillVector(node->right, values);
}

bool ScapegoatTree::checkBalanced(Node * node) const
{
    return node == nullptr || std::max(Node::numbersOfNodes(node->left), Node::numbersOfNodes(node->right)) <= alpha * Node::numbersOfNodes(node);
}

ScapegoatTree::Node * ScapegoatTree::buildHeightBalancedTree(std::vector<int> & array, const unsigned int left, const unsigned int right)
{
    unsigned int middle = left + (right - left) / 2;
    auto * node = new Node(array[middle]);
    node->sizeTree = right - left + 1;
    if (left < middle) {
        node->link_left(buildHeightBalancedTree(array, left, middle - 1));
    }
    if (middle < right) {
        node->link_right(buildHeightBalancedTree(array, middle + 1, right));
    }
    return node;
}

ScapegoatTree::Node * ScapegoatTree::rebuildTree(Node * scapegoat)
{
    std::vector<int> values;
    Node::fillVector(scapegoat, values);
    Node * node = buildHeightBalancedTree(values, 0, values.size() - 1);
    node->parent = scapegoat->parent;
    if (scapegoat->parent) {
        if (scapegoat->parent->key < scapegoat->key) {
            scapegoat->parent->right = node;
        }
        else {
            scapegoat->parent->left = node;
        }
    }
    delete scapegoat;
    return node;
}

ScapegoatTree::ScapegoatTree(const double constAlpha)
    : alpha(constAlpha)
{
    if (alpha < 0.5 || 1.0 < alpha) {
        throw std::invalid_argument("Alpha need to be between 0.5 and 1");
    }
}

bool ScapegoatTree::contains(const int value) const
{
    return Node::has(root, value);
}

bool ScapegoatTree::insert(const int value)
{
    if (Node::has(root, value)) {
        return false;
    }
    Node * added = Node::add(root, value);
    while (added->parent != nullptr && checkBalanced(added)) {
        added = added->parent;
    }
    if (!checkBalanced(added)) {
        added = rebuildTree(added);
    }
    while (added->parent) {
        added = added->parent;
    }
    root = added;
    return true;
}

bool ScapegoatTree::remove(const int value)
{
    if (!Node::has(root, value)) {
        return false;
    }
    root = Node::erase(root, value);
    if (!checkBalanced(root)) {
        root = rebuildTree(root);
    }
    return true;
}

std::size_t ScapegoatTree::size() const
{
    return Node::numbersOfNodes(root);
}

bool ScapegoatTree::empty() const
{
    return size() == 0;
}

std::vector<int> ScapegoatTree::values() const
{
    std::vector<int> vector;
    Node::fillVector(root, vector);
    return vector;
}

ScapegoatTree::~ScapegoatTree()
{
    delete root;
}
