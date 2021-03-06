package com.michaelszymczak.speccare.specminer.featurefiles;


import org.junit.Assert;
import org.junit.Test;

public class NotFoundFeatureShould {

    @Test public void beCreatedUsingFactoryMethod() {
        Assert.assertSame(NotFoundFeature.class, NotFoundFeature.getInstance().getClass());
    }

    @Test public void beAlwaysTheSameInstanceAsItIsImmutableObject() {
        Assert.assertSame(NotFoundFeature.getInstance(), NotFoundFeature.getInstance());
    }

    @Test public void presetItselfAsNotFoundFeature() {
        NotFoundFeature feature = NotFoundFeature.getInstance();

        Assert.assertEquals("Feature not found", feature.getName());
        Assert.assertEquals(0, feature.getContent().size());
        Assert.assertEquals("", feature.getPath());
    }
}
