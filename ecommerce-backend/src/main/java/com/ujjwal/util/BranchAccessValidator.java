package com.ujjwal.util;

import com.ujjwal.domain.UserRole;
import com.ujjwal.exception.UserException;
import com.ujjwal.modal.User;

/**
 * Utility class for validating branch access permissions.
 * Ensures that branch-level users (Branch Admin, Branch Manager, Branch
 * Cashier)
 * can only access data from their assigned branch.
 */
public class BranchAccessValidator {

    /**
     * Validates if a user has access to a specific branch.
     * Branch-level users can only access their assigned branch.
     * Store-level users and admins can access any branch.
     *
     * @param user     The user attempting to access the branch
     * @param branchId The ID of the branch being accessed
     * @throws UserException if the user doesn't have access to the branch
     */
    public static void validateBranchAccess(User user, Long branchId) throws UserException {
        if (user == null) {
            throw new UserException("User cannot be null");
        }

        if (branchId == null) {
            throw new UserException("Branch ID cannot be null");
        }

        // Branch-level roles can only access their assigned branch
        if (isBranchLevelRole(user.getRole())) {
            requireBranchAssignment(user);

            if (!user.getBranch().getId().equals(branchId)) {
                throw new UserException(
                        "Access denied: User can only access their assigned branch (Branch ID: "
                                + user.getBranch().getId() + ")");
            }
        }
        // Store-level roles and admins can access any branch within their store
        // Additional store-level validation should be done in the service layer
    }

    /**
     * Checks if a role is a branch-level role that requires branch assignment.
     *
     * @param role The user role to check
     * @return true if the role is branch-level, false otherwise
     */
    public static boolean isBranchLevelRole(UserRole role) {
        return role == UserRole.ROLE_BRANCH_ADMIN ||
                role == UserRole.ROLE_BRANCH_MANAGER ||
                role == UserRole.ROLE_BRANCH_CASHIER;
    }

    /**
     * Validates that a user with a branch-level role has a branch assigned.
     *
     * @param user The user to validate
     * @throws UserException if the user has a branch-level role but no branch
     *                       assigned
     */
    public static void requireBranchAssignment(User user) throws UserException {
        if (user == null) {
            throw new UserException("User cannot be null");
        }

        if (isBranchLevelRole(user.getRole()) && user.getBranch() == null) {
            throw new UserException(
                    "User with role " + user.getRole() + " must have a branch assigned");
        }
    }

    /**
     * Validates that a branch belongs to a specific store.
     *
     * @param branchStoreId   The store ID of the branch
     * @param expectedStoreId The expected store ID
     * @throws UserException if the branch doesn't belong to the expected store
     */
    public static void validateBranchBelongsToStore(Long branchStoreId, Long expectedStoreId)
            throws UserException {
        if (branchStoreId == null || expectedStoreId == null) {
            throw new UserException("Store IDs cannot be null");
        }

        if (!branchStoreId.equals(expectedStoreId)) {
            throw new UserException(
                    "Branch does not belong to the specified store");
        }
    }
}
