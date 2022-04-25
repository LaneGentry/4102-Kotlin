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
*   toString - overridden to print out all sections with all their data (just there to make testing easier)
*
* AUTHOR: Lauren Stewart
* LAST MODIFIED: 4/21/22
* */
class Budget(val income: Double)
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
    *   if an amount is passed it must be validated beforehand - should be non-null positive number > 0
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
         * (sectionName must be matched to the name property of one of the BudgetSection objects)
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
    *    getFreePercentage returns (1 - total of budget section percentages)
    *    returns a negative number when totaled sections take up > 100% of the income for the budget
    *    100% full getFreePercentage returns (0)     120% full getFreePercentage returns (-0.2)
    *
    * PRECONDITIONS: getFreePercentage must return a valid number
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


    /*
    * GLOBALS:
    *   income  - the $ amount of income used to calculate the budget
    *   sections - Set that stores the BudgetSection objects of the Budget
    *
    * Provides and easy way to get a clear visual representation of the budget object to make testing easier
    *
    * PRECONDITIONS: income and sections globals must be initialized
    * POSTCONDITIONS: a formatted string containing information about the budget and the data for each section of the budget has been returned
    * @return a formatted string containing the income for the budget and the 'name', 'percentage', and 'amount'
    *   properties for each section (BudgetSection obj) in the budget
    * */
    override fun toString(): String
    {
        var budget = "\nIncome: $income\n" +
                        "Empty Space: ${this.hasEmptySpace()}\n" +
                        "Unused %: ${this.getFreePercentage()}\n" +
                        "Unused $: ${this.getFreeAmount()}\n"

        //loops through sections Set, with each element temp assigned to sectionObj
        sections.forEach { sectionObj ->
            budget += "\nSection: ${sectionObj.name}\n" +
                      "\tPercentage: ${sectionObj.percentage}\n" +
                      "\tAmount: ${sectionObj.amount}\n"
        }
        return budget
    }

    /*
    * DESCRIPTION: used to create objects to represent each section of the budget. each section should have
    *   a name (EX: "savings", "utilities", etc), the percentage of the budgets income allocated to the section,
    *   and the $ amount of the income allocated to the section. Inner class definition gives access to instance globals
    *   needed to calculate percentages/amounts such as income and insures that each BudgetSection is associated with a Budget obj.
    *   NOTE: a sections percentage and amount are linked in that if the budget has an income of $100
    *       if percentage = 0.3 amount should be amount = 30 likewise if amount = 45 percentage should
    *       be percentage = 0.45 Custom setters are defined for both to account for this. When one property is set
    *       its new value is used to calculate and set a new value for the other (see properties explicit declarations)
    *
    * CONSTRUCTOR(S): single constructor, required param for the name of the section. percentage and amount params are optional (defaults to 0)
    *   but both amount and percentage should not be passed, only one of the two.
    *   NOTE: as an inner class its constructor can only be called with receiver of containing class (Budget)
    * VARIABLES:
    *   income -  INSTANCE GLOBAL of containing Budget class holding income for the budget instance
    *   name - string holding the name of the section. Used in Budget instance methods to retrieve a particular BudgetSection obj
    *       (therefore all sections in a particular budget instance must have unique name)
    *   percentage - double holding the percentage (as a decimal) of the budget instance's income allocated to the instance of the BudgetSection
    *   amount - double holding the amount of the budget instance's income allocated to the instance of the BudgetSection
    * METHODS:
    *   getSectionData - returns a list with name, percentage, and amount of the calling instance
    *   getBudgetObj - returns the instance of the containing class (Budget) that the calling instance (BudgetSection) belongs to
    *   toString - overridden to print data for the instance (just there to make testing easier)
    *
    * */
    inner class BudgetSection(var name: String, percentage: Double = 0.0, amount: Double = 0.0)
    {

        /* percentage and amount need custom setters and so needs to be explicitly declared here instead of the primary constructor
         * when one is set the other must be updated - when each is set it calculates what the new value of the other should be
         * tests for value difference between current property value and the calculated one based on the new value
         * this avoids an infinite ping pong of doom between the two setters
        */
        var percentage: Double = percentage
            set(value)
            {
                field = value
                val newAmount = field * income
                //if current amount differs from amount calculated by new percentage also set amount
                if(newAmount != amount) {amount = newAmount}
            }
            get() = round(field, 4) //rounds percentage to 4 decimal places

        var amount: Double = amount
            set(value)
            {
                field = value
                val newPercentage = field / income
                //if current percentage differs from percentage calculated by new amount also set percentage
                if(newPercentage != percentage) percentage = newPercentage
            }
            get() = round(field, 2) //rounds amount to 2 decimal places

        /*
        * returns a list with name, percentage, and amount of the calling instance
        * PRECONDITIONS: instance properties must be non null
        * POSTCONDITIONS: a list with the sections properties has been returned
        * @return List of strings containing the data for the BudgetSection instance
        * */
        fun getSectionData(): List<String>
        {
            return listOf(name, percentage.toString(), amount.toString())
        }

        /*
         * returns the instance of the Budget object the calling BudgetSection belongs to
         *  EX:  Budget1 has sections sectionA and sectionB while Budget2 has sectionC
         *      sectionA.getBudgetObj() will return Budget1
         *      sectionB.getBudgetObj() will return Budget1
         *      sectionC.getBudgetObj() will return Budget2
         *
         * POSTCONDITIONS: instance of the Budget object the calling BudgetSection belongs to has been returned
         * @return instance of the Budget object the calling BudgetSection belongs to
         */
        fun getBudgetObj(): Budget
        {
            //@Budget annotation specifies that 'this' refers to instance of the Budget class
            return this@Budget
        }

        /*
        * provides an easy way get a visual representation of the BudgetSection
        * PRECONDITIONS: none
        * POSTCONDITIONS: a formatted string containing the sections data is returned
        * @return a formatted string with the data for the instance
        * */
        override fun toString(): String {
            var section = ""
            section += "\nSection: $name\n" +
                    "\tPercentage: $percentage\n" +
                    "\tAmount: $amount\n"
            return section
        }

    }//end of inner class BudgetSection
}//end of Budget class

/*
* A simple rounding function that rounds a decimal to the number of places specified by the 'scale' parameter
* This does use DecimalFormat and RoundingMode which are part of Java libraries
* moved here to be a package declaration so it can be used in the dynamic table file
* */
fun round(decimal: Double, scale: Int = 0): Double
{
    //sets number of decimal points based on scale param
    val df = DecimalFormat("#.${"#".repeat(scale)}")
    df.roundingMode = RoundingMode.HALF_UP
    return df.format(decimal).toDouble()
}