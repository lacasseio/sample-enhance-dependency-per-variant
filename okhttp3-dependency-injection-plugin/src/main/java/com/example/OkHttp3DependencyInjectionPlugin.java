package com.example;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ComponentMetadataContext;
import org.gradle.api.artifacts.ComponentMetadataRule;
import org.gradle.api.attributes.Attribute;

import javax.inject.Inject;

/*private*/ abstract /*final*/ class OkHttp3DependencyInjectionPlugin implements Plugin<Project> {
    private static final Attribute<Boolean> ENHANCING_ATTRIBUTE = Attribute.of("com.example.enhanced", Boolean.class);

    @Inject
    public OkHttp3DependencyInjectionPlugin() {}

    @Override
    public void apply(Project project) {
        project.getDependencies().components(it -> it.withModule("com.squareup.okhttp3:okhttp", EnhanceRule.class));

        // We can configure the dependency
    }

    /*private*/ static abstract class EnhanceRule implements ComponentMetadataRule {
        @Inject
        public EnhanceRule() {}

        public void execute(ComponentMetadataContext context) {
            // Warning: "runtimeElements" is heavily dependendent on the published artifact.
            //   There is no way without internal API usage to perform a fuzzy find.
            context.getDetails().addVariant("runtimeElements-enhanced", "runtimeElements", variant -> {
                variant.attributes(it -> it.attribute(ENHANCING_ATTRIBUTE, true));
                variant.withDependencies(it -> it.add("org.apache.commons:commons-lang3:3.17.0"));
            });

            // NOTE: we don't declare the "not enhanced" attribute so the original variant gets selected
            //   when no opinion is given about the enhancement of the dependency.
        }
    }
}
