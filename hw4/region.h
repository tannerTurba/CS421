#include <set>
#include <iostream>

using namespace std;

class Region {
 private:
  int x, y, width, height;
  Region* r1;
  Region* r2;

  bool contains( int x_in, int y_in );
  
 public:
  static const int PAGE_DIMENSION = 200;
  static const int DEFAULT_MAX_SIZE = 15;
  
  Region( int x, int y, int width, int height, Region* r1,  Region* r2 );
  Region( int x, int y, int w, int h );
  Region();
  
  int isLeaf() const;
  int centerX( ) const;
  int centerY() const;
  int area() const;
  int getX() const;
  int getY() const;
  int getWidth() const;
  int getHeight() const;
  Region* getR1() const;
  Region* getR2() const;
  
  bool operator<( Region& other );
  Region* operator()( int x_in, int y_in);
  Region& operator=( Region& rhs);
  Region* operator,( Region& other ); 
  double distance( Region& rhs );

  // THESE FUNCTIONS ARE NOT CLASS MEMBERS
  // THEY ARE DEFINED OUTSIDE OF THE CLASS
  // BUT REQUIRE ACCESS TO PRIVATE FIELDS
  friend ostream& operator<<(ostream&, Region&);
  friend ostream& operator/(ostream&, Region&);
  friend Region* reduce( set<Region*>&  );
};
