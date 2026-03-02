# CI/CD Flow and Branch Strategy

This project follows [GitFlow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow) for branching and versioning.

## Branch Types

| Branch Pattern | Base | Purpose | Merges Into |
|----------------|------|---------|-------------|
| `develop` | — | Main development branch; latest development sources | — |
| `feature/JNG-NUMBER_summary` | `develop` | New features for the next version | `develop` |
| `release/X.Y-betaN` | `develop` | Stabilization before release | `master` + `develop` |
| `bugfix/JNG-NUMBER_summary` | release branch | Bug fixes during release stabilization | release branch + `develop` |
| `support/JNG-NUMBER_summary` | release branch | Minor changes to a previous release | release branch |
| `hotfix/JNG-NUMBER_summary` | `master` | Critical fixes to production | `master` + `develop` |
| `master` | — | Latest released version | — |

```mermaid
gitGraph
    commit id: "initial"
    branch develop
    commit id: "dev-1"
    branch feature/JNG-1
    commit id: "feat-1"
    commit id: "feat-2"
    checkout develop
    merge feature/JNG-1 id: "merge-feat"
    branch release/1.0-beta1
    commit id: "stabilize"
    branch bugfix/JNG-4
    commit id: "fix"
    checkout release/1.0-beta1
    merge bugfix/JNG-4 id: "merge-fix"
    checkout develop
    merge release/1.0-beta1 id: "back-merge"
    checkout master
    merge release/1.0-beta1 id: "release-1.0"
```

## Version Numbering

Versions follow semantic versioning with these rules:

- **Feature branches**: do not change version numbers
- **Starting a release branch**: increment the 2nd number on `develop`
- **Bugfix branches**: do not change version numbers (applied to release branches before they reach `master`)
- **Support branches**: increment the 3rd number when started
- **Hotfix branches**: increment the 4th number when started

## GitHub Actions Workflows

The CI/CD pipeline consists of four interconnected workflows:

```mermaid
flowchart TD
    subgraph "build.yml"
        B1["Push on develop<br/>or PR on develop/master/release/*"]
        B2{Branch type?}
        B3["Version = pom.xml<br/>(without -SNAPSHOT)"]
        B4["Version = major.minor.qualifier<br/>.date_commitId_branchName"]
        B5["Build & deploy to Nexus"]
        B6["Create git tag v{version}"]
        B7{increment/* or release/*?}
        B8["Create merge-pr/{version} tag"]
        B9{develop?}
        B10["Build changelog +<br/>create GitHub prerelease"]

        B1 --> B2
        B2 -->|master, release/*| B3
        B2 -->|develop, increment/*| B4
        B3 --> B5
        B4 --> B5
        B5 --> B6
        B6 --> B7
        B7 -->|Yes| B8
        B7 -->|No| B9
        B8 --> B9
        B9 -->|Yes| B10
    end

    subgraph "merge-pr-tagged.yml"
        M1["Push on merge-pr/* tag"]
        M2{Version format?}
        M3["Merge PR to master"]
        M4["Squash PR to develop"]
        M5["Delete merge-pr/* tag"]

        M1 --> M2
        M2 -->|major.minor.qualifier| M3
        M2 -->|other| M4
        M3 --> M5
        M4 --> M5
    end

    subgraph "create-release-on-master.yml"
        R1["Push on master"]
        R2["Build changelog"]
        R3["Create GitHub release (latest)"]

        R1 --> R2 --> R3
    end

    subgraph "release.yml"
        L1["Manual trigger with version"]
        L2{Version = 'auto'?}
        L3["Release version from pom.xml"]
        L4["Release version = given"]
        L5["Next version = qualifier + 1"]
        L6["Create PR on master"]
        L7["Create PR on develop"]

        L1 --> L2
        L2 -->|Yes| L3
        L2 -->|No| L4
        L3 --> L5
        L4 --> L5
        L5 --> L6
        L5 --> L7
    end

    B8 -.->|triggers| M1
    M3 -.->|triggers| R1
    L6 -.->|triggers| B1
    L7 -.->|triggers| B1
```

### Workflow Summary

1. **build.yml** — Triggered on pushes to `develop` and PRs targeting `develop`, `master`, or `release/*`. Builds, tests, deploys to Nexus, creates version tags, and publishes GitHub prereleases for `develop`.

2. **merge-pr-tagged.yml** — Triggered by `merge-pr/*` tags (created by build.yml). Merges PRs to `master` (for release versions) or squashes to `develop` (for development versions).

3. **create-release-on-master.yml** — Triggered on pushes to `master`. Generates a changelog and creates a GitHub release marked as "latest".

4. **release.yml** — Manually triggered with a version number (or `'auto'`). Creates two PRs: one targeting `master` with the release version, and one targeting `develop` with the next development version.

## Development Rules

> **Important:** There is no commit without a ticket number. Every commit and PR must reference a JIRA ticket in the format `JNG-xxx`.

Issue tracking is managed in [JIRA](https://blackbelt.atlassian.net/jira/dashboards).
