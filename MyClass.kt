package com.example.libkotlin

import kotlin.math.absoluteValue

fun main() {
    var groupSize: String

    println("Hello there.")
    //get a valid number of group members - valid whole number less than or equal to 5
    do
    {
        var validInput = false

        println("How many members are in your group?")
        groupSize = readLine().toString()

        when
        {
            groupSize.toIntOrNull() == null -> println("You must enter a valid whole number, please try again.")
            groupSize.toInt() > 5  -> println("That's too many members, one of them will have to go.")
            groupSize.toInt() < 1  -> println("You must have at least one member in your group.")
            else -> validInput = true
        }
    }while (!validInput)

    //get information for each group member
    val group = HashMap<Int, MutableList<String>>()

    //add template data - also used for table headers in printing
    group.put(0, mutableListOf("Name", "Age", "Course"))
    getMemberInfo(groupSize.toInt(), group)

    //ask user if they would like to see the group table
    print("Would you like to see the Group Table? (Y/N)  ")
    var showOutput: String = readLine().toString()
    while ( ! (Regex("Y|YES|N|NO").containsMatchIn(showOutput.uppercase()) ) )
    {
        print("Sorry you must enter either Y or N: ")
        showOutput = readLine().toString()
    }
    if (showOutput.uppercase() == "Y" || showOutput.uppercase() == "YES")
    {
        displayTable(group)
    }
    else
    {
        println("Bye.")
    }
}

fun getMemberInfo(numMembers: Int, group: HashMap<Int, MutableList<String>>)
{
    for (i in 0 until numMembers)
    {
        val groupNum = i + 1
        println("Please enter information for member number $groupNum in your group")

        //<group number, ref to MutableList with info for group member>
        group.put(groupNum, mutableListOf())

        var validInput = false

        //get valid name and store in group
        do
        {
            print("Name: ")
            val name = readLine().toString()

            if (name.isEmpty())
            {
                println("You must enter a name\n")
            }
            else
            {
                validInput = true
                group.get(groupNum)?.add(name)
            }

        }while (!validInput)

        //get valid age and store in group
        do
        {
            validInput = false

            print("Age: ")
            val age = readLine().toString()

            if (age.toIntOrNull() == null)
            {
                println("no silly, you have to enter a number!")
            }
            else if (age.toInt() < 0)
            {
                println("a negative number is not a valid age.")
            }
            else
            {
                validInput = true
                group.get(groupNum)?.add(age)
            }

        }while (!validInput)

        //get valid course and store in group
        do
        {
            validInput = false
            print("Course: ")
            val course = readLine().toString()

            if (course.isEmpty())
            {
                println("You must enter a course\n")
            }
            else
            {
                validInput = true
                group.get(groupNum)?.add(course)
            }
        }while (!validInput)
    }//For loop end
}//function end

fun displayTable(group: HashMap<Int, MutableList<String>>)
{
    for (i in (0 until  group.get(0)!!.size))
    {
        for (j in 0 until  group.size)
        {
            val curr = group.get(j)?.getOrElse(index = i, defaultValue = {"empty"})
            var padSpace = ""   //spacing will be even unless the field is very long
            padSpace = padSpace.padEnd(length = (20 - curr!!.length).absoluteValue)
            print("\t[$curr]$padSpace")
        }
        println()
    }
}