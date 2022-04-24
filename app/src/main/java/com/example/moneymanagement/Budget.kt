package com.example.termproject_a

import java.math.RoundingMode
import java.text.DecimalFormat

/*
* DESCRIPTION: Budget is made up of a group of BudgetSection objects stored in sections Set. Class BudgetSection is defined
*   as an inner class of the Budget class (see class definition for details on its function)
*   Provides methods to add/remove sections and to calculate information about the budget as a whole.
* CONSTRUCTORS: primary and secondary both accept 1 param since all budgets must have an income
*   Primary: accepts income:Double
*   Secondary: accepts income:Int then converts it to a double and passes it to the primary
*       In almost all cases income will be an Int but making int the secondary allows the class to handling both int and double type incomes
*       and converting int -> double doesn't have the issue of cutting the decimal the way double -> int would
*   init block: executed regardless of which constructor is used. uses the income param from which ever constructor is called
*       and uses it and the defaults Map to create a BudgetSection obj for each section in defaults and stores then in
*       the sections Set
* CLASS GLOBALS:
*   income: Double - user input passed into constructor with income for the budget. will be used to calculate amounts
*       and info on being over/under budget
*   sections: mutableSetOf<BudgetSections objects> - a mutable set storing the BudgetSection objects belonging to the Budget
*   defaults: mapOf<String, Double> - PRIVATE CONSTANT it is only used internally to gen a default budget so there's no reason for it to be accessible anywhere else
*       a default budget structure to used to generate default BudgetSection objects to create a default budget
*       structure of Map: ["section name" -> percent as a decimal that the section should take up of the whole budget]
* METHODS: more detailed information at method declarations
*   addSection  - adds a new section to the budget
*   removeSection - removes a specified section from the budget
*   getFreeAmount - returns the $ amount of income that is unallocated in the budget (negative if over budget)
*   getFreePercentage - return the % amount of income that is unallocated in the budget (negative if over budget)
* */
class Budget(var income: Double)
{
    //income will usually be an Int but having the primary be a Double accounts for the few times it isn't
    constructor(income : Int) : this(income.toDouble())

    //unique list to hold the BudgetSection objects
    var sections = mutableSetOf<BudgetSection>()

    //default budget sections and percentages
    private val defaults = mapOf<String, Double>(
        "savings"           to 0.10,
        "housing"           to 0.30,
        "utilities"         to 0.05,
        "transportation"    to 0.10,
        "food"              to 0.20,
        "entertainment"     to 0.05,
        "medical"           to 0.10,
        "other"             to 0.10
    )

    //create default budget on object creation
    init
    {
        /*
        *  get default section names and percentages and add new BudgetSection objects to sections set
        *  calc amount from default percentages and income param passed to constructor upon object creation
        * */
        defaults.forEach{ (section, percentage) ->
            sections.add(BudgetSection(section, percentage, percentage * income))
        }
    }

