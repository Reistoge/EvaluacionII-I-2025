import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "filter")
public class ArchRules {

    @ArchTest
    public static final ArchRule filters_should_implement_RawDataFilter =
            classes().that().areNotInterfaces().and().resideInAPackage("..filter..")
                    .should().implement(filter.RawDataFilter.class);

    @ArchTest
    public static final ArchRule filters_should_not_access_repository =
            noClasses().that().resideInAPackage("..filter..")
                    .and().haveSimpleNameNotEndingWith("ExtremeValueFilter") // allow only last filter if needed
                    .should().dependOnClassesThat().resideInAPackage("..repository..");
}