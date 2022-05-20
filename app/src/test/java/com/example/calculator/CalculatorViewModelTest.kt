package com.example.calculator

import com.example.calculator.model.number.NumberKind
import com.example.calculator.model.operator.OperatorKind
import com.example.calculator.viewmodel.CalculatorViewModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class CalculatorViewModelTest {
    companion object {
        private lateinit var viewModel: CalculatorViewModel


        @BeforeAll
        @JvmStatic
        fun setUp() {
            viewModel = CalculatorViewModel()
        }
    }


//    @Test
//    fun `test viewModel add function with`
}