# Blue Staffy mod — convenience targets
# Usage: make <target>

.PHONY: build run clean deps jar help

help:
	@echo "Targets:"
	@echo "  make build   — compile and package the mod JAR"
	@echo "  make run     — launch Minecraft client for in-game testing"
	@echo "  make clean   — delete build artefacts"
	@echo "  make deps    — force-refresh all Gradle dependencies"
	@echo "  make jar     — build and print the path to the distributable JAR"

build:
	./gradlew build

run:
	./gradlew runClient

clean:
	./gradlew clean

deps:
	./gradlew --refresh-dependencies clean build

jar: build
	@ls build/libs/*.jar | grep -v sources | grep -v slim
