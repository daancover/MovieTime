package com.coverlabs.domain.model

enum class OrderBy(val value: String) {
    TITLE("title"),
    POPULARITY("popularity"),
    RELEASE_DATE("releaseDate"),
    NONE("");
}