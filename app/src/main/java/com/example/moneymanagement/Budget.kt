package com.example.termproject_a

import java.math.RoundingMode
import java.text.DecimalFormat

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
    * adds a new section to the budget - percentage and amount are optional params and will default to 0.0
    * if nothing is passed - use named argument annotation when leaving out optional params both for clarity and
    * to allow the optional functionality of params regardless of their position ie passing name and amount params but
    * not percentage addSection(name, amount = 4200)
    *
    * @param name the name of the section - must be unique and not match the name of any other sections name
    * @param percentage the percentage the budget the section should be equal to - optional and if not passed will
    *   be calculated from amount param
    * @param amount the dollar amount for the budget section - optional and if not passed will be calculated from
    *   the percentage param
    * @exception IllegalArgumentException thrown when budget name already exists in another BudgetSection object
    * */
    fun addSection(name: String, percentage: Double = 0.0, amount: Double = 0.0)
    {
        //section names should be unique - throws exception if .none equates to false (param section is already a name property)
        require(sections.none{ it.name == name }){"section $name already exists in this budget"}

        //really either percentage or amount should be passed but if both are passed check that they equal the same amount of income
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

    fun removeSection(name: String)
    {
        sections.remove(getSection(name))
    }

    /*
     * returns BudgetSection object or throws exception if section name passed does not match the name
     * property of any BudgetSection object
     *
     * @param sectionName the name of the budget section to be returned
     * @return the BudgetSection object with a 'name' property equaling the section name passed
     * @exception IllegalArgumentException thrown for 2 reasons:
     *  1: When no BudgetSection objects name property matches the section name param
     *  2: If for any reason the BudgetSection cannot be found - this should never be possible, an exception
     *     would already be thrown at this point because of reason 1.
    **/
    fun getSection(sectionName: String): BudgetSection
    {
        /* requires .any to evaluate to true
         * (sectionName must be match to a name property of one of the BudgetSection objects
        * */
        require(sections.any{ it.name == sectionName }) {"Section [$sectionName] is not a valid section name for this budget"}
        return requireNotNull(sections.find{ it.name == sectionName })
    }

    fun getFreeAmount(): Double
    {
        var total = 0.0
        for (section in sections){ total += section.amount }
        return round((income - total), 2)
    }

    fun getFreePercentage(): Double
    {
        var total = 0.0
        for (section in sections){ total += section.percentage }
        return round((1 - total), 4)
    }

    /*
    * returns Boolean based on:
    *    getFreePercentage returns 1 - total of budget section percentages
    *    returns a negative when space is exceeded
    *    100% full return (0)     120% full return (-0.2)
    * @return true if getFreePercentage is greater than 0 ie budget does not use 100% of income
    *   and returns false if getFreePercentage is zero or negative ie budget is using 100%
    *   or over 100% of income
    * */
    fun hasEmptySpace(): Boolean
    {
        return (getFreePercentage() > 0)
    }

    //I didn't want to fuck with BigDecimal so here's this instead
    private fun round(decimal: Double, scale: Int = 0): Double
    {
        //sets number of decimal points based on scale param
        val df: DecimalFormat = DecimalFormat("#.${"#".repeat(scale)}")
        df.roundingMode = RoundingMode.HALF_UP
        return df.format(decimal).toDouble()
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
        
        fun getSectionData(): List<String>
        {
            return listOf(name, percentage.toString(), amount.toString())
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