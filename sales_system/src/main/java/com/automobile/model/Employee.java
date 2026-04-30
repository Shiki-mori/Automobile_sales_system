package com.automobile.model;

public class Employee {
    private int employeeId;
    private String name;
    private String jobNumber;
    private String role;
    private String department;

    public Employee() {
    }

    public Employee(int employeeId, String name, String jobNumber, String role, String department) {
        this.employeeId = employeeId;
        this.name = name;
        this.jobNumber = jobNumber;
        this.role = role;
        this.department = department;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", name='" + name + '\'' +
                ", jobNumber='" + jobNumber + '\'' +
                ", role='" + role + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
