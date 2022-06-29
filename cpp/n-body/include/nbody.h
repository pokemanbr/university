#include <iostream>
#include <memory>
#include <string_view>
#include <vector>

struct Cartesian
{
    Cartesian(double _x, double _y);

    Cartesian operator+(const Cartesian & coord) const;
    Cartesian operator-(const Cartesian & coord) const;

    Cartesian & operator+=(const Cartesian & coord);
    Cartesian & operator-=(const Cartesian & coord);

    Cartesian operator*(double scalar) const;
    Cartesian operator/(double scalar) const;

    Cartesian & operator*=(double scalar);
    Cartesian & operator/=(double scalar);

    Cartesian operator-() const;

    static Cartesian abs(const Cartesian & coord);

    double x;
    double y;
};

const double G = 6.67 * 1e-11;

class Quadrant;

// Single body representation, required for Problem 1 and Problem 2
class Body
{
public:
    Body(Cartesian pos, Cartesian vel, double m, std::string _name);

    double distance(const Body & b) const;

    // calculate the force-on current body by the 'b' and add the value to accumulated force value
    void add_force(const Body & b);
    // reset accumulated force value
    void reset_force();

    // update body's velocity and position
    void update(double delta_t);

    friend std::ostream & operator<<(std::ostream &, const Body &);

    // The methods below to be done for Burnes-Hut algorithm only
    // Test if body is in quadrant
    bool in(const Quadrant q) const;
    // Create new body representing center-of-mass of the invoking body and 'b'
    Body plus(const Body & b);

    Cartesian get_coords() const;

    std::string_view get_name() const;

private:
    Cartesian coord;
    Cartesian velocity;
    double mass;
    std::string name;
    Cartesian force = Cartesian(0, 0);
};

// Quadrant representation, required for Problem 2
class Quadrant
{
public:
    // Create quadrant with center (x, y) and size 'length'
    Quadrant(Cartesian center, double length);

    // Test if point (x, y) is in the quadrant
    bool contains(Cartesian p) const;
    double length() const;

    Cartesian get_center();

    // The four methods below construct new Quadrant representing sub-quadrant of the invoking quadrant
    Quadrant nw() const;
    Quadrant ne() const;
    Quadrant sw() const;
    Quadrant se() const;
    // nw||ne
    // ======
    // sw||se

    Cartesian get_lower_left_corner() const;
    Cartesian get_upper_right_corner() const;

    friend std::ostream & operator<<(std::ostream &, const Quadrant &);

private:
    Cartesian center;
    double size;
};

const double Theta = 0.5;

// Burnes-Hut tree representation, required for Problem 2
class BHTreeNode
{
public:
    BHTreeNode(Cartesian center, double size, const Body & body);

    void insert(const Body & b);
    // Update net acting force-on 'b'
    void update_force(Body & b);

private:
    Quadrant quadrate;
    Body main_body;
    Body center_mass;
    bool is_leaf = true;
    std::shared_ptr<BHTreeNode> nw = nullptr;
    std::shared_ptr<BHTreeNode> ne = nullptr;
    std::shared_ptr<BHTreeNode> sw = nullptr;
    std::shared_ptr<BHTreeNode> se = nullptr;
};

using Track = std::vector<Cartesian>;

class PositionTracker
{
protected:
    PositionTracker(const std::string & filename);

    virtual ~PositionTracker() = default;

    double size_universe;
    std::vector<Body> bodies;

public:
    virtual Track track(const std::string & body_name, size_t end_time, size_t time_step) = 0;
};

class BasicPositionTracker : public PositionTracker
{
public:
    BasicPositionTracker(const std::string & filename);
    ~BasicPositionTracker() = default;
    Track track(const std::string & body_name, size_t end_time, size_t time_step) override;
};

class FastPositionTracker : public PositionTracker
{
public:
    FastPositionTracker(const std::string & filename);
    ~FastPositionTracker() = default;
    Track track(const std::string & body_name, size_t end_time, size_t time_step) override;
};
