#include <cstring>
#include <ctime>
#include <fstream>
#include <iostream>
#include <map>
#include <string>
#include <vector>

namespace {

void push_numbers(std::vector<std::string> & vec, std::string & str, std::size_t start)
{
    for (std::size_t j = start; j <= str.size(); ++j) {
        if (j == str.size() || !std::isdigit(str[j])) {
            vec.push_back(str.substr(start, j - start));
            start = j + 1;
        }
    }
}

std::map<std::string, std::size_t> numbers_after_flags({{"r", 0}, {"-i", 1}, {"-n", 1}});

std::vector<std::string> parse_flags(int argc, char ** argv)
{
    std::vector<std::string> flags;
    for (int i = 1; i < argc; ++i) {
        std::string str = argv[i];
        if (str.size() >= 2 && str[0] == '-' && str[1] == '-') {
            std::size_t cursor = 0;
            while (cursor < str.size() && str[cursor] != '=') {
                ++cursor;
            }
            flags.push_back(str.substr(0, cursor));
            if (cursor < str.size()) {
                push_numbers(flags, str, cursor + 1);
            }
        }
        else if (str[0] == '-') {
            flags.push_back(str);
            if (numbers_after_flags[str]) {
                str = argv[++i];
                push_numbers(flags, str, 0);
            }
        }
        else {
            flags.push_back(str);
        }
    }
    return flags;
}

void print_shuffle_of_vector(std::vector<std::string> & lines, int count, bool repeat)
{
    for (int i = 0; !lines.empty() && (count == -1 || i < count); ++i) {
        std::size_t cursor = std::rand() % lines.size();
        std::cout << lines[cursor] << std::endl;
        if (!repeat) {
            std::swap(lines[cursor], lines[lines.size() - 1]);
            lines.pop_back();
        }
    }
}

void print_shuffle_of_numbers(unsigned lo, unsigned hi, int count, bool repeat)
{
    std::map<unsigned, unsigned> used;
    for (int i = 0; lo <= hi && (count == -1 || i < count); ++i) {
        unsigned number = lo + std::rand() % (hi - lo + 1);
        std::cout << (used.count(number) == 0 ? number : used[number]) << std::endl;
        if (!repeat) {
            used[number] = (used.count(hi) == 0 ? hi : used[hi]);
            --hi;
        }
    }
}

} // namespace

int main(int argc, char ** argv)
{
    std::srand(std::time(nullptr));
    int count = -1;
    bool repeat = false;
    bool input_file = true;
    unsigned lo = 0;
    unsigned hi = 0;
    std::vector<std::string> flags = parse_flags(argc, argv);
    for (std::size_t i = 0; i < flags.size(); ++i) {
        if (flags[i] == "-i" || flags[i] == "--input-range") {
            lo = std::stoul(flags[++i]);
            hi = std::stoul(flags[++i]);
            input_file = false;
        }
        else if (flags[i] == "-r" || flags[i] == "--repeat") {
            repeat = true;
        }
        else if (flags[i] == "-n" || flags[i] == "--head-count") {
            count = std::stoi(flags[++i]);
        }
    }
    if (input_file) {
        std::ifstream file(flags.back());
        std::string str;
        std::vector<std::string> lines;
        while (getline(file, str)) {
            lines.push_back(str);
        }
        file.close();
        print_shuffle_of_vector(lines, count, repeat);
    }
    else {
        print_shuffle_of_numbers(lo, hi, count, repeat);
    }
    return 0;
}