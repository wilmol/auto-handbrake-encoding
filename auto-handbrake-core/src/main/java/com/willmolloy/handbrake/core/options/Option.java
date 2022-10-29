package com.willmolloy.handbrake.core.options;

/**
 * HandBrake options.
 *
 * @see <a href=https://handbrake.fr/docs/en/latest/cli/command-line-reference.html>Command line
 *     reference</a>
 * @author <a href=https://willmolloy.com>Will Molloy</a>
 */
public sealed interface Option permits Option.KeyValueOption, FrameRateControl {

  String key();

  /**
   * Option with key and value.
   *
   * @param <T> value type
   */
  sealed interface KeyValueOption<T> extends Option permits Encoder, Input, Output, Preset {

    T value();
  }
}
