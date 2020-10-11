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
package org.assertj.core.api.future;

import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.InstanceOfAssertFactories.THROWABLE;
import static org.assertj.core.util.AssertionsUtil.expectAssertionError;
import static org.assertj.core.util.FailureMessages.actualIsNull;

@DisplayName("CompletableFutureAssert failsWithin")
class CompletableFutureAssert_failsWithin_Test {

  @Test
  void should_allow_assertion_on_future_when_failed_normally() {
    // GIVEN
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
      throw new IllegalStateException("Illegal argument");
    });
    // WHEN/THEN
    assertThat(future).failsWithin(1, MILLISECONDS)
                      .isExactlyInstanceOf(IllegalStateException.class);
  }

  @Test
  void should_allow_narrowed_assertion_on_failure() {
    // GIVEN
    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
      throw new RuntimeException("boom");
    });

    // WHEN/THEN
    assertThat(future).failsWithin(1, MILLISECONDS, as(THROWABLE))
                      .hasMessage("boom");
  }

  @Test
  void should_fail_if_completable_future_does_not_fail_within_given_timeout() {
    // GIVEN
    int sleepDuration = 100000;
    CompletableFuture<String> future = completedFutureAfter("ook!", sleepDuration);
    // WHEN
    AssertionError assertionError = expectAssertionError(() -> assertThat(future).failsWithin(10, MILLISECONDS));
    // THEN
    then(assertionError).hasMessageStartingWith(format("%nExpecting%n" +
                                                       "  <CompletableFuture[Incomplete]>%n" +
                                                       "to be failed within 10L Millis.%n"));
  }

  @Test
  void should_fail_if_completable_future_is_cancelled() {
    // GIVEN
    CompletableFuture<String> future = new CompletableFuture<>();
    future.cancel(true);
    // WHEN
    AssertionError assertionError = expectAssertionError(() -> assertThat(future).failsWithin(1, MILLISECONDS));
    // THEN
    then(assertionError).hasMessageStartingWith(format("%nExpecting%n" +
                                                       "  <CompletableFuture[Cancelled]>%n" +
                                                       "to be failed within 1L Millis.%n" +
                                                       "%n" +
                                                       "exception caught while trying to get the future result: java.util.concurrent.CancellationException"));
  }

  @Test
  void should_fail_when_completable_future_is_null() {
    // GIVEN
    CompletableFuture<String> future = null;
    // WHEN
    AssertionError assertionError = expectAssertionError(() -> assertThat(future).failsWithin(1, MILLISECONDS));
    // THEN
    then(assertionError).hasMessage(actualIsNull());
  }

  @Test
  void should_fail_if_completable_future_is_completed_exceptionally() {
    // GIVEN
    CompletableFuture<String> future = new CompletableFuture<>();
    future.completeExceptionally(new RuntimeException("boom%s%n"));
    // WHEN
    AbstractThrowableAssert<?, ? extends Throwable> throwableAssert = assertThat(future).failsWithin(1, MILLISECONDS);
    // THEN
    throwableAssert.isExactlyInstanceOf(RuntimeException.class)
                   .hasMessage("boom%s%n");
  }

  private static <U> CompletableFuture<U> completedFutureAfter(U value, long sleepDuration) {
    CompletableFuture<U> completableFuture = new CompletableFuture<>();
    Executors.newSingleThreadExecutor().submit(() -> {
      Thread.sleep(sleepDuration);
      completableFuture.complete(value);
      return null;
    });
    return completableFuture;
  }
}
