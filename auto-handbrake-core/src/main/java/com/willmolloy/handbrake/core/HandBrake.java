package com.willmolloy.handbrake.core;

import static com.google.common.base.Preconditions.checkNotNull;

import com.willmolloy.handbrake.core.options.Input;
import com.willmolloy.handbrake.core.options.Option;
import com.willmolloy.handbrake.core.options.Output;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HandBrake interface.
 *
 * @author <a href=https://willmolloy.com>Will Molloy</a>
 */
public class HandBrake {

  private static final Logger log = LogManager.getLogger();

  private static final Lock LOCK = new ReentrantLock();

  private final Cli cli;

  HandBrake(Cli cli) {
    this.cli = checkNotNull(cli);
  }

  public HandBrake() {
    this(new Cli());
  }

  /**
   * Runs HandBrake encoding.
   *
   * @param input input file
   * @param output output file
   * @param options HandBrake options
   * @return {@code true} if encoding was successful
   */
  public boolean encode(Input input, Output output, Option... options) {
    if (Files.exists(output.path())) {
      log.warn("Output ({}) already exists", output.path());
      return true;
    }

    List<String> command =
        Stream.concat(
                Stream.of(
                    "HandBrakeCLI",
                    input.key(),
                    input.path().toString(),
                    output.key(),
                    output.path().toString()),
                Arrays.stream(options)
                    .flatMap(
                        option -> {
                          // TODO exhaustive switch for sealed type
                          // TODO record deconstructor
                          if (option instanceof Option.KeyValueOption o) {
                            return Stream.of(o.key(), o.value());
                          }
                          if (option instanceof Option.ValueOnlyOption o) {
                            return Stream.of(o.value());
                          }
                          return Stream.of();
                        }))
            .toList();

    LOCK.lock();
    try {
      return cli.execute(command, new HandBrakeLogger(log));
    } catch (Exception e) {
      log.error("Error encoding: %s".formatted(input), e);
      return false;
    } finally {
      LOCK.unlock();
    }
  }
}
