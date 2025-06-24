import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "filter")
public class ArchRules {

    @ArchTest
    public static final ArchRule filtros_deben_implementar_RawDataFilter =
            classes().that().areNotInterfaces().and().resideInAPackage("..filter..")
                    .should().implement(filter.RawDataFilter.class);

    @ArchTest
    public static final ArchRule capa_filtro_no_debe_acceder_directamente_repositorio =
            noClasses().that().resideInAPackage("..filter..")
                    .and().haveSimpleNameNotEndingWith("ExtremeValueFilter") // excepto el tercer filtro
                    .should().dependOnClassesThat().resideInAPackage("..repository..");
}