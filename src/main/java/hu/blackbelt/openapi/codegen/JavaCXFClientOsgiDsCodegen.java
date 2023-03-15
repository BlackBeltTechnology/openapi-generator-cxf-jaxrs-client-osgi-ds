package hu.blackbelt.openapi.codegen;

/*-
 * #%L
 * Blackbelt :: OpeanAPI :: Codegen :: CXF JaxRS OSGi service client
 * %%
 * Copyright (C) 2018 - 2023 BlackBelt Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import io.swagger.v3.oas.models.media.Schema;
import org.openapitools.codegen.CliOption;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.SupportingFile;
import org.openapitools.codegen.languages.JavaCXFClientCodegen;

import java.io.File;
public class JavaCXFClientOsgiDsCodegen extends JavaCXFClientCodegen {

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

        cliOptions.add(new CliOption(ACTIVATOR_PACKAGE, "Default activator package").defaultValue(this.getActivatorPackage()));
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

        if (additionalProperties.containsKey(ACTIVATOR_PACKAGE)) {
            this.setActivatorPackage((String) additionalProperties.get(ACTIVATOR_PACKAGE));
        } else {
            additionalProperties.put(ACTIVATOR_PACKAGE, getActivatorPackage());
        }

        if (additionalProperties.containsKey(ACTIVATOR_DS_CONFIGURATION_REQUIRED)) {
            boolean activatorDsConfigurationRequired = convertPropertyToBooleanAndWriteBack(ACTIVATOR_DS_CONFIGURATION_REQUIRED);
            this.setActivatorDsConfigurationRequired(activatorDsConfigurationRequired);
        } else {
            writePropertyBack(ACTIVATOR_DS_CONFIGURATION_REQUIRED, this.getActivatorConfigurationRequired());
        }

        if (additionalProperties.containsKey(USE_GZIP_FEATURE)) {
            boolean useGzipFeature = convertPropertyToBooleanAndWriteBack(USE_GZIP_FEATURE);
            this.setUseGzipFeature(useGzipFeature);
        } else {
            writePropertyBack(USE_GZIP_FEATURE, this.getUseGzipFeature());
        }

        if (additionalProperties.containsKey(USE_LOGGING_FEATURE)) {
            boolean useLoggingFeature = convertPropertyToBooleanAndWriteBack(USE_LOGGING_FEATURE);
            this.setUseLoggingFeature(useLoggingFeature);
        } else {
            writePropertyBack(USE_LOGGING_FEATURE, this.getUseLoggingFeature());
        }

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
        return (outputFolder + File.separator + sourceFolder + File.separator + getActivatorPackage().replace('.', File.separatorChar)).replace('/', File.separatorChar);
    }

    public String activatorFolder() {
        return (sourceFolder + File.separator + getActivatorPackage().replace('.', File.separatorChar)).replace('/', File.separatorChar);
    }

    public String getActivatorPackage() {
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

    public Boolean getActivatorConfigurationRequired() {
        return activatorConfigurationRequired;
    }

    public Boolean getUseGzipFeature() {
        return useGzipFeature;
    }

    public Boolean getUseLoggingFeature() {
        return useLoggingFeature;
    }

}
