package com.michaelszymczak.livingdocumentation.specificationprovider;

import java.util.ArrayList;
import java.util.List;

class Scenario {

    private String name;
    private TextFragmentProvider tfp;

    public Scenario(TextFragmentProvider textFragmentProvider, List<String> scenarioContent) {
        tfp = textFragmentProvider;
        name = extractName(scenarioContent);
    }

    public String getName() {
        return name;
    }


    private String extractName(List<String> scenarioContent) {
        ArrayList<String> scenarioNames = tfp.getAllFragmentsThatFollows(scenarioContent, new String[]{"Scenario:", "Scenario Outline:"});
        if (scenarioNames.size() == 0) {
            throw new InvalidScenarioContentException("No 'Scenario:' nor 'Scenario Outline:' line in scenario content: " + scenarioContent.toString());
        }
        if (scenarioNames.size() > 1) {
            throw new InvalidScenarioContentException("Too many 'Scenario:' or 'Scenario Outline:' lines in scenario content: " + scenarioContent.toString());
        }
        return scenarioNames.get(0);
    }
}