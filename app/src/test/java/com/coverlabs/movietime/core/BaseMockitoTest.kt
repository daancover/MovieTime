package com.coverlabs.movietime.core

import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule


@RunWith(JUnit4::class)
abstract class BaseMockitoTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Before
    open fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    fun <T> any(): T = Mockito.any<T>()
}