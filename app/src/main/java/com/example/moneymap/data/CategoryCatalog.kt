package com.example.moneymap.data

data class ExpenseCategoryDef(
    val emoji: String,
    val name: String,
    val subcategories: List<String>,
)

object CategoryCatalog {
    val incomeCategoryName = "Income"

    val incomeSubcategories: List<String> = listOf(
        "Salary",
        "Freelance",
        "Internship Stipend",
        "Business",
        "Pocket Money",
        "Investments",
        "Side Hustle",
        "Other Income",
    )

    val expenseCategories: List<ExpenseCategoryDef> = listOf(
        ExpenseCategoryDef(
            "🍔",
            "Food",
            listOf(
                "Restaurant",
                "Fast Food",
                "Coffee / Tea",
                "Snacks",
                "Swiggy / Zomato",
                "Groceries",
                "Mess / Hostel Food",
            ),
        ),
        ExpenseCategoryDef(
            "🚗",
            "Transport",
            listOf(
                "Fuel",
                "Uber / Ola",
                "Bus",
                "Metro",
                "Train",
                "Auto / Taxi",
                "Parking",
                "Toll Charges",
                "Vehicle Service",
                "Minor Repairs",
            ),
        ),
        ExpenseCategoryDef(
            "🛍️",
            "Shopping",
            listOf(
                "Clothes",
                "Shoes",
                "Electronics",
                "Gadgets",
                "Accessories",
                "Amazon / Flipkart",
                "Personal Care",
                "Home Items",
            ),
        ),
        ExpenseCategoryDef(
            "💡",
            "Bills",
            listOf(
                "Rent",
                "Electricity",
                "Water",
                "Internet / WiFi",
                "Mobile Recharge",
                "Netflix",
                "Spotify",
                "Insurance",
                "EMI Payments",
            ),
        ),
        ExpenseCategoryDef(
            "🎬",
            "Entertainment",
            listOf(
                "Movies",
                "Gaming",
                "Events",
                "Concerts",
                "Trips / Outings",
                "Streaming Services",
                "Hobbies",
            ),
        ),
        ExpenseCategoryDef(
            "🏥",
            "Health",
            listOf(
                "Doctor Consultation",
                "Medicines",
                "Hospital Bills",
                "Gym Membership",
                "Supplements",
                "Health Checkup",
                "Therapy",
            ),
        ),
        ExpenseCategoryDef(
            "📚",
            "Education",
            listOf(
                "College Fees",
                "Online Courses",
                "Books",
                "Stationery",
                "Exam Fees",
                "Certifications",
                "Workshops",
            ),
        ),
        ExpenseCategoryDef(
            "🔄",
            "Other",
            listOf(
                "Gifts",
                "Donations",
                "Emergency Expenses",
                "Miscellaneous",
                "Random Payments",
            ),
        ),
    )

    fun subcategoriesFor(categoryName: String): List<String> =
        expenseCategories.firstOrNull { it.name == categoryName }?.subcategories.orEmpty()
}
