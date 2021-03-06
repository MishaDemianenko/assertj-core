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
package org.assertj.core.internal.files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.error.ShouldBeFile.shouldBeFile;
import static org.assertj.core.error.ShouldHaveExtension.shouldHaveExtension;
import static org.assertj.core.test.TestData.someInfo;
import static org.assertj.core.util.FailureMessages.actualIsNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.api.AssertionInfo;
import org.assertj.core.internal.FilesBaseTest;
import org.junit.jupiter.api.Test;

/**
 * Tests for
 * <code>{@link org.assertj.core.internal.Files#assertHasExtension(org.assertj.core.api.AssertionInfo, java.io.File, String)}</code>
 * .
 * 
 * @author Jean-Christophe Gay
 */
class Files_assertHasExtension_Test extends FilesBaseTest {

  private String expectedExtension = "java";

  @Test
  void should_throw_error_if_actual_is_null() {
    assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> files.assertHasExtension(someInfo(), null,
                                                                                              expectedExtension))
                                                   .withMessage(actualIsNull());
  }

  @Test
  void should_throw_npe_if_extension_is_null() {
    assertThatNullPointerException().isThrownBy(() -> files.assertHasExtension(someInfo(), actual, null))
                                    .withMessage("The expected extension should not be null.");
  }

  @Test
  void should_throw_error_if_actual_is_not_a_file() {
    AssertionInfo info = someInfo();
    when(actual.isFile()).thenReturn(false);

    Throwable error = catchThrowable(() -> files.assertHasExtension(info, actual, expectedExtension));

    assertThat(error).isInstanceOf(AssertionError.class);
    verify(failures).failure(info, shouldBeFile(actual));
  }

  @Test
  void should_throw_error_if_actual_does_not_have_the_expected_extension() {
    AssertionInfo info = someInfo();
    when(actual.isFile()).thenReturn(true);
    when(actual.getName()).thenReturn("file.png");

    Throwable error = catchThrowable(() -> files.assertHasExtension(info, actual, expectedExtension));

    assertThat(error).isInstanceOf(AssertionError.class);
    verify(failures).failure(info, shouldHaveExtension(actual, "png", expectedExtension));
  }

  @Test
  void should_pass_if_actual_has_expected_extension() {
    when(actual.isFile()).thenReturn(true);
    when(actual.getName()).thenReturn("file.java");
    files.assertHasExtension(someInfo(), actual, expectedExtension);
  }
}
