#include "nbody.h"

#include <cmath>
#include <fstream>
#include <utility>

Cartesian::Cartesian(double _x, double _y)
    : x(_x)
    , y(_y)
{
}
Cartesian & Cartesian::operator+=(const Cartesian & coord)
{
    x += coord.x;
    y += coord.y;
    return *this;
}
Cartesian Cartesian::operator+(const Cartesian & coord) const
{
    Cartesian result = *this;
    result += coord;
    return result;
}
Cartesian & Cartesian::operator-=(const Cartesian & coord)
{
    operator+=(-coord);
    return *this;
}
Cartesian Cartesian::operator-(const Cartesian & coord) const
{
    Cartesian result = *this;
    result -= coord;
    return result;
}
Cartesian & Cartesian::operator*=(double scalar)
{
    x *= scalar;
    y *= scalar;
    return *this;
}
Cartesian Cartesian::operator*(double scalar) const
{
    Cartesian result = *this;
    result *= scalar;
    return result;
}
Cartesian & Cartesian::operator/=(double scalar)
{
    x /= scalar;
    y /= scalar;
    return *this;
}
Cartesian Cartesian::operator/(double scalar) const
{
    Cartesian result = *this;
    result /= scalar;
    return result;
}
Cartesian Cartesian::operator-() const
{
    return {-x, -y};
}
Cartesian Cartesian::abs(const Cartesian & coord)
{
    return {std::fabs(coord.x), std::fabs(coord.y)};
}

Body::Body(Cartesian pos, Cartesian vel, double m, std::string _name)
    : coord(pos)
    , velocity(vel)
    , mass(m)
    , name(std::move(_name))
{
}
double Body::distance(const Body & b) const
{
    Cartesian dist(coord - b.coord);
    return std::sqrt(dist.x * dist.x + dist.y * dist.y);
}
void Body::add_force(const Body & b)
{
    double r = distance(b);
    double F = G * mass * b.mass / (r * r);
    force += Cartesian::abs(coord - b.coord) * F / r;
}
void Body::reset_force()
{
    force = {0, 0};
}
void Body::update(double delta_t)
{
    Cartesian a(force / mass);
    velocity += a * delta_t;
    coord += velocity * delta_t;
}
std::ostream & operator<<(std::ostream & in, const Body & b)
{
    return in << b.name << ' ' << b.coord.x << ' ' << b.coord.y;
}
bool Body::in(const Quadrant q) const
{
    Cartesian lower_left_corner = q.get_lower_left_corner();
    Cartesian upper_right_corner = q.get_upper_right_corner();
    return lower_left_corner.x <= coord.x && coord.x <= upper_right_corner.x && lower_left_corner.y <= coord.y && coord.y <= upper_right_corner.y;
}
Body Body::plus(const Body & b)
{
    double new_mass = mass + b.mass;
    Cartesian new_position = (coord * mass + b.coord * b.mass) / new_mass;
    return {new_position, velocity, new_mass, name};
}
Cartesian Body::get_coords() const
{
    return coord;
}
std::string_view Body::get_name() const
{
    return name;
}

Quadrant::Quadrant(Cartesian _center, double length)
    : center(_center)
    , size(length)
{
}
bool Quadrant::contains(Cartesian p) const
{
    return center.x - size / 2 <= p.x && p.x <= center.x + size / 2 && center.y - size / 2 <= p.y && p.y <= center.y + size / 2;
}
double Quadrant::length() const
{
    return size;
}
Cartesian Quadrant::get_center()
{
    return center;
}
Quadrant Quadrant::nw() const
{
    return Quadrant({center.x - size / 4, center.y + size / 4}, size / 2);
}
Quadrant Quadrant::ne() const
{
    return Quadrant({center.x + size / 4, center.y + size / 4}, size / 2);
}
Quadrant Quadrant::sw() const
{
    return Quadrant({center.x - size / 4, center.y - size / 4}, size / 2);
}
Quadrant Quadrant::se() const
{
    return Quadrant({center.x + size / 4, center.y - size / 4}, size / 2);
}
Cartesian Quadrant::get_lower_left_corner() const
{
    return {center.x - size / 2, center.y - size / 2};
}
Cartesian Quadrant::get_upper_right_corner() const
{
    return {center.x + size / 2, center.y + size / 2};
}
std::ostream & operator<<(std::ostream & in, const Quadrant & q)
{
    return in << q.center.x << ' ' << q.center.y << ' ' << q.size;
}

