#include <iostream>
#include <algorithm>
#include <math.h>
#include <set>
#include <limits.h>
#include "region.h"

using namespace std;

Region::Region(int x, int y, int width, int height, Region* r1,  Region* r2 ) {
    this->x = x;
    this->y = y;
    this->width = width;
    this->height = height;
    this->r1 = r1;
    this->r2 = r2;
}

Region::Region( int x, int y, int w, int h ) {
    this->x = x;
    this->y = y;
    this->width = w;
    this->height = h;
    this->r1 = NULL;
    this->r2 = NULL;
}

Region::Region() {
    this->x = rand() % PAGE_DIMENSION;
    this->y = rand() % PAGE_DIMENSION;
    this->width = rand() % DEFAULT_MAX_SIZE + 1;
    this->height = rand() % DEFAULT_MAX_SIZE + 1;
    this->r1 = NULL;
    this->r2 = NULL;
}

int Region::isLeaf() const {
    return r1 == NULL && r2 == NULL;
}

int Region::centerX() const {
    return x + (width / 2);
}

int Region::centerY() const {
    return y + (height / 2);
}

int Region::area() const {
    return x * y;
}

int Region::getX() const {
    return x;
}

int Region::getY() const {
    return y;
}

int Region::getWidth() const {
    return width;
}

int Region::getHeight() const {
    return height;
}

Region* Region::getR1() const {
    return r1;
}

Region* Region::getR2() const {
    return r2;
}

bool Region::contains(int x_in, int y_in) {
    bool withinX = x <= x_in && x_in <= x + width;
    bool withinY = y <= y_in && y_in <= y + height;
    return withinX && withinY;
}

bool Region::operator<(Region& other) {
    return area() < other.area();
}

Region* Region::operator()(int x_in, int y_in) {
    if(isLeaf()) {
        //point contained in inner-most region, so return self.
        return this;
    }
    else if(!this->contains(x_in, y_in)) {
        //point does not exist inside region, return null.
        return NULL;
    }
    else if(r1->contains(x_in, y_in)) {
        //point exists in R1, go there.
        return (*r1)(x_in, y_in);
    }
    else if(r2->contains(x_in, y_in)) {
        //point exists in R2, go there.
        return (*r2)(x_in, y_in);
    }
    else {
        //point exists in the current region, but not any deeper. Return self. 
        return this;
    }
}

Region& Region::operator=( Region& rhs) {
    this->x = rhs.x;
    this->y = rhs.y;
    this->width = rhs.width;
    this->height = rhs.height;
    this->r1 = rhs.r1;
    this->r2 = rhs.r2;
    return *this;
}

Region* Region::operator,( Region& other ) {
    //Set the upper and lower bounds on x and y axis.
    int xLower, xUpper, yLower, yUpper;
    if(x <= other.x) {
        xLower = x;
    }
    else {
        xLower = other.x;
    }

    if(y <= other.y) {
        yLower = y;
    }
    else {
        yLower = other.y;
    }

    if((x + width) <= (other.x + other.width)) {
        xUpper = other.x + other.width;
    }
    else {
        xUpper = x + width;
    }

    if((y + height) <= (other.y + other.height)) {
        yUpper = other.y + other.height;
    }
    else {
        yUpper = y + height;
    }

    //Create and return pointer to the new region.
    return new Region(xLower, yLower, xUpper - xLower, yUpper - yLower, this, &other);
}

double Region::distance( Region& rhs ) {
    // sqrt(a^2 + b^2) = c^2
    int xDiff = abs(centerX() - rhs.centerX());
    int yDiff = abs(centerY() - rhs.centerY());
    return sqrt(pow(xDiff, 2) + pow(yDiff, 2));
}

// THESE FUNCTIONS ARE NOT CLASS MEMBERS
// THEY ARE DEFINED OUTSIDE OF THE CLASS
// BUT REQUIRE ACCESS TO PRIVATE FIELDS
ostream& operator<<(ostream& stream, Region& reg) {
    return stream << "[ " << reg.getX() << ", " << reg.getY() << ", " << reg.getWidth() << ", " << reg.getHeight() << " ]";
}

ostream& operator/(ostream& stream, Region& reg) {
    if(reg.isLeaf()) {
        stream << "<rect x=\"" << reg.getX() << "\" y=\"" << reg.getY() << "\" width=\"" << reg.getWidth() << "\" height=\"" << reg.getHeight() << "\" style=\"";
        return stream << "fill:blue;stroke:black;stroke-width:.05;fill-opacity:.1;stroke-opacity:.9\" />" << endl;
    }
    else {
        //Visit children before returning.
        stream/(*reg.getR1());
        stream/(*reg.getR2());
        stream << "<rect x=\"" << reg.getX() << "\" y=\"" << reg.getY() << "\" width=\"" << reg.getWidth() << "\" height=\"" << reg.getHeight() << "\" style=\"";
        return stream << "fill:yellow;stroke:black;stroke-width:.05;fill-opacity:.0;stroke-opacity:.5\" />" << endl;
    }
}

Region* reduce( set<Region*>& regions ) {
    set<Region*>& S = regions;
    //While the set S is not empty.
    while(S.size() > 1) {
        int minimalDistance = INT_MAX;
        Region *A = NULL;
        Region *B = NULL;

        //Find the distance between each permutation of sets.
        std::set<Region*>::iterator regionsIterator = S.begin();
        while( regionsIterator != S.end() ) {
            Region *current = *regionsIterator;
            std::set<Region*>::iterator innerRegionsIterator = regionsIterator;
            innerRegionsIterator++;
            while( innerRegionsIterator != S.end() ) {
                Region *innerCurrent = *innerRegionsIterator;

                //Keep the pair with the smallest distance.
                int distance = current->distance(*innerCurrent);
                if(distance < minimalDistance) {
                    minimalDistance = distance;
                    A = current;
                    B = innerCurrent;
                }
                else if(distance == minimalDistance) {
                    if(A->area() + B->area() > current->area() + innerCurrent->area()) {
                        A = current;
                        B = innerCurrent;
                    }
                }
                innerRegionsIterator++;
            }
            regionsIterator++;
        }

        //Remove A and B from the set S, combine them, and add to set S.
        S.erase(A);
        S.erase(B);
        Region *C = (*A,*B);
        S.insert(C);
    }
    return *S.begin();
}