export const userRoles = [
  "ROLE_STORE_ADMIN",
  "ROLE_STORE_MANAGER",
  "ROLE_BRANCH_MANAGER",
  "ROLE_BRANCH_ADMIN",
  "ROLE_BRANCH_CASHIER",
  "ROLE_CUSTOMER",
];

export const storeAdminRole = [
  "ROLE_STORE_MANAGER",
  "ROLE_BRANCH_MANAGER",
  "ROLE_BRANCH_ADMIN",
  "ROLE_BRANCH_CASHIER",
];

export const branchAdminRole = [
  "ROLE_BRANCH_MANAGER",
  "ROLE_BRANCH_ADMIN",
  "ROLE_BRANCH_CASHIER",
];

// Role label mapping for user-friendly display
export const roleLabels = {
  ROLE_STORE_ADMIN: "Store Admin",
  ROLE_STORE_MANAGER: "Store Manager",
  ROLE_BRANCH_MANAGER: "Branch Manager",
  ROLE_BRANCH_ADMIN: "Branch Admin",
  ROLE_BRANCH_CASHIER: "Branch Cashier",
  ROLE_CUSTOMER: "Customer",
};

// Helper function to get label from role
export const getRoleLabel = (role) => {
  return roleLabels[role] || role;
};

// Helper function to convert role array to dropdown options
export const getRoleOptions = (roles) => {
  return roles.map((role) => ({
    value: role,
    label: roleLabels[role] || role,
  }));
};

