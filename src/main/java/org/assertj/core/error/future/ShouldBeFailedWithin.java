/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2020 the original author or authors.
 */
package org.assertj.core.error.future;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.error.future.TimeUnitConversionUtils.toChronoUnit;
import static org.assertj.core.util.Strings.escapePercent;
import static org.assertj.core.util.Throwables.getStackTrace;

public class ShouldBeFailedWithin extends BasicErrorMessageFactory {

  private static final String SHOULD_BE_FAILED_WITHIN_DURATION = "%n"
                                                                + "Expecting%n"
                                                                + "  <%s>%n"
                                                                + "to be failed within %s.%n%n"
                                                                + "exception caught while trying to get the future result: ";

  private static final String SHOULD_BE_FAILED_WITHIN = "%n"
                                                      + "Expecting%n"
                                                      + "  <%s>%n"
                                                      + "to be failed within %s %s.%n%n"
                                                      + "exception caught while trying to get the future result: ";

  public static ErrorMessageFactory shouldBeFailed(Future<?> actual) {
    return new ShouldBeFailedWithin(actual);
  }

  public static ErrorMessageFactory shouldBeFailed(Future<?> actual, Duration timeout, Exception e) {
    return new ShouldBeFailedWithin(actual, timeout, e);
  }

  public static ErrorMessageFactory shouldBeFailed(Future<?> actual, long timeout, TimeUnit unit, Exception e) {
    return new ShouldBeFailedWithin(actual, timeout, unit, e);
  }

  private ShouldBeFailedWithin(Future<?> actual, Duration duration, Exception exception) {
    // don't put the stack trace as a parameter to avoid AssertJ default String formatting
    super(SHOULD_BE_FAILED_WITHIN_DURATION + escapePercent(getStackTrace(exception)), actual, duration);
  }

  private ShouldBeFailedWithin(Future<?> actual, long timeout, TimeUnit unit, Exception exception) {
    // don't put the stack trace as a parameter to avoid AssertJ default String formatting
    super(SHOULD_BE_FAILED_WITHIN + escapePercent(getStackTrace(exception)), actual, timeout, toChronoUnit(unit));
  }

  private ShouldBeFailedWithin(Future<?> actual) {
    super(SHOULD_BE_FAILED_WITHIN, actual);
  }
}