BHTreeNode::BHTreeNode(Cartesian center, double size, const Body & body)
    : quadrate(center, size)
    , main_body(body)
    , center_mass(body)
{
}
void BHTreeNode::insert(const Body & b)
{
    if (is_leaf) {
        double new_length = quadrate.length() / 2;
        Cartesian center = quadrate.get_center();
        if (main_body.in(quadrate.nw())) {
            nw = std::make_shared<BHTreeNode>(Cartesian(center.x - new_length / 2, center.y + new_length / 2), new_length, main_body);
        }
        else if (main_body.in(quadrate.ne())) {
            ne = std::make_shared<BHTreeNode>(Cartesian(center.x + new_length / 2, center.y + new_length / 2), new_length, main_body);
        }
        else if (main_body.in(quadrate.sw())) {
            sw = std::make_shared<BHTreeNode>(Cartesian(center.x - new_length / 2, center.y - new_length / 2), new_length, main_body);
        }
        else if (main_body.in(quadrate.se())) {
            se = std::make_shared<BHTreeNode>(Cartesian(center.x + new_length / 2, center.y - new_length / 2), new_length, main_body);
        }
        is_leaf = false;
    }
    center_mass = center_mass.plus(b);
    if (b.in(quadrate.nw())) {
        if (nw == nullptr) {
            double new_length = quadrate.length() / 2;
            Cartesian center = quadrate.get_center();
            nw = std::make_shared<BHTreeNode>(Cartesian(center.x - new_length / 2, center.y + new_length / 2), new_length, b);
        }
        else {
            nw->insert(b);
        }
    }
    else if (b.in(quadrate.ne())) {
        if (ne == nullptr) {
            double new_length = quadrate.length() / 2;
            Cartesian center = quadrate.get_center();
            ne = std::make_shared<BHTreeNode>(Cartesian(center.x + new_length / 2, center.y + new_length / 2), new_length, b);
        }
        else {
            ne->insert(b);
        }
    }
    else if (b.in(quadrate.sw())) {
        if (sw == nullptr) {
            double new_length = quadrate.length() / 2;
            Cartesian center = quadrate.get_center();
            sw = std::make_shared<BHTreeNode>(Cartesian(center.x - new_length / 2, center.y - new_length / 2), new_length, b);
        }
        else {
            sw->insert(b);
        }
    }
    else if (b.in(quadrate.se())) {
        if (se == nullptr) {
            double new_length = quadrate.length() / 2;
            Cartesian center = quadrate.get_center();
            se = std::make_shared<BHTreeNode>(Cartesian(center.x + new_length / 2, center.y - new_length / 2), new_length, b);
        }
        else {
            se->insert(b);
        }
    }
}
void BHTreeNode::update_force(Body & b)
{
    if (is_leaf) {
        if (b.get_name() != main_body.get_name()) {
            b.add_force(main_body);
        }
    }
    else {
        double s = quadrate.length() * quadrate.length();
        double d = center_mass.distance(b);
        if (s / d < Theta) {
            b.add_force(center_mass);
        }
        else {
            if (nw != nullptr) {
                nw->update_force(b);
            }
            if (ne != nullptr) {
                ne->update_force(b);
            }
            if (sw != nullptr) {
                sw->update_force(b);
            }
            if (se != nullptr) {
                se->update_force(b);
            }
        }
    }
}

PositionTracker::PositionTracker(const std::string & filename)
{
    std::ifstream file(filename);
    file >> size_universe;
    double x_0, y_0, v_x, v_y, m;
    std::string name;
    while (file >> x_0 >> y_0 >> v_x >> v_y >> m >> name) {
        bodies.emplace_back(Cartesian(x_0, y_0), Cartesian(v_x, v_y), m, name);
    }
    file.close();
}

BasicPositionTracker::BasicPositionTracker(const std::string & filename)
    : PositionTracker(filename)
{
}
Track BasicPositionTracker::track(const std::string & body_name, size_t end_time, size_t time_step)
{
    size_t our_body = 0;
    while (bodies[our_body].get_name() != body_name) {
        ++our_body;
    }
    Track result;
    for (size_t step = 0; step < end_time; step += time_step) {
        for (size_t i = 0; i < bodies.size(); ++i) {
            for (size_t j = 0; j < bodies.size(); ++j) {
                if (i != j) {
                    bodies[i].add_force(bodies[j]);
                }
            }
        }
        for (size_t i = 0; i < bodies.size(); ++i) {
            bodies[i].update(time_step);
            if (our_body == i) {
                result.emplace_back(bodies[i].get_coords());
            }
            bodies[i].reset_force();
        }
    }
    return result;
}

FastPositionTracker::FastPositionTracker(const std::string & filename)
    : PositionTracker(filename)
{
}
Track FastPositionTracker::track(const std::string & body_name, size_t end_time, size_t time_step)
{
    size_t our_body = 0;
    while (bodies[our_body].get_name() != body_name) {
        ++our_body;
    }
    Track result;
    BHTreeNode tree({0, 0}, size_universe, bodies[0]);
    for (size_t i = 1; i < bodies.size(); ++i) {
        tree.insert(bodies[i]);
    }
    for (size_t step = 0; step < end_time; step += time_step) {
        for (size_t i = 0; i < bodies.size(); ++i) {
            tree.update_force(bodies[i]);
        }
        for (size_t i = 0; i < bodies.size(); ++i) {
            bodies[i].update(time_step);
            if (our_body == i) {
                result.emplace_back(bodies[i].get_coords());
            }
            bodies[i].reset_force();
        }
    }
    return result;
}
