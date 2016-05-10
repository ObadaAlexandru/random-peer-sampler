package de.tum.unit;

import cucumber.api.java.en.When;

/**
 * Created by Alexandru Obada on 09/05/16.
 */
public class UnitSteps {

    @When("^test$")
    public void test() {
        System.out.println("Hello");
    }
}
