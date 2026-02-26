# codegen Specification

## Purpose

Generates CXF JAX-RS client stubs packaged as OSGi Declarative Services bundles from OpenAPI 3.x specifications. The generator extends `JavaCXFClientCodegen` and produces API interfaces, model POJOs, an OSGi DS activator, and a Maven POM.

## Architecture

The generator consists of a single class `JavaCXFClientOsgiDsCodegen` (extending `JavaCXFClientCodegen`) and two Mustache templates:
- `modelServiceActivator.mustache` — generates `ClientApiServiceActivator.java`, an OSGi `@Component` that creates CXF client proxies and registers them as OSGi services
- `pom.mustache` — generates a Maven POM with `bundle` packaging and OSGi/CXF/Jackson dependencies

The generator is registered via Java SPI (`META-INF/services/org.openapitools.codegen.CodegenConfig`).

The generated activator holds a `Map<Class, ServiceRegistration>` to track registered services and uses `JAXRSClientFactory.create()` to produce typed client proxies.

## Requirements

### Requirement: Generator registration via SPI

The generator SHALL be discoverable by OpenAPI Generator through the Java ServiceLoader mechanism.

#### Scenario: OpenAPI Generator discovers the plugin
- **GIVEN** the JAR is on the classpath
- **WHEN** OpenAPI Generator loads `CodegenConfig` implementations via `ServiceLoader`
- **THEN** `JavaCXFClientOsgiDsCodegen` is available with name `jaxrs-cxf-client-osgi-ds`

### Requirement: Custom activator package

The generator SHALL support configuring the Java package for the generated activator class via the `activatorPackage` option.

#### Scenario: Default activator package
- **WHEN** no `activatorPackage` is specified
- **THEN** the activator is generated in `org.openapitools.activator`

#### Scenario: Custom activator package
- **GIVEN** `activatorPackage=com.example.osgi`
- **WHEN** the generator runs
- **THEN** `ClientApiServiceActivator.java` is placed in `com/example/osgi/` under the source folder

### Requirement: OSGi service registration

The generated activator SHALL register each API interface as an OSGi service on activation and unregister all on deactivation.

#### Scenario: Activation registers all API services
- **GIVEN** an OpenAPI spec with tags `PetApi` and `StoreApi`
- **WHEN** the OSGi component is activated
- **THEN** `JAXRSClientFactory.create()` is called for each API interface and each is registered via `BundleContext.registerService()`

#### Scenario: Deactivation unregisters all API services
- **GIVEN** the component is active with registered services
- **WHEN** the OSGi component is deactivated
- **THEN** all `ServiceRegistration` entries are unregistered and removed from the tracking map

### Requirement: CXF client configuration

The generated activator SHALL configure CXF HTTP client policies from OSGi configuration properties.

#### Scenario: Timeouts applied from config
- **GIVEN** OSGi configuration with `connection_timeout=5000` and `receive_timeout=10000`
- **WHEN** a client proxy is created
- **THEN** the `HTTPClientPolicy` on the `HTTPConduit` has `connectionTimeout=5000` and `receiveTimeout=10000`

#### Scenario: Logging feature enabled
- **GIVEN** `log_payload=true` and `log_payload_limit=2000` in config
- **WHEN** a client proxy is created
- **THEN** a CXF `LoggingFeature` with limit 2000 and pretty printing is added to the client

### Requirement: DS configuration policy

The generator SHALL support requiring OSGi DS configuration before component activation via the `activatorDsConfigurationRequired` option.

#### Scenario: Configuration required
- **GIVEN** `activatorDsConfigurationRequired=true`
- **WHEN** the generator produces the activator
- **THEN** the `@Component` annotation includes `configurationPolicy = ConfigurationPolicy.REQUIRE`

#### Scenario: Configuration optional (default)
- **GIVEN** `activatorDsConfigurationRequired` is not set or is `false`
- **WHEN** the generator produces the activator
- **THEN** the `@Component` annotation does not include a `configurationPolicy`

### Requirement: GZIP feature support

The generator SHALL support adding GZIP in/out interceptors to the CXF client via the `useGzipFeature` option.

#### Scenario: GZIP enabled
- **GIVEN** `useGzipFeature=true`
- **WHEN** the generator produces the activator
- **THEN** the generated code adds `GZIPOutInterceptor` and `GZIPInInterceptor` to the client configuration

### Requirement: OSGi bundle packaging

The generated POM SHALL configure the output project as an OSGi bundle with correct package exports and JAX-RS import range.

#### Scenario: Bundle manifest
- **WHEN** the generated project is built
- **THEN** the `maven-bundle-plugin` produces a bundle that exports the API and model packages and imports `javax.ws.rs;version="[1.1,3)"`

### Requirement: Supporting files replacement

The generator SHALL clear all default supporting files inherited from `JavaCXFClientCodegen` and provide only its own templates.

#### Scenario: No extra files
- **WHEN** the generator runs
- **THEN** only `pom.xml` and `ClientApiServiceActivator.java` are produced as supporting files (in addition to the standard API and model files)
