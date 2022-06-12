# auto-handbrake-encoding

[![build](https://github.com/will-molloy/auto-handbrake-encoding/workflows/build/badge.svg?branch=main)](https://github.com/will-molloy/auto-handbrake-encoding/actions?query=workflow%3Abuild)
[![integration-test](https://github.com/will-molloy/auto-handbrake-encoding/workflows/integration-test/badge.svg?branch=main)](https://github.com/will-molloy/auto-handbrake-encoding/actions?query=workflow%3Aintegration-test)
[![codecov](https://codecov.io/gh/will-molloy/auto-handbrake-encoding/branch/main/graph/badge.svg)](https://codecov.io/gh/will-molloy/auto-handbrake-encoding)

Automating HandBrake encoding

## Requirements

- Java 17
- HandBrakeCLI

## Use cases

### Encoding Nvidia ShadowPlay videos with a CFR preset

#### Why?

- Nvidia ShadowPlay records video with a variable/peak frame rate (PFR), leading to audio sync issues in video editing software

#### How?

1. Recursively scans input directory for `.mp4` files to encode
2. Encodes `.mp4` files with a Constant Frame Rate (CFR) preset
    - Encoded files are named with the suffix `.cfr.mp4`
    - The preset used is HandBrake's built-in "Production Standard" preset (H.264)
      - It works with any video resolution
      - It works with any framerate
      - It creates quite a large file afterwards, but it's ideal "as an intermediate format for video editing"
      - I recommend deleting the encoded file after using it, and retaining the original archived file
3. Archives original videos
    - Archived files are named with the suffix `.archived.mp4`
    - They won't be detected by the program again, if you want to encode again, remove this suffix first

#### Usage:

1. Build and test via Gradle:
   ```bash
   ./gradlew build integrationTest
   ```

2. Run via [Gradle task](nvidia-shadowplay/build.gradle):
   ```bash
   ./gradlew :nvidia-shadowplay:run -PinputDirectory="" -PoutputDirectory="" -ParchiveDirectory=""
   ```
    - Set `inputDirectory` to directory containing `.mp4` files to encode
    - Set `outputDirectory` where you want encoded files to be saved
    - Set `archiveDirectory` where you want archived files to be saved
    - (These can all be the same directory, personally I record and encode to an SSD, then archive to NAS)
