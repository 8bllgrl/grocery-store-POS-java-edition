package com.grocerypos.service;

import com.grocerypos.model.Employee;
import com.grocerypos.repository.EmployeeRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads employee login records from PostgreSQL and handles login lookups.
 *
 * Mirrors CartService's "load into an in-memory map" pattern, now backed by
 * EmployeeRepository instead of employees.json. No permissions/tags system
 * yet — that's a deliberate later step. For now this is purely: can we find
 * a matching employee, and is their password right.
 */
public class EmployeeService {

    // Keyed by userId (the PIN) for fast lookup during login.
    private final Map<String, Employee> employeeDatabase = new HashMap<>();

    public EmployeeService() {
        loadEmployeesFromDatabase();
    }

    private void loadEmployeesFromDatabase() {
        EmployeeRepository repository = new EmployeeRepository();
        List<Employee> employees = repository.findAll();

        if (employees.isEmpty()) {
            System.err.println("Critical Error: no employees loaded from database!");
            return;
        }

        for (Employee e : employees) {
            employeeDatabase.put(e.getUserId(), e);
        }

        System.out.println("[SYSTEM] Successfully loaded " + employeeDatabase.size() + " employee records from PostgreSQL.");
    }

    // ─────────────────────────────────────────────────────────────────
    // TEST USER BYPASS SWITCH
    //
    // Set this to `false` to disable the blank-username/blank-password
    // shortcut entirely. This is the ONE line to comment/toggle to turn
    // the bypass off — everything else routes through isTestUserBypassEnabled().
    // ─────────────────────────────────────────────────────────────────
    private static final boolean TEST_USER_BYPASS_ENABLED = true;

    public boolean isTestUserBypassEnabled() {
        return TEST_USER_BYPASS_ENABLED;
    }

    /**
     * Attempts to log in with the given userId (PIN) and password.
     *
     * Behaviour:
     *  - If both userId and password are blank AND the bypass switch is on,
     *    logs in as whichever employee record is flagged testUser=true in
     *    the database (falls back to a transient in-memory Test User if none
     *    is flagged, so this never hard-fails).
     *  - Otherwise does a normal PIN + password lookup against the loaded
     *    employee map. Password check is plain string equality for now
     *    (NO encryption yet — that's a later step, explicitly out of scope
     *    for this pass).
     *
     * @return the matched Employee on success, or null on failed login.
     */
    public Employee login(String userId, String password) {
        boolean blankUserId = (userId == null || userId.isBlank());
        boolean blankPassword = (password == null || password.isBlank());

        if (blankUserId && blankPassword && TEST_USER_BYPASS_ENABLED) {
            Employee testUser = findFlaggedTestUser();
            if (testUser != null) {
                testUser.setStatus("online");
                return testUser;
            }
            // Safety net: bypass is on but no testUser:true record exists in
            // the database. Don't lock the operator out — hand back a throwaway
            // default so the checkout flow can still proceed.
            return new Employee("User", "Test", 0.0, "0000", "", "online", true);
        }

        Employee match = employeeDatabase.get(userId);
        if (match == null) return null;

        // NOTE: plaintext comparison only, see class javadoc.
        if (!match.getPassword().equals(password)) return null;

        match.setStatus("online");
        return match;
    }

    private Employee findFlaggedTestUser() {
        return employeeDatabase.values().stream()
                .filter(Employee::isTestUser)
                .findFirst()
                .orElse(null);
    }

    /**
     * Marks an employee offline on logout. Safe to call even if they were
     * never found via login() (e.g. the throwaway fallback Test User).
     */
    public void logout(Employee employee) {
        if (employee == null) return;
        Employee tracked = employeeDatabase.get(employee.getUserId());
        if (tracked != null) tracked.setStatus("offline");
    }

    public Map<String, Employee> getAllEmployees() {
        return employeeDatabase;
    }
}