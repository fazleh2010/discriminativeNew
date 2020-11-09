/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.massive.correlation.utils.test;

import java.util.List;

/**
 *
 * @author elahi
 */
public class Employee {

    public Employee(String name, int wage, String position, List<Employee> colleagues) {
        this.name = name;
        this.wage = wage;
        this.position = position;
        this.colleagues = colleagues;
    }

    // Without a default constructor, Jackson will throw an exception
    public Employee() {
    }

    private String name;
    private int wage;
    private String position;
    private List<Employee> colleagues;

    // Getters and setters
    @Override
    public String toString() {
        return "\nName: " + name + "\nWage: " + wage + "\nPosition: " + position + "\nColleagues: " + colleagues + "\n";
    }

}
