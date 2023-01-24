#include <iostream>
#include <fstream>
#include <set>
#include <string>
#include "region.h"

using namespace std;

void makeTestFile( int totalRegions, int totalTests, int baselineTests, char* outfile ) {
  fstream fout;
  fout.open( outfile, fstream::out );
  
  fout << totalRegions << endl;
  // GENERATE RECTANGLES AND SAVE 5 FOR HIT TESTING
  Region* tests[ baselineTests ];
  for( int i = 0; i < totalRegions; i++ ) {
    Region *r = new Region();
    if( i < baselineTests ) tests[i] = r;
    fout << r->getX() << " " << r->getY() << " " << r->getWidth() << " " << r->getHeight() << endl;
  }

  // GENERATE HIT TESTS.  USE 5 KNOWN RECTANGLES AS BASELINE TESTS
  fout << totalTests << endl;
  for( int i = 0; i < totalTests-baselineTests; i++  ) {
    Region *r = new Region();
    fout << r->centerX() << " " << r->centerY() << endl;
  }

  for( int i = 0; i < baselineTests; i++  ) {    
    fout << tests[i]->centerX() << " " << tests[i]->centerY() << endl;
  }

  fout.close();
}

void hitTests( char *filename ) {
  int size;
  set<Region*> regions;
  fstream fin;
  fin.open( filename, fstream::in );

  // READ THE REGIONS INTO A SET
  fin >> size;
  for( int i = 0; i < size; i++ ) {
    int x, y, w, h;
    fin >> x >> y >> w >> h;
    regions.insert( new Region(x,y,w,h) );
  }

  // GENERATE THE HIT-TESTING TREE
  Region *root = reduce( regions );

  // RUN HIT TESTS AGAINST THE TREE
  fin >> size;
  for( int i = 0; i < size; i++ ) {
    int x, y;
    fin >> x >> y;
    Region *hit = (*root)(x,y);
    if( hit != NULL ) {
      cout << "root(" << x << ", " << y << ") => " << *(*root)(x, y) << endl;
    } else {
      cout << "root(" << x << ", " << y << ") => NULL" << endl;
    }
  }

  fin.close();
}

Region* toSVG( char *infile, char *outfile ) {
  set<Region*> regions;
  fstream fin( infile, fstream::in );
  fstream fout( outfile, fstream::out );

  int SIZE;
  fin >> SIZE;
  for( int i = 0; i < SIZE; i++ ) {
    int x, y, w, h;
    fin >> x >> y >> w >> h;
    regions.insert( new Region(x,y,w,h) );
  }
  
  Region *root = reduce( regions );
  
  fout << "<svg viewBox=\"-5 -5 210 210\" xmlns=\"http://www.w3.org/2000/svg\">" << endl;
  fout / *root << endl;
  fout << "</svg>";

  fout.close();
  fin.close();
  return root;
}

void usage() {
  cout << "Usage:" << endl;
  cout << "regional TOTAL_REGIONS TOTAL_TESTS BASELINE_TESTS OUTFILE" << endl;
  cout << "regional INFILE OUTFILE" << endl;
  cout << "regional INFILE" << endl;
}

int main( int argc, char** argv ) {
  srand (time(NULL));

  if( argc == 5 ) {
    int totalRegions = atoi( argv[1] );
    int totalTests = atoi( argv[2] );
    int baselineTests = atoi( argv[3] );

    if( totalTests < baselineTests ) {
      cout << "totalTests must be greater-than-or-equalt-to baselineTests" << endl;
      exit(1);
    }
        
    makeTestFile( totalRegions, totalTests, baselineTests, argv[4] );
    exit(1);
  } else if( argc == 3 ) {
      toSVG( argv[1], argv[2] );
  } else if( argc == 2 ) {
      hitTests( argv[1] );
  } else {
      usage();
  }

}
