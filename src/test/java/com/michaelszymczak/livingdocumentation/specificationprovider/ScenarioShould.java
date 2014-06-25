package com.michaelszymczak.livingdocumentation.specificationprovider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ScenarioShould {

    @Test public void provideScenarioNameBasedOnTheContentPassedDuringCreation() {
        List<String> scenarioContent = Arrays.asList(
            "Scenario: Foo title",
            "    Given bar"
        );

        Scenario scenario = createScenarioFromContent(scenarioContent);

        Assert.assertEquals("Foo title", scenario.getName());
    }

    @Test public void provideScenarioNameIgnoringEmptyLinesAndTrailingSpaces() {
        List<String> scenarioContent = Arrays.asList(
            "    ",
            "  Scenario: Bar title    ",
            "    Given bar"
        );

        Scenario scenario = createScenarioFromContent(scenarioContent);

        Assert.assertEquals("Bar title", scenario.getName());
    }

    @Test public void treatUseScenarioTemplateAsScenarioNameAsWell() {
        List<String> scenarioContent = Arrays.asList(
            "  Scenario Outline: Foo scenario outline name",
            "    Given bar"
        );

        Scenario scenario = createScenarioFromContent(scenarioContent);

        Assert.assertEquals("Foo scenario outline name", scenario.getName());
    }

    @Test public void acceptEmptyScenarioName() {
        List<String> scenarioContent = Arrays.asList("Scenario:");

        Scenario scenario = createScenarioFromContent(scenarioContent);

        Assert.assertEquals("", scenario.getName());
    }

    @Test(expected = InvalidScenarioContentException.class)
    public void throwExceptionIfNoScenarioLine() {
        List<String> scenarioContent = Arrays.asList(
            "Given foo"
        );

        createScenarioFromContent(scenarioContent);
    }

    @Test(expected = InvalidScenarioContentException.class)
    public void throwExceptionIfTooManyScenarioLines() {
        List<String> scenarioContent = Arrays.asList(
            "Scenario: Foo",
            "Scenario: Bar"
        );

        createScenarioFromContent(scenarioContent);
    }

    @Test(expected = InvalidScenarioContentException.class)
    public void throwExceptionIfBothScenarioAndScenarioOutlinePresent() {
        List<String> scenarioContent = Arrays.asList(
            "Scenario: Foo",
            "Scenario Outline: Bar"
        );

        createScenarioFromContent(scenarioContent);
    }

    private Scenario createScenarioFromContent(List<String> scenarioContent) {
        return new Scenario(new TextFragmentProvider(), scenarioContent);
    }

}