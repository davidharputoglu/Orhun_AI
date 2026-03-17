package com.harputoglu.orhun.util

import kotlin.math.min

object PhoneticMatcher {

    /**
     * Simple Levenshtein distance for fuzzy matching.
     */
    fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost)
            }
        }
        return dp[s1.length][s2.length]
    }

    /**
     * Simplified Soundex Implementation.
     */
    fun soundex(s: String): String {
        if (s.isEmpty()) return ""
        val firstLetter = s[0].uppercaseChar()
        val tail = s.substring(1).lowercase(java.util.Locale.ROOT)
        var res = firstLetter.toString()
        val codes = mapOf(
            'b' to '1', 'f' to '1', 'p' to '1', 'v' to '1',
            'c' to '2', 'g' to '2', 'j' to '2', 'k' to '2', 'q' to '2', 's' to '2', 'x' to '2', 'z' to '2',
            'd' to '3', 't' to '3',
            'l' to '4',
            'm' to '5', 'n' to '5',
            'r' to '6'
        )
        
        var lastCode = codes[firstLetter.lowercaseChar()] ?: '0'
        for (char in tail) {
            val code = codes[char] ?: '0'
            if (code != '0' && code != lastCode) {
                res += code
            }
            lastCode = code
            if (res.length == 4) break
        }
        return res.padEnd(4, '0')
    }

    /**
     * Check if two names match either exactly, fuzzy or phonetically.
     */
    fun isMatch(query: String, target: String, threshold: Int = 2): Boolean {
        val q = query.lowercase().trim()
        val t = target.lowercase().trim()
        
        if (t.contains(q)) return true
        if (levenshteinDistance(q, t) <= threshold) return true
        if (soundex(q) == soundex(t)) return true
        
        return false
    }
}
