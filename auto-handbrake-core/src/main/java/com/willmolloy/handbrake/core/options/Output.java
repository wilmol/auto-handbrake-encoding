package com.willmolloy.handbrake.core.options;

import java.nio.file.Path;

/**
 * HandBrake output option.
 *
 * @see <a href=https://handbrake.fr/docs/en/latest/cli/command-line-reference.html>Source
 *     Options</a>
 * @author <a href=https://willmolloy.com>Will Molloy</a>
 */
public sealed interface Output extends Option.KeyValueOption<Path> permits Internal.OutputImpl {

  static Output of(Path path) {
    return new Internal.OutputImpl("--output", path);
  }
}
