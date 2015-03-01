#include "../CellPossibleValues.h"
#include "CellPossibleValuesTest.h"
#include <string.h>


CPPUNIT_TEST_SUITE_REGISTRATION(CellPossibleValuesTest);

CellPossibleValuesTest::CellPossibleValuesTest() {
}

CellPossibleValuesTest::~CellPossibleValuesTest() {
}

void CellPossibleValuesTest::setUp() {
}

void CellPossibleValuesTest::tearDown() {
}

void CellPossibleValuesTest::last_value_throws_logical_error() {

    CellPossibleValues cpv;
    try {
        cpv.last_value();
    } catch (const std::logic_error& error) {
        CPPUNIT_ASSERT(strcmp("Not solved yet!", error.what()) == 0);
        return;
    }
    CPPUNIT_FAIL("Should have thrown logical_error!");
}

void CellPossibleValuesTest::remove_8_times_causes_cell_to_be_solved() {
    CellPossibleValues cpv;
    for (unsigned short i = 1; i < 8; ++i) {
        CPPUNIT_ASSERT_MESSAGE("Should have been able to remove!", cpv.remove(i));
        CPPUNIT_ASSERT_MESSAGE("Should NOT have been solved!", !cpv.solved());
    }
    CPPUNIT_ASSERT_MESSAGE("Should have been able to remove!", cpv.remove(9));
    CPPUNIT_ASSERT_MESSAGE("Should have been solved!", cpv.solved());
    unsigned short actual = cpv.last_value();
    unsigned short expected = 8;
    CPPUNIT_ASSERT_EQUAL_MESSAGE("The last value should have been 8!",
            actual, expected);


}

void CellPossibleValuesTest::remove_9_times_throws_logical_error() {
    CellPossibleValues cpv;
    for (unsigned short i = 1; i < 9; ++i) {
        cpv.remove(i);        
    }
    CPPUNIT_ASSERT_THROW_MESSAGE("Should have thrown logic_error!",
            cpv.remove(9),logic_error);
    
}

void CellPossibleValuesTest::remove_with_invalid_args_throws_invalid_arg() {
    CellPossibleValues cpv;
    CPPUNIT_ASSERT_THROW_MESSAGE("Should have rejected -1 as input!",
            cpv.remove(-1), invalid_argument);
    CPPUNIT_ASSERT_THROW_MESSAGE("Should have rejected 10 as input!",
            cpv.remove(10), invalid_argument);
}