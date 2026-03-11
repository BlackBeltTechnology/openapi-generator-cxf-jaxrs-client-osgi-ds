# OpenAPI Generator CXF JAX-RS Client OSGi DS - Project Documentation

## Project Overview


**Repository:** BlackBeltTechnology/openapi-generator-cxf-jaxrs-client-osgi-ds
**License:** Apache License 2.0
**Java Version:** 21
**Build System:** Maven 3.9.4+ with Maven Wrapper

1. An OpenAPI Generator plugin that produces CXF JAX-RS client stubs packaged as OSGi Declarative Services bundles
2. Extends `JavaCXFClientCodegen` from OpenAPI Generator 4.2.2 with custom Mustache templates for OSGi DS activator and Maven POM generation
3. The generated activator creates CXF client proxies via `JAXRSClientFactory` and registers each API interface as an OSGi service
4. Configurable CXF features: logging, GZIP compression, connection/receive timeouts via OSGi Configuration Admin

## Code Instructions

1. First think through the problem, read the codebase for relevant files.
2. Before you make any major changes, check in with me and I will verify the plan.
3. Please every step of the way just give me a high level explanation of what changes you made.
4. Make every task and code change you do as simple as possible. We want to avoid making any massive or complex changes. Every change should impact as little code as possible. Everything is about simplicity.
5. Maintain a documentation file that describes how the architecture of the app works inside and out.
6. Never speculate about code you have not opened. If the user references a specific file, you MUST read the file before answering. Make sure to investigate and read relevant files BEFORE answering questions about the codebase. Never make any claims about code before investigating unless you are certain of the correct answer - give grounded and hallucination-free answers.
7. For implementation use TDD (Test-Driven Development): write or update tests first to define the expected behaviour, verify they fail, then write the minimal implementation to make them pass.
8. Use DRY (Don't Repeat Yourself): extract reusable logic into separate classes, utilities, or components. If the same pattern appears in multiple places, refactor it into a shared helper.

## Directory Structure

```
├── src/
│   ├── main/java/hu/blackbelt/openapi/codegen/
│   │   └── JavaCXFClientOsgiDsCodegen.java        # The sole generator class
│   ├── main/resources/
│   │   ├── JavaJaxRS/cxf/osgi/ds/
│   │   │   ├── modelServiceActivator.mustache      # OSGi DS activator template
│   │   │   └── pom.mustache                        # Generated project POM template
│   │   └── META-INF/services/
│   │       └── org.openapitools.codegen.CodegenConfig  # SPI registration
│   └── test/
│       ├── java/hu/blackbelt/openapi/SampleRunner.java # Unit test
│       └── resources/test.yaml                     # Test OpenAPI spec
├── src/it/k8s/                                     # Integration test (Maven Invoker)
│   ├── pom.xml                                     # Invoker test POM
│   ├── test.yaml                                   # Test spec
│   ├── goals.txt                                   # Maven goals to run
│   └── verify.groovy                               # Verification script
├── .github/workflows/                              # CI/CD workflows
├── pom.xml                                         # Project POM
├── .mvn/                                           # Maven Wrapper config
├── openspec/                                       # OpenSpec configuration
└── .claude/                                        # Claude Code configuration
```

## Core Modules

This is a single-module project (no sub-modules).

| Component | Type | Purpose |
|-----------|------|---------|
| `JavaCXFClientOsgiDsCodegen` | Generator class | Extends `JavaCXFClientCodegen` to add OSGi DS support; registers via Java SPI |
| `modelServiceActivator.mustache` | Mustache template | Generates `ClientApiServiceActivator.java` — an OSGi `@Component` that creates CXF client proxies and registers them as services |
| `pom.mustache` | Mustache template | Generates the output project's POM with `bundle` packaging, CXF/Jackson/OSGi dependencies |
| `SampleRunner` | Test | Programmatically runs the generator against a sample OpenAPI spec |
| `src/it/k8s/` | Integration test | Maven Invoker test that verifies generated code compiles and passes tests |

## Technology Stack

### Core Technologies
- **OpenAPI Generator** 4.2.2 — code generation framework
- **Apache CXF** 3.3.0 — JAX-RS client implementation (in generated output)
- **Jackson** 2.9.9 — JSON serialization (in generated output)
- **OSGi Core + Compendium** 6.0.0 — service registration and configuration (in generated output)
- **Mustache** — template engine for code generation (bundled in openapi-generator)
- **Lombok** 1.18.34 — annotation processing (delombok for javadoc)

### Build & Quality
- **Maven** 3.9.4+ with Maven Wrapper
- **JUnit 4.13** — unit testing
- **Maven Invoker Plugin** — integration testing
- **JaCoCo** 0.8.12 — code coverage
- **SonarQube** (via sonar-maven-plugin 3.9.1) — code quality analysis
- **flatten-maven-plugin** 1.2.7 — CI-friendly `${revision}` version resolution

## Build Commands

```bash
# Run unit tests
./mvnw clean test

# Full build with integration tests
./mvnw clean install

# Deploy to Judo Nexus
./mvnw deploy -Prelease-judong

# Deploy to Maven Central
./mvnw deploy -Prelease-central

# Update license headers
./mvnw process-sources -Pupdate-source-code-license
```

### Maven Profiles

| Profile | Purpose |
|---------|---------|
| `modules` | Default active; controls module resolution |
| `sign-artifacts` | Sign artifacts with GPG (via sign-maven-plugin) |
| `release-central` | Deploy to Maven Central (OSSRH) via nexus-staging-maven-plugin |
| `release-judong` | Deploy to Judo Nexus (nexus.judo.technology) |
| `generate-github-asciidoc-diagrams` | Generate documentation diagrams from AsciiDoc sources |
| `update-source-code-license` | Update Apache 2.0 license headers on all source files |

## Key Configuration Files

| File | Purpose |
|------|---------|
| `pom.xml` | Project build configuration; version managed via `${revision}` property (1.0.2-SNAPSHOT) |
| `.mvn/jvm.config` | JVM settings for Maven: `-Xms1024m -Xmx2048m` |
| `.mvn/extensions.xml` | Maven extensions: wagon-file, wagon-webdav, buildtime, profile-activator |
| `src/main/resources/META-INF/services/org.openapitools.codegen.CodegenConfig` | Java SPI service registration for the generator |
| `logback-test.xml` | Logging configuration for test execution |
| `.github/workflows/build.yml` | Main CI/CD workflow (build, test, deploy, tag) |

## Development Environment

**Required:**
- Java 21 JDK (Zulu distribution recommended)
- Maven 3.9.4+ (or use included `./mvnw`)

**JVM configuration** is preset in `.mvn/jvm.config` — no manual tuning needed.

## Git Workflow

- **Main Branch:** `develop`
- **Versioning:** CI-friendly `${revision}` property (currently 1.0.2-SNAPSHOT)
- **Branch naming:** `feature/JNG-NUMBER_summary`, `bugfix/JNG-NUMBER_summary`, `release/X.Y-betaN`
- **Golden rule:** Every commit must reference a JIRA ticket (`JNG-xxx`)
- **CI/CD:** GitHub Actions with workflows for build, release, merge-pr, and master release creation

## Important Notes

1. This is a **single-class generator** — all customization logic lives in `JavaCXFClientOsgiDsCodegen.java`
2. The generator **clears all default supporting files** from the parent class and replaces them with its own two Mustache templates
3. The `apiFilename()` override redirects `*ServiceActivator.mustache` output to the `activatorPackage` directory instead of the API package
4. The generated output uses **`bundle` packaging** (via maven-bundle-plugin) — it is an OSGi bundle, not a regular JAR
5. The generator is registered via **Java SPI** (`META-INF/services/org.openapitools.codegen.CodegenConfig`), which is how OpenAPI Generator discovers it at runtime
6. The integration test (`src/it/k8s/`) actually runs `mvn clean test` on the generated code to verify it compiles — this is the most thorough validation of template correctness
7. Surefire is configured with `--add-opens` flags for Java 21 compatibility

## Related Documentation

- [README.md](README.md) — Usage guide with Maven/Gradle/CLI examples and configuration options
- [CONTRIBUTING.md](CONTRIBUTING.md) — Development setup and PR guidelines
- [.github/CIFLOW.md](.github/CIFLOW.md) — CI/CD pipeline and branch strategy details
