package fr.rossi.fizzbuzz;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StepDefinitions {
    private int value;
    private String actualAnswer;

    @Given("value is {int}")
    public void value_is(int value) {
        this.value = value;
    }

    @When("I call fizzbuzz")
    public void i_call_fizzbuzz() {
        this.actualAnswer = FizzBuzz.fizzbuzz(this.value);
    }

    @Then("I should be told {string}")
    public void i_should_be_told(String expectedAnswer) {
        assertEquals(expectedAnswer, actualAnswer);
    }
}
