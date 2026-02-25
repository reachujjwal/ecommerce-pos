import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateHashes {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

        System.out.println("-- ROLE_SUPER_ADMIN - Password: SuperAdmin@2024");
        System.out.println(
                "('" + "ROLE_SUPER_ADMIN" + "', '" + encoder.encode("SuperAdmin@2024") + "', FALSE, 'SYSTEM'),");

        System.out.println("\n-- ROLE_STORE_ADMIN - Password: StoreAdmin@2024");
        System.out.println(
                "('" + "ROLE_STORE_ADMIN" + "', '" + encoder.encode("StoreAdmin@2024") + "', FALSE, 'SYSTEM'),");

        System.out.println("\n-- ROLE_STORE_MANAGER - Password: StoreManager@2024");
        System.out.println(
                "('" + "ROLE_STORE_MANAGER" + "', '" + encoder.encode("StoreManager@2024") + "', FALSE, 'SYSTEM'),");

        System.out.println("\n-- ROLE_BRANCH_MANAGER - Password: BranchManager@2024");
        System.out.println(
                "('" + "ROLE_BRANCH_MANAGER" + "', '" + encoder.encode("BranchManager@2024") + "', FALSE, 'SYSTEM'),");

        System.out.println("\n-- ROLE_BRANCH_ADMIN - Password: BranchAdmin@2024");
        System.out.println(
                "('" + "ROLE_BRANCH_ADMIN" + "', '" + encoder.encode("BranchAdmin@2024") + "', FALSE, 'SYSTEM'),");

        System.out.println("\n-- ROLE_BRANCH_CASHIER - Password: BranchCashier@2024");
        System.out.println(
                "('" + "ROLE_BRANCH_CASHIER" + "', '" + encoder.encode("BranchCashier@2024") + "', FALSE, 'SYSTEM');");
    }
}