    /*
    * adds a new section to the budget - checks that budget section does not already exist and throws an
    *   exception if it does. new section can either be created with a name and the percentage of the budget it should be
    *   or created with a name and the $ amount of the budget it should be. whichever param isn't passed will be calculated
    *   from the one that was. Data is then passed to a BudgetSection constructor and the resulting object stored in 'sections'
    *
    * percentage and amount are optional params and will default to 0.0
    * if nothing is passed - use named argument annotation when leaving out optional params both for clarity and
    * to allow the optional functionality of params regardless of their position ie passing name and amount params but
    * not percentage addSection(name, amount = 4200)
    *
    * GLOBALS:
    *   income  - the $ amount of income used to calculate the budget
    *   sections - Set that stores the BudgetSection objects of the Budget
    *
    * @param name the name of the section - must be unique and not match the name of any other sections name
    * @param percentage the percentage the budget the section should be equal to - optional and if not passed will
    *   be calculated from amount param
    * @param amount the dollar amount for the budget section - optional and if not passed will be calculated from
    *   the percentage param
    *
    * PRECONDITIONS:
    *   if an amount is passed it must be validated beforehand - should be non-null positive number
    *   if a percent is passed it must be validated beforehand - should be non-null decimal [0-1]
    *   the 'name' != a 'name' property in an existing BudgetSection object
    * POSTCONDITIONS: a new BudgetSection object has been created and added to the sections Set of BudgetSections for the Budget
    *
    * @exception IllegalArgumentException thrown when budget name already exists in another BudgetSection object
    * */
    fun addSection(name: String, percentage: Double = 0.0, amount: Double = 0.0)
    {
        //section names should be unique - throws exception if .none equates to false (param section is already a name property)
        require(sections.none{ it.name == name }){"section $name already exists in this budget"}

        //really either percentage or amount should be passed but in the unfortunate case that both
        // are passed throw an exception if they do not equal the same amount of income
        if(percentage != 0.0 && amount != 0.0 )
        {
            require(percentage * income == amount){"${percentage * 100}% of budget is [${percentage * income}] " +
                                                         "but amount passed is $amount if both arguments are passed calculated amounts must be equal"}
        }

        //if percentage param isn't passed calc from amount param and vice versa - both are optional params so results can still be zero for both
        val sectionPercentage = if (percentage == 0.0) amount / income else percentage
        val sectionAmount = if (amount == 0.0) percentage * income else amount

        sections.add(BudgetSection(name, sectionPercentage, sectionAmount))
    }


    /*
    * removes the section whose 'name' property matches the 'sectionName' param.
    *
    * GLOBALS:
    *   sections - Set that stores the BudgetSection objects of the Budget
    *
    * PRECONDITIONS:  sectionName must correspond to the 'name' property of one of the budgets BudgetSection objects
    * POSTCONDITIONS: sections Set no longer contains specified BudgetSection budget
    *
    * @param sectionName - the 'name' of the budget section that should be removed
    * @return true if the section is successfully remove and false if getSection fails (throws an exception) to find the section to be removed
    * */
    fun removeSection(sectionName: String): Boolean
    {
        var rc = true
        try
        {
            //getSection throws IllegalArg if it can't find section
            sections.remove(getSection(sectionName))
        }
        catch (e: IllegalArgumentException)
        {
            rc = false
        }

        return rc
    }

    /*
     * returns BudgetSection object or throws exception if section name passed does not match the name
     * property of any BudgetSection object
     *
     * GLOBALS:
     *   sections - Set that stores the BudgetSection objects of the Budget
     *
     * @param sectionName the name of the budget section to be returned
     *
     * PRECONDITIONS: sectionName must correspond to the 'name' property of one of the budgets BudgetSection objects
     * POSTCONDITIONS: a BudgetSection having a 'name' property matching the 'sectionName' param has been returned
     *
     * @return the BudgetSection object with a 'name' property equaling the section name passed
     * @exception IllegalArgumentException thrown for 2 reasons:
     *  1: When no BudgetSection objects name property matches the section name param
     *  2: If for any reason the BudgetSection cannot be found - this should never be possible, an exception
     *     would already be thrown at this point because of reason 1.
    **/
    fun getSection(sectionName: String): BudgetSection
    {
        /* requires .any to evaluate to true
         * (sectionName must be match to a name property of one of the BudgetSection objects)
         *  if no exception is thrown the requireNotNull should never be able to fail but including it
         *  allows the returned BudgetSection to be used without null-safety checks
        * */
        require(sections.any{ it.name == sectionName }) {"Section [$sectionName] is not a valid section name for this budget"}
        return requireNotNull(sections.find{ it.name == sectionName })
    }


    /*
    * adds up the $ amounts for each section in the budget, subtracts that from the income and returns the result
    *
    * GLOBALS:
    *   income  - the $ amount of income used to calculate the budget
    *   sections - Set that stores the BudgetSection objects of the Budget
    * PRECONDITIONS: none
    * POSTCONDITIONS: amount of unallocated income has been returned
    *
    * @return the amount of income that is unallocated to any part of the budget rounded to 2 decimal places
    *   (a negative number if the budget uses >100% of income)
    * */
    fun getFreeAmount(): Double
    {
        var total = 0.0
        //iterate through sections to get a total of $ amount of the sections
        for (section in sections){ total += section.amount }
        return round((income - total), 2)
    }


