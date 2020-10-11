package hu.blackbelt.openapi.codegen;

import io.swagger.v3.oas.models.media.Schema;
import org.openapitools.codegen.CliOption;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.SupportingFile;
import org.openapitools.codegen.languages.JavaCXFClientCodegen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
public class JavaCXFClientOsgiDsCodegen extends JavaCXFClientCodegen {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaCXFClientOsgiDsCodegen.class);
    public static final String ACTIVATOR_PACKAGE = "activatorPackage";
    public static final String ACTIVATOR_DS_CONFIGURATION_REQUIRED = "activatorDsConfigurationRequired";
    public static final String USE_GZIP_FEATURE = "useGzipFeature";
    public static final String USE_LOGGING_FEATURE = "useLoggingFeature";

    protected String activatorPackage = "org.openapitools.activator";
    protected Boolean activatorConfigurationRequired = false;
    protected boolean useGzipFeature = false;
    protected boolean useLoggingFeature = false;

    public JavaCXFClientOsgiDsCodegen() {
        super();
        outputFolder = "generated-code/JavaJaxRS-CXF-Osgi-Ds";

        cliOptions.add(CliOption.newString(ACTIVATOR_PACKAGE, "Default activator package"));
        cliOptions.add(CliOption.newBoolean(ACTIVATOR_DS_CONFIGURATION_REQUIRED, "Activator DS configuration required"));
        cliOptions.add(CliOption.newBoolean(USE_GZIP_FEATURE, "Use Gzip Feature"));
        cliOptions.add(CliOption.newBoolean(USE_LOGGING_FEATURE, "Use Logging Feature"));
    }

    @Override
    public String getName() {
        return "jaxrs-cxf-client-osgi-ds";
    }

    @Override
    public String getHelp() {
        return "Generates a Java JAXRS Client registered as OSGi Declarative service based on Apache CXF framework.";
    }

    @Override
    public void processOpts() {
        super.processOpts();

        this.setUseGzipFeature(convertPropertyToBooleanAndWriteBack(USE_GZIP_FEATURE));
        this.setActivatorDsConfigurationRequired(convertPropertyToBooleanAndWriteBack(ACTIVATOR_DS_CONFIGURATION_REQUIRED));
        this.setUseLoggingFeature(convertPropertyToBooleanAndWriteBack(USE_LOGGING_FEATURE));

        if (!additionalProperties.containsKey(ACTIVATOR_PACKAGE)) {
            additionalProperties.put(ACTIVATOR_PACKAGE, activatorPackage);
        }
        this.setActivatorPackage((String) additionalProperties.get(ACTIVATOR_PACKAGE));

        supportingFiles.clear(); // Don't need extra files provided by AbstractJAX-RS & Java Codegen

        writeOptional(outputFolder, new SupportingFile("osgi/ds/pom.mustache", "", "pom.xml"));
        supportingFiles.add(new SupportingFile("osgi/ds/modelServiceActivator.mustache", activatorFolder(), "ClientApiServiceActivator.java"));

    }

    @Override
    public String apiFilename(String templateName, String tag) {
        String result = super.apiFilename(templateName, tag);
        if (templateName.endsWith("ServiceActivator.mustache")) {
            int ix = result.lastIndexOf(File.separator);
            result = result.substring(0, ix) + result.substring(ix, result.length() - 5) + "ServiceActivator.java";
            result = result.replace(apiFileFolder(), activatorFileFolder());
        }

        return result;
    }

    @Override
    public CodegenModel fromModel(String name, Schema model) {
        return super.fromModel(name, model);
    }

    @Override
    public String toModelFilename(String name) {
        return super.toModelFilename(name);
    }


    public String activatorFileFolder() {
        return (outputFolder + File.separator + sourceFolder + File.separator + activatorPackage().replace('.', File.separatorChar)).replace('/', File.separatorChar);
    }

    public String activatorFolder() {
        return (sourceFolder + File.separator + activatorPackage().replace('.', File.separatorChar)).replace('/', File.separatorChar);
    }

    public String activatorPackage() {
        return activatorPackage;
    }

    public void setActivatorPackage(String activatorPackage) {
        this.activatorPackage = activatorPackage;
    }

    public void setUseGzipFeature(boolean useGzipFeature) {
        this.useGzipFeature = useGzipFeature;
    }

    public void setUseLoggingFeature(boolean useLoggingFeature) {
        this.useLoggingFeature = useLoggingFeature;
    }

    public void setActivatorDsConfigurationRequired(boolean activatorConfigurationRequired) {
        this.activatorConfigurationRequired = activatorConfigurationRequired;
    }

}