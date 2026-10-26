package dev.cjrv.azureversionator.data.model.azure

/**
 * Branch statistics relative to a base branch.
 * [behindCount] is the number of commits in the base branch missing from this branch.
 */
data class AzureBranchStats(
    val name: String,
    val commitId: String,
    val aheadCount: Int,
    val behindCount: Int,
    val isBaseVersion: Boolean
)
