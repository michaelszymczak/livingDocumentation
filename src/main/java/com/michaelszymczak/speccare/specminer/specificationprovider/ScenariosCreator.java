package com.michaelszymczak.speccare.specminer.specificationprovider;

import com.michaelszymczak.speccare.specminer.domain.ExistingScenario;
import com.michaelszymczak.speccare.specminer.domain.Feature;
import com.michaelszymczak.speccare.specminer.domain.Scenario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// TODO: use http://mvnrepository.com/artifact/info.cukes/gherkin/2.7.3 instead
class ScenariosCreator implements ScenariosCreatable {

    private final TextFragmentProvider tfp;
    private final FeaturesCreator featuresCreator;

    public ScenariosCreator(TextFragmentProvider textFragmentProvider, FeaturesCreator featuresCreator) {
        this.tfp = textFragmentProvider;
        this.featuresCreator = featuresCreator;
    }

    public List<Scenario> create() throws IOException {
        List<Scenario> scenarios = new ArrayList<>();
        for (Feature feature : featuresCreator.create()) {
            for (Scenario scenario : createFromOneFeature(feature)) {
                scenarios.add(scenario);
            }
        }
        return scenarios;
    }

    public List<Scenario> createFromOneFeature(Feature feature) {

        SingleScenarioCreator ssc = new SingleScenarioCreator(tfp, feature);

        List<List<String>> scenariosContent = new ArrayList<>();
        List<String> currentScenarioLines = null;
        List<Scenario> scenarios = new ArrayList<>();
        boolean isInMultilineQuotation = false;

        for (String line : feature.getContent()) {
            if (tfp.isMultilineQuotation(line)) {
                isInMultilineQuotation = !isInMultilineQuotation;
            }
            if (isScenarioStartingLine(line) && !isInMultilineQuotation) {
                ssc.addNewScenario(scenarios, currentScenarioLines);
                currentScenarioLines = createPlaceholderForNewScenarioLines(scenariosContent);
            }
            if (null != currentScenarioLines) {
                currentScenarioLines.add(line);
            }
        }

        ssc.addNewScenario(scenarios, currentScenarioLines);
        return scenarios;
    }

    private List<String> createPlaceholderForNewScenarioLines(List<List<String>> scenariosContent) {
        List<String> currentScenarioLines;
        currentScenarioLines = new ArrayList<>();
        scenariosContent.add(currentScenarioLines);
        return currentScenarioLines;
    }

    private boolean isScenarioStartingLine(String line) {
        return tfp.returnStringFollowingAnyOf(line, new String[]{Scenario.SCENARIO_START, Scenario.SCENARIO_OUTLINE_START}) != null;
    }

    private class SingleScenarioCreator {
        private final TextFragmentProvider tfp;
        private final Feature feature;
        public SingleScenarioCreator(TextFragmentProvider tfp, Feature feature) {
            this.tfp = tfp;
            this.feature = feature;
        }

        public void addNewScenario(List<Scenario> scenarios, List<String> scenarioContent) {
            if (null != scenarioContent) {
                scenarios.add(create(scenarioContent));
            }
        }

        private Scenario create(List<String> scenarioContent) {
            return new ExistingScenario(tfp, scenarioContent, feature);
        }
    }
}