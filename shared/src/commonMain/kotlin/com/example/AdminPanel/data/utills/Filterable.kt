package com.example.AdminPanel.data.utills

import kotlin.collections.filter

interface Filterable {
    fun matchesSearch(query: String): Boolean

    // Optional tags for dropdown categorizations
    fun primaryCategory(): String? = null
    fun secondaryCategory(): String? = null
    fun group(): String? = null
    fun recordTimestamp(): Long? = null
}

fun <T : Filterable> List<T>.applyGlobalFilter(query: FilterQuery): List<T> {
    return this.filter { item ->
        // 1. Text search match
        val matchesText = query.search.isEmpty() || item.matchesSearch(query.search)

        // 2. Category Match (e.g., status Selected)
        val matchesPrimary = query.category.isEmpty() ||
                query.category == "All Statuses" ||
                query.category == "All Groups" ||
                item.primaryCategory().equals(query.category, ignoreCase = true)


        val matchSubCategory = query.subCategory.isEmpty() ||
                query.subCategory == "All" ||
                item.secondaryCategory().equals(query.subCategory, ignoreCase = true)

        // 3. Time bounds match
        val itemTime = item.recordTimestamp()
        val matchesTime = if (query.startDate != null && itemTime != null) {
            itemTime in query.startDate..(query.endDate ?: Long.MAX_VALUE)
        } else {
            true
        }

        val itemGroup = item.group()
        var matchesGroup = query.group.isEmpty() ||
                query.group == "All Groups" ||
                itemGroup.equals(query.group, ignoreCase = true)

        if (query.group == "no groups") {
            matchesGroup = itemGroup.isNullOrEmpty()
        }

        matchesText && matchesPrimary && matchSubCategory && matchesTime && matchesGroup
    }
}