    /*
    * adds up the percentage of the income allocated to each section in the budget, subtracts that from 1 and returns the result
    *
    * GLOBALS:
    *   income  - the $ amount of income used to calculate the budget
    *   sections - Set that stores the BudgetSection objects of the Budget
    * PRECONDITIONS: none
    * POSTCONDITIONS: the percentage (decimal form) of income not allocated to any part of the budget has been returned
    *
    * @return the percentage (decimal form to 4 places) of income that is unallocated to any part of the budget
    *   (a negative number if the budget uses >100% of income)
    * */
    fun getFreePercentage(): Double
    {
        var total = 0.0
        //iterate through sections to sum up the total % of the income all the sections are equal to
        for (section in sections){ total += section.percentage }
        return round((1 - total), 4)
    }

    /*
    * checks if the budget is using 100% of income - based on percentage using getFreePercentage()
    * returns Boolean based on:
    *    getFreePercentage returns 1 - total of budget section percentages
    *    returns a negative when space is exceeded
    *    100% full return (0)     120% full return (-0.2)
    *
    * PRECONDITIONS: none
    * POSTCONDITIONS: a Boolean value will have been returned based on whether or not the the budget is using less than 100% of the income
    *
    * @return true if getFreePercentage is greater than 0 ie budget does not use 100% of income
    *   and returns false if getFreePercentage is zero or negative ie budget is using 100%
    *   or over 100% of income
    * */
    fun hasEmptySpace(): Boolean
    {
        return (getFreePercentage() > 0)
    }


    override fun toString(): String
    {
        var budget = ""

        sections.forEach { sectionObj ->
            budget += "\nSection: ${sectionObj.name}\n" +
                      "\tPercentage: ${sectionObj.percentage}\n" +
                      "\tAmount: ${sectionObj.amount}\n"
        }
        return budget
    }

    //defined as 'inner' because it needs access to some of the Budget class' properties and methods
    inner class BudgetSection(var name: String, percentage: Double, amount: Double)
    {

        /* percentage and amount need custom setters and so cannot be explicitly declared in constructor
         * when one is set the other must be updated - tests for value difference to avoid infinite ping pong of doom
        ** change name to lowercase so that 'savings' and 'Savings' will be recognized as the same name - ca
        */
        var percentage: Double = percentage
            set(value)
            {
                field = value
                val newAmount = field * income
                //if current amount differs from amount calculated by new percentage also set amount
                if(newAmount != amount) amount = newAmount
            }
            get() = round(field, 4)

        var amount: Double = amount
            set(value)
            {
                field = value
                val newPercentage = field / income
                //if current percentage differs from percentage calculated by new amount also set percentage
                if(newPercentage != percentage) percentage = newPercentage
            }
            get() = round(field, 2)

        fun getSectionData(): List<String>
        {
            return listOf(name, percentage.toString(), amount.toString())
        }

        //YOOOOOOOOO i did not know you could do this?!?!?!
        //returns the instance of the Budget object the section it is called on belongs to
        /*EX:  Budget1 has sections sectionA and sectionB while Budget2 has sectionC
         *  sectionA.getBudgetObj() will return Budget1
         *  sectionB.getBudgetObj() will return Budget1
         *  sectionC.getBudgetObj() will return Budget2
         */
        fun getBudgetObj(): Budget
        {
            return this@Budget
        }

        override fun toString(): String {
            var section = ""
            section += "\nSection: $name\n" +
                    "\tPercentage: $percentage\n" +
                    "\tAmount: $amount\n"
            return section
        }

    }//end of inner class BudgetSection
}//end of Budget class

//I didn't want to fuck with BigDecimal so here's this instead
fun round(decimal: Double, scale: Int = 0): Double
{
    //sets number of decimal points based on scale param
    val df: DecimalFormat = DecimalFormat("#.${"#".repeat(scale)}")
    df.roundingMode = RoundingMode.HALF_UP
    return df.format(decimal).toDouble()
}