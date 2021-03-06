package com.michaelszymczak.speccare.specminer.featurefiles;

import com.michaelszymczak.speccare.specminer.core.Feature;
import com.michaelszymczak.speccare.specminer.core.FeatureBuilder;
import com.michaelszymczak.speccare.specminer.core.Scenario;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GherkinScenarioProviderShould {

    @Test public void createAllScenariosFoundInAllFeatureFiles() throws IOException {
        givenFeatureFilesRetrieverHasTwoFeatureFilesContainingTwoScenariosEach();
        List<Scenario> scenarios = sc.create();
        Assert.assertEquals(4, scenarios.size());
    }

    @Test public void createScenariosBasedOnDataReceivedFromFeatureFiles() throws IOException {
        givenSomeFeatureFilesAndTheLastContaining(
                "Feature: Feature3",
                "",
                "  Scenario: Feature 3, Scenario 1",
                "    Given foo",
                "  Scenario: Feature 3, Scenario 2",
                "    Given foo",
                "  Scenario: Feature 3, Scenario 3",
                "    Given Feature 3, Scenario 3 precondition"
        );

        List<Scenario> scenarios = sc.create();

        assertLastScenarioCreatedWithFollowingContentAndFeatureContent(
                scenarios,
                Arrays.asList(
                        "  Scenario: Feature 3, Scenario 3",
                        "    Given Feature 3, Scenario 3 precondition"),
                Arrays.asList(
                        "Feature: Feature3",
                        "",
                        "  Scenario: Feature 3, Scenario 1",
                        "    Given foo",
                        "  Scenario: Feature 3, Scenario 2",
                        "    Given foo",
                        "  Scenario: Feature 3, Scenario 3",
                        "    Given Feature 3, Scenario 3 precondition"
                )
        );
    }

    @Test public void createScenariosUsingSingleFeatureContent() {
        Feature feature = createFeatureWithContent(
                "Feature: Foo feature",
                "",
                "  Scenario: Bar scenario",
                "    Given some precondition",
                "    Then  some expected result"
        );

        List<Scenario> scenarios = sc.createFromOneFeature(feature);

        Assert.assertEquals(Arrays.asList(
                "  Scenario: Bar scenario",
                "    Given some precondition",
                "    Then  some expected result"
        ), scenarios.get(0).getContent());
    }

    @Test public void usePassedFeatureAsScenariosWrappingFeature() {
        Feature feature = FeatureBuilder.use().build();
        List<Scenario> scenarios = sc.createFromOneFeature(feature);
        Assert.assertSame(feature, scenarios.get(0).getFeature());
    }

    @Test public void createAllFoundScenarios() {
        Feature feature = createFeatureWithContent(
                "Feature: Foo feature",
                "",
                "  Scenario: First scenario",
                "    Given first foo",
                "  Scenario: Second scenario",
                "    Given second foo",
                "  Scenario: Third scenario",
                "    Given third foo"
        );

        List<Scenario> scenarios = sc.createFromOneFeature(feature);

        Assert.assertEquals(Arrays.asList(
                "  Scenario: Third scenario",
                "    Given third foo"
        ), scenarios.get(2).getContent());
    }

    @Test public void treatScenarioOutlineAsAKindOfScenario() {
        Feature feature = createFeatureWithContent(
                "Feature: Foo feature",
                "",
                "  Scenario: First scenario",
                "    Given first foo",
                "  Scenario Outline: Outline scenario",
                "    Given second foo"
        );

        List<Scenario> scenarios = sc.createFromOneFeature(feature);

        Assert.assertEquals(Arrays.asList(
                "  Scenario Outline: Outline scenario",
                "    Given second foo"
        ), scenarios.get(1).getContent());
    }

    @Test public void ignoreScenariosInsideInlineQuotation() {
        Feature feature = createFeatureWithContent(
                "Feature: Foo feature",
                "",
                "  Scenario: First scenario",
                "    Given first foo",
                "  \"Scenario: Second scenario\"",
                "    Given second foo",
                "  Scenario: Third scenario",
                "    Given third foo"
        );

        List<Scenario> scenarios = sc.createFromOneFeature(feature);

        Assert.assertEquals(2, scenarios.size());
    }

    @Test public void ignoreScenariosInsideMultilineQuotation() {
        Feature feature = createFeatureWithContent(
                "Feature: Foo feature",
                "",
                "  Scenario: First scenario",
                "    Given first foo:",
                "    \"\"\"",
                "      Scenario: this is not a real scenario, only quotation",
                "        Given second foo",
                "    \"\"\"",
                "  Scenario: Third scenario",
                "    Given third foo"
        );

        List<Scenario> scenarios = sc.createFromOneFeature(feature);

        Assert.assertEquals(2, scenarios.size());
    }

    private GherkinScenarioProvider sc;
    private FeatureFilesRetrieverStub retriever;
    private FeatureCreator featureCreator;

    @Before
    public void setUp() throws Exception {
        retriever = new FeatureFilesRetrieverStub();
        sc = new GherkinScenarioProvider(new TextFragmentProvider(), new FeaturesFromFilesCreator(new TextFragmentProvider(), retriever));
        featureCreator = new FeatureCreator(new TextFragmentProvider());
    }

    private void assertLastScenarioCreatedWithFollowingContentAndFeatureContent(List<Scenario> scenarios, List<String> lastScenarioContent, List<String> lastScenarioFeatureContent) {
        Scenario lastScenario = scenarios.get(scenarios.size() - 1);
        Assert.assertEquals(lastScenarioContent, lastScenario.getContent());
        Assert.assertEquals(lastScenarioFeatureContent, lastScenario.getFeature().getContent());
    }

    private void givenSomeFeatureFilesAndTheLastContaining(String... lastFeatureContent) {
        givenFeatureFilesRetrieverHasTwoFeatureFilesContainingTwoScenariosEach();

        retriever.files.put("/foo/bar/Feature3.feature", Arrays.asList(lastFeatureContent));
    }


    private void givenFeatureFilesRetrieverHasTwoFeatureFilesContainingTwoScenariosEach() {
        retriever.files.put("Feature1.feature", Arrays.asList(
                "Feature: Feature1",
                "",
                "  Scenario: Feature 1, Scenario 1",
                "    Given foo",
                "  Scenario: Feature 1, Scenario 2",
                "    Given foo"
        ));
        retriever.files.put("foo/bar/Feature2.feature", Arrays.asList(
                "Feature: Feature2",
                "",
                "  Scenario: Feature 2, Scenario 1",
                "    Given foo",
                "  Scenario: Feature 2, Scenario 2",
                "    Given foo"
        ));
    }

    private Feature createFeatureWithContent(String... content) {
        return featureCreator.create(Arrays.asList(content), "path/to/Feature.feature");
    }
}