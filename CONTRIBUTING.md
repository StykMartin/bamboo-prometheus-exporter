# Contributing

Thanks for your interest in improving the Prometheus exporter for Bamboo.

## Prerequisites

- Java 21
- Atlassian Plugin SDK — see `setup-atlassian-sdk.sh` for a one-shot installer

## Build and test

```sh
atlas-mvn clean verify
```

To run a Bamboo instance with the plugin loaded:

```sh
atlas-debug
```

## Code style

Formatting is enforced by Spotless. Before opening a PR:

```sh
atlas-mvn spotless:apply
```

## Pull requests

- Keep changes focused; one concern per PR.
- Use [Conventional Commits](https://www.conventionalcommits.org/) for commit messages (`feat:`, `fix:`, `chore:`, …).
- Make sure `atlas-mvn verify` passes locally.

## Reporting issues

Open an issue at https://github.com/StykMartin/bamboo-prometheus-exporter/issues with the Bamboo version, plugin version, and steps to reproduce.
