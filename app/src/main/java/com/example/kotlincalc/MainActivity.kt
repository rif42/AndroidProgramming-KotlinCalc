package com.example.kotlincalc

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()
{
    private var canAddOperation = false
    private var canAddDecimal = true


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadData()
    }

    fun testappend(arr: MutableList<String>, element: String): MutableList<String> {
        val list: MutableList<String> = arr.toMutableList()
        list.add(element)
        return list
    }

    var historyresult = arrayOf("").toMutableList()

    fun numberAction(view: View)
    {
        if (view is Button)
        {
            if (view.text == ".")
            {
                if (canAddDecimal)
                    workingsTV.append(view.text)
                canAddDecimal = false
            }
            else
                workingsTV.append(view.text)
            canAddOperation = true
        }
    }

    fun operationAction(view: View)
    {
        if (view is Button && canAddOperation)
        {
            workingsTV.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun delHistory(view: View)
    {
        historyresult.clear()
        historyresult = testappend(historyresult, "")
        history.text = null
        history2.text = null
        history3.text = null
        history4.text = null
        history5.text = null
    }

    fun allClearAction(view: View)
    {
        workingsTV.text = ""
        resultsTV.text = ""
    }

    fun backSpaceAction(view: View)
    {
        val length = workingsTV.length()
        if (length > 0)
            workingsTV.text = workingsTV.text.subSequence(0, length - 1)
    }

    fun equalsAction(view: View)
    {
        resultsTV.text = calculateResults()
    }

    @SuppressLint("CommitPrefEdits")
    private fun calculateResults() : String
    {
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if (timesDivision.isEmpty()) return ""

        val result = addSubtractCalculate(timesDivision)

        val calcHistory = result.toString()

        val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("STRING_KEY", calcHistory)
        }.apply()

        historyresult = testappend(historyresult, result.toString())
        history.text = historyresult[1]

        if (historyresult.size > 2 ) {
            history2.text = historyresult[2]
        }

        if (historyresult.size > 3 ) {
            history3.text = historyresult[3]
        }

        if (historyresult.size > 4 ) {
            history4.text = historyresult[4]
        }

        if (historyresult.size > 5 ) {
            Toast.makeText(this, "history full! press H to delete history", Toast.LENGTH_SHORT).show()
            history5.text = historyresult[5]
        }

        return result.toString()
    }


    private fun loadData()
    {
        val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedString: String? = sharedPreferences.getString("STRING_KEY", null)
//        history.text = savedString
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float
    {
        var result = passedList[0] as Float

        for (i in passedList.indices)
        {
            if (passedList[i] is Char && i != passedList.lastIndex)
            {
                val operator = passedList[i]
                val nextDigit = passedList[i+1] as Float
                if (operator == '+')
                {
                    result += nextDigit
                }

                if (operator == '-')
                {
                    result -= nextDigit
                }
            }
        }
        return result
    }

    private fun timesDivisionCalculate (passedList: MutableList<Any>): MutableList<Any>
    {
        var list = passedList
        while (list.contains('/') || list.contains('x'))
        {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv (passedList: MutableList<Any>): MutableList<Any>
    {
        val newList = mutableListOf<Any>()

        var restartIndex = passedList.size

        for(i in passedList.indices)
        {
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex)
            {
                val operator = passedList[i]
                val prevDigit = passedList[i-1] as Float
                val nextDigit = passedList[i+1] as Float
                when (operator)
                {
                    'x' ->
                    {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i+1
                    }
                    '/' ->
                    {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i+1

                    }
                    else ->
                    {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if (i > restartIndex)
                newList.add(passedList[i])
        }

        return newList
    }

    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in workingsTV.text)
        {
            if(character.isDigit() || character == '.')
                currentDigit += character
            else
            {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }
        if (currentDigit != "")
            list.add(currentDigit.toFloat())
        return list
    }

}

