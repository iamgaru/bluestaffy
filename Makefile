# Blue Staffy mod — convenience targets
# Usage: make <target>

.PHONY: build build-fabric build-neoforge run-fabric run-neoforge clean jar-fabric jar-neoforge help

help:
	@echo "Targets:"
	@echo "  make build            — compile and package both Fabric and NeoForge JARs"
	@echo "  make build-fabric     — Fabric JAR only"
	@echo "  make build-neoforge   — NeoForge JAR only"
	@echo "  make run-fabric       — launch Fabric client for in-game testing"
	@echo "  make run-neoforge     — launch NeoForge client for in-game testing"
	@echo "  make clean            — delete build artefacts"
	@echo "  make jar-fabric       — build and print the Fabric JAR path"
	@echo "  make jar-neoforge     — build and print the NeoForge JAR path"

build:
	./gradlew :fabric:build :neoforge:build

build-fabric:
	./gradlew :fabric:build

build-neoforge:
	./gradlew :neoforge:build

run-fabric:
	./gradlew :fabric:runClient

run-neoforge:
	./gradlew :neoforge:runClient

clean:
	./gradlew clean

jar-fabric: build-fabric
	@ls fabric/build/libs/*.jar | grep -v dev-shadow

jar-neoforge: build-neoforge
	@ls neoforge/build/libs/*.jar | grep -v dev-shadow
