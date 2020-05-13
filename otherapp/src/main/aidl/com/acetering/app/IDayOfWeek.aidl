// IDayOfWeek.aidl
package com.acetering.app;

// Declare any non-default types here with import statements

interface IDayOfWeek {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int getWeekday(int year,int month,int day);
}
