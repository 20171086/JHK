package com.example.test5;

import android.app.Activity;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;

@LargeTest
public class MainActivityTest extends TestCase {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);

